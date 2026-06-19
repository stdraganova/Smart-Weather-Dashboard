package com.example.smartweatherdashboard.repository;

import com.example.smartweatherdashboard.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for SearchHistory entity.
 * Provides database operations and custom queries for search history.
 */
@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    /**
     * Retrieves the most recent searches, ordered by descending timestamp.
     *
     * @return List of search history records, limited to recent searches
     */
    @Query(value = "SELECT * FROM search_history ORDER BY searched_at DESC LIMIT 10", nativeQuery = true)
    List<SearchHistory> findRecentSearches();

    /**
     * Retrieves all unique city names from search history, ordered by most recent.
     *
     * @return List of unique city names
     */
    @Query(value = "SELECT city_name FROM search_history GROUP BY city_name ORDER BY MAX(searched_at) DESC", nativeQuery = true)
    List<String> findUniqueCities();
}

