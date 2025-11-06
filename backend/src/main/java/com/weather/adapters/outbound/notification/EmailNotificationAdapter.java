package com.weather.adapters.outbound.notification;

import com.weather.application.port.output.NotificationPort;
import com.weather.domain.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Email notification adapter using Spring Mail
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationAdapter implements NotificationPort {

    private final JavaMailSender mailSender;

    @Override
    public void sendAlert(Subscription subscription, AlertMessage message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(subscription.getEmail());
            mailMessage.setSubject(message.subject());
            mailMessage.setText(buildEmailBody(message));
            mailMessage.setFrom("noreply@weatherplatform.com");

            mailSender.send(mailMessage);

            log.info("Sent alert email to {} for {}", subscription.getEmail(), subscription.getLocation().getCity());

        } catch (Exception e) {
            log.error("Failed to send alert email to {}: {}", subscription.getEmail(), e.getMessage());
        }
    }

    private String buildEmailBody(AlertMessage message) {
        StringBuilder body = new StringBuilder();
        body.append(message.body()).append("\n\n");

        if (message.weatherReading() != null) {
            body.append("Weather Details:\n");
            body.append("Temperature: ").append(message.weatherReading().getTemperatureCelsius()).append("°C\n");
            body.append("Humidity: ").append(message.weatherReading().getHumidity()).append("%\n");
            body.append("Wind Speed: ").append(message.weatherReading().getWindSpeed()).append(" m/s\n\n");
        }

        if (message.aqiReading() != null) {
            body.append("Air Quality Details:\n");
            body.append("AQI: ").append(message.aqiReading().getAqi()).append("\n");
            body.append("Level: ").append(message.aqiReading().getLevel()).append("\n");
            body.append("PM2.5: ").append(message.aqiReading().getPm25()).append(" μg/m³\n\n");
        }

        body.append("---\n");
        body.append("Weather & Air Quality Platform\n");
        body.append("Generated with Claude Code");

        return body.toString();
    }
}
