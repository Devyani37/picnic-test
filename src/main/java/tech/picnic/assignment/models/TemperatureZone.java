package tech.picnic.assignment.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TemperatureZone {
    @JsonProperty("ambient")
    AMBIENT,
    @JsonProperty("chilled")
    CHILLED;
}
