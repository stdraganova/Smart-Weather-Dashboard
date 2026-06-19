package com.example.smartweatherdashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Application configuration class.
 * Declares Spring beans for REST client and other application-wide configurations.
 */
@Configuration
public class AppConfig {

    /**
     * Creates and configures a RestTemplate bean for making HTTP requests.
     * Includes timeout settings to prevent hanging requests.
     *
     * @return configured RestTemplate with timeout settings
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        return restTemplate;
    }

    /**
     * Creates a ClientHttpRequestFactory with connection and read timeouts.
     * This prevents requests from hanging indefinitely if the API is slow or unresponsive.
     *
     * @return ClientHttpRequestFactory with timeout settings
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Connection timeout - time to establish connection with API server (ms)
        factory.setConnectTimeout(5000);  // 5 seconds
        // Read timeout - time to wait for response after connection is established (ms)
        factory.setReadTimeout(10000);    // 10 seconds
        return factory;
    }
}

