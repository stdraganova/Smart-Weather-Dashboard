# Smart Weather Dashboard - Architecture Documentation

## 📐 System Architecture Overview

This document provides an in-depth explanation of the Smart Weather Dashboard architecture, design patterns, and module interactions.

## 🎯 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     User Interface (Browser)                 │
│           HTML (Thymeleaf) + CSS + JavaScript              │
└──────────────────────────────┬──────────────────────────────┘
                               ↓
┌─────────────────────────────────────────────────────────────┐
│                   Web Layer (Controller)                     │
│               WeatherController (REST API)                  │
│         GET / | /weather | /toggle-unit | /refresh         │
└──────────────────────────────┬──────────────────────────────┘
                               ↓
┌─────────────────────────────────────────────────────────────┐
│                   Service Layer                              │
│  ┌──────────────┬──────────────┬────────────────────────┐  │
│  │  Weather     │  Search      │  Unit Converter        │  │
│  │  Service     │  History     │  Service               │  │
│  │              │  Service     │                        │  │
│  └──────────────┴──────────────┴────────────────────────┘  │
└──────────────────────────────┬──────────────────────────────┘
                               ↓
┌─────────────────────────────────────────────────────────────┐
│              Data Access & Integration Layer                │
│  ┌──────────────┬────────────────────────────────────────┐ │
│  │  Weather API │      Database (H2)                    │ │
│  │  Client      │      (SearchHistoryRepository)        │ │
│  │              │                                        │ │
│  └──────────────┴────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
              ↓                              ↓
     OpenWeatherMap API              In-Memory H2 DB
```

## 🏛️ Layered Architecture Pattern

The application follows a **4-layer architecture**:

### Layer 1: Presentation Layer (UI)
- **Technology**: Thymeleaf templates + HTML5 + CSS3 + JavaScript
- **Components**:
  - `index.html` - Main dashboard with search form, weather display, forecast grid
  - `error.html` - Error page template
  - `style.css` - Responsive styling
  - `app.js` - Client-side validation and interactions
- **Responsibilities**:
  - User input collection (city name)
  - Display weather information
  - Responsive layout on multiple devices
  - Form validation and user feedback

### Layer 2: Web/Controller Layer
- **Technology**: Spring MVC
- **Component**: `WeatherController.java`
- **Responsibilities**:
  - HTTP request handling
  - Route mapping (`/`, `/weather`, `/toggle-unit`, `/refresh`)
  - Session management (temperature unit preference)
  - Model population for views
  - Request validation
  - HTTP status codes and redirects

### Layer 3: Service/Business Logic Layer
- **Technology**: Spring Services
- **Components**:
  - `WeatherService.java` - Core business logic
  - `SearchHistoryService.java` - History management
  - `UnitConverter.java` - Utility service
- **Responsibilities**:
  - API orchestration
  - Data transformation
  - Unit conversion
  - Business rule implementation
  - Exception handling
  - Search history persistence

### Layer 4: Data Access Layer
- **Technology**: Spring Data JPA + REST Client
- **Components**:
  - `WeatherApiClient.java` - External API communication
  - `SearchHistoryRepository.java` - Database access
  - DTOs (Data Transfer Objects)
  - Models (Domain objects)
- **Responsibilities**:
  - API communication
  - Database CRUD operations
  - Data mapping (JSON ↔ Java objects)

## 📦 Module Dependency Diagram

```
Controller
    ↓
    └─→ WeatherService ──┬─→ WeatherApiClient ──→ OpenWeatherMap API
                         ├─→ UnitConverter
                         └─→ SearchHistoryService ──→ SearchHistoryRepository ──→ H2 DB

ExceptionHandler
    ↓
    └─→ Catches exceptions from all layers
```

## 🔄 Request Flow - Weather Search Example

```
User enters "London" and clicks Search
        ↓
GET /weather?city=London&unit=metric
        ↓
WeatherController.searchWeather()
        ├─ Validate input
        ├─ Call WeatherService.getCurrentWeather("London", "metric")
        │   ├─ WeatherApiClient.getCurrentWeather("London")
        │   │   └─ HTTP GET to OpenWeatherMap API
        │   ├─ Transform DTO to WeatherData model
        │   ├─ Apply unit conversion if needed
        │   └─ SearchHistoryService.saveSearch("London")
        ├─ Call WeatherService.getFiveDayForecast("London", "metric")
        │   ├─ WeatherApiClient.getFiveDayForecast("London")
        │   ├─ Aggregate 3-hourly data into daily summaries
        │   └─ Apply unit conversion
        ├─ Add models to Spring Model
        └─ Return view: "index" → Render with Thymeleaf
        ↓
