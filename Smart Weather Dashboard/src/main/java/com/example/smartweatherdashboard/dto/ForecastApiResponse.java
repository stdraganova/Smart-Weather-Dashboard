package com.example.smartweatherdashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for OpenWeatherMap 5-day forecast API response.
 * Maps the forecast data list with 3-hour intervals.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastApiResponse {

    @JsonProperty("list")
    private List<ForecastItem> forecastList;

    @JsonProperty("city")
    private City city;

    /**
     * Individual forecast item (3-hour interval)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastItem {
        @JsonProperty("dt")
        private long timestamp;

        @JsonProperty("main")
        private WeatherApiResponse.MainWeatherData main;

        @JsonProperty("weather")
        private List<WeatherApiResponse.Weather> weather;

        @JsonProperty("wind")
        private WeatherApiResponse.Wind wind;

        @JsonProperty("clouds")
        private WeatherApiResponse.Clouds clouds;

        @JsonProperty("visibility")
        private int visibility;

        @JsonProperty("pop")
        private double precipitationProbability;
    }

    /**
     * City information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        @JsonProperty("name")
        private String name;

        @JsonProperty("country")
        private String country;

        @JsonProperty("coord")
        private WeatherApiResponse.Coordinates coordinates;
    }
}

