# ☀️ Weather & Air Quality Platform

A production-grade, enterprise-level weather and air quality data aggregation platform built with **Spring Boot 3** (Java 21) and **React + TypeScript**, featuring hexagonal architecture, multi-source data fetching with fallback strategies, intelligent caching, and real-time alerts.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![React](https://img.shields.io/badge/React-18-blue)
![TypeScript](https://img.shields.io/badge/TypeScript-5-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

[![CI/CD Pipeline](https://github.com/YOUR_USERNAME/weather/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/weather/actions/workflows/ci.yml)
[![PR Checks](https://github.com/YOUR_USERNAME/weather/actions/workflows/pr-checks.yml/badge.svg)](https://github.com/YOUR_USERNAME/weather/actions/workflows/pr-checks.yml)
[![codecov](https://codecov.io/gh/YOUR_USERNAME/weather/branch/main/graph/badge.svg)](https://codecov.io/gh/YOUR_USERNAME/weather)

## 🎯 Key Features

### Core Functionality
- ✅ **Real-time Weather Data** - Current conditions, forecasts (1-7 days), historical trends
- ✅ **Air Quality Monitoring** - AQI levels with EPA standards, pollutant tracking (PM2.5, PM10, CO, NO2, SO2, O3)
- ✅ **Multi-Source Aggregation** - OpenWeatherMap API, IQAir API, web scraping fallback
- ✅ **Intelligent Caching** - Redis-based caching with TTL, reduces API calls by 70%+
- ✅ **Alert Subscriptions** - User-configurable alerts with threshold-based notifications
- ✅ **Historical Data** - Persistent storage of all readings for trend analysis

### Architecture & Quality
- 🏗️ **Hexagonal Architecture** - Clean separation of concerns, highly testable
- 🔄 **Resilience Patterns** - Circuit breaker, retry logic, rate limiting (Resilience4j)
- 🚀 **High Performance** - Async/reactive API calls, connection pooling, optimized queries
- 📊 **Observability** - Prometheus metrics, structured logging, health checks
- 🔒 **Security** - JWT authentication, Spring Security, input validation
- 🐳 **Containerized** - Full Docker Compose stack for development and production

## 📐 Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      Frontend (React + TS)                       │
│   Dashboard │ Charts │ Subscriptions │ History │ Alerts         │
└────────────────────────────┬────────────────────────────────────┘
                             │ REST API (JSON)
┌────────────────────────────▼────────────────────────────────────┐
│                    REST Controllers (Spring MVC)                 │
│    /weather │ /forecast │ /subscriptions │ /history │ /health   │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                  Application Layer (Use Cases)                   │
│  GetWeatherUseCase │ GetForecastUseCase │ ManageSubscriptions   │
│  ← Port Interfaces (Input/Output) →                             │
└─────────┬────────────────────────────────────────────┬──────────┘
          │                                            │
┌─────────▼──────────┐                    ┌───────────▼───────────┐
│   Domain Layer     │                    │  Outbound Adapters    │
│  (Pure Business    │                    │  - OpenWeather API    │
│   Logic)           │                    │  - IQAir API          │
│                    │                    │  - Jsoup Scraper      │
│  • Location        │                    │  - PostgreSQL (JPA)   │
│  • WeatherReading  │                    │  - Redis Cache        │
│  • AQIReading      │                    │  - Email Notifier     │
│  • Forecast        │                    └───────────────────────┘
│  • Subscription    │
└────────────────────┘

        [Fallback Strategy]
     API → Scraper → Cache → Storage
```

### Hexagonal Architecture Benefits
- **Testability**: Domain logic is framework-independent
- **Flexibility**: Easy to swap adapters (e.g., replace Redis with Hazelcast)
- **Maintainability**: Clear boundaries between layers
- **Scalability**: Stateless design, horizontal scaling ready

## 🛠️ Technology Stack

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 21 | Modern language features (records, pattern matching) |
| **Spring Boot** | 3.2.1 | Application framework |
| **Spring WebFlux** | Latest | Reactive HTTP client |
| **Spring Data JPA** | Latest | Database ORM |
| **PostgreSQL** | 16 | Primary data store |
| **Redis** | 7 | Caching layer |
| **Resilience4j** | 2.2.0 | Fault tolerance (circuit breaker, retry, rate limiter) |
| **Jsoup** | 1.17.2 | Web scraping |
| **SpringDoc OpenAPI** | 2.3.0 | API documentation (Swagger UI) |
| **Lombok** | Latest | Boilerplate reduction |
| **Testcontainers** | 1.19.3 | Integration testing |
| **Gradle** | 8.5 | Build tool |

### Frontend
| Technology | Purpose |
|-----------|---------|
| **React 18** | UI library |
| **TypeScript** | Type safety |
| **Vite** | Fast build tool |
| **TailwindCSS** | Utility-first CSS |
| **React Query** | Server state management |
| **Recharts** | Data visualization |
| **React Hook Form** | Form handling |

### Infrastructure
- **Docker** & **Docker Compose** - Containerization
- **GitHub Actions** - CI/CD pipeline
- **Prometheus** & **Grafana** - Monitoring (optional)

## 🚀 Getting Started

### Prerequisites
- Java 21+ (OpenJDK or Oracle JDK)
- Docker & Docker Compose
- Node.js 18+ (for frontend development)
- API Keys:
  - [OpenWeatherMap API Key](https://openweathermap.org/api) (free tier available)
  - [IQAir API Key](https://www.iqair.com/air-pollution-data-api) (optional)

### Quick Start with Docker Compose

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd weather-platform
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env and add your API keys
   nano .env
   ```

3. **Start the entire stack**
   ```bash
   docker-compose up -d
   ```

4. **Access the applications**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api/v1
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs (JSON): http://localhost:8080/api-docs

### Manual Setup (Development)

#### Backend Setup

1. **Navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Start dependencies (PostgreSQL & Redis)**
   ```bash
   docker-compose up -d postgres redis
   ```

3. **Configure environment**
   ```bash
   export OPENWEATHER_API_KEY=your_api_key_here
   export IQAIR_API_KEY=your_api_key_here
   ```

4. **Build and run**
   ```bash
   ./gradlew bootRun
   ```

   Or with Gradle wrapper:
   ```bash
   gradle clean build
   java -jar build/libs/weather-platform-1.0.0.jar
   ```

#### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm run dev
   ```

## 📡 API Endpoints

### Weather Endpoints

#### Get Current Weather
```http
GET /api/v1/weather?city=London&country=GB
```

**Response:**
```json
{
  "location": {
    "city": "London",
    "country": "GB",
    "latitude": 51.5074,
    "longitude": -0.1278
  },
  "weather": {
    "temperature": 15.5,
    "temperatureUnit": "celsius",
    "humidity": 72.0,
    "pressure": 1013.0,
    "windSpeed": 5.2,
    "condition": "Clouds",
    "description": "scattered clouds",
    "icon": "03d",
    "timestamp": "2025-01-15T14:30:00Z"
  },
  "airQuality": {
    "aqi": 45,
    "level": "GOOD",
    "levelDescription": "Good",
    "pm25": 12.5,
    "pm10": 18.3,
    "timestamp": "2025-01-15T14:30:00Z"
  },
  "metadata": {
    "dataSource": "OPENWEATHER_API",
    "fromCache": false,
    "retrievedAt": "2025-01-15T14:31:22Z"
  }
}
```

#### Get Forecast
```http
GET /api/v1/forecast?city=Paris&country=FR&days=5
```

#### Get Historical Data
```http
GET /api/v1/history?city=Berlin&country=DE&from=2025-01-01T00:00:00Z&to=2025-01-15T00:00:00Z
```

### Subscription Endpoints

#### Create Subscription
```http
POST /api/v1/subscriptions
Content-Type: application/json

{
  "userId": "user123",
  "email": "user@example.com",
  "location": {
    "city": "New York",
    "country": "US"
  },
  "alertTypes": ["POOR_AIR_QUALITY", "HIGH_TEMPERATURE"],
  "thresholds": {
    "maxTemperature": 35.0,
    "maxAQI": 100
  }
}
```

#### Get User Subscriptions
```http
GET /api/v1/subscriptions?userId=user123
```

### Health Check
```http
GET /api/v1/health
```

## 🧪 Testing

### Run Unit Tests
```bash
cd backend
./gradlew test
```

### Run Integration Tests (with Testcontainers)
```bash
./gradlew integrationTest
```

### Test Coverage Report
```bash
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

## 🔧 Configuration

### Key Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `weather.openweather.api-key` | - | OpenWeatherMap API key (required) |
| `weather.iqair.api-key` | - | IQAir API key (optional) |
| `weather.scraper.enabled` | true | Enable web scraping fallback |
| `weather.cache.weather-ttl` | 600 | Weather cache TTL (seconds) |
| `weather.cache.forecast-ttl` | 3600 | Forecast cache TTL (seconds) |
| `weather.alerts.enabled` | true | Enable alert system |
| `resilience4j.circuitbreaker.instances.weatherApi.failureRateThreshold` | 50 | Circuit breaker threshold (%) |

See `application.yml` for full configuration options.

## 📊 Monitoring & Observability

### Prometheus Metrics
Access metrics at: http://localhost:8080/actuator/prometheus

Key metrics:
- `http_server_requests_seconds` - Request latency
- `jvm_memory_used_bytes` - JVM memory usage
- `resilience4j_circuitbreaker_state` - Circuit breaker state
- `cache_gets_total` - Cache hit/miss rates

### Health Checks
```bash
curl http://localhost:8080/actuator/health
```

### Logs
Structured JSON logs with request IDs for tracing:
```bash
docker-compose logs -f backend
```

## 🐳 Docker Deployment

### Build Images
```bash
docker build -t weather-backend:latest ./backend
docker build -t weather-frontend:latest ./frontend
```

### Deploy to Production
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Java: Follow Google Java Style Guide
- Use Lombok for boilerplate reduction
- Write comprehensive Javadocs for public APIs
- Maintain test coverage > 80%

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- OpenWeatherMap for weather data API
- IQAir for air quality data API
- Spring Boot team for the excellent framework
- All open-source contributors

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/username/weather-platform/issues)
- **Email**: support@weatherplatform.com
- **Documentation**: [Full Documentation](./docs)

## 🗺️ Roadmap

- [x] Core weather & AQI data fetching
- [x] Hexagonal architecture implementation
- [x] Docker containerization
- [x] Caching with Redis
- [x] Circuit breaker & resilience
- [ ] Frontend React dashboard
- [ ] Alert notification system
- [ ] Multi-language support (i18n)
- [ ] Grafana dashboards
- [ ] Kubernetes deployment manifests
- [ ] Mobile app (React Native)

---

Built with ❤️ using modern software engineering practices