HTML Response with weather data
        ↓
Browser renders page with CSS styling + JavaScript enhancements
```

## 🏗️ Module-by-Module Breakdown

### 1. Configuration Module

**File**: `config/AppConfig.java`

```
Components:
- RestTemplate Bean: HTTP client for API communication
  
Purpose:
- Centralized Spring configuration
- Bean instantiation and dependency injection setup
```

**Why This Matters**:
- All REST calls use the same configured RestTemplate
- Easy to add interceptors, error handlers, timeouts in one place
- Follows the "Inversion of Control" principle

### 2. API Integration Module

**Files**:
- `client/WeatherApiClient.java` - REST client wrapper
- `dto/WeatherApiResponse.java` - Current weather DTO
- `dto/ForecastApiResponse.java` - Forecast DTO

```
Responsibility Chain:
WeatherApiClient
├─ getCurrentWeather(city: String): WeatherApiResponse
│  └─ HTTP GET /weather?q={city}&units=metric
├─ getFiveDayForecast(city: String): ForecastApiResponse
│  └─ HTTP GET /forecast?q={city}&units=metric
└─ Logger for debugging

Data Transformation:
JSON Response → ObjectMapper (Jackson) → DTO Object
```

**Why This Design**:
- **Separation of Concerns**: API logic isolated from business logic
- **Reusability**: Can be used by multiple services
- **Testability**: Easy to mock for unit tests
- **Maintainability**: Changes to API format only affect DTOs

**DTO Nested Structure**:
```
WeatherApiResponse
├── Coordinates (lat, lon)
├── Weather (main, description, icon)
├── MainWeatherData (temp, humidity, pressure)
├── Wind (speed, direction, gust)
├── Clouds (percentage)
└── SystemData (country, sunrise, sunset)
```

### 3. Data Model Module

**Files**:
- `model/WeatherData.java` - Business model for current weather
- `model/ForecastData.java` - Business model for forecast
- `model/SearchHistory.java` - JPA Entity for database

**Key Differences**:
- **DTO**: Mirrors API response structure (for deserialization)
- **Model**: Business-focused structure (for application logic)
  - Includes unit field ("C" or "F")
  - Pre-converted values
  - Formatted for display

**Example**:
```
DTO (from API):
{
  "temp": 15.0,          // Always in Celsius from OpenWeatherMap
  "feels_like": 13.0,
  "weather": {...}
}

Model (business):
WeatherData {
  temperature: 59.0,     // Converted to Fahrenheit if requested
  feelsLike: 55.4,
  temperatureUnit: "F",  // Explicit unit
  weatherMain: "Clear"   // Extracted from nested structure
}
```

### 4. Service Layer

**Core Services**:

#### a) WeatherService
```
Responsibilities:
1. Orchestrate API calls
2. Transform DTOs to Models
3. Coordinate unit conversion
4. Save search history
5. Handle forecast aggregation

Methods:
- getCurrentWeather(city, unit): WeatherData
- getFiveDayForecast(city, unit): List<ForecastData>
- transformToWeatherData(response, unit): WeatherData
- aggregateByDay(response, unit): List<ForecastData>
- createDailySummary(items, date, unit, tempUnit): ForecastData
```

**Forecast Aggregation Logic**:
```
API Returns: 3-hourly data for 5 days (40 items)
           ↓
Group by Date:
  2026-06-18: [item@00:00, item@03:00, item@06:00, ..., item@21:00]
  2026-06-19: [item@00:00, item@03:00, ...]
              ↓
Create Daily Summary:
  - Min/Max/Avg temperature
  - Most common weather condition
  - Average humidity, wind speed
  - Max precipitation probability
              ↓
Return: List<ForecastData> (5 items, one per day)
```

#### b) SearchHistoryService
```
Responsibilities:
- Save city searches to database
- Retrieve recent searches
- Get unique cities
- Clear history

Why No Duplicates in UI?:
- Repository query uses DISTINCT city_name
- User sees last search time per city
```

#### c) UnitConverter
```
Conversions:
- Celsius to Fahrenheit: (C × 9/5) + 32
- Fahrenheit to Celsius: (F - 32) × 5/9

