package com.weather.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain entity representing a geographical location.
 * This is framework-agnostic pure domain model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private String timezone;

    /**
     * Returns a unique identifier for this location
     */
    public String getLocationKey() {
        return String.format("%s,%s", city, country).toLowerCase();
    }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
}
