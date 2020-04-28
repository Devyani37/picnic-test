package tech.picnic.assignment.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO class as per our Output Requirement
 */

public final class Picker {
    private final String id;
    private final String name;
    private final Date activeSince;
    private final List<PickItem> pickItemList = new ArrayList<>();


    public Picker(final String id, final String name, final Date activeSince) {
        this.id = id;
        this.name = name;
        this.activeSince = activeSince;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonProperty("picker_name")
    public String getName() {
        return name;
    }

    @JsonProperty("active_since")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") //Formatting the date and time
    public Date getActiveSince() {
        return activeSince;
    }

    @JsonProperty("picks")
    public List<PickItem> getPickItemList() {
        return pickItemList;
    }

    public static final class PickItem {
        private final String articleName;
        private final Date timestamp;


        public PickItem(final String articleName, final Date timestamp) {
            this.articleName = articleName;
            this.timestamp = timestamp;
        }

        @JsonProperty("article_name")
        public String getArticleName() {
            return articleName;
        }

        @JsonProperty("timestamp")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        public Date getTimestamp() {
            return timestamp;
        }
    }


}