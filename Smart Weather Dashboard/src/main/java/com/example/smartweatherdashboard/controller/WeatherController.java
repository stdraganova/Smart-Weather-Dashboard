package com.example.smartweatherdashboard.controller;

import com.example.smartweatherdashboard.model.ForecastData;
import com.example.smartweatherdashboard.model.WeatherData;
import com.example.smartweatherdashboard.service.SearchHistoryService;
import com.example.smartweatherdashboard.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Main controller for weather dashboard.
 * Handles user requests for weather information, unit toggling, and search history.
 */
@Slf4j
@Controller
@SessionAttributes("unit")
public class WeatherController {

    private final WeatherService weatherService;
    private final SearchHistoryService searchHistoryService;

    public WeatherController(WeatherService weatherService, SearchHistoryService searchHistoryService) {
        this.weatherService = weatherService;
        this.searchHistoryService = searchHistoryService;
    }

    /**
     * Displays the home page with search form.
     *
     * @param model the model to add attributes
     * @param unit the temperature unit from session (optional, defaults to metric)
     * @return "index" view
     */
    @GetMapping("/")
    public String home(Model model, @RequestParam(value = "unit", required = false) String unit) {
        // Set default unit if not in session
        if (unit == null) {
            unit = "metric";
        }

        model.addAttribute("unit", unit);
        model.addAttribute("recentSearches", searchHistoryService.getUniqueCities());

        return "index";
    }

    /**
     * Searches for weather information by city name.
     * Fetches current weather and 5-day forecast.
     *
     * @param city the city name to search for
     * @param unit the temperature unit ("metric" or "imperial")
     * @param model the model to add attributes
     * @return "index" view with weather data
     */
    @GetMapping("/weather")
    public String searchWeather(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "unit", defaultValue = "metric") String unit,
            Model model) {

        log.info("User searched for city: {} with unit: {}", city, unit);

        if (city == null || city.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Please enter a city name.");
            model.addAttribute("unit", unit);
            model.addAttribute("recentSearches", searchHistoryService.getUniqueCities());
            return "index";
        }

        try {
            // Fetch current weather
            WeatherData weatherData = weatherService.getCurrentWeather(city.trim(), unit);

            // Fetch 5-day forecast
            List<ForecastData> forecastData = weatherService.getFiveDayForecast(city.trim(), unit);

            model.addAttribute("weather", weatherData);
            model.addAttribute("forecast", forecastData);
            model.addAttribute("unit", unit);
            model.addAttribute("recentSearches", searchHistoryService.getUniqueCities());

            log.info("Successfully displayed weather for city: {}", city);
            return "index";

        } catch (Exception e) {
            log.error("Error fetching weather for city: {}", city, e);
            // Display error message on the same page instead of redirecting to error page
            model.addAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "Failed to fetch weather information. Please check the city name and try again.");
            model.addAttribute("unit", unit);
            model.addAttribute("recentSearches", searchHistoryService.getUniqueCities());
            return "index";
        }
    }

    /**
     * Toggles temperature unit and refreshes the weather display.
     *
     * @param city the current city being displayed
     * @param currentUnit the current unit
     * @param model the model
     * @return redirect to /weather with the new unit
     */
    @GetMapping("/toggle-unit")
    public String toggleUnit(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "unit", defaultValue = "metric") String currentUnit,
            Model model) {

        String newUnit = currentUnit.equalsIgnoreCase("metric") ? "imperial" : "metric";

        if (city != null && !city.trim().isEmpty()) {
            String redirectUrl = UriComponentsBuilder.fromPath("/weather")
                    .queryParam("city", city)
                    .queryParam("unit", newUnit)
                    .build()
                    .toUriString();
            return "redirect:" + redirectUrl;
        }

        return "redirect:/?unit=" + newUnit;
    }

    /**
     * Refreshes weather data for the current city.
     *
     * @param city the city to refresh
     * @param unit the temperature unit
     * @param model the model
     * @return "index" view with refreshed data
     */
    @GetMapping("/refresh")
    public String refresh(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "unit", defaultValue = "metric") String unit,
            Model model) {

        if (city != null && !city.trim().isEmpty()) {
            log.info("User refreshed weather for city: {}", city);
            return searchWeather(city, unit, model);
        }

        return "redirect:/?unit=" + unit;
    }
}

