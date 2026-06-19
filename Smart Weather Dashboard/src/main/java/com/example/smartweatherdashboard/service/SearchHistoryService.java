package com.example.smartweatherdashboard.service;

import com.example.smartweatherdashboard.model.SearchHistory;
import com.example.smartweatherdashboard.repository.SearchHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing search history.
 * Handles saving recent searches and retrieving search history.
 */
@Slf4j
@Service
public class SearchHistoryService {

    private final SearchHistoryRepository repository;

    public SearchHistoryService(SearchHistoryRepository repository) {
        this.repository = repository;
    }

    /**
     * Saves a new search to the history.
     *
     * @param cityName the name of the city searched
     */
    public void saveSearch(String cityName) {
        try {
            SearchHistory history = SearchHistory.builder()
                    .cityName(cityName.trim())
                    .searchedAt(LocalDateTime.now())
                    .build();
            repository.save(history);
            log.info("Saved search history for city: {}", cityName);
        } catch (Exception e) {
            log.error("Error saving search history for city: {}", cityName, e);
        }
    }

    /**
     * Retrieves the most recent searches.
     *
     * @return List of recent SearchHistory records
     */
    public List<SearchHistory> getRecentSearches() {
        try {
            return repository.findRecentSearches();
        } catch (Exception e) {
            log.error("Error retrieving recent searches", e);
            return List.of();
        }
    }

    /**
     * Retrieves unique city names from search history.
     *
     * @return List of unique city names ordered by most recent search
     */
    public List<String> getUniqueCities() {
        try {
            return repository.findUniqueCities();
        } catch (Exception e) {
            log.error("Error retrieving unique cities", e);
            return List.of();
        }
    }

    /**
     * Clears all search history.
     */
    public void clearHistory() {
        try {
            repository.deleteAll();
            log.info("Cleared all search history");
        } catch (Exception e) {
            log.error("Error clearing search history", e);
        }
    }
}

