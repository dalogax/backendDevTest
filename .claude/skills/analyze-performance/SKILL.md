---
name: analyze-performance
description: Run or interpret the k6 load test for the Similar Products service, read the metrics, and produce actionable recommendations on latency, error rate and resilience. Use when investigating slow responses, timeouts, high p95/p99, or preparing performance improvements.
allowed-tools: Bash(docker-compose:*), Bash(docker:*), Bash(curl:*)
---

# Analyze performance

This service must be efficient and resilient under load, so this skill checks both **performance** and **resilience**.

## 1. Make sure infra and app are up

```bash
docker-compose ps
```

Expected running: `simulado`, `influxdb`, `grafana`, and either the `yourapp` container or a local app on port 5000.

## 2. Run the load test

```bash
docker-compose run --rm k6 run scripts/test.js
```

The k6 script runs 5 scenarios (200 VUs, 10s each): `normal` (product 1), `notFound` (4), `error` (5), `slow` (2), `verySlow` (3).

## 3. Read the results

Grafana dashboard: http://localhost:3000/d/Le2Ku9NMk/k6-performance-test

Focus on the k6 summary printed in the terminal — `http_req_duration` (avg/p90/p95), `http_req_failed`, and `iterations`.

## 4. Evaluate against targets

| Scenario | Target p95 | Target error rate |
|----------|-----------|-------------------|
| normal / notFound / error | < 200 ms | 0% |
| slow / verySlow | ~ 2 s (timeout-bounded, never 5s+) | 0% |

Error rate should be **0%** — upstream 404/500 are handled, not propagated. This k6 version (0.28.0)
has no `http_req_failed` metric, so confirm resilience via `docker-compose logs yourapp` (no exceptions)
plus all requests completing.

## 5. Diagnose and recommend

| Symptom | Likely cause | Fix to propose |
|---------|-------------|----------------|
| slow/verySlow p95 far above 2s | timeout not applied | verify `.timeout(...)` in `ProductClient` (detail AND similarids) |
| errors in app logs / requests not completing | error escaping the reactive chain | check `onErrorResume(e -> Mono.empty())` |
| high median latency + low throughput | pool **too large** → single-process mock overloaded | *lower* `max-connections` (measure; smaller is faster here) |
| `/product/2/similar` drops product 100 under load | pool too small → doomed products starve the available one | *raise* `max-connections` toward correctness (Pareto trade-off) |
| cascading failures on upstream 5xx | no circuit breaker | consider a Resilience4j circuit breaker on `getProductDetail` |

Do **not** propose a cache for this setup — it was measured and rejected (throughput is bounded by k6
pacing + timeout, not upstream volume). See the performance-analyst agent / `app/README.md` for the data.

Report findings as: current numbers → gap vs target → concrete code change (with file), and say which
side of the throughput/completeness trade-off it moves.