Usage:
- Used throughout WeatherService
- Pure functions (no side effects)
- Static-like methods (but instance for Spring injection)
```

### 5. Repository Layer

**File**: `repository/SearchHistoryRepository.java`

```
Extends: JpaRepository<SearchHistory, Long>

Custom Queries:
1. findRecentSearches()
   - SELECT * FROM search_history
   - ORDER BY searched_at DESC LIMIT 10
   - Returns last 10 searches

2. findUniqueCities()
   - SELECT DISTINCT city_name FROM search_history
   - ORDER BY MAX(searched_at) DESC
   - Returns unique cities sorted by most recent
```

**Why H2 Database**:
- ✅ No external database to setup
- ✅ In-memory (fast)
- ✅ Persists during app runtime
- ✅ Data resets on restart (acceptable for this use case)
- ⚠️ Not suitable for production multi-instance deployments

### 6. Exception Handling Module

**Files**:
- `exception/CityNotFoundException.java` - Custom exception
- `exception/GlobalExceptionHandler.java` - Global exception handler

**Exception Flow**:
```
Exception thrown in Service Layer
        ↓
Caught by GlobalExceptionHandler (if unhandled)
        ↓
@ExceptionHandler methods route to error view
        ↓
ModelAndView with error details
        ↓
Thymeleaf renders error.html
        ↓
User sees friendly error message
```

**Handled Exceptions**:
1. `CityNotFoundException` → 404 error page "City not found"
2. `RestClientException` → 500 error page "API error"
3. Generic `Exception` → 500 error page "Something went wrong"

### 7. Controller Layer

**File**: `controller/WeatherController.java`

**Routes**:
| Route | Method | Purpose |
|-------|--------|---------|
| `/` | GET | Home page with search form |
| `/weather` | GET | Process search, display weather |
| `/toggle-unit` | GET | Toggle C/F and reload |
| `/refresh` | GET | Reload weather for current city |

**Session Management**:
```java
@SessionAttributes("unit")
```
- Stores temperature unit preference in session
- Persists across requests for same user
- Defaults to "metric" if not set

**Error Handling**:
- Empty city name: Show error message on same page
- API failures: Rethrow to GlobalExceptionHandler
- Validation happens at form level (JavaScript) and server level (controller)

### 8. UI/Template Layer

**index.html Structure**:
```
<header>
  Title + Description
</header>

<main>
  <search-section>
    - Input form with city name
    - Unit toggle button
    - Refresh button
    - Error message display
  </search-section>
  
  <weather-section> (if weather data available)
    - City name + country
    - Main temperature display (large)
    - Weather condition
    - Details grid (humidity, pressure, wind, etc.)
  </weather-section>
  
  <forecast-section> (if forecast data available)
    - Grid of 5 forecast cards
    - Per card: date, condition, min/max temp, humidity, wind
  </forecast-section>
  
  <recent-searches-section> (if history available)
    - Tag list of recent cities
    - Click to search again
  </recent-searches-section>
</main>

<footer>
  Copyright + Attribution
</footer>
```

**Thymeleaf Features Used**:
- `th:if` - Conditional rendering
- `th:each` - Loop over collections
- `th:text` - Display variable values
- `th:href="@{}"` - Generate URLs with parameters
- `th:value` - Set input values
- Expression formatting: `#{numbers.formatDecimal()}`

**CSS Responsive Approach**:
- **Mobile-first**: Default styles for small screens
- **Media queries**: Breakpoints at 768px and 480px
- **CSS Grid/Flexbox**: Flexible layouts
- **Relative units**: rem/em over px for consistency

**JavaScript Functionality**:
- Input validation (letters, spaces, hyphens only)
- Focus management (Ctrl+K shortcut)
- Keyboard event handling
- Button loading states
- Utility functions for formatting

### 9. Testing Module

**Unit Tests** (WeatherServiceTest):
```
Tests covered:
✓ Metric unit weather fetching
✓ Imperial unit weather fetching
✓ Celsius to Fahrenheit conversion
✓ Fahrenheit to Celsius conversion
✓ Same unit conversion handling

Mocking:
- WeatherApiClient mocked
- Real UnitConverter (deterministic)
- Real SearchHistoryService mocked
```

**Integration Tests** (WeatherControllerTest):
```
Tests covered:
✓ Home page loads
✓ Weather search with valid city
✓ Weather search with empty city
✓ Unit toggle redirect
✓ Refresh functionality

Testing Approach:
- MockMvc for HTTP testing
- Mocked WeatherService
- Verify controller behavior without actual API calls
```

