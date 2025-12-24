package com.example.demo;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.common.bulkhead.configuration.CommonBulkheadConfigurationProperties;
import io.github.resilience4j.common.circuitbreaker.configuration.CommonCircuitBreakerConfigurationProperties;
import io.github.resilience4j.common.retry.configuration.CommonRetryConfigurationProperties;
import io.github.resilience4j.common.timelimiter.configuration.CommonTimeLimiterConfigurationProperties;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Configuration
@EnableConfigurationProperties(R4jConfig.R4jProperties.class)
/**
 * Configuration class for setting up Resilience4j beans.
 * <p>
 * This class reads configuration properties and manually creates instances of
 * Bulkhead, Retry, TimeLimiter, and CircuitBreaker to ensure they are
 * correctly registered in the application context.
 *
 * @author Manideep Reddy Chinthareddy
 */
public class R4jConfig {

    private Map<String, Bulkhead> bulkhead = new java.util.HashMap<>();
    private Map<String, Retry> retry = new java.util.HashMap<>();
    private Map<String, TimeLimiter> timeLimiter = new java.util.HashMap<>();
    private Map<String, CircuitBreaker> circuitBreaker = new java.util.HashMap<>();

    /**
     * Constructs the R4jConfig and initializes Resilience4j instances.
     *
     * @param properties the configuration properties for Resilience4j
     */
    public R4jConfig(R4jProperties properties) {

        properties.getCircuitbreaker().getInstances().forEach((key, value) -> {
            CircuitBreakerConfig.Builder builder = CircuitBreakerConfig.custom();

            builder.slidingWindowType(value.getSlidingWindowType());
            builder.slidingWindowSize(value.getSlidingWindowSize());
            builder.permittedNumberOfCallsInHalfOpenState(value.getPermittedNumberOfCallsInHalfOpenState());
            builder.waitDurationInOpenState(value.getWaitDurationInOpenState());
            builder.failureRateThreshold(value.getFailureRateThreshold());
            CircuitBreakerConfig circuitBreakerConf = builder.build();
            CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(circuitBreakerConf);
            circuitBreaker.put(key, registry.circuitBreaker(key, circuitBreakerConf));
        });

        properties.getRetry().getInstances().forEach((key, value) -> {
            RetryConfig.Builder builder = RetryConfig.custom();
            builder.maxAttempts(value.getMaxAttempts());
            builder.waitDuration(value.getWaitDuration());
            RetryConfig retryConf = builder.build();
            RetryRegistry registry = RetryRegistry.of(retryConf);
            retry.put(key, registry.retry(key, retryConf));
        });

        properties.getTimelimiter().getInstances().forEach((key, value) -> {
            TimeLimiterConfig.Builder builder = TimeLimiterConfig.custom();
            builder.timeoutDuration(value.getTimeoutDuration());
            builder.cancelRunningFuture(value.getCancelRunningFuture());
            TimeLimiterConfig timeLimiterConf = builder.build();
            TimeLimiterRegistry registry = TimeLimiterRegistry.of(timeLimiterConf);
            timeLimiter.put(key, registry.timeLimiter(key, timeLimiterConf));
        });

        properties.getBulkhead().getInstances().forEach((key, value) -> {
            BulkheadConfig.Builder builder = BulkheadConfig.custom();
            builder.maxConcurrentCalls(value.getMaxConcurrentCalls());
            builder.maxWaitDuration(value.getMaxWaitDuration());
            BulkheadConfig bulkheadConf = builder.build();
            BulkheadRegistry registry = BulkheadRegistry.of(bulkheadConf);
            bulkhead.put(key, registry.bulkhead(key, bulkheadConf));
        });

    }

    @Data
    @ConfigurationProperties(prefix = "resilience4j")
    /**
     * Properties wrapper for Resilience4j configuration.
     *
     * @author Manideep Reddy Chinthareddy
     */
    public static class R4jProperties {
        private CommonBulkheadConfigurationProperties bulkhead;
        private CommonRetryConfigurationProperties retry;
        private CommonTimeLimiterConfigurationProperties timelimiter;
        private CommonCircuitBreakerConfigurationProperties circuitbreaker;
    }

}
