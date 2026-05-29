# Similar Products API

Spring Boot REST API that aggregates similar product details for a given product.

## Architecture

```
Controller → Service → ProductApiClient → External API (simulado:3001)
```

- **Controller**: Exposes `GET /product/{productId}/similar` on port 5000
- **Service**: Scatter-gather orchestration — fetches product details concurrently, skips failures gracefully
- **Client**: WebClient with Caffeine cache, Resilience4j Circuit Breaker and Retry
- **DTOs**: Java records for immutability and thread safety

### Concurrency

Product details are fetched in parallel using `Flux.flatMapSequential`, which launches all HTTP calls concurrently while preserving the similarity order from the upstream API.

### Resilience

| Mechanism | Config |
|---|---|
| Connect timeout | 500ms |
| Read timeout | 2000ms |
| Circuit Breaker | Opens at 50% failure rate (sliding window of 10), 5s recovery |
| Retry | 2 attempts on connection errors (similarIds only) |
| Partial failures | Failed product details are omitted, not propagated |

### Caching

In-memory Caffeine caches for both `similarIds` (1000 entries) and `productDetail` (5000 entries), with 30s TTL. This dramatically reduces upstream calls under K6 load.

## Requirements

- **Docker** (for running via docker-compose)
- **JDK 21+** (only if running locally outside Docker)

## Run with Docker (recommended)

From the repository root:

```bash
docker-compose up -d simulado influxdb grafana
docker-compose up -d similar-products
```

Verify: [http://localhost:5000/product/1/similar](http://localhost:5000/product/1/similar)

## Run locally

```bash
# Start the mock API first
docker-compose up -d simulado

# Then run the app (requires JAVA_HOME pointing to JDK 21+)
cd similarProducts
./mvnw spring-boot:run
```

## Run tests

```bash
cd similarProducts
./mvnw test
```

Coverage report is generated at `target/site/jacoco/index.html`.

## Run K6 performance test

```bash
docker-compose run --rm k6 run scripts/test.js
```

Results: [http://localhost:3000/d/Le2Ku9NMk/k6-performance-test](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test)

## API Documentation

Swagger UI: [http://localhost:5000/swagger-ui.html](http://localhost:5000/swagger-ui.html)

## Health & Monitoring

- Health check: [http://localhost:5000/actuator/health](http://localhost:5000/actuator/health) (includes upstream reachability and circuit breaker state)
- Metrics: [http://localhost:5000/actuator/prometheus](http://localhost:5000/actuator/prometheus)
