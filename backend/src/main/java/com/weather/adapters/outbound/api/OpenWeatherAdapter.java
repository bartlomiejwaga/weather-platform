package com.weather.adapters.outbound.api;

import com.weather.application.port.output.WeatherProviderPort;
import com.weather.domain.model.Forecast;
import com.weather.domain.model.Location;
import com.weather.domain.model.WeatherReading;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenWeatherAdapter implements WeatherProviderPort {

    private final WebClient.Builder webClientBuilder;

    @Value("${weather.openweather.api-key}")
    private String apiKey;

    @Value("${weather.openweather.base-url}")
    private String baseUrl;

    @Value("${weather.openweather.timeout:5000}")
    private int timeout;

    @Override
    @CircuitBreaker(name = "weatherApi", fallbackMethod = "getCurrentWeatherFallback")
    @Retry(name = "weatherApi")
    public Optional<WeatherReading> getCurrentWeather(String city, String country) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("OpenWeather API key not configured");
            return Optional.empty();
        }

        String query = country != null ? city + "," + country : city;

        try {
            WebClient client = webClientBuilder.baseUrl(baseUrl).build();

            OpenWeatherCurrentResponse response = client.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/weather")
                    .queryParam("q", query)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build())
                .retrieve()
                .bodyToMono(OpenWeatherCurrentResponse.class)
                .timeout(Duration.ofMillis(timeout))
                .block();

            if (response == null) {
                log.warn("Empty response from OpenWeather API for {}", query);
                return Optional.empty();
            }

            WeatherReading reading = mapToWeatherReading(response);
            log.info("Successfully fetched weather from OpenWeather for {}", query);
            return Optional.of(reading);

        } catch (Exception e) {
            log.error("Error fetching weather from OpenWeather for {}: {}", query, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @CircuitBreaker(name = "weatherApi", fallbackMethod = "getForecastFallback")
    @Retry(name = "weatherApi")
    public Optional<List<Forecast>> getForecast(String city, String country, int days) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("OpenWeather API key not configured");
            return Optional.empty();
        }

        String query = country != null ? city + "," + country : city;

        try {
            WebClient client = webClientBuilder.baseUrl(baseUrl).build();

            OpenWeatherForecastResponse response = client.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/forecast")
                    .queryParam("q", query)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .queryParam("cnt", days * 8) // 8 forecasts per day (3-hour intervals)
                    .build())
                .retrieve()
                .bodyToMono(OpenWeatherForecastResponse.class)
                .timeout(Duration.ofMillis(timeout))
                .block();

            if (response == null || response.list() == null) {
                log.warn("Empty forecast response from OpenWeather API for {}", query);
                return Optional.empty();
            }

            List<Forecast> forecasts = mapToForecasts(response, city, country);
            log.info("Successfully fetched forecast from OpenWeather for {}", query);
            return Optional.of(forecasts);

        } catch (Exception e) {
            log.error("Error fetching forecast from OpenWeather for {}: {}", query, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public String getProviderName() {
        return "OpenWeatherMap";
    }

    private WeatherReading mapToWeatherReading(OpenWeatherCurrentResponse response) {
        Location location = Location.builder()
            .city(response.name())
            .country(response.sys() != null ? response.sys().country() : null)
            .latitude(response.coord() != null ? response.coord().lat() : null)
            .longitude(response.coord() != null ? response.coord().lon() : null)
            .build();

        return WeatherReading.builder()
            .location(location)
            .timestamp(Instant.ofEpochSecond(response.dt()))
            .temperatureCelsius(response.main().temp())
            .humidity(response.main().humidity().doubleValue())
            .pressure(response.main().pressure().doubleValue())
            .windSpeed(response.wind() != null ? response.wind().speed() : null)
            .windDirection(response.wind() != null ? response.wind().deg() : null)
            .weatherCondition(response.weather() != null && !response.weather().isEmpty()
                ? response.weather().get(0).main() : null)
            .weatherDescription(response.weather() != null && !response.weather().isEmpty()
                ? response.weather().get(0).description() : null)
            .weatherIcon(response.weather() != null && !response.weather().isEmpty()
                ? response.weather().get(0).icon() : null)
            .visibility(response.visibility() != null ? response.visibility().doubleValue() : null)
            .cloudiness(response.clouds() != null ? response.clouds().all() : null)
            .build();
    }

    private List<Forecast> mapToForecasts(OpenWeatherForecastResponse response, String city, String country) {
        Location location = Location.builder()
            .city(city)
            .country(country)
            .latitude(response.city() != null && response.city().coord() != null
                ? response.city().coord().lat() : null)
            .longitude(response.city() != null && response.city().coord() != null
                ? response.city().coord().lon() : null)
            .build();

        List<Forecast> forecasts = new ArrayList<>();
        response.list().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                item -> Instant.ofEpochSecond(item.dt()).atZone(ZoneId.systemDefault()).toLocalDate()
            ))
            .forEach((date, items) -> {
                forecasts.add(aggregateDailyForecast(location, date, items));
            });

        return forecasts;
    }

    private Forecast aggregateDailyForecast(Location location, LocalDate date,
                                           List<OpenWeatherForecastResponse.ForecastItem> items) {
        double tempMin = items.stream()
            .mapToDouble(i -> i.main().tempMin())
            .min().orElse(0);
        double tempMax = items.stream()
            .mapToDouble(i -> i.main().tempMax())
            .max().orElse(0);
        double tempAvg = items.stream()
            .mapToDouble(i -> i.main().temp())
            .average().orElse(0);

        var firstItem = items.get(0);

        return Forecast.builder()
            .location(location)
            .date(date)
            .tempMin(tempMin)
            .tempMax(tempMax)
            .tempAvg(tempAvg)
            .humidity(firstItem.main().humidity())
            .windSpeed(firstItem.wind() != null ? firstItem.wind().speed() : null)
            .weatherCondition(firstItem.weather() != null && !firstItem.weather().isEmpty()
                ? firstItem.weather().get(0).main() : null)
            .weatherDescription(firstItem.weather() != null && !firstItem.weather().isEmpty()
                ? firstItem.weather().get(0).description() : null)
            .weatherIcon(firstItem.weather() != null && !firstItem.weather().isEmpty()
                ? firstItem.weather().get(0).icon() : null)
            .precipitationProbability(firstItem.pop() != null ? firstItem.pop() * 100 : null)
            .cloudiness(firstItem.clouds() != null ? firstItem.clouds().all() : null)
            .createdAt(Instant.now())
            .build();
    }

    private Optional<WeatherReading> getCurrentWeatherFallback(String city, String country, Exception e) {
        log.error("Circuit breaker activated for getCurrentWeather: {}", e.getMessage());
        return Optional.empty();
    }

    private Optional<List<Forecast>> getForecastFallback(String city, String country, int days, Exception e) {
        log.error("Circuit breaker activated for getForecast: {}", e.getMessage());
        return Optional.empty();
    }

    record OpenWeatherCurrentResponse(
        Coord coord,
        List<Weather> weather,
        String base,
        Main main,
        Integer visibility,
        Wind wind,
        Clouds clouds,
        long dt,
        Sys sys,
        int timezone,
        long id,
        String name,
        int cod
    ) {}

    record Coord(double lat, double lon) {}

    record Weather(int id, String main, String description, String icon) {}

    record Main(double temp, double feelsLike, double tempMin, double tempMax,
                Integer pressure, Integer humidity) {}

    record Wind(Double speed, Integer deg) {}

    record Clouds(Integer all) {}

    record Sys(int type, long id, String country, long sunrise, long sunset) {}

    record OpenWeatherForecastResponse(
        String cod,
        int message,
        int cnt,
        List<ForecastItem> list,
        City city
    ) {
        record ForecastItem(
            long dt,
            Main main,
            List<Weather> weather,
            Clouds clouds,
            Wind wind,
            Integer visibility,
            Double pop,
            Sys sys,
            String dtTxt
        ) {}

        record City(long id, String name, Coord coord, String country,
                   int population, int timezone, long sunrise, long sunset) {}
    }
}
