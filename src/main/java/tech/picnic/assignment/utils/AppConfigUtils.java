package tech.picnic.assignment.utils;

import tech.picnic.assignment.models.TemperatureZone;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

public final class AppConfigUtils {

    private final static Set<TemperatureZone> excludedTempZoneSet = new HashSet<>();

    private AppConfigUtils() {
    }

    public static Set<TemperatureZone> getExcludedTemperatureZoneConfig() throws IOException {
        if (!excludedTempZoneSet.isEmpty()) {
            return excludedTempZoneSet;
        }
        Properties prop = new Properties();
        prop.load(AppConfigUtils.class.getClassLoader().getResourceAsStream("config.properties"));
        String val = prop.getProperty("temperature_zone_excluded");

        if (Objects.nonNull(val)) {
            for (String tempZone : val.split(",")) {
                TemperatureZone temperatureZone = TemperatureZone.fromString(tempZone);

                if (Objects.nonNull(temperatureZone)) {
                    excludedTempZoneSet.add(temperatureZone);
                }
            }

        }

        return excludedTempZoneSet;
    }


}
