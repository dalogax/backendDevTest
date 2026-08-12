---
description: Build and start the Similar Products Spring Boot app locally on port 5000
allowed-tools: Bash(mvn:*), Bash(cd:*), Bash(curl:*), Bash(docker-compose:*)
---

Start the Similar Products application locally.

1. Ensure the mock server is running (the app depends on it):

```bash
docker-compose up -d simulado
```

2. Build and run the app from the `app/` directory:

```bash
cd app && mvn spring-boot:run
```

3. Once it is listening on port 5000, verify with a sample request:

```bash
curl -s http://localhost:5000/product/1/similar
```

Expected: a JSON array with the detail of products 2, 3 and 4.

> Requires Java 21 and Maven. To run everything in Docker instead, use `/infra` and start the `yourapp` service.
