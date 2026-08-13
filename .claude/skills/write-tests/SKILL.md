---
name: write-tests
description: Add or extend automated tests for the Similar Products service — picking the right layer, stubbing the upstream product API with MockWebServer, and testing reactive timeout/order/skip behaviour without flakiness. Use when writing new tests, covering a bug fix, or when a test is flaky or hangs.
allowed-tools: Read, Edit, Write, Grep, Glob, Bash(mvn:*)
---

# Write tests

Tests live in `app/src/test/java/com/inditex/similarproducts/`, mirroring the main package layout.
Run them with `mvn test` from `app/` (see the `/unit-test` command). The build uses JDK 21 via Maven
even though `java` on the PATH is 17, so no Docker is needed.

## Pick the right layer

| Testing… | Use | Example |
|----------|-----|---------|
| Status codes, timeouts, JSON mapping of an upstream call | `ProductClientTest` — MockWebServer + `StepVerifier` | a 503 must be skipped |
| Order, parallelism, which products survive | `SimilarProductsServiceTest` — Mockito `ProductClient` | a skipped product must not shift order |
| Response status/body of the endpoint | `SimilarProductsControllerTest` — `@WebFluxTest` + `@MockBean` | an exception must map to 404 |
| A whole scenario over a real socket | `SimilarProductsIntegrationTest` — `@SpringBootTest(RANDOM_PORT)` | one of the five Simulado scenarios |

Prefer the narrowest layer that can express the behaviour; add an integration test only when the
scenario is about the layers working together.

## Stubbing the upstream

`ProductClient` talks to a real socket, so the upstream is stubbed with **MockWebServer** (test-scoped,
version managed by the Spring Boot BOM), never by mocking `WebClient`.

- **Sequential expectations** → `upstream.enqueue(...)`, one response per request.
- **Concurrent detail calls** → set a `Dispatcher` that answers **by path**. The default queue dispatcher
  hands out responses in arrival order, which is not deterministic once the calls overlap.
- **Slow upstream** → `new MockResponse().setHeadersDelay(n, TimeUnit.SECONDS)`.
- Build the `WebClient` through `new WebClientConfig().productWebClient(...)` so the pool and connector
  under test are the production ones.

## Reactive assertions

Use `StepVerifier`, never `.block()`:

```java
StepVerifier.create(productClient.getProductDetail("5"))
        .verifyComplete();                       // empty = the product was skipped
```

For "these calls must overlap", use virtual time so the assertion is deterministic rather than a
wall-clock guess — and always give it a wall-clock bound:

```java
StepVerifier.withVirtualTime(() -> service.getSimilarProducts("1"))
        .expectSubscription()
        .thenAwait(Duration.ofSeconds(1))        // 3 × 1s calls in parallel = 1s
        .expectNext(PRODUCT_2, PRODUCT_3, PRODUCT_4)
        .expectComplete()
        .verify(Duration.ofSeconds(10));         // see the hang pitfall below
```

Stub the delayed Monos with `willAnswer(call -> Mono.just(p).delayElement(d))`, not `willReturn(...)`:
the Mono must be assembled *after* the virtual scheduler is installed, or the delay binds to the real clock.

## Pitfalls that have already bitten here

- **Cold-start flakiness.** The first HTTP call in a fresh JVM took ~1.2s (class loading, Netty, Jackson).
  Tests that are *not* about timing must use the production 2s timeout; only the timeout tests use a short
  budget, paired with a stub delay far above it. Never tighten a stub delay to speed a test up.
- **A virtual-time test hangs instead of failing.** If the code stops being concurrent, the virtual clock
  never advances far enough and `verifyComplete()` waits forever. Always end with
  `.expectComplete().verify(Duration.ofSeconds(10))` so a regression fails fast.
- **`verifyComplete(Duration)` does not exist** — it is `.expectComplete().verify(Duration)`.
- **Spring Boot 3.3 uses `@MockBean`**, not `@MockitoBean` (that arrives in 3.4).
- Point the integration test at the stub with `@DynamicPropertySource`, and start the `MockWebServer` in a
  `static` initializer — the registry callback runs before `@BeforeAll`.

## Before finishing

Run the full suite (`cd app && mvn test`) and confirm the new test actually fails when the behaviour it
covers is broken — temporarily invert the production code, watch it go red, then revert.