**Why Separate**:
- **Unit Tests**: Focus on service logic, fast execution, real unit converter
- **Integration Tests**: Focus on HTTP layer, URL routing, form handling

## 🔐 Security Considerations

### Data Protection
```
API Key Security:
✓ Stored in application.properties (not committed with default key)
✓ Loaded from environment variables in production
✓ Never exposed in logs
✓ Never transmitted to frontend

Input Protection:
✓ City names sanitized (client + server)
✓ Special characters filtered
✓ SQL injection prevention (JPA parameterized queries)
✓ XSS prevention (Thymeleaf escaping by default)

HTTPS:
✓ All API calls use HTTPS
✓ No sensitive data sent over HTTP
✓ SSL/TLS certificates for OpenWeatherMap API
```

### API Rate Limiting
```
OpenWeatherMap Free Tier:
- 60 calls/minute
- 1,000,000 calls/month

To Implement:
- Use Spring @RateLimiter annotation
- Or implement custom interceptor
- Add Resilience4j configuration
```

## 🎯 Design Patterns Used

### 1. **Dependency Injection (DI)**
```java
// Constructor injection (preferred)
public WeatherService(WeatherApiClient apiClient, 
                     UnitConverter unitConverter,
                     SearchHistoryService searchHistoryService) {
    this.apiClient = apiClient;
    this.unitConverter = unitConverter;
    this.searchHistoryService = searchHistoryService;
}
```
Benefits: Testability, loose coupling, flexibility

### 2. **Service Layer Pattern**
- Encapsulates business logic
- Reusable by multiple controllers
- Easier testing

### 3. **Data Transfer Object (DTO)**
- Separates API structure from business model
- Prevents model mutation from API changes
- Flexibility in format conversion

### 4. **Repository Pattern**
- Abstracts data access
- Enables easy switching between databases
- Testable with mocks

### 5. **Global Exception Handler**
- Centralized error handling
- Consistent error responses
- Single point for error logging

### 6. **Controller Advice**
- Cross-cutting concern handling
- Applied to all controllers
- Cleaner controller code

### 7. **Builder Pattern**
```java
WeatherData.builder()
    .cityName("London")
    .temperature(15.0)
    .build();
```
Benefits: Readable, flexible construction

## 🔄 Data Transformation Pipeline

```
API JSON Response
        ↓ (Jackson ObjectMapper)
WeatherApiResponse DTO
        ↓ (transformToWeatherData)
WeatherData Model
        ↓ (unit conversion if needed)
WeatherData (with converted temps)
        ↓ (Spring Model)
Thymeleaf template
        ↓ (th:text formatting)
HTML in browser
```

## 📊 State Management

### Session State
```
Session Attributes:
- unit: "metric" or "imperial"
- Persists across requests
- Default: "metric" if not set
```

### Database State
```
SearchHistory Table:
- Persisted to H2 in-memory database
- Survives controller/service restarts
- Lost when application stops
```

### Form State
```
Search Form Submission:
- City name + unit sent as query parameters
- Preserved in browser URL
- Can be bookmarked/shared
- Example: /weather?city=London&unit=imperial
```

## 🚀 Performance Optimizations

### Current Optimizations
1. **API Caching**: None (fresh data on each search)
2. **Database Indexing**: Not needed for small in-memory DB
3. **HTTP Caching**: Not implemented
4. **Compression**: Built-in Spring Boot compression

### Future Optimizations
1. **Redis Cache**: Cache weather data for 30 minutes per city
2. **Lazy Loading**: Load forecast only if user clicks expand
3. **Pagination**: Limit search history display
4. **Async Processing**: Use @Async for API calls
5. **CDN**: Serve static CSS/JS from CDN
6. **Database Indexes**: Add on city_name when moving to production DB

## 🔮 Scaling Considerations

### To Production Scale
1. **Replace H2 with PostgreSQL/MySQL**
2. **Add connection pooling (HikariCP)**
3. **Implement caching (Redis)**
4. **API rate limiting with Resilience4j**
5. **Load balancing (Nginx)**
6. **Session storage (Redis/database)**
7. **Logging aggregation (ELK Stack)**
8. **Monitoring (Prometheus + Grafana)**
9. **API versioning for backward compatibility**
10. **Database migrations (Flyway/Liquibase)**

---

**Architecture Document Version**: 1.0  
**Last Updated**: June 18, 2026  
**Maintainer**: Development Team

