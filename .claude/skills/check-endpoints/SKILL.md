---
name: check-endpoints
description: Smoke-test all five mock scenarios against the running Similar Products app on port 5000 and validate each response against the expected result. Use after code changes, before running load tests, or when verifying resilience behaviour (timeouts, 404/500 skipping).
allowed-tools: Bash(curl:*)
---

# Check endpoints

Exercise every scenario the k6 test covers and confirm the app handles each correctly.

## Run the smoke test

```bash
for id in 1 2 3 4 5; do
  echo "=== /product/$id/similar ==="
  curl -s -w "\n-> HTTP %{http_code} in %{time_total}s\n" "http://localhost:5000/product/$id/similar"
  echo
done
```

## Expected results

| Endpoint | HTTP | Products returned | Why |
|----------|------|-------------------|-----|
| /product/1/similar | 200 | 2, 3, 4 | All upstreams fast |
| /product/2/similar | 200 | 3, 100 | Product 1000 (5s) exceeds the 2s timeout → skipped |
| /product/3/similar | 200 | 100 | Products 1000 (5s) and 10000 (50s) time out → skipped |
| /product/4/similar | 200 | 1, 2 | Product 5 returns 404 → skipped |
| /product/5/similar | 200 | 1, 2 | Product 6 returns 500 → skipped |

Slow scenarios (2 and 3) should complete in roughly the timeout window (~2s), never the full upstream delay.

## If something is wrong

- **Timeout not respected (response takes 5s+):** check `product-api.detail-timeout-ms` and the `.timeout(...)` in `ProductClient.getProductDetail`.
- **404/500 propagated instead of skipped:** check the `onErrorResume` / non-2xx handling in `ProductClient.getProductDetail` — it must resolve to `Mono.empty()`.
- **Connection refused:** the app isn't running. Start it (and the mock) with `docker-compose up -d simulado yourapp` — build first with `docker-compose build yourapp` if code changed. (Local `mvn spring-boot:run` needs Java 21; this host has Java 17, so prefer Docker.)
