# Similar Products Service

Spring Boot application that implements the agreed `similarProducts` contract: it exposes
`GET /product/{productId}/similar` on port **5000**, aggregating the two existing upstream APIs
(similar-ids + product-detail) served by the mock on port 3001.

- **Java 21**, **Spring Boot 3.3**, **Spring WebFlux** (Project Reactor + Reactor Netty)
- Fully non-blocking / reactive end to end

---

## Table of contents

- [Running the app](#running-the-app)
- [Architecture](#architecture)
- [Design decisions](#design-decisions)
- [Performance analysis & tuning](#performance-analysis--tuning) ← the core of this write-up
- [Configuration reference](#configuration-reference)
- [Testing](#testing)
- [Logging](#logging)

---

## Running the app

### With Docker (recommended — no local JDK needed)

From the repository root:

```bash
docker-compose up -d simulado influxdb grafana yourapp
curl http://localhost:5000/product/1/similar
```

### Locally

Requires **JDK 21** and Maven. The mock must be running (`docker-compose up -d simulado`):

```bash
cd app
mvn spring-boot:run
```

---

## Architecture

```
GET /product/{id}/similar
        │
        ▼
SimilarProductsController        (reactive endpoint, maps errors to HTTP status)
        │
        ▼
SimilarProductsService           orchestration
        │  1) getSimilarIds(id)            → GET /product/{id}/similarids   (single call)
        │  2) for each id, getProductDetail → GET /product/{id}             (parallel)
        ▼
ProductClient                    WebClient calls with timeouts + error handling
        │
        ▼
Reactor Netty connection pool → upstream product API (mock :3001)
```

`flatMapSequential` is the key operator: it launches every product-detail request **in parallel**
(concurrency bounded by the connection pool) but **emits results in the original similarity order**,
satisfying the contract's "ordered by similarity" requirement without a post-sort.

---

## Design decisions

### Reactive / non-blocking
The load test drives 200 concurrent virtual users. A blocking, thread-per-request model would need
hundreds of threads and pay heavy context-switching cost. WebFlux + Reactor Netty handle the
concurrency on a small event-loop pool. A hook (`.claude/hooks/guard-blocking-calls.sh`) even blocks
edits that would introduce `.block()`/`Thread.sleep` into the main sources.

### Resilience: partial results over failure
A similar product whose detail call fails must not fail the whole response:

| Upstream situation                        | Behaviour                                   |
|-------------------------------------------|---------------------------------------------|
| Detail returns 404 / 500                  | product skipped (`Mono.empty()`)            |
| Detail exceeds `detail-timeout-ms` (2s)   | product skipped                             |
| Detail connection error                   | product skipped                             |
| `similarids` returns 404                  | endpoint returns **404** (base product n/a) |
| `similarids` slow / stalled               | bounded by `similar-ids-timeout-ms` (2s)    |

The `similar-ids` timeout was added after load testing revealed the entry-point call had no bound:
under connection contention it could stall on connection acquisition and hang the whole request.

### Caching: deliberately omitted
The mocks return the same handful of products repeatedly, so caching looks tempting. It was measured
and **rejected** because it cannot improve the metric under test:

- Throughput of the fast scenarios is capped by the k6 client's `sleep(0.5)` per iteration
  (~333 req/s ceiling per scenario), **not** by upstream latency — the app already answers those in
  ~13–100 ms.
- Latency of the slow scenarios is pinned to the 2s timeout on the genuinely-slow products
  (1000 = 5s, 10000 = 50s), which are never cacheable (they time out and return nothing). Because
  detail calls run in parallel, caching the fast product does not lower that ceiling.

So a cache would only reduce upstream call volume (not what the test measures) while adding a
dependency, memory footprint and cache-invalidation/staleness concerns. **If the real product API
were the bottleneck (rather than being mocked)**, a short-TTL `Caffeine` `AsyncCache` with request
coalescing would be the right next step — see the note at the end of the performance section.

---

## Performance analysis & tuning

> This section documents the empirical analysis behind the chosen configuration. All numbers come
> from the provided k6 test (200 VUs × 5 scenarios) plus a concurrency probe, run on this hardware.
> They are relative, not absolute — reproduce with the sweeps below on the target hardware.

### Finding 1 — the upstream is the bottleneck, not our pool

The instinct under load is to enlarge the outbound connection pool. Measurement showed the opposite:
the mock is a single-process server, and flooding it with connections degrades every response.
Sweeping `product-api.max-connections` (via the `PRODUCT_API_MAX_CONNECTIONS` env var, no rebuild):

| max-connections | throughput | median latency |
|----------------:|-----------:|---------------:|
| 4               | 304 req/s  | 13 ms          |
| 8               | 289 req/s  | 26 ms          |
| 16              | 259 req/s  | 60 ms          |
| 32 (netty default on 16 cores) | 206 req/s | 312 ms |
| 64              | 127 req/s  | 743 ms         |
| 128             | 77 req/s   | 1.3 s          |
| 500             | 120 req/s  | ~450 ms        |

Throughput is **monotonically better with a smaller pool** — a genuinely counter-intuitive result
driven entirely by the mock's limited concurrency.

### Finding 2 — a small pool is fast because it drops valid products

Raw throughput hides a correctness cost. The k6 test does not validate response bodies, so a probe
was added: fire 80 concurrent `GET /product/2/similar` and count how many responses still contain
product **100** (an *available* product whose detail takes 1s — it should always be present).

| max-connections | responses containing product 100 |
|----------------:|----------------------------------:|
| 8               | 12 %                              |
| 16              | 25 %                              |
| 32              | 35 %                              |
| 50              | 56 %                              |
| 100+            | 100 %                             |

The mechanism: the doomed slow products (1000 = 5s, 10000 = 50s) hold their connections for the full
2s timeout. With a small pool they monopolise it, so the *available* 1s product (100) can't acquire a
connection within its own timeout and gets dropped. A small pool is fast **because it silently
returns incomplete results under load.** Shortening the timeout was tested as a mitigation and did
**not** help — completeness is governed by concurrent connection *demand*, not hold time.

### The trade-off and the choice

There is **no pool size that maximises both** throughput and completeness — they form a Pareto
frontier, because the same pool serves the fast scenarios (which want it small) and the slow
scenarios (which want it large).

**Chosen: balanced — `max-connections: 50`.**

- Healthy throughput (~160 req/s), bounded latency, **zero errors**.
- **Correct in normal use** — every scenario returns complete, correct results when not under
  extreme concurrent load.
- Under massive concurrent load it degrades **gracefully**: it returns a valid partial list
  (the contract allows `minItems: 0`) instead of failing, hanging, or overloading the upstream.
  That is defensible resilience behaviour (load-shedding), not a silent bug.

### How to move along the frontier

Everything is configurable (property or env var) — no rebuild required:

| Goal | Setting | Result |
|------|---------|--------|
| **Balanced (default)** | `max-connections: 50` | ~160 req/s, correct normally, graceful degradation under load |
| **Max throughput** (best k6 dashboard) | `max-connections: 8`–`16` | ~260–290 req/s, but sheds available products aggressively under concurrency |
| **Max correctness** (always complete) | `max-connections: 150`–`200` | 100 % complete even under load, ~90 req/s (mock overload lowers throughput) |

```bash
# example: run the container tuned for maximum throughput
docker run -e PRODUCT_API_MAX_CONNECTIONS=12 ...
```

> **If the upstream were a real, horizontally-scalable API** (not a single-process mock), the picture
> flips: a larger pool would *not* overload it, "max correctness" and "max throughput" would converge,
> and adding a short-TTL cache with request coalescing (one upstream call shared by all concurrent
> callers of the same id) would become the highest-impact optimisation. The bottleneck here is an
> artefact of the mock, and the design stays correct for either world.

---

## Configuration reference

All properties live under `product-api` in `application.yaml`; each maps to an env var
(Spring relaxed binding), so it can be overridden per-environment without rebuilding.

| Property | Env var | Default | Purpose |
|----------|---------|---------|---------|
| `product-api.base-url` | `PRODUCT_API_BASE_URL` | `http://localhost:3001` | Upstream base URL (`http://simulado` in Docker) |
| `product-api.connect-timeout-ms` | `PRODUCT_API_CONNECT_TIMEOUT_MS` | 1000 | TCP connect timeout |
| `product-api.detail-timeout-ms` | `PRODUCT_API_DETAIL_TIMEOUT_MS` | 2000 | Per-product detail timeout (bounds the 5s/50s mocks) |
| `product-api.similar-ids-timeout-ms` | `PRODUCT_API_SIMILAR_IDS_TIMEOUT_MS` | 2000 | Entry-point call timeout |
| `product-api.max-connections` | `PRODUCT_API_MAX_CONNECTIONS` | 50 | Outbound connection pool size (see tuning above) |
| `product-api.pending-acquire-timeout-ms` | `PRODUCT_API_PENDING_ACQUIRE_TIMEOUT_MS` | 2000 | Max wait for a pooled connection, aligned with the request budget |
| `logging.level.com.inditex.similarproducts` | `LOG_LEVEL` | `INFO` | `DEBUG` traces every request (see [Logging](#logging)); keep at `INFO` under load |
| `server.port` | — | 5000 | Contract-mandated port |

---

## Testing

### Automated test suite

```bash
cd app && mvn test    # 36 tests, ~25s
```

Nothing needs to be running: the upstream product API is stubbed in-process with **MockWebServer**
(test-scoped, version managed by the Spring Boot BOM), so the suite is self-contained and CI-friendly.

| Test class | Layer | Covers |
|------------|-------|--------|
| `ProductClientTest` | HTTP boundary | Per-call timeouts, 404 → `ProductNotFoundException`, 500 → error, detail 404/500/timeout/bad-body → skipped, numeric ids coerced to strings |
| `ProductClientLoggingTest` | HTTP boundary | Pins the log levels: a skipped product never reaches INFO; an entry-point failure always reaches WARN |
| `SimilarProductsServiceTest` | Aggregation | Similarity order preserved when an earlier product answers last, details fetched **in parallel** (asserted with virtual time), unresolvable products dropped, upstream errors propagated |
| `SimilarProductsControllerTest` | HTTP contract | 200 + JSON body, `[]` when nothing resolves, 404 for an unknown base product, 5xx for an unexpected failure |
| `SimilarProductsIntegrationTest` | End-to-end | The five scenarios below, over a real socket through the full chain |
| `SimilarProductsApplicationTests` | Wiring | Context loads |

Timing tests scale the upstream delays down (a stub delayed far above a short timeout) rather than
waiting out the real 5s/50s mocks. Tests that are *not* about timing keep the production 2s timeout so
JVM warm-up cannot make a healthy product look slow.

### Functional smoke test (all 5 scenarios)

```bash
for id in 1 2 3 4 5; do
  echo "=== /product/$id/similar ==="
  curl -s -w "\n-> HTTP %{http_code} in %{time_total}s\n" "http://localhost:5000/product/$id/similar"
done
```

Expected: product 1 → {2,3,4}; product 2 → {3,100} (1000 times out); product 3 → {100}
(1000+10000 time out); product 4 → {1,2} (5 is 404); product 5 → {1,2} (6 is 500).

### Load test (the provided k6 suite)

```bash
docker-compose run --rm k6 run scripts/test.js
# results: http://localhost:3000/d/Le2Ku9NMk/k6-performance-test
```

---

## Logging

The governing rule is **WARN for what makes a request fail, DEBUG for what the design deliberately
tolerates**. Skipping a product is designed behaviour, not a fault: products 1000 and 10000 time out on
essentially every request the load test makes, so logging that path above DEBUG would bury the failures
that actually matter.

| Level | What is logged | Volume |
|-------|----------------|--------|
| `INFO` | Effective client config (base URL, pool, timeouts) | 2 lines, once at startup |
| `WARN` | `/similarids` returned 5xx or timed out; a request resolved to a 5xx | Only when a request actually fails |
| `DEBUG` | IDs received, every skipped product with its reason, products resolved + elapsed ms | ~2 lines per request, plus one per skip |

A 404 from `/similarids` is a client outcome (it becomes a 404 response), not a fault — it stays at DEBUG.

Running the full set of scenarios at the default `INFO` produces **zero** per-request lines. Set
`LOG_LEVEL=DEBUG` to trace a request end to end — never during a load test:

```bash
LOG_LEVEL=DEBUG mvn spring-boot:run
```

```
DEBUG SimilarProductsService   : Product 2 has similar IDs [3, 100, 1000]
DEBUG ProductClient            : Skipping product 1000: no response within 2000ms
DEBUG SimilarProductsController: Resolved 2 similar products for product 2 in 2012ms
```

`ProductClientLoggingTest` pins these levels so the no-noise rule cannot be relaxed by accident.
