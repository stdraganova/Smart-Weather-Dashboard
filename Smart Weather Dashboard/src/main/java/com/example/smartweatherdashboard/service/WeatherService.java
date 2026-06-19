package com.example.smartweatherdashboard.service;

import com.example.smartweatherdashboard.client.WeatherApiClient;
import com.example.smartweatherdashboard.dto.ForecastApiResponse;
import com.example.smartweatherdashboard.dto.WeatherApiResponse;
import com.example.smartweatherdashboard.exception.CityNotFoundException;
import com.example.smartweatherdashboard.model.ForecastData;
import com.example.smartweatherdashboard.model.WeatherData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main weather service that orchestrates API calls, data transformation, and unit conversion.
 * Provides weather data to controllers and handles business logic.
 */
@Slf4j
@Service
public class WeatherService {

    private final WeatherApiClient apiClient;
    private final UnitConverter unitConverter;
    private final SearchHistoryService searchHistoryService;

    public WeatherService(WeatherApiClient apiClient, UnitConverter unitConverter, SearchHistoryService searchHistoryService) {
        this.apiClient = apiClient;
        this.unitConverter = unitConverter;
        this.searchHistoryService = searchHistoryService;
    }

    /**
     * Gets current weather for a city, with optional unit conversion.
     *
     * @param cityName the name of the city
     * @param unit "metric" for Celsius, "imperial" for Fahrenheit
     * @return WeatherData object with current weather information
     * @throws CityNotFoundException if the city is not found
     */
    public WeatherData getCurrentWeather(String cityName, String unit) {
        try {
            log.info("Fetching weather for city: {} with unit: {}", cityName, unit);

             // Fetch from API (always in metric/Celsius)
             WeatherApiResponse apiResponse = apiClient.getCurrentWeather(cityName);

             // Validate API response
             if (apiResponse == null || apiResponse.getCode() == null || !apiResponse.getCode().equals("200")) {
                 throw new CityNotFoundException("City '" + cityName + "' not found.");
             }

            // Save to search history
            searchHistoryService.saveSearch(cityName);

            // Transform API response to WeatherData
            return transformToWeatherData(apiResponse, unit);

        } catch (RestClientException e) {
            if (e.getMessage().contains("404")) {
                throw new CityNotFoundException("City '" + cityName + "' not found.", e);
            }
            throw new RestClientException("Failed to fetch weather data: " + e.getMessage(), e);
        }
    }

    /**
     * Gets 5-day forecast for a city.
     *
     * @param cityName the name of the city
     * @param unit "metric" for Celsius, "imperial" for Fahrenheit
     * @return List of ForecastData objects (one per day, 5 days)
     * @throws CityNotFoundException if the city is not found
     */
    public List<ForecastData> getFiveDayForecast(String cityName, String unit) {
        try {
            log.info("Fetching 5-day forecast for city: {} with unit: {}", cityName, unit);

            ForecastApiResponse apiResponse = apiClient.getFiveDayForecast(cityName);

            if (apiResponse == null || apiResponse.getForecastList().isEmpty()) {
                throw new CityNotFoundException("City '" + cityName + "' not found.");
            }

            // Group forecast by date and create daily summaries
            return aggregateByDay(apiResponse, unit);

        } catch (RestClientException e) {
            if (e.getMessage().contains("404")) {
                throw new CityNotFoundException("City '" + cityName + "' not found.", e);
            }
            throw new RestClientException("Failed to fetch forecast data: " + e.getMessage(), e);
        }
    }

