# Similar Products API

![Java 21](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=springboot&logoColor=white)
![Spring WebFlux](https://img.shields.io/badge/WebFlux-Reactive-6DB33F?logo=spring&logoColor=white)
![Resilience4j](https://img.shields.io/badge/Resilience4j-Circuit_Breaker-orange)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white)

Spring Boot REST API that aggregates similar product details for a given product, acting as a **Backend-for-Frontend (BFF)** for the _"Similar Products"_ storefront widget.

---

## Architecture

```
GET /product/{id}/similar
         │
         ▼
 ┌───────────────────┐
 │    Controller     │  REST layer — maps HTTP ↔ domain
 └────────┬──────────┘
          │
          ▼
 ┌───────────────────┐
 │      Service      │  Scatter-gather: fetches N product details concurrently
 └────────┬──────────┘  Tolerant Reader: skips failed products, never throws partial
          │
          ▼
 ┌───────────────────┐       ┌──────────────────────────┐
 │  ProductApiClient │──────▶│  External API (port 3001) │
 └───────────────────┘       └──────────────────────────┘
   Caffeine cache
   Circuit Breaker / Retry
   Fail-fast timeouts
```

**Layer responsibilities**

| Layer | Class | Responsibility |
|---|---|---|
| Controller | `SimilarProductsController` | Exposes `GET /product/{id}/similar` on port 5000 |
| Service | `SimilarProductsService` | Concurrent scatter-gather with graceful partial failures |
| Client | `ProductApiClient` | HTTP calls, caching, resilience operators |

---

## Key Design Decisions

### Concurrency
Product details are fetched with `Flux.flatMapSequential` — all N HTTP calls run **in parallel**, results are collected in the original similarity order.

### Resilience

| Mechanism | Configuration | Behaviour |
|---|---|---|
| Connect timeout | 500 ms | Fast TCP connection failure |
| Read timeout | 500 ms (fail-fast) | Cuts slow upstream immediately |
| Circuit Breaker | 50 % failure rate · window 10 · 5 s recovery | Stops hammering a degraded upstream |
| Retry | Disabled | Timeouts fail immediately — no latency accumulation |
| Partial failures | Tolerant Reader pattern | One failed product detail → omitted, not propagated |
| Graceful shutdown | 10 s drain on SIGTERM | In-flight requests complete before the process exits |

### Caching

| Cache | Type | Size | TTL | Extra |
|---|---|---|---|---|
| `similarIds` | `Cache` (sync) | 1 000 entries | 30 s | — |
| `productDetail` | `AsyncLoadingCache` | 5 000 entries | 30 s | Negative caching (404s) · Request deduplication |

- **Negative caching** — 404 responses are stored as absent; the upstream is never re-queried for non-existent product IDs.
- **Request deduplication** — concurrent cache misses for the same key share a single in-flight HTTP call instead of each spawning their own.
- **Dedicated executor** — cache loading uses a virtual-thread executor, isolated from `ForkJoinPool.commonPool()`.

### Error contract

All error responses follow the same shape: `{ "code": "<ErrorCode>", "message": "..." }`

| HTTP status | `code` | Cause |
|---|---|---|
| `404` | `PRODUCT_NOT_FOUND` | Requested product ID does not exist |
| `502` | `EXTERNAL_SERVICE_ERROR` | Upstream returned 5xx |
| `502` | `EXTERNAL_SERVICE_UNAVAILABLE` | Upstream unreachable (connection refused) |
| `503` | `SERVICE_UNAVAILABLE` | Circuit Breaker is open |
| `504` | `UPSTREAM_TIMEOUT` | Upstream did not respond within 500 ms |
| `500` | `INTERNAL_ERROR` | Unexpected server error |

---

## Quick Start

### With Docker (recommended)

```bash
# 1. Start mock API + observability stack
docker-compose up -d simulado influxdb grafana

# 2. Start the application
docker-compose up -d similar-products

# 3. Verify
curl http://localhost:5000/product/1/similar
```

### Run locally

```bash
docker-compose up -d simulado          # mock API required

cd similarProducts
./mvnw spring-boot:run                 # requires JAVA_HOME → JDK 21+
```

---

## Testing

```bash
cd similarProducts
./mvnw test                            # unit + integration tests
```

Coverage report → `target/site/jacoco/index.html`

### Load test (K6)

```bash
docker-compose run --rm k6 run scripts/test.js
```

Results dashboard → [http://localhost:3000/d/Le2Ku9NMk/k6-performance-test](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test)

---

## Observability & API Documentation

| Endpoint | Purpose |
|---|---|
| [/swagger-ui.html](http://localhost:5000/swagger-ui.html) | Interactive API docs |
| [/actuator/health](http://localhost:5000/actuator/health) | Liveness + upstream reachability + Circuit Breaker state |
| [/actuator/prometheus](http://localhost:5000/actuator/prometheus) | Prometheus metrics |
