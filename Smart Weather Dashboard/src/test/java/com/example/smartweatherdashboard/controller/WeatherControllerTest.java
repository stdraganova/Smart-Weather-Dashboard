package com.example.smartweatherdashboard.controller;

import com.example.smartweatherdashboard.model.ForecastData;
import com.example.smartweatherdashboard.model.WeatherData;
import com.example.smartweatherdashboard.service.SearchHistoryService;
import com.example.smartweatherdashboard.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for WeatherController.
 * Tests HTTP routes and view rendering.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WeatherController Tests")
class WeatherControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WeatherService weatherService;

    @Mock
    private SearchHistoryService searchHistoryService;

    private WeatherController weatherController;

    @BeforeEach
    void setUp() {
        weatherController = new WeatherController(weatherService, searchHistoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    }

    /**
     * Test home page loads successfully
     */
    @Test
    @DisplayName("GET / should return home page")
    void testHomePageLoad() throws Exception {
        when(searchHistoryService.getUniqueCities()).thenReturn(List.of("London", "Paris"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("unit", "recentSearches"));
    }

    /**
     * Test weather search with valid city
     */
    @Test
    @DisplayName("GET /weather with valid city should return weather data")
    void testWeatherSearchValid() throws Exception {
        // Arrange
        String cityName = "London";
        WeatherData weatherData = createMockWeatherData(cityName);
        List<ForecastData> forecastData = List.of(
                createMockForecastData(LocalDate.now()),
                createMockForecastData(LocalDate.now().plusDays(1))
        );

        when(weatherService.getCurrentWeather(cityName, "metric"))
                .thenReturn(weatherData);
        when(weatherService.getFiveDayForecast(cityName, "metric"))
                .thenReturn(forecastData);
        when(searchHistoryService.getUniqueCities())
                .thenReturn(List.of(cityName));

        // Act & Assert
        mockMvc.perform(get("/weather")
                .param("city", cityName)
                .param("unit", "metric"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("weather", "forecast", "unit", "recentSearches"));

        verify(weatherService, times(1)).getCurrentWeather(cityName, "metric");
        verify(weatherService, times(1)).getFiveDayForecast(cityName, "metric");
    }

    /**
     * Test weather search with empty city name
     */
    @Test
    @DisplayName("GET /weather with empty city should show error message")
    void testWeatherSearchEmpty() throws Exception {
        when(searchHistoryService.getUniqueCities()).thenReturn(List.of());

        mockMvc.perform(get("/weather")
                .param("city", "")
                .param("unit", "metric"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(weatherService, never()).getCurrentWeather(anyString(), anyString());
    }

    /**
     * Test unit toggle functionality
     */
    @Test
    @DisplayName("GET /toggle-unit should redirect with new unit")
    void testUnitToggle() throws Exception {
        mockMvc.perform(get("/toggle-unit")
                .param("city", "London")
                .param("unit", "metric"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/weather?city=London&unit=imperial"));
    }

    /**
     * Test refresh endpoint
     */
    @Test
    @DisplayName("GET /refresh should reload weather data")
    void testRefresh() throws Exception {
        String cityName = "London";
        WeatherData weatherData = createMockWeatherData(cityName);
        List<ForecastData> forecastData = List.of(createMockForecastData(LocalDate.now()));

        when(weatherService.getCurrentWeather(cityName, "metric"))
                .thenReturn(weatherData);
        when(weatherService.getFiveDayForecast(cityName, "metric"))
                .thenReturn(forecastData);
        when(searchHistoryService.getUniqueCities())
                .thenReturn(List.of(cityName));

        mockMvc.perform(get("/refresh")
                .param("city", cityName)
                .param("unit", "metric"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(weatherService, times(1)).getCurrentWeather(cityName, "metric");
    }

    /**
     * Helper method to create mock WeatherData
     */
    private WeatherData createMockWeatherData(String cityName) {
        return WeatherData.builder()
                .cityName(cityName)
                .country("GB")
                .temperature(15.0)
                .feelsLike(13.0)
                .tempMin(12.0)
                .tempMax(18.0)
                .humidity(70)
                .pressure(1013)
                .windSpeed(5.5)
                .windDirection(240)
                .weatherMain("Clear")
                .weatherDescription("clear sky")
                .cloudiness(20)
                .temperatureUnit("C")
                .build();
    }

    /**
     * Helper method to create mock ForecastData
     */
    private ForecastData createMockForecastData(LocalDate date) {
        return ForecastData.builder()
                .date(date)
                .tempMin(10.0)
                .tempMax(20.0)
                .tempAvg(15.0)
                .weatherMain("Cloudy")
                .humidity(65)
                .cloudiness(50)
                .windSpeed(4.5)
                .temperatureUnit("C")
                .build();
    }
}

