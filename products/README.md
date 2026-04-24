# Products Service

A high-performance microservice built to manage and serve product information efficiently, utilizing caching strategies and resilient communication patterns with external dependencies.

## 🛠 Tech Stack

* **Runtime:** Java 21
* **Framework:** Spring Boot 3
* **Caching:** Redis
* **Resilience:** Resilience4j (Circuit Breaker, Retry patterns)
* **Observability:** Spring Boot Actuator
* **Testing:** 
  * JUnit 5
  * Mockito
  * Spring Boot Starter Test
  * WireMock (for mocking external HTTP dependencies)
  * Embedded Redis (for integration testing)

## 🚀 Getting Started

To launch the complete infrastructure, including the application, database, and monitoring tools, use the following command:

```bash
docker compose up -d simulado grafana influxdb products products-redis
```

### Performance Testing
The k6 performance tests should be executed following the same procedure described in the original project documentation. Ensure the environment is fully up before initiating load tests.

---

### 🔍 Validation & Monitoring

Once the containers are running, you can verify the application's health and the status of the integrated resilience patterns through the following Actuator endpoints:

* **System Health & Infrastructure:** [http://localhost:5000/actuator/health](http://localhost:5000/actuator/health)
  Displays the overall status of the application, including the connection status to the Redis cache and the health of the configured Circuit Breakers.
* **Circuit Breaker Status:** [http://localhost:5000/actuator/circuitbreakers](http://localhost:5000/actuator/circuitbreakers)
  Shows the current state (`CLOSED`, `OPEN`, `HALF_OPEN`) of the `productClient` circuit breaker, along with real-time metrics (failure rate, buffered calls, etc.).
* **Circuit Breaker Events:** [http://localhost:5000/actuator/circuitbreakerevents](http://localhost:5000/actuator/circuitbreakerevents)
  Provides a chronological log of the latest events, useful for seeing exactly when the circuit opens due to timeouts or 5xx errors from the external mocks.

---

### 📝 Architectural Decisions

#### Error Handling & API Contract
A specific design choice was made regarding error propagation: **the application does not return 5xx status codes on its endpoints.** This decision is rooted in the established contract between the backend and the frontend. By intercepting internal or downstream failures and mapping them to specific client-side expectations, we ensure the frontend can handle state transitions gracefully without encountering unhandled server-side exceptions. This maintains a robust and predictable user experience even when external dependencies are unavailable.