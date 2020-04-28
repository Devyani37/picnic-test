package tech.picnic.assignment.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

public final class Picker {
    private final String id;
    private final String name;
    private final Date activeSince;


    /**
     * Constructor to create instance of a Picker
     *
     * @param id          id of the Picker.
     * @param name        Name of the Picker.
     * @param activeSince Picker activation timestamp.
     */

    @JsonCreator
    public Picker(@JsonProperty("id") final String id,
                  @JsonProperty("name") final String name,
                  @JsonProperty("active_since") final Date activeSince) {
        this.id = Objects.requireNonNull(id, "Id should not be null");
        this.name = Objects.requireNonNull(name, "Name should not be null");
        this.activeSince = Objects.requireNonNull(activeSince, "ActiveSince should not be null");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getActiveSince() {
        return activeSince;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picker picker = (Picker) o;
        return id.equals(picker.id) &&
                name.equals(picker.name) &&
                activeSince.equals(picker.activeSince);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, activeSince);
    }

    @Override
    public String toString() {
        return "Picker{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", activeSince=" + activeSince +
                '}';
    }
}
