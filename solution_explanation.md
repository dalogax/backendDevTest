# Solution Explanation

## Architecture
The solution is built using a microservices architecture. The main components are:
- A Spring Boot application that exposes the REST API on port 5000.
- A mock server (Simulado) that provides the existing APIs.
- InfluxDB and Grafana for performance testing and monitoring.

## Design Patterns
The following design patterns were used in the implementation:
- **Controller**: The Spring Boot application uses the Controller pattern to handle incoming HTTP requests and delegate the processing to the appropriate service.
- **Service**: The Service pattern is used to encapsulate the business logic and interact with the external APIs.
- **Repository**: The Repository pattern is used to abstract the data access layer and interact with the mock server.

## Technologies
The following technologies were used in the implementation:
- **Spring Boot**: A framework for building Java-based applications.
- **Docker**: A platform for developing, shipping, and running applications in containers.
- **Simulado**: A mock server for simulating external APIs.
- **InfluxDB**: A time-series database for storing performance metrics.
- **Grafana**: A monitoring and visualization platform for analyzing performance metrics.

## Trade-offs and Considerations
- **Performance**: The solution is designed to handle a high number of requests per second. However, the performance may be affected by the response times of the external APIs.
- **Resilience**: The solution is resilient to failures in the external APIs. If an API returns an error or a timeout, the application will handle it gracefully and return an appropriate response.
- **Maintainability**: The code is organized into separate layers (Controller, Service, Repository) to improve maintainability and readability.
- **Scalability**: The solution can be easily scaled by running multiple instances of the Spring Boot application and load balancing the requests.
