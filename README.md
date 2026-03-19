# API Gateway — EBYN (Electronic Declaration System)

A Spring Cloud Gateway-based API Gateway service developed at GİB Teknoloji, providing secure and centralized access to declaration microservices.

## What It Does

This gateway routes all incoming requests to the backend microservices (cache-config-manager, queue-management-service, etc.). It applies token validation, API key verification, authorization, request/response logging, trace ID generation, and method-based timeout handling on every request.

## Architecture

Built on Spring Cloud Gateway (WebFlux/reactive). Requests pass through a filter chain in the following order: PreLogging captures request details, Trace generates a unique trace ID, StripBasePath removes the `/api` prefix, Authorization performs a 3-layer security check (API key, fixed token, or external token validation), MethodBasedTimeout applies different timeouts based on the HTTP method, and PostLogging captures the response details.

Route definitions are managed through `application.yml` and separated by environment using Helm values files (dev/test/prod). This allows the same gateway to run with different configurations across environments.

## Tech Stack

- **Java 17+, Spring Boot 3, Spring Cloud Gateway** (WebFlux/Reactive)
- **Custom Gateway Filters:** Authorization, PreLogging, PostLogging, Trace, StripBasePath, MethodBasedTimeout
- **3-Layer Security:** API Key + Fixed Token + External Token Validation Service
- **Elastic APM:** Distributed tracing and performance metrics collection
- **Elastic Metrics Export:** Metrics shipping to Elasticsearch
- **Swagger/OpenAPI:** API documentation
- **Helm Charts:** Kubernetes deployment configs for dev/test/prod environments
- **Docker:** Containerization
- **Gradle:** Build tool
- **Built-in Dashboard:** Real-time monitoring panel (HTML/CSS/JS with charts and export)
- **Unit Tests:** Comprehensive tests for filters, utils, services, constants, and models (15+ test classes)

## Filter Chain (Request Flow)

```
Incoming Request
    ↓
[1] PreLoggingFilter — Log request details (method, URL, headers)
    ↓
[2] TraceFilter — Generate unique trace ID and attach to headers
    ↓
[3] StripBasePathFilter — Remove /api prefix from the path
    ↓
[4] AuthorizationFilter — 3-layer security check:
    • Is the API key valid?
    • Is the token in the fixed token list?
    • Does the external validation service approve the token?
    • Whitelisted endpoints bypass all checks
    ↓
[5] MethodBasedTimeoutFilter — Apply different timeouts per HTTP method (GET/POST/PUT)
    ↓
[6] Route → Target Microservice (cache-config-manager, queue-management, etc.)
    ↓
[7] PostLoggingFilter — Log response details
```

## Project Structure

```
api-gateway/
├── src/main/java/.../api/gateway/
│   ├── components/
│   │   ├── filters/
│   │   │   ├── customs/
│   │   │   │   └── AuthorizationGatewayFilterFactory.java  — Authorization filter
│   │   │   └── defaults/
│   │   │       ├── PreLoggingGatewayFilterFactory.java      — Request logging
│   │   │       ├── PostLoggingGatewayFilterFactory.java     — Response logging
│   │   │       ├── TraceGatewayFilterFactory.java           — Trace ID generation
│   │   │       ├── StripBasePathGatewayFilterFactory.java   — Path rewriting
│   │   │       └── MethodBasedTimeoutGatewayFilterFactory.java — Method-based timeout
│   │   ├── context/
│   │   │   └── UserContextHolder.java                       — Thread-local user context
│   │   └── generators/
│   │       └── RandomTraceIdGenerator.java                  — Trace ID generator
│   ├── configurations/
│   │   ├── routes/DashboardRoute.java                       — Dashboard routing
│   │   ├── properties/                                       — Config classes (APM, security, timeout)
│   │   └── ApmConfiguration.java                            — Elastic APM configuration
│   ├── services/
│   │   ├── AuthServiceImpl.java                             — 3-layer token validation
│   │   └── LogServiceImpl.java                              — Logging service
│   ├── utils/
│   │   ├── SecurityUtil.java                                — Whitelist matching
│   │   └── HeaderUtil.java                                  — Header management
│   ├── constants/                                            — Constants (auth, headers, whitelist)
│   └── exceptions/                                           — Custom exception classes
├── src/main/resources/
│   ├── application.yml                                       — Gateway routes and filter configuration
│   └── static/dashboard/                                     — Built-in monitoring panel (HTML/CSS/JS)
├── src/test/                                                 — 15+ unit test classes
├── helm/api-gateway/                                         — Kubernetes Helm charts
│   ├── dev-values.yaml                                       — Development environment
│   ├── test-values.yaml                                      — Test environment
│   └── prod-values.yaml                                      — Production environment
└── Dockerfile                                                — Container image
```

## Key Design Decisions

| Decision | Choice | Why |
|----------|--------|-----|
| Gateway Framework | Spring Cloud Gateway (WebFlux) | Reactive and non-blocking, suitable for high throughput |
| Filter Architecture | Custom AbstractGatewayFilterFactory | Each filter is independent, ordering controlled via OrderEnum, can be toggled on/off |
| Security | 3-layer (API Key + Fixed Token + External Validation) | Flexibility for different client types: internal services use API key, static apps use fixed token, users go through external validation |
| Environment Management | Helm values files (dev/test/prod) | Same gateway runs with different routes and configs per environment |
| Observability | Elastic APM + Metrics Export + Pre/Post Logging | Every request is traceable via trace ID, performance metrics are collected |
| Timeout Strategy | MethodBasedTimeout | GET requests are fast, POST/PUT can take longer — different timeouts per method |

## What I Learned

- Developing custom filters in Spring Cloud Gateway's reactive (WebFlux) pipeline
- Controlling the request lifecycle through filter ordering with OrderEnum
- Combining 3 different security layers in a single filter with whitelist management
- Deploying the same application to different environments using Helm values
- Integrating Elastic APM for distributed tracing and performance metric collection
- Carrying user context through a reactive pipeline using ThreadLocal (UserContextHolder)

## Status
🟢 Running in production — serving GİB's electronic declaration systems
