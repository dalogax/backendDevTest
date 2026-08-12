---
description: Start, stop or check the Docker testing infrastructure (mocks, InfluxDB, Grafana)
allowed-tools: Bash(docker-compose:*), Bash(curl:*)
---

Manage the Docker testing infrastructure (mocks, InfluxDB, Grafana).

## Start infrastructure only (run app locally)

```bash
docker-compose up -d simulado influxdb grafana
```

## Start everything including the app

```bash
docker-compose up -d simulado influxdb grafana yourapp
```

## Stop everything

```bash
docker-compose down
```

## Verify mocks are responding

```bash
curl http://localhost:3001/product/1/similarids
curl http://localhost:3001/product/1
```

## Useful URLs

- Simulado mock server: http://localhost:3001
- Grafana dashboard: http://localhost:3000/d/Le2Ku9NMk/k6-performance-test
- App endpoint: http://localhost:5000/product/1/similar
