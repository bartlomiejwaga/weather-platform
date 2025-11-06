package com.weather.application.port.output;

import com.weather.domain.model.Subscription;
import com.weather.domain.model.WeatherReading;
import com.weather.domain.model.AQIReading;

/**
 * Output port for sending notifications/alerts
 */
public interface NotificationPort {

    /**
     * Sends alert notification via email
     */
    void sendAlert(Subscription subscription, AlertMessage message);

    /**
     * Alert message structure
     */
    record AlertMessage(
        String subject,
        String body,
        AlertType type,
        WeatherReading weatherReading,
        AQIReading aqiReading
    ) {}

    enum AlertType {
        WEATHER_ALERT,
        AIR_QUALITY_ALERT,
        EXTREME_CONDITION_ALERT
    }
}
