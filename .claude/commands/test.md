---
description: Run the k6 load test against the Similar Products service and open Grafana
allowed-tools: Bash(docker-compose:*)
---

Run the k6 load test against the Similar Products service.

## Prerequisites

Start the infrastructure and the app (if not already running):

```bash
docker-compose up -d simulado influxdb grafana yourapp
```

Or if running the app locally: `cd app && mvn spring-boot:run`

## Run the test

```bash
docker-compose run --rm k6 run scripts/test.js
```

## View results

Open Grafana: http://localhost:3000/d/Le2Ku9NMk/k6-performance-test

## Test scenarios (200 VUs each, 10s duration)

| Scenario | Product | Expected behaviour |
|----------|---------|-------------------|
| normal | 1 | Fast response, all 3 similar products returned |
| notFound | 4 | Product 5 (404) skipped, returns 2 products |
| error | 5 | Product 6 (500) skipped, returns 2 products |
| slow | 2 | Product 1000 (5s) times out, returns 2 products in ~2s |
| verySlow | 3 | Products 1000+10000 time out, returns 1 product in ~2s |
