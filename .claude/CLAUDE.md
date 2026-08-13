# Similar Products Service

Spring Boot 3.3 reactive application that exposes `GET /product/{productId}/similar` on port 5000.

## Architecture

Aggregates two upstream APIs on port 3001 (Simulado mock):
1. `GET /product/{id}/similarids` → list of product IDs ordered by similarity
2. `GET /product/{id}` → product detail

All detail requests run in **parallel** via `flatMapSequential` (preserves similarity order while fetching concurrently).

```
Client → SimilarProductsController
            → SimilarProductsService
                → ProductClient.getSimilarIds()     (sequential first)
                → ProductClient.getProductDetail()  (all in parallel)
```

## Tech Stack

- Java 21, Spring Boot 3.3, Spring WebFlux (Project Reactor + Netty)
- Maven

## Build & Run

### Locally (requires Java 21 + Maven)
```bash
cd app
mvn spring-boot:run
```

### Docker Compose (builds and runs everything)
```bash
docker-compose up -d simulado influxdb grafana yourapp
```

## Testing

```bash
cd app && mvn test      # 31 tests, ~25s — nothing needs to be running
```

The upstream API is stubbed in-process with MockWebServer, so the suite needs neither Docker nor Simulado.
Maven runs on JDK 21 (`~/.jdks/jdk-21.0.6`) even though `java` on the PATH is 17.

| Test class | Covers |
|------------|--------|
| `ProductClientTest` | Timeouts, 404/500 skipping, JSON id coercion, malformed bodies |
| `SimilarProductsServiceTest` | Similarity order, parallel fetching, dropping unresolvable products |
| `SimilarProductsControllerTest` | 200 / `[]` / 404 / 5xx mapping |
| `SimilarProductsIntegrationTest` | The five mock scenarios end-to-end over a real socket |

See the **write-tests** skill for conventions and the timing pitfalls (cold-start flakiness, virtual-time hangs).

## Load Testing

```bash
# Run k6 test (app + infra must be up)
docker-compose run --rm k6 run scripts/test.js

# View results in Grafana
# http://localhost:3000/d/Le2Ku9NMk/k6-performance-test
```

## Key Design Decisions

| Decision | Reason |
|----------|--------|
| WebClient (non-blocking) | 200 concurrent VUs; reactive I/O avoids thread-per-request overhead |
| `flatMapSequential` | Parallel HTTP calls, results emitted in original similarity order |
| 2s per-product timeout | Mocks have 5s/50s delays that must be bounded for acceptable p99 |
| Skip on 404/500/timeout | Individual product failures should not fail the whole request |
| 404 from similarids → 404 response | Contract requirement; means the base product has no similar IDs |
| `similar-ids-timeout-ms` (2s) | Entry-point call was unbounded; could hang on connection contention |
| No cache | Throughput is capped by k6 client pacing + timeout, not upstream calls — a cache can't beat that ceiling here |
| `max-connections: 50` (balanced) | See performance note below — the main tuning lever |

## Performance — the connection pool is the key lever

Load testing (see `app/README.md` for the full data) established:
- **The single-process mock is the bottleneck, not our pool.** Smaller pool = higher throughput
  (pool 8 → ~289 req/s; pool 128 → ~77 req/s). Oversizing overloads the mock.
- **A small pool is fast because it silently drops available products under load** (the doomed 5s/50s
  products monopolise connections). Completeness rises with pool size (pool 8 → 12%; pool 100+ → 100%).
- Throughput vs completeness is a **Pareto trade-off**; `max-connections: 50` is the chosen balance
  (fast + correct in normal use, graceful degradation under extreme load).
- Presets (via `PRODUCT_API_MAX_CONNECTIONS`): max throughput = 8–16; max correctness = 150–200.

When investigating performance, do NOT reflexively enlarge the pool — measure first.

## Logging

Rule: **WARN is for what makes a request fail; DEBUG is for what the design deliberately tolerates.**
Skipping a product is designed behaviour that happens on nearly every request under load, so it must
never be logged above DEBUG — it would bury the failures that matter.

| Level | What | Frequency |
|-------|------|-----------|
| INFO | Effective client config + timeouts | 2 lines, once at startup |
| WARN | `/similarids` returned 5xx or timed out; request resolved to a 5xx | Only on a request that fails |
| DEBUG | IDs received, each skipped product + reason, products resolved + elapsed ms | ~2 lines/request + 1 per skip |

At INFO the whole k6 scenario set produces **zero** per-request lines. Set `LOG_LEVEL=DEBUG` to trace a
request end to end — never during a load test.

A 404 from `/similarids` is a client outcome, not a fault: DEBUG, not WARN. `ProductClientLoggingTest`
pins these levels so they can't be relaxed by accident.

## Mock Scenarios (port 3001)

| Our endpoint | Similar IDs | What happens |
|-------------|-------------|--------------|
| /product/1/similar | [2,3,4] | Fast — all 3 products return quickly |
| /product/2/similar | [3,100,1000] | Product 100 (1s) OK; product 1000 (5s) → timeout → skipped |
| /product/3/similar | [100,1000,10000] | Product 100 OK; 1000 and 10000 → timeout → skipped |
| /product/4/similar | [1,2,5] | Product 5 returns 404 → skipped; returns [1,2] |
| /product/5/similar | [1,2,6] | Product 6 returns 500 → skipped; returns [1,2] |

## Configuration

All properties live under `product-api` in `application.yaml` and map to an env var (Spring relaxed
binding), so they can be overridden without a rebuild. See `app/README.md` for the full reference.

| Property | Env var | Default | Description |
|----------|---------|---------|-------------|
| `product-api.base-url` | `PRODUCT_API_BASE_URL` | `http://localhost:3001` | Upstream API base URL (`http://simulado` in Docker) |
| `product-api.connect-timeout-ms` | `PRODUCT_API_CONNECT_TIMEOUT_MS` | 1000 | TCP connect timeout |
| `product-api.detail-timeout-ms` | `PRODUCT_API_DETAIL_TIMEOUT_MS` | 2000 | Per-product detail request timeout |
| `product-api.similar-ids-timeout-ms` | `PRODUCT_API_SIMILAR_IDS_TIMEOUT_MS` | 2000 | Entry-point (similarids) call timeout |
| `product-api.max-connections` | `PRODUCT_API_MAX_CONNECTIONS` | 50 | Outbound connection pool size — the main perf lever |
| `product-api.pending-acquire-timeout-ms` | `PRODUCT_API_PENDING_ACQUIRE_TIMEOUT_MS` | 2000 | Max wait for a pooled connection |
| `logging.level.com.inditex.similarproducts` | `LOG_LEVEL` | `INFO` | `DEBUG` traces every request; keep at INFO under load |
