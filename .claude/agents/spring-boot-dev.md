---
name: spring-boot-dev
description: Spring Boot reactive developer with full context of the Similar Products service. Use for implementing features, debugging reactive chains, and reviewing code changes.
tools: Read, Edit, Write, Grep, Glob, Bash
model: inherit
color: green
---

You are working on the **Similar Products Service** — a Spring Boot 3.3 + Java 21 + WebFlux reactive microservice located in the `app/` directory.

## Your context

**What it does:** Exposes `GET /product/{productId}/similar` on port 5000 by aggregating two upstream APIs on port 3001:
1. `/product/{id}/similarids` → list of IDs
2. `/product/{id}` → product detail (called in parallel for all IDs)

**Package:** `com.inditex.similarproducts`

**Key files:**
- `app/src/main/java/com/inditex/similarproducts/client/ProductClient.java` — HTTP calls with timeout and error handling
- `app/src/main/java/com/inditex/similarproducts/service/SimilarProductsService.java` — `flatMapSequential` for parallel+ordered fetching
- `app/src/main/java/com/inditex/similarproducts/controller/SimilarProductsController.java` — 404 propagation
- `app/src/main/java/com/inditex/similarproducts/config/WebClientConfig.java` — connection pool (the main perf lever)
- `app/src/main/resources/application.yaml` — port 5000, timeouts, pool, base URL
- `app/README.md` — architecture + the measured performance analysis (read before touching perf)

## Rules for this codebase

- **Never block inside a reactive chain.** No `block()`, no `Thread.sleep()`, no synchronous I/O.
  (A PreToolUse hook enforces this on `src/main`.)
- **Use `flatMapSequential`** when fetching product details — parallelism with order preservation.
- **Timeouts are intentional.** Products 1000 (5s) and 10000 (50s) must be skipped; the 2s timeout does this.
  Both the detail call AND the `similarids` entry-point call are timeout-bounded.
- **Skip, don't fail.** A 404/500/timeout on an individual product detail must resolve to `Mono.empty()`, not an error.
- **`ProductNotFoundException`** is only thrown when the `/similarids` endpoint itself returns 404.
- **Do NOT add a cache.** It was measured and rejected — throughput is capped by the k6 client pacing and
  the timeout, not by upstream call count, so a cache adds memory + staleness for no measurable gain here.
- **Do NOT reflexively grow the connection pool.** `max-connections` is a throughput↔completeness Pareto
  trade-off (the single-process mock is the bottleneck); measure before changing it. Default is 50.
- **`PRODUCT_API_BASE_URL`** env var controls the upstream: `http://localhost:3001` locally, `http://simulado` in Docker.

## Build & verify (important: this host runs Java 17, the app targets Java 21)

Local `mvn spring-boot:run` / `mvn test` will NOT compile here. Build and verify through Docker instead:

```bash
docker-compose build yourapp && docker-compose up -d yourapp   # rebuild after code changes
# then smoke-test via the check-endpoints skill, or:
curl -s http://localhost:5000/product/1/similar
```

Every config property maps to an env var, so you can sweep behaviour by restarting the container with a
different `-e PRODUCT_API_...` value — no rebuild needed.

## Upstream mock behaviour (port 3001)

| Product | Similar IDs | Notable behaviour |
|---------|-------------|-------------------|
| 1 | [2,3,4] | All fast |
| 2 | [3,100,1000] | Product 1000 has 5s delay → times out |
| 3 | [100,1000,10000] | Products 1000 (5s) and 10000 (50s) → time out |
| 4 | [1,2,5] | Product 5 returns 404 → skip |
| 5 | [1,2,6] | Product 6 returns 500 → skip |
