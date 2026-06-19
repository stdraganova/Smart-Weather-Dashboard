package com.example.smartweatherdashboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for converting temperature units between Celsius and Fahrenheit.
 */
@Slf4j
@Service
public class UnitConverter {

    /**
     * Converts temperature from Celsius to Fahrenheit.
     *
     * @param celsius temperature in Celsius
     * @return temperature in Fahrenheit
     */
    public double celsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }

    /**
     * Converts temperature from Fahrenheit to Celsius.
     *
     * @param fahrenheit temperature in Fahrenheit
     * @return temperature in Celsius
     */
    public double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }

    /**
     * Converts temperature based on the desired unit.
     * If the temperature is in Celsius and Fahrenheit is requested, converts to Fahrenheit.
     *
     * @param temperature the temperature value
     * @param fromUnit "C" for Celsius, "F" for Fahrenheit
     * @param toUnit "C" for Celsius, "F" for Fahrenheit
     * @return converted temperature
     */
    public double convert(double temperature, String fromUnit, String toUnit) {
        if (fromUnit.equalsIgnoreCase(toUnit)) {
            return temperature;
        }

        if (fromUnit.equalsIgnoreCase("C") && toUnit.equalsIgnoreCase("F")) {
            return celsiusToFahrenheit(temperature);
        } else if (fromUnit.equalsIgnoreCase("F") && toUnit.equalsIgnoreCase("C")) {
            return fahrenheitToCelsius(temperature);
        }

        log.warn("Invalid unit conversion: {} to {}", fromUnit, toUnit);
        return temperature;
    }
}

