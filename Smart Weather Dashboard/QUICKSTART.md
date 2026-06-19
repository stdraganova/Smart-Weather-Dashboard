# Quick Start Guide - Smart Weather Dashboard

## ⚡ 5-Minute Setup

### Step 1: Get an OpenWeatherMap API Key (2 minutes)

1. Visit [OpenWeatherMap Free API](https://openweathermap.org/api)
2. Sign up for a free account
3. Copy your **API Key** (under "API keys" section)
4. Save it - you'll need this in the next step

### Step 2: Configure the Application (1 minute)

Open `src/main/resources/application.properties` and update:

```properties
# Replace YOUR_API_KEY_HERE with your actual API key
weather.api.key=YOUR_API_KEY_HERE
```

**Example**:
```properties
weather.api.key=8f032fa8a3e9c40f0e2cdb5f8d9e5a3f
```

### Step 3: Build the Project (1 minute)

```bash
cd "Smart Weather Dashboard"
./gradlew clean build
```

**Windows users**:
```bash
gradlew.bat clean build
```

### Step 4: Run the Application (1 minute)

```bash
./gradlew bootRun
```

Or directly run the JAR:
```bash
java -jar build/libs/Smart\ Weather\ Dashboard-0.0.1-SNAPSHOT.jar
```

### Step 5: Open in Browser

Navigate to: **http://localhost:8080**

## 🎯 First Actions

1. **Search for a City**: Type "London" and click Search
2. **Toggle Units**: Click "Switch to °F" to see Fahrenheit
3. **View Forecast**: Scroll down to see 5-day forecast
4. **Recent Searches**: Click on saved cities for quick access

## ⚙️ Configuration Quick Reference

**File**: `src/main/resources/application.properties`

| Property | Default | Purpose |
|----------|---------|---------|
| `weather.api.key` | Required | Your OpenWeatherMap API key |
| `weather.api.base-url` | https://api.openweathermap.org/data/2.5 | API endpoint |
| `weather.default.unit` | metric | Default: metric (C) or imperial (F) |
| `server.port` | 8080 | Port to run the app on |

## 🧪 Running Tests

```bash
./gradlew test
```

Expected output:
```
BUILD SUCCESSFUL
11 tests completed
```

## 🐛 Troubleshooting

### Issue: "Cannot find symbol RestTemplate"
**Solution**: Ensure Spring Boot starter-web is in dependencies
```bash
./gradlew clean build
```

### Issue: "City not found" error
**Solution**: Check spelling, try "New York" or "Tokyo"

### Issue: API returns 401 Unauthorized
**Solution**: Verify your API key is correct in `application.properties`

### Issue: Port 8080 already in use
**Solution**: Change port in `application.properties`:
```properties
server.port=8081
```

### Issue: Application won't start
**Solution**: Check Java version (requires Java 25+)
```bash
java -version
```

## 📁 Project Structure

```
Smart Weather Dashboard/
├── src/main/java/         ← Java source code
├── src/main/resources/    ← Configuration & templates
│   ├── application.properties  ← Edit API key here!
│   ├── templates/         ← HTML files
│   └── static/            ← CSS & JavaScript
├── src/test/java/         ← Test code
├── build.gradle           ← Dependencies & build config
└── README.md             ← Full documentation
```

## 🔑 Environment Variables (Optional, for Production)

Instead of hardcoding API key, use environment variables:

**On macOS/Linux**:
```bash
export WEATHER_API_KEY=your_api_key_here
./gradlew bootRun
```

**On Windows PowerShell**:
```powershell
$env:WEATHER_API_KEY = "your_api_key_here"
./gradlew bootRun
```

Update `application.properties`:
```properties
weather.api.key=${WEATHER_API_KEY}
```

## 📱 Responsive Design Test

- **Desktop** (1200px+): Open in desktop browser
- **Tablet** (768px-1199px): Resize browser or use tablet device
- **Mobile** (< 768px): Resize to phone width or use mobile device

## 🚀 Next Steps

1. **Explore the Code**: Check `ARCHITECTURE.md` for technical details
2. **Run Tests**: `./gradlew test` to verify all features work
3. **Deploy**: Package as Docker container or deploy to cloud
4. **Enhance**: Add more features like hourly forecast, alerts, etc.

## 📚 API Documentation

### Example Weather Response

```json
{
  "coord": {"lon": -0.1257, "lat": 51.5085},
  "weather": [{"main": "Clouds", "description": "overcast clouds"}],
  "main": {
    "temp": 15.2,
    "feels_like": 14.8,
    "humidity": 72
  },
  "wind": {"speed": 4.5},
  "clouds": {"all": 90},
  "name": "London",
  "sys": {"country": "GB"}
}
```

### Example Search Flow

1. **User enters**: "Paris"
2. **URL**: `http://localhost:8080/weather?city=Paris&unit=metric`
3. **API call**: `https://api.openweathermap.org/data/2.5/weather?q=Paris&appid=YOUR_KEY&units=metric`
4. **Response parsed** and displayed
5. **Search saved** to database

## 🎨 UI Features at a Glance

| Feature | Location | How to Use |
|---------|----------|-----------|
| Search | Top of page | Enter city name, click Search |
| Current Weather | Below search | Shows temp, humidity, wind, etc. |
| 5-Day Forecast | Below weather | Grid of daily forecasts |
| Unit Toggle | Top right | Click to switch C/F |
| Refresh Button | Top right | Click to reload data |
| Recent Searches | Bottom | Click any city to search again |
| Error Messages | Top of page | Appears if city not found |

## ✅ Verification Checklist

- [ ] Java 25+ installed (`java -version`)
- [ ] Gradle works (`./gradlew --version`)
- [ ] API key obtained from OpenWeatherMap
- [ ] API key added to `application.properties`
- [ ] Project builds successfully (`./gradlew clean build`)
- [ ] Application starts without errors (`./gradlew bootRun`)
- [ ] Browser opens to `http://localhost:8080`
- [ ] Can search for a city
- [ ] Weather data displays correctly
- [ ] Unit toggle works
- [ ] Forecast shows 5 days

## 📞 Getting Help

1. **Check Console Logs**: Look for error messages when starting
2. **Read ARCHITECTURE.md**: Understand the system design
3. **Review Test Files**: See examples of expected behavior
4. **Check application.properties**: Verify all settings

## 🎓 Learning Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Template Engine](https://www.thymeleaf.org/)
- [OpenWeatherMap API](https://openweathermap.org/api)
- [Java REST Client Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.rest-client)

## 🎉 You're All Set!

Your Smart Weather Dashboard is now running. Share the `localhost:8080` URL with friends or deploy to the cloud!

---

**Version**: 1.0  
**Last Updated**: June 18, 2026

