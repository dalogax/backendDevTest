# 📦 Similar Products API

A Spring Boot backend service that provides a single endpoint to retrieve similar products for a given product ID. Designed as part of the ITX Backend Test.

## 🚀 Features

- REST API exposing:
    - Fetching a list of similar products
- In-memory caching layer with **1 hour TTL**
- Custom thread pool executor to avoid blocking the main thread on I/O
- Resiliency with [Resilience4j](https://resilience4j.readme.io/) (e.g., timeouts, circuit breakers)

---

## 📚 Why these decisions?

### 🧠 Why not Reactive Programming?

Although Spring WebFlux is a good option, in this specific use case:
- The data source is not truly reactive.
- The complexity of reactive flows was unnecessary for such a small and predictable workload.
- A well-configured thread pool and caching already cover the performance needs.

### 🗂 Caching with Caffeine

We used **Caffeine** (`spring-boot-starter-cache` + `caffeine`) for:
- Client-side caching with a **1-hour expiration policy**.
- Avoiding unnecessary calls to the upstream API.
- Very efficient memory footprint.

### 🔗 Custom Thread Pool

To avoid blocking `@Async` or scheduled tasks on the common pool, we created a dedicated **ExecutorConfig** to fine-tune:
- Core/maximum pool size
- Queue capacity
- Rejection policy

This ensures that spikes in load won’t starve other processes and improves throughput predictability.

---

## 🧪 How to run

### Prerequisites

- Java 21+
- Maven 3.8+

### Run locally

```bash
mvn clean spring-boot:run
