package tech.picnic.assignment.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class Article {
    private final String id;
    private final String name;
    private final TemperatureZone temperatureZone;

    /**
     * Constructor of Article
     *
     * @param id              ID for article
     * @param name            Name of an Article
     * @param temperatureZone TemperatureZone of an article
     */
    @JsonCreator
    public Article(@JsonProperty("id") final String id,
                   @JsonProperty("name") final String name,
                   @JsonProperty("temperature_zone") final TemperatureZone temperatureZone) {
        this.id = Objects.requireNonNull(id, "Id should not be null");
        this.name = Objects.requireNonNull(name, "Name should not be null");
        this.temperatureZone = Objects.requireNonNull(temperatureZone, "TemperatureZone should not be null");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TemperatureZone getTemperatureZone() {
        return temperatureZone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return id.equals(article.id) &&
                name.equals(article.name) &&
                temperatureZone == article.temperatureZone;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", temperatureZone=" + temperatureZone +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, temperatureZone);
    }


}
