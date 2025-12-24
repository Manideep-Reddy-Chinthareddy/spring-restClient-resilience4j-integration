# Spring RestClient with Resilience4j

This project demonstrates how to integrate **Resilience4j** patterns with the Spring Boot `RestClient` using a programmatic (imperative) approach.

Instead of relying on AOP annotations like `@CircuitBreaker` or `@Retry`, this implementation explicitly wraps service calls using Resilience4j's `Decorators` API. This offers finer control over the execution flow and is particularly useful when you need to apply resilience logic dynamically or outside of Spring-managed bean proxies.

## Features

*   **Manual Configuration**: A custom `R4jConfig` class that reads properties directly from `application.yml` and explicitly creates Resilience4j registries (`CircuitBreakerRegistry`, `RetryRegistry`, etc.).
*   **Imperative Decoration**: The `SpringRestClientWithR4j` component demonstrates how to wrap `RestClient` calls with:
    *   **Circuit Breaker**: Prevents cascading failures when a remote service is down.
    *   **Retry**: Automatically retries failed requests with configurable backoff.
    *   **Bulkhead**: Limits the number of concurrent calls to a specific service.
    *   **TimeLimiter**: Enforces a time limit on the execution of the request.
*   **Java 25 & Spring Boot 4 (Milestone)**: leveraging the latest language features and framework updates.

## Getting Started

### Prerequisites

*   Java 23
*   Gradle 8.x

### Configuration

Resilience settings are defined in `src/main/resources/application.yml`. You can tune parameters like failure thresholds, wait durations, and sliding window sizes there.

```yaml
resilience4j:
  circuitbreaker:
    instances:
      default:
        failureRateThreshold: 50
        waitDurationInOpenState: 5s
```

### Running the Application

To build and run the application:

```bash
./gradlew clean bootRun
```

### Running Tests

To execute the unit and integration tests:

```bash
./gradlew test
```

## Project Structure

*   `R4jConfig.java`: Handles the manual parsing of YAML properties and registration of Resilience4j instances.
*   `SpringRestClientWithR4j.java`: A wrapper service that executes HTTP requests using `RestClient` while applying the configured resilience decorators.
*   `RestClientConfig.java`: Configures the underlying `RestClient` bean.

## Author

**Manideep Reddy Chinthareddy**
