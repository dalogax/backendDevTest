# Similar Products API

## Overview
The **Similar Products API** is a RESTful service designed to provide product details for similar products based on a given product ID. It serves as a backend-for-frontend (BFF) aggregator, consuming external APIs to resolve similar product IDs and fetch their details.

## Tech Stack
- **Java 21**: Core programming language.
- **Spring Boot 3.5.8**: Application framework.
- **Spring Web MVC**: Standard web stack (Blocking I/O).
- **Virtual Threads**: Lightweight threads for high-throughput concurrency (Project Loom).
- **Resilience4j**: Fault tolerance library (Circuit Breaker, Retry, Bulkhead).
- **Caffeine**: In-memory caching.
- **MapStruct**: Java bean mapping.
- **SpringDoc OpenAPI**: API documentation.
- **Maven**: Build tool.

## Key Features
- **Virtual Threads**: Leverages Java 21 Virtual Threads for high throughput and scalability handling blocking I/O efficiently.
- **Resilience**: Implements circuit breakers, retries, and bulkheads to handle external service failures gracefully.
- **Caching**: Caches results to reduce load on downstream services and improve response times.
- **API Documentation**: Automatically generated OpenAPI documentation.

## Prerequisites
- Java 21 SDK
- Maven 3.x
- Docker & Docker Compose (for running mocks and infrastructure)

## Getting Started

### 1. Start Support Infrastructure
Before running the application, you need to start the mock services (Product API simulado) and other infrastructure (InfluxDB, Grafana) defined in the root project.

From the root directory of the repository:
```bash
docker-compose up -d simulado influxdb grafana
```
This starts the mock Product API at `http://localhost:3001`.

### 2. Build the Application
Navigate to the `similar-products-api` directory and build the project:
```bash
cd similar-products-api
mvn clean install
```

### 3. Run the Application
You can run the application using the Maven Spring Boot plugin:
```bash
mvn spring-boot:run
```
The application will start on port **5000**.

## Configuration
The application is configured via `src/main/resources/application.yml`. Key configurations include:

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Application port | `5000` |
| `external.product-service.base-url` | URL of the external Product Detail/Mock API | `http://host.docker.internal:3001` |
| `similar-products.max-concurrent-calls` | Concurrency limit | `10` |

### Resilience Configuration
Resilience4j is configured to handle failures from the external Product Service:
- **Retry**: Up to 4 attempts with exponential backoff for network/server errors.
- **Circuit Breaker**: Opens after 50% failure rate (sliding window of 10 calls).

## API Endpoints
The main operation is:

`GET /product/{productId}/similar`

Returns a list of similar product details for the given `{productId}`.

### Swagger UI
Once the application is running, you can access the API documentation at:
http://localhost:5000/webjars/swagger-ui/index.html

## Testing
Unit and integration tests are included. Run them with:
```bash
mvn test
```
