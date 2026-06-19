package com.example.smartweatherdashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing current weather data.
 * Contains temperature, humidity, wind speed, and weather conditions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherData {

    private String cityName;
    private String country;

    private double temperature;           // in selected unit (C or F)
    private double feelsLike;
    private double tempMin;
    private double tempMax;

    private int humidity;                 // 0-100%
    private int pressure;                 // hPa

    private double windSpeed;             // m/s
    private int windDirection;            // degrees
    private double windGust;

    private String weatherMain;           // e.g., "Clear", "Rainy"
    private String weatherDescription;    // e.g., "clear sky"
    private String weatherIcon;           // icon code

    private int cloudiness;               // 0-100%

    private long timestamp;               // Unix timestamp
    private long sunrise;                 // Unix timestamp
    private long sunset;                  // Unix timestamp

    private String temperatureUnit;       // "C" or "F"
}

