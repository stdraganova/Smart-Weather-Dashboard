package com.example.smartweatherdashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Model class representing a day's forecast data.
 * Aggregates 3-hour forecast intervals into daily summaries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForecastData {

    private LocalDate date;

    private double tempMin;
    private double tempMax;
    private double tempAvg;

    private String weatherMain;          // Primary weather condition for the day
    private String weatherDescription;
    private String weatherIcon;

    private int humidity;
    private int cloudiness;
    private double windSpeed;
    private int windDirection;

    private double precipitationProbability;  // 0-100%

    private String temperatureUnit;      // "C" or "F"

    private List<String> weatherConditions;  // All weather conditions for the day
}

