package tech.picnic.assignment.utils;

import tech.picnic.assignment.models.TemperatureZone;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * Class to read config.properties file.
 */
public final class AppConfigUtils {

    private AppConfigUtils() {
    }

    /**
     * This function reads config.properties file and create TemperatureZone enum set with excluded TemperatureZones.
     * (To exclude more temperatureZones just add them to config file, considering future requirements.)
     *
     * @return HashSet of excluded temperatureZones.
     * @throws IOException
     */
    public static Set<TemperatureZone> getExcludedTemperatureZoneConfig() throws IOException {
        Set<TemperatureZone> excludedTempZoneSet = new HashSet<>();
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
