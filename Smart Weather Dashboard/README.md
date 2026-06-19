# Smart Weather Dashboard

A modern, responsive weather application built with **Spring Boot**, **Thymeleaf**, and **OpenWeatherMap API**. Get accurate weather information and 5-day forecasts with a beautiful, user-friendly interface.

## 🎯 Features

### Functional Features
- 🔍 **City Search** - Search for weather information by entering any city name
- 🌡️ **Current Weather** - Display temperature, humidity, wind speed, and weather conditions
- 📅 **5-Day Forecast** - View weather predictions for the next 5 days
- 🔄 **Refresh Data** - Update weather information at any time
- ⚠️ **Error Handling** - Clear error messages for invalid city names
- 🔀 **Unit Toggle** - Switch between Celsius and Fahrenheit
- 📋 **Search History** - View and quickly access recent searches

### Non-Functional Features
- 📱 **Responsive Design** - Works seamlessly on desktop and mobile devices
- ⚡ **Fast Performance** - Weather data loads within seconds
- 💎 **Beautiful UI** - Modern gradient design with smooth animations
- 🔒 **Secure HTTPS** - All API communications use HTTPS
- 🗄️ **Persistent Storage** - Search history stored in H2 database

## 🏗️ System Architecture

### Module Breakdown

#### 1. **API Integration Module** (`client/`)
- `WeatherApiClient.java` - REST client for OpenWeatherMap API
- Handles HTTPS communication with secure endpoints
- Methods: `getCurrentWeather()`, `getFiveDayForecast()`

#### 2. **Data Transfer Objects** (`dto/`)
- `WeatherApiResponse.java` - Maps current weather API response
- `ForecastApiResponse.java` - Maps 5-day forecast API response
- Nested classes for structured data (Temperature, Wind, Clouds, etc.)

#### 3. **Data Models** (`model/`)
- `WeatherData.java` - Current weather information model
- `ForecastData.java` - Daily forecast model
- `SearchHistory.java` - JPA entity for search history persistence

#### 4. **Service Layer** (`service/`)
- `WeatherService.java` - Business logic orchestration
  - Fetches data from API
  - Transforms raw data to model objects
  - Handles unit conversion
- `SearchHistoryService.java` - Manages search history persistence
- `UnitConverter.java` - Temperature unit conversion (C ↔ F)

#### 5. **Repository Layer** (`repository/`)
- `SearchHistoryRepository.java` - Spring Data JPA interface
- Database operations for search history

#### 6. **Controller Layer** (`controller/`)
- `WeatherController.java` - HTTP request handling
- Routes: `/`, `/weather`, `/toggle-unit`, `/refresh`

#### 7. **Exception Handling** (`exception/`)
- `CityNotFoundException.java` - Custom exception for invalid cities
- `GlobalExceptionHandler.java` - Centralized error handling with `@ControllerAdvice`

#### 8. **Configuration** (`config/`)
- `AppConfig.java` - Spring beans configuration
- `application.properties` - Application settings

#### 9. **UI Layer** (`templates/` + `static/`)
- `index.html` - Main dashboard template (Thymeleaf)
- `error.html` - Error display template
- `style.css` - Responsive styling (mobile + desktop)
- `app.js` - Client-side interactions

#### 10. **Testing** (`test/`)
- `WeatherServiceTest.java` - Unit tests (JUnit 5 + Mockito)
- `WeatherControllerTest.java` - Integration tests (Spring Test)

## 📋 Project Structure

```
Smart Weather Dashboard/
├── src/
│   ├── main/
│   │   ├── java/com/example/smartweatherdashboard/
│   │   │   ├── config/
│   │   │   │   └── AppConfig.java
│   │   │   ├── controller/
│   │   │   │   └── WeatherController.java
│   │   │   ├── service/
│   │   │   │   ├── WeatherService.java
│   │   │   │   ├── SearchHistoryService.java
│   │   │   │   └── UnitConverter.java
│   │   │   ├── client/
│   │   │   │   └── WeatherApiClient.java
│   │   │   ├── model/
│   │   │   │   ├── WeatherData.java
│   │   │   │   ├── ForecastData.java
│   │   │   │   └── SearchHistory.java
│   │   │   ├── dto/
│   │   │   │   ├── WeatherApiResponse.java
│   │   │   │   └── ForecastApiResponse.java
│   │   │   ├── repository/
│   │   │   │   └── SearchHistoryRepository.java
│   │   │   ├── exception/
│   │   │   │   ├── CityNotFoundException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── SmartWeatherDashboardApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── templates/
│   │       │   ├── index.html
│   │       │   └── error.html
│   │       └── static/
│   │           ├── css/
│   │           │   └── style.css
│   │           └── js/
│   │               └── app.js
│   └── test/
│       └── java/com/example/smartweatherdashboard/
│           ├── service/
│           │   └── WeatherServiceTest.java
│           └── controller/
│               └── WeatherControllerTest.java
├── build.gradle
├── settings.gradle
├── gradlew
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- **Java 25+** (Spring Boot 4.1.0 compatible)
- **Gradle 9.5.1+**
- **OpenWeatherMap API Key** (free tier available)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd "Smart Weather Dashboard"
   ```

