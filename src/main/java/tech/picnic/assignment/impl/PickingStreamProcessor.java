package tech.picnic.assignment.impl;

import tech.picnic.assignment.api.StreamProcessor;
import tech.picnic.assignment.dtos.Picker;
import tech.picnic.assignment.models.Event;
import tech.picnic.assignment.models.TemperatureZone;
import tech.picnic.assignment.utils.AppConfigUtils;
import tech.picnic.assignment.utils.JsonUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Picking Event Stream Processor Class
 */
public class PickingStreamProcessor implements StreamProcessor {

    private final int maxEvents;
    private final Duration maxTime;
    private final static Logger LOGGER = Logger.getLogger(PickingStreamProcessor.class.getName());

    public PickingStreamProcessor(final int maxEvents, final Duration maxTime) {
        this.maxEvents = maxEvents;
        this.maxTime = Objects.requireNonNull(maxTime, "MaxTime should not be null");

    }

    /**
     * This function used to process input stream and after performing several operations like deserialization
     * and serialization of data, filtering and sorting as per the requirement and providing the desired output.
     *
     * @param source The source of data to be processed.
     * @param sink   The sink to which the processing result is sent.
     * @throws IOException
     */

    @Override
    public void process(InputStream source, OutputStream sink) throws IOException {
        if (Objects.isNull(source) || Objects.isNull(sink)) {
            throw new IllegalArgumentException("I/O streams should not be null.");
        }
        List<String> jsonEventList = doProcess(source);
        List<Event> filteredEvents = convertToEventAndFilter(jsonEventList);
        List<Picker> pickers = toPickers(filteredEvents);
        List<Picker> sortedPickers = sortPickerAndPicks(pickers);

        //Serialization of Result and writing to output stream
        String output = JsonUtils.serialize(sortedPickers);
        writeToOutputStream(output, sink);
    }

    /**
     * This Function read the input stream line by line with respect to maxTime and maxEvent whichever comes first.
     * BufferedReader.readLine() is a blocking operation , so to overcome this, we read the stream in a
     * separate thread to handle the maxTime operation with TimeOut.
     *
     * @param source InputStream
     * @return List<String>
     * @throws IOException
     */
    private List<String> doProcess(final InputStream source) throws IOException {
        List<String> eventList = new ArrayList<>();

        try {
            CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(source, StandardCharsets.UTF_8))) {
                    String line;
                    long endTime = System.currentTimeMillis() + maxTime.toMillis();
                    // Here extra check for maxTime to avoid edge case e.g. maxTime = 0 millis
                    while (System.currentTimeMillis() < endTime
                            && eventList.size() < maxEvents
                            && (line = reader.readLine()) != null) {

                        line = line.trim();

                        if (!line.isEmpty()) {
                            eventList.add(line);
                        }
                    }
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }).orTimeout(maxTime.toMillis(), TimeUnit.MILLISECONDS).get(); //NOTE: Keeping track of overall timeOut.
        } catch (Exception e) {
            if (!(e.getCause() instanceof TimeoutException)) {
                throw new IOException("Exception Occurred during async stream processing ", e);
            }
            //Adding log as reached the time limit.
            LOGGER.log(Level.FINE, "Maximum Time limit reached");
        }

        return eventList;
    }


    /**
     * This function takes the raw event Lists and filter it according to Temperature zone (excluding chilled articles).
     * And return list of events into deserialized form (Event model).
     *
     * @param jsonEventList - List of Events in JSON.
     * @return List<Event> - List of Filterd Events.
     * @throws IOException
     */
    private List<Event> convertToEventAndFilter(final List<String> jsonEventList) throws IOException {
        List<Event> eventList = new ArrayList<>();
        Set<TemperatureZone> excludedTemperatureZones = AppConfigUtils.getExcludedTemperatureZoneConfig();

        for (String json : jsonEventList) {
            Event event = JsonUtils.deserialize(json, Event.class);

            if (!excludedTemperatureZones.contains(event.getArticle().getTemperatureZone())) {
                eventList.add(event);
            }
        }
        return eventList;
    }

    /**
     * Group Events per Picker and returns List of Picker(dto).
     *
     * @param eventList - List of filtered Event.
     * @return List<Picker> - List of Picker (dto)
     */

    private List<Picker> toPickers(final List<Event> eventList) {

        Map<String, Picker> groupedByPickerId = new HashMap<>();

        for (Event event : eventList) {
            String pickerId = event.getPicker().getId();
            //Here taking care of upperCasing of articles.
            Picker.PickItem pickItem = new Picker.PickItem(event.getArticle().getName().toUpperCase(), event.getTimestamp());

            if (groupedByPickerId.containsKey(pickerId)) {
                groupedByPickerId.get(pickerId).getPickItemList().add(pickItem);
            } else {
                Picker picker = new Picker(pickerId, event.getPicker().getName(), event.getPicker().getActiveSince());
                picker.getPickItemList().add(pickItem);
                groupedByPickerId.put(pickerId, picker);
            }
        }

        return new ArrayList<>(groupedByPickerId.values());
    }

    /**
     * This function takes List of Picker(dto) and sort Pickers chronologically(ascending) based on activeSince timestamp,
     * breaking ties by ID. It also sorts items picked by pickers chronologically(ascending) based on timestamp.
     * Then returns the serialized value of the sorted collection.
     *
     * @param pickers List of Picker(Dto)
     * @return List<Picker> - List of Sorted Picker (dto)
     */
    private List<Picker> sortPickerAndPicks(final List<Picker> pickers) {
        //Sorting of Picker object firstly by active since and secondly by picker id
        pickers.sort(Comparator.comparing(Picker::getActiveSince).thenComparing(Picker::getId));

        //Sorting Picker's pick as per the timestamp
        for (Picker picker : pickers) {
            picker.getPickItemList().sort(Comparator.comparing(Picker.PickItem::getTimestamp));
        }
        return pickers;
    }

    /**
     * This function take the final output data made in the process function and write it to given output stream.
     *
     * @param data Final data which intended to be written in output stream.
     * @param sink Output stream
     * @throws IOException
     */

    private void writeToOutputStream(final String data, final OutputStream sink) throws IOException {
        if (Objects.isNull(data) || data.isEmpty()) {
            sink.write(new byte[0]); // To keep output json consistent in case of no result.
        } else {
            sink.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }

}
