# 🚀 Quick Start Guide

Get the Weather & Air Quality Platform running in **5 minutes**!

## Prerequisites Check

```bash
# Check Java version (need 21+)
java -version

# Check Docker
docker --version
docker-compose --version

# Check Node (need 18+)
node --version
npm --version
```

## Option 1: Docker Compose (Recommended)

### Step 1: Get API Keys (2 minutes)

1. **OpenWeatherMap** (Required)
   - Go to https://openweathermap.org/api
   - Sign up for free account
   - Generate API key (free tier: 1000 calls/day)

2. **IQAir** (Optional)
   - Go to https://www.iqair.com/air-pollution-data-api
   - Sign up for free account
   - Generate API key

### Step 2: Configure Environment

```bash
# Copy example env file
cp .env.example .env

# Edit .env file and add your keys
nano .env  # or use your favorite editor
```

**Minimum required in .env:**
```env
OPENWEATHER_API_KEY=your_key_here
```

### Step 3: Start Everything

```bash
# Start all services (backend, frontend, PostgreSQL, Redis)
docker-compose up -d

# Check logs
docker-compose logs -f
```

### Step 4: Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

### Test It Out!

```bash
# Get weather for London
curl "http://localhost:8080/api/v1/weather?city=London&country=GB"

# Get 5-day forecast
curl "http://localhost:8080/api/v1/forecast?city=Paris&days=5"
```

## Option 2: Manual Setup (Development)

### Backend

```bash
cd backend

# Start dependencies
docker-compose up -d postgres redis

# Set API key
export OPENWEATHER_API_KEY=your_key_here

# Run application
./gradlew bootRun
```

Backend will start on http://localhost:8080

### Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start dev server
npm run dev
```

Frontend will start on http://localhost:3000

## 🧪 Testing the System

### Test Weather Endpoint

```bash
# Current weather
curl -X GET "http://localhost:8080/api/v1/weather?city=Tokyo&country=JP" | jq

# Expected response:
{
  "location": {
    "city": "Tokyo",
    "country": "JP",
    "latitude": 35.6895,
    "longitude": 139.6917
  },
  "weather": {
    "temperature": 15.5,
    "temperatureUnit": "celsius",
    "humidity": 72.0,
    "condition": "Clear",
    "description": "clear sky"
  },
  "airQuality": {
    "aqi": 45,
    "level": "GOOD",
    "levelDescription": "Good"
  },
  "metadata": {
    "dataSource": "OPENWEATHER_API",
    "fromCache": false
  }
}
```

### Test Caching

```bash
# First call (from API)
time curl "http://localhost:8080/api/v1/weather?city=London"
# Note the response time

# Second call (from cache - should be faster)
time curl "http://localhost:8080/api/v1/weather?city=London"
# Should return same data with "fromCache": true
```

### Test Forecast

```bash
curl "http://localhost:8080/api/v1/forecast?city=Berlin&days=7" | jq
```

## 🛑 Troubleshooting

### Backend won't start

```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# Check logs
docker-compose logs backend

# Common issue: Port 8080 in use
netstat -an | grep 8080
# Kill process or change port in docker-compose.yml
```

### Frontend won't connect to backend

```bash
# Check if backend is running
curl http://localhost:8080/actuator/health

# Check frontend env
cat frontend/.env.local

# Should have:
VITE_API_URL=http://localhost:8080/api/v1
```

### API returns errors

```bash
# Check if API key is set
docker-compose exec backend env | grep OPENWEATHER_API_KEY

# Test API key directly
curl "https://api.openweathermap.org/data/2.5/weather?q=London&appid=YOUR_KEY"
```

### Database connection issues

```bash
# Reset database
docker-compose down -v
docker-compose up -d postgres
docker-compose up backend
```

## 🔧 Useful Commands

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Restart a service
docker-compose restart backend

# Stop everything
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v

# Rebuild images
docker-compose build --no-cache

# Scale backend (for load testing)
docker-compose up -d --scale backend=3
```

## 📊 Explore the Features

### 1. Check Different Cities

Open http://localhost:3000 and search for:
- New York
- Tokyo
- London
- Paris
- Sydney

### 2. View API Documentation

Visit http://localhost:8080/swagger-ui.html to see all available endpoints and try them interactively.

### 3. Monitor Metrics

Visit http://localhost:8080/actuator/metrics to see:
- Request counts
- Response times
- Cache hit rates
- Circuit breaker states

### 4. Test Resilience

```bash
# Simulate API failure by using invalid key
# The system should fallback to scraper or cached data

# Check circuit breaker state
curl http://localhost:8080/actuator/metrics/resilience4j.circuitbreaker.state
```

## 🎯 Next Steps

1. **Set up alerts**: Create a subscription to get notified of poor air quality
2. **Explore history**: View historical weather data over time
3. **Customize**: Modify frontend styling or add new features
4. **Deploy**: Use the Docker images in production

## 📖 Learn More

- [Full README](README.md)
- [API Documentation](http://localhost:8080/swagger-ui.html)
- [Architecture Details](PROJECT_STATUS.md)

## 💡 Tips

- **Cache TTL**: Weather data is cached for 10 minutes by default
- **Rate Limiting**: Free OpenWeather tier allows 1000 calls/day
- **Fallback**: If API fails, system tries web scraping
- **Performance**: First request is slow (API call), subsequent requests use cache

## 🎉 Success!

If you can see weather data for any city, you're all set! The platform is running successfully.

Visit the frontend at http://localhost:3000 to see the beautiful dashboard.

---

Need help? Check the [troubleshooting section](#-troubleshooting) or open an issue on GitHub.