2. **Get OpenWeatherMap API Key**
   - Sign up at [OpenWeatherMap](https://openweathermap.org/api)
   - Copy your free API key

3. **Configure API Key**
   Edit `src/main/resources/application.properties`:
   ```properties
   weather.api.key=YOUR_API_KEY_HERE
   ```

4. **Build the project**
   ```bash
   ./gradlew clean build
   ```

5. **Run the application**
   ```bash
   ./gradlew bootRun
   ```
   Or:
   ```bash
   java -jar build/libs/Smart Weather Dashboard-0.0.1-SNAPSHOT.jar
   ```

6. **Open in browser**
   Navigate to: `http://localhost:8080`

## 📖 Usage

### Search Weather
1. Enter a city name in the search box
2. Click "Search" or press Enter
3. Current weather and 5-day forecast will be displayed

### Toggle Temperature Unit
1. Click "Switch to °F" (or °C) button
2. All temperatures will convert instantly

### Refresh Weather
- Click "🔄 Refresh" to reload current weather data

### View Recent Searches
- Click on any city in the "Recent Searches" section
- Last 10 unique cities are saved automatically

### Error Handling
- If a city is not found, an error message appears with instructions
- Connection issues are caught and displayed gracefully

## 🧪 Running Tests

Execute unit and integration tests:

```bash
./gradlew test
```

Test coverage:
- **WeatherServiceTest** - Service logic, unit conversion, data transformation
- **WeatherControllerTest** - HTTP routes, view rendering, form handling

## 📊 API Configuration

### OpenWeatherMap Endpoints Used
- **Current Weather**: `/data/2.5/weather?q={city}&appid={apiKey}&units=metric`
- **5-Day Forecast**: `/data/2.5/forecast?q={city}&appid={apiKey}&units=metric`

### Configuration Properties
```properties
weather.api.key=YOUR_API_KEY
weather.api.base-url=https://api.openweathermap.org/data/2.5
weather.default.unit=metric
server.port=8080
```

## 🗃️ Database

### Technology
- **H2 Database** - In-memory database
- No external database setup required
- Data persists during application runtime

### Schema
**search_history** table:
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| city_name | VARCHAR | City name searched |
| searched_at | TIMESTAMP | Search timestamp |

## 🎨 UI/UX Features

### Responsive Design
- **Desktop** (1200px+) - Multi-column layouts
- **Tablet** (768px-1199px) - Adjusted grid layouts
- **Mobile** (< 768px) - Single column, touch-friendly buttons

### Visual Features
- Gradient background (purple theme)
- Smooth hover animations
- Shadow effects for depth
- Weather emoji indicators
- Color-coded temperature ranges

### Accessibility
- Semantic HTML structure
- Proper form labels
- Error message styling
- Keyboard navigation support (Ctrl+K to search)

## 🔐 Security

### HTTPS
- All API calls use HTTPS protocol
- API key stored securely in `application.properties`
- Key never exposed in logs or frontend

### Input Validation
- City name input sanitized (letters, spaces, hyphens only)
- Empty search prevented with form validation
- Error messages sanitized to prevent XSS

## 📝 Code Quality

### Logging
- SLF4J with Logback
- DEBUG level for application logs
- Error logging with stack traces

### Code Style
- Follows Spring Boot conventions
- Proper constructor injection (no field injection)
- Comprehensive JavaDoc comments
- Clean separation of concerns

## 🚧 Future Enhancements

- [ ] Weather alerts and notifications
- [ ] Multiple location favorites
- [ ] Weather charts and graphs
- [ ] Dark mode theme
- [ ] Weather API caching
- [ ] Hourly forecast details
- [ ] Air quality index (AQI)
- [ ] Weather radar integration
- [ ] User authentication
- [ ] Mobile app version

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📞 Support

For issues or questions:
- Check existing issues on GitHub
- Create a new issue with detailed description
- Contact: [your-email@example.com]

## 📚 Technologies Used

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 25 | Backend language |
| Spring Boot | 4.1.0 | Web framework |
| Thymeleaf | (included) | Template engine |
| Spring Data JPA | (included) | Database access |
| H2 Database | (included) | In-memory DB |
| Lombok | (included) | Boilerplate reduction |
| JUnit 5 | (included) | Testing framework |
| Mockito | (included) | Mocking library |
| Gradle | 9.5.1 | Build tool |
| HTML5/CSS3 | Latest | Frontend |
| JavaScript | ES6+ | Client-side interactivity |

---

**Smart Weather Dashboard** © 2026. Built with ❤️ using Spring Boot.

