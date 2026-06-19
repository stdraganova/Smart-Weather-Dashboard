package com.example.smartweatherdashboard.exception;

/**
 * Custom exception thrown when a city is not found in the API.
 */
public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(String message) {
        super(message);
    }

    public CityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