    /**
     * Transforms the API response to WeatherData model.
     *
     * @param apiResponse the API response from OpenWeatherMap
     * @param unit "metric" for Celsius, "imperial" for Fahrenheit
     * @return transformed WeatherData
     */
    private WeatherData transformToWeatherData(WeatherApiResponse apiResponse, String unit) {
        double temperature = apiResponse.getMain().getTemperature();
        double feelsLike = apiResponse.getMain().getFeelsLike();
        double tempMin = apiResponse.getMain().getTempMin();
        double tempMax = apiResponse.getMain().getTempMax();

        String tempUnit = unit.equalsIgnoreCase("metric") ? "C" : "F";

        // Convert if Fahrenheit is requested
        if (unit.equalsIgnoreCase("imperial")) {
            temperature = unitConverter.celsiusToFahrenheit(temperature);
            feelsLike = unitConverter.celsiusToFahrenheit(feelsLike);
            tempMin = unitConverter.celsiusToFahrenheit(tempMin);
            tempMax = unitConverter.celsiusToFahrenheit(tempMax);
        }

        WeatherApiResponse.Weather weather = apiResponse.getWeatherList().isEmpty() ?
                new WeatherApiResponse.Weather() : apiResponse.getWeatherList().get(0);

        return WeatherData.builder()
                .cityName(apiResponse.getCityName())
                .country(apiResponse.getSystemData().getCountry())
                .temperature(temperature)
                .feelsLike(feelsLike)
                .tempMin(tempMin)
                .tempMax(tempMax)
                .humidity(apiResponse.getMain().getHumidity())
                .pressure(apiResponse.getMain().getPressure())
                .windSpeed(apiResponse.getWind().getSpeed())
                .windDirection(apiResponse.getWind().getDegree())
                .windGust(apiResponse.getWind().getGust() > 0 ? apiResponse.getWind().getGust() : 0)
                .weatherMain(weather.getMain())
                .weatherDescription(weather.getDescription())
                .weatherIcon(weather.getIcon())
                .cloudiness(apiResponse.getClouds().getPercentage())
                .timestamp(apiResponse.getTimestamp())
                .sunrise(apiResponse.getSystemData().getSunrise())
                .sunset(apiResponse.getSystemData().getSunset())
                .temperatureUnit(tempUnit)
                .build();
    }

    /**
     * Aggregates 3-hour forecast data into daily summaries.
     *
     * @param apiResponse the API response containing forecast data
     * @param unit "metric" for Celsius, "imperial" for Fahrenheit
     * @return List of ForecastData aggregated by day
     */
    private List<ForecastData> aggregateByDay(ForecastApiResponse apiResponse, String unit) {
        Map<LocalDate, List<ForecastApiResponse.ForecastItem>> groupedByDay = new HashMap<>();

        // Group by date
        for (ForecastApiResponse.ForecastItem item : apiResponse.getForecastList()) {
            LocalDate date = Instant.ofEpochSecond(item.getTimestamp())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            groupedByDay.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
        }

        String tempUnit = unit.equalsIgnoreCase("metric") ? "C" : "F";

        // Create daily summaries
        return groupedByDay.entrySet().stream()
                .limit(5)  // Only return 5 days
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> createDailySummary(entry.getValue(), entry.getKey(), unit, tempUnit))
                .collect(Collectors.toList());
    }

    /**
     * Creates a daily summary from multiple 3-hour forecast items.
     *
     * @param items the forecast items for a single day
     * @param date the date
     * @param unit "metric" or "imperial"
     * @param tempUnit "C" or "F"
     * @return ForecastData for the day
     */
    private ForecastData createDailySummary(List<ForecastApiResponse.ForecastItem> items, LocalDate date, String unit, String tempUnit) {
        double minTemp = items.stream()
                .mapToDouble(item -> item.getMain().getTempMin())
                .min()
                .orElse(0);

        double maxTemp = items.stream()
                .mapToDouble(item -> item.getMain().getTempMax())
                .max()
                .orElse(0);

        double avgTemp = items.stream()
                .mapToDouble(item -> item.getMain().getTemperature())
                .average()
                .orElse(0);

        int avgHumidity = (int) items.stream()
                .mapToInt(item -> item.getMain().getHumidity())
                .average()
                .orElse(0);

        int avgCloudiness = (int) items.stream()
                .mapToInt(item -> item.getClouds().getPercentage())
                .average()
                .orElse(0);

        double avgWindSpeed = items.stream()
                .mapToDouble(item -> item.getWind().getSpeed())
                .average()
                .orElse(0);

        double maxPrecipProb = items.stream()
                .mapToDouble(ForecastApiResponse.ForecastItem::getPrecipitationProbability)
                .max()
                .orElse(0);

        // Get the most common weather condition
        Map<String, Long> weatherCount = items.stream()
                .flatMap(item -> item.getWeather().stream())
                .collect(Collectors.groupingBy(WeatherApiResponse.Weather::getMain, Collectors.counting()));

        String mainWeather = weatherCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Clear");

        // Convert temperatures if necessary
        if (unit.equalsIgnoreCase("imperial")) {
            minTemp = unitConverter.celsiusToFahrenheit(minTemp);
            maxTemp = unitConverter.celsiusToFahrenheit(maxTemp);
            avgTemp = unitConverter.celsiusToFahrenheit(avgTemp);
        }

        return ForecastData.builder()
                .date(date)
                .tempMin(minTemp)
                .tempMax(maxTemp)
                .tempAvg(avgTemp)
                .weatherMain(mainWeather)
                .humidity(avgHumidity)
                .cloudiness(avgCloudiness)
                .windSpeed(avgWindSpeed)
                .precipitationProbability(maxPrecipProb)
                .temperatureUnit(tempUnit)
                .build();
    }
}

