package com.example.smartweatherdashboard.client;

import com.example.smartweatherdashboard.dto.ForecastApiResponse;
import com.example.smartweatherdashboard.dto.WeatherApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * API client for communicating with OpenWeatherMap API over HTTPS.
 * Handles fetching current weather and 5-day forecast data.
 */
@Slf4j
@Component
public class WeatherApiClient {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.base-url}")
    private String baseUrl;

    public WeatherApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches current weather data for a given city.
     *
     * @param cityName the name of the city
     * @return WeatherApiResponse containing current weather data
     * @throws RestClientException if the API call fails
     */
    public WeatherApiResponse getCurrentWeather(String cityName) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + "/weather")
                .queryParam("q", cityName)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .build()
                .toUriString();

        try {
            log.info("Fetching current weather for city: {}", cityName);
            WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);
            log.info("Successfully fetched weather for city: {}", cityName);
            return response;
        } catch (RestClientException e) {
            log.error("Error fetching weather for city: {}", cityName, e);
            throw new RestClientException("Failed to fetch weather data for city: " + cityName, e);
        }
    }

    /**
     * Fetches 5-day weather forecast for a given city.
     *
     * @param cityName the name of the city
     * @return ForecastApiResponse containing 5-day forecast data
     * @throws RestClientException if the API call fails
     */
    public ForecastApiResponse getFiveDayForecast(String cityName) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + "/forecast")
                .queryParam("q", cityName)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .build()
                .toUriString();

        try {
            log.info("Fetching 5-day forecast for city: {}", cityName);
            ForecastApiResponse response = restTemplate.getForObject(url, ForecastApiResponse.class);
            log.info("Successfully fetched forecast for city: {}", cityName);
            return response;
        } catch (RestClientException e) {
            log.error("Error fetching forecast for city: {}", cityName, e);
            throw new RestClientException("Failed to fetch forecast data for city: " + cityName, e);
        }
    }
}

