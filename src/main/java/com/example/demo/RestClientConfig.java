package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
/**
 * Configuration class for creating the RestClient bean.
 *
 * @author Manideep Reddy Chinthareddy
 */
public class RestClientConfig {

    /**
     * Creates and configures the RestClient bean.
     *
     * @return the configured RestClient instance
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .build();
    }
}
