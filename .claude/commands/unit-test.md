---
description: Run the JUnit/Reactor test suite for the Similar Products app (no Docker, no mocks needed)
allowed-tools: Bash(mvn:*)
---

Run the automated test suite. Unlike `/test` (which drives k6 against a running stack), this needs
**nothing running** — the upstream product API is stubbed in-process by MockWebServer.

## Run everything

```bash
cd app && mvn test
```

Add `-o` to run offline once the dependencies are cached.

## Run a subset

```bash
cd app && mvn test -Dtest=ProductClientTest
cd app && mvn test -Dtest=SimilarProductsServiceTest#returnsProductsInSimilarityOrder
```

## What the suite covers

| Test class | Layer | Focus |
|------------|-------|-------|
| `ProductClientTest` | HTTP boundary | Timeouts, 404/500 handling, JSON id coercion, malformed bodies |
| `SimilarProductsServiceTest` | Aggregation | Similarity order, parallel fetching, skipping unresolvable products |
| `SimilarProductsControllerTest` | HTTP contract | 200 / empty array / 404 / 500 mapping |
| `SimilarProductsIntegrationTest` | End-to-end | The five Simulado scenarios over a real socket |
| `SimilarProductsApplicationTests` | Wiring | Context loads |

## Notes

- The build runs on **JDK 21** (`~/.jdks/jdk-21.0.6`, via Maven) even though `java` on the PATH is 17 —
  so `mvn test` works locally; you do not need Docker for this.
- Reports land in `app/target/surefire-reports/`; read the `.txt` for a failing class to get the stack trace.

## If a test fails

- **A timing test fails on a cold/loaded machine:** the non-timing tests deliberately use the production
  2s timeout so JVM warm-up cannot trip them. Only the timeout tests use a short budget, with the stub
  delayed far above it. Widen the margin rather than shortening the stub delay.
- **`fetchesDetailsInParallelRatherThanOneAfterAnother` fails:** the detail fetch stopped being concurrent —
  check that `SimilarProductsService` still uses `flatMapSequential` (not `concatMap`/`flatMap` on a Mono chain).
- **Ordering test fails:** `flatMapSequential` was swapped for `flatMap`, which emits in completion order.
