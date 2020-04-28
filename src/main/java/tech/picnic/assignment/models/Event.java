package tech.picnic.assignment.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

public final class Event {
    private final String id;
    private final Date timestamp;
    private final Picker picker;
    private final Article article;
    private final int quantity;

    /**
     * Constructor to create instance of an Event
     *
     * @param id        Id of the Event
     * @param timestamp Timestamp of the Event
     * @param picker    See Picker Class
     * @param article   See Article Class
     * @param quantity  Quantity
     */

    @JsonCreator
    public Event(@JsonProperty("id") final String id,
                 @JsonProperty("timestamp") final Date timestamp,
                 @JsonProperty("picker") final Picker picker,
                 @JsonProperty("article") final Article article,
                 @JsonProperty("quantity") final int quantity) {
        this.id = Objects.requireNonNull(id, "Id should not be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp should not be null");
        this.picker = Objects.requireNonNull(picker, "Picker should not be null");
        this.article = Objects.requireNonNull(article, "Article should not be null");
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Picker getPicker() {
        return picker;
    }

    public Article getArticle() {
        return article;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return quantity == event.quantity &&
                id.equals(event.id) &&
                timestamp.equals(event.timestamp) &&
                picker.equals(event.picker) &&
                article.equals(event.article);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, picker, article, quantity);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", picker=" + picker +
                ", article=" + article +
                ", quantity=" + quantity +
                '}';
    }
}
