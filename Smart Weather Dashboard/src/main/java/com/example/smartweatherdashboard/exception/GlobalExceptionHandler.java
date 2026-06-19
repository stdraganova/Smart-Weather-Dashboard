package com.example.smartweatherdashboard.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Global exception handler for the application.
 * Catches exceptions and redirects to error views with appropriate messages.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles CityNotFoundException
     */
    @ExceptionHandler(CityNotFoundException.class)
    public ModelAndView handleCityNotFoundException(CityNotFoundException ex) {
        log.error("City not found: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorTitle", "City Not Found");
        mav.addObject("errorMessage", ex.getMessage() + ". Please check the spelling and try again.");
        mav.addObject("errorCode", "404");
        return mav;
    }

    /**
     * Handles RestClientException (API call failures)
     */
    @ExceptionHandler(RestClientException.class)
    public ModelAndView handleRestClientException(RestClientException ex) {
        log.error("API error: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorTitle", "API Error");
        mav.addObject("errorMessage", "Failed to fetch weather data. Please try again later.");
        mav.addObject("errorCode", "500");
        return mav;
    }

    /**
     * Handles generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorTitle", "Something Went Wrong");
        mav.addObject("errorMessage", "An unexpected error occurred. Please try again.");
        mav.addObject("errorCode", "500");
        return mav;
    }
}

