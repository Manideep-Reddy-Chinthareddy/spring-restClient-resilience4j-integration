package com.example.demo;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * Client class that wraps RestClient with Resilience4j patterns.
 *
 * @author Manideep Reddy Chinthareddy
 */
public class SpringRestClientWithR4j {

    private final RestClient restClient;
    private final R4jConfig r4jConfig;
    private final Executor executor;

    public SpringRestClientWithR4j(RestClient restClient, R4jConfig r4jConfig, Executor executor) {
        this.restClient = restClient;
        this.r4jConfig = r4jConfig;
        this.executor = executor;
    }

    /**
     * Executes a REST request with full resilience patterns.
     *
     * @param url          the URL
     * @param method       the HTTP method
     * @param vars         URI variables
     * @param headers      HTTP headers consumer
     * @param responseType the response type class
     * @return the response body
     * @throws Exception if an error occurs
     */
    public <T> T exchange(String url, HttpMethod method, Map<String, String> vars, Consumer<HttpHeaders> headers,
            Class<T> responseType) throws Exception {
        final Supplier<ResponseEntity<T>> supplier = () -> {
            RestClient.RequestBodySpec requestBodySpec = restClient.method(method).uri(url, vars).headers(headers);
            requestBodySpec.body(vars);
            return requestBodySpec.retrieve().toEntity(responseType);
        };
        TimeLimiter timeLimiter = r4jConfig.getTimeLimiter().get("default");
        Supplier<CompletableFuture<ResponseEntity<T>>> timeLimiterSupplier = () -> CompletableFuture.supplyAsync(
                supplier,
                executor);
        Callable<ResponseEntity<T>> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, timeLimiterSupplier);
        callable = Bulkhead.decorateCallable(r4jConfig.getBulkhead().get("default"), callable);
        callable = CircuitBreaker.decorateCallable(r4jConfig.getCircuitBreaker().get("default"), callable);
        callable = Retry.decorateCallable(r4jConfig.getRetry().get("default"), callable);
        return callable.call().getBody();

    }

    /**
     * Executes a REST request without TimeLimiter.
     *
     * @param url          the URL
     * @param method       the HTTP method
     * @param vars         URI variables
     * @param headers      HTTP headers consumer
     * @param responseType the response type class
     * @return the response body
     */
    public <T> T exchangeWithoutTimelimitter(String url, HttpMethod method, Map<String, String> vars,
            Consumer<HttpHeaders> headers,
            Class<T> responseType) {
        final Supplier<ResponseEntity<T>> supplier = () -> {
            RestClient.RequestBodySpec requestBodySpec = restClient.method(method).uri(url, vars).headers(headers);
            requestBodySpec.body(vars);
            return requestBodySpec.retrieve().toEntity(responseType);
        };
        final Decorators.DecorateSupplier<ResponseEntity<T>> decoratorSupplier = Decorators.ofSupplier(supplier);
        decoratorSupplier.withBulkhead(r4jConfig.getBulkhead().get("default"));
        decoratorSupplier.withCircuitBreaker(r4jConfig.getCircuitBreaker().get("default"));
        decoratorSupplier.withRetry(r4jConfig.getRetry().get("default"));
        return decoratorSupplier.get().getBody();
    }

}
