# What Next / Roadmap

This document outlines the future roadmap and improvements for the `similar-products-api`.

## 🏗️ Architecture & Performance
- [ ] **WebFlux vs. Virtual Threads**: Decide on the long-term concurrency model. Evaluate switching to Virtual Threads (Java 21) for simpler blocking code vs. staying with WebFlux.
- [ ] **GraalVM Native Image**: Create a GraalVM native image artifact to minimize startup time and memory footprint.
- [ ] **Performance Benchmarks**: Create comparison versions to benchmark:
    1.  Standard Spring Boot (Blocking)
    2.  Spring WebFlux (Reactive)
    3.  Spring Boot with Virtual Threads
    4.  GraalVM variants of the above
- [ ] **Resource Limits**: Tune JVM options and container `cgroup` limits to prevent OOM kills in Kubernetes/Docker environments.

## 🔭 Observability
- [ ] **Actuator Setup**: Add `spring-boot-starter-actuator` to expose metrics and health endpoints.
- [ ] **Metrics**: Add Micrometer/Prometheus metrics for deeper insights.
- [ ] **Correlation IDs**: Implement Request ID / Correlation ID via MDC for log tracing.
- [ ] **Tracing**: Implement distributed tracing (e.g., OpenTelemetry/Zipkin) to trace requests across microservices.
- [ ] **Structured Logging**: Enable JSON log formatting for better parsing in log aggregators (ELK/Splunk).
- [ ] **Async Logging**: Ensure logging is non-blocking to match the reactive architecture.
- [ ] **Health Checks**: Enhance Spring Boot Actuator probes (Liveness/Readiness) for container orchestration.

## 🧪 Testing
- [ ] **Acceptance Tests**: Implement automated acceptance tests using **Karate**.
- [ ] **Load Testing**: Formalize the K6 load testing pipeline. *Note: Current load tests run on dev machines; ideal benchmarks should run in a prod-like environment.*
- [ ] **SonarQube**: Integrate SonarQube quality gates for code analysis.

## 🔒 Security
- [ ] **API Gateway Integration**: Confirm and document that Authentication/Authorization is offloaded to the API Gateway.
- [ ] **Network Policy**: Define security policies for ingress/egress traffic (Service Mesh/Istio).
- [ ] **Dependency Scanning**: Add OWASP Dependency Check to the build pipeline.
- [ ] **Security Scans**: Implement automated security checks (SAST/dependencies).

## 🧹 Code Quality & Standards
- [ ] **Date Handling**: Standardize on `OffsetDateTime` over `LocalDateTime` for timezone safety.
- [ ] **Exceptions**: Refactor custom exceptions to extend a common base exception hierarchy rather than generic `RuntimeException`.
- [ ] **OpenAPI Generation**: Review naming conventions for generated OpenAPI files to ensure clarity.
- [ ] **API Versioning**: Implement a versioning strategy for the API.
- [ ] **Pre-commit Hooks**: Enforce formatting (Spotless) and tests before commit.

## 🐳 infrastructure
- [ ] **Create Dockerfile**: Implement a multi-stage Dockerfile for optimized production builds.
- [ ] **Container Configuration**: Optimize `Dockerfile` layers and base images.
- [ ] **CI/CD Pipeline**: Create a GitHub Actions workflow for Build, Test, and Safety checks.
- [ ] **Time synchronization**: Ensure container clocks are synchronized (NTP) to avoid timezone/timestamp issues.