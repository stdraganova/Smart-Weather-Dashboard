package com.example.smartweatherdashboard.service;

import com.example.smartweatherdashboard.client.WeatherApiClient;
import com.example.smartweatherdashboard.dto.WeatherApiResponse;
import com.example.smartweatherdashboard.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WeatherService class.
 * Tests weather data transformation and unit conversion logic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WeatherService Tests")
class WeatherServiceTest {

    private WeatherService weatherService;

    @Mock
    private WeatherApiClient apiClient;

    @Mock
    private SearchHistoryService searchHistoryService;

    private UnitConverter unitConverter;

    @BeforeEach
    void setUp() {
        unitConverter = new UnitConverter();
        weatherService = new WeatherService(apiClient, unitConverter, searchHistoryService);
    }

    /**
     * Test successful weather retrieval in metric units
     */
    @Test
    @DisplayName("Should fetch weather in metric units")
    void testGetCurrentWeatherMetric() {
        // Arrange
        String cityName = "London";
        WeatherApiResponse mockResponse = createMockWeatherResponse(cityName, 15.0);
        mockResponse.setCode("200");
        when(apiClient.getCurrentWeather(cityName)).thenReturn(mockResponse);

        // Act
        WeatherData result = weatherService.getCurrentWeather(cityName, "metric");

        // Assert
        assertNotNull(result);
        assertEquals("London", result.getCityName());
        assertEquals(15.0, result.getTemperature());
        assertEquals("C", result.getTemperatureUnit());
        verify(searchHistoryService, times(1)).saveSearch(cityName);
    }

    /**
     * Test successful weather retrieval in imperial units
     */
    @Test
    @DisplayName("Should fetch weather in imperial units and convert to Fahrenheit")
    void testGetCurrentWeatherImperial() {
        // Arrange
        String cityName = "London";
        WeatherApiResponse mockResponse = createMockWeatherResponse(cityName, 15.0);
        mockResponse.setCode("200");
        when(apiClient.getCurrentWeather(cityName)).thenReturn(mockResponse);

        // Act
        WeatherData result = weatherService.getCurrentWeather(cityName, "imperial");

        // Assert
        assertNotNull(result);
        assertEquals("F", result.getTemperatureUnit());
        // 15°C = 59°F
        assertEquals(59.0, result.getTemperature(), 0.1);
        verify(searchHistoryService, times(1)).saveSearch(cityName);
    }

    /**
     * Test unit converter - Celsius to Fahrenheit
     */
    @Test
    @DisplayName("Should correctly convert Celsius to Fahrenheit")
    void testCelsiusToFahrenheit() {
        // Test freezing point
        assertEquals(32.0, unitConverter.celsiusToFahrenheit(0), 0.1);

        // Test boiling point
        assertEquals(212.0, unitConverter.celsiusToFahrenheit(100), 0.1);

        // Test room temperature
        assertEquals(68.0, unitConverter.celsiusToFahrenheit(20), 0.1);
    }

    /**
     * Test unit converter - Fahrenheit to Celsius
     */
    @Test
    @DisplayName("Should correctly convert Fahrenheit to Celsius")
    void testFahrenheitToCelsius() {
        // Test freezing point
        assertEquals(0.0, unitConverter.fahrenheitToCelsius(32), 0.1);

        // Test boiling point
        assertEquals(100.0, unitConverter.fahrenheitToCelsius(212), 0.1);

        // Test room temperature
        assertEquals(20.0, unitConverter.fahrenheitToCelsius(68), 0.1);
    }

    /**
     * Test unit converter handles same unit conversion
     */
    @Test
    @DisplayName("Should return same temperature when converting to same unit")
    void testConvertSameUnit() {
        double temp = 20.0;
        assertEquals(temp, unitConverter.convert(temp, "C", "C"));
        assertEquals(temp, unitConverter.convert(temp, "F", "F"));
    }

    /**
     * Helper method to create mock WeatherApiResponse
     */
     private WeatherApiResponse createMockWeatherResponse(String cityName, double temperature) {
         WeatherApiResponse response = new WeatherApiResponse();
         response.setCityName(cityName);
         response.setCode("200");

        WeatherApiResponse.MainWeatherData main = new WeatherApiResponse.MainWeatherData();
        main.setTemperature(temperature);
        main.setFeelsLike(temperature - 2);
        main.setTempMin(temperature - 3);
        main.setTempMax(temperature + 5);
        main.setHumidity(70);
        main.setPressure(1013);
        response.setMain(main);

        WeatherApiResponse.Wind wind = new WeatherApiResponse.Wind();
        wind.setSpeed(5.5);
        wind.setDegree(240);
        wind.setGust(10.0);
        response.setWind(wind);

        WeatherApiResponse.Clouds clouds = new WeatherApiResponse.Clouds();
        clouds.setPercentage(20);
        response.setClouds(clouds);

        WeatherApiResponse.Weather weather = new WeatherApiResponse.Weather();
        weather.setMain("Clear");
        weather.setDescription("clear sky");
        weather.setIcon("01d");
        response.setWeatherList(List.of(weather));

        WeatherApiResponse.SystemData systemData = new WeatherApiResponse.SystemData();
        systemData.setCountry("GB");
        systemData.setSunrise(System.currentTimeMillis() / 1000);
        systemData.setSunset(System.currentTimeMillis() / 1000 + 3600);
        response.setSystemData(systemData);

        return response;
    }
}

