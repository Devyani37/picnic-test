package tech.picnic.assignment.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TemperatureZone {
    @JsonProperty("ambient")
    AMBIENT("ambient"),
    @JsonProperty("chilled")
    CHILLED("chilled");

    String val;

    TemperatureZone(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public static TemperatureZone fromString(String tempZone) {
        for (TemperatureZone temperatureZone : TemperatureZone.values()) {
            if (temperatureZone.val.equals(tempZone)) {
                return temperatureZone;
            }
        }
        return null;
    }
}
