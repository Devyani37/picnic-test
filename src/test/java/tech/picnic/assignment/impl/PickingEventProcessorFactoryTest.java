package tech.picnic.assignment.impl;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import tech.picnic.assignment.api.EventProcessorFactory;
import tech.picnic.assignment.api.StreamProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

final class PickingEventProcessorFactoryTest {

    @ParameterizedTest
    @MethodSource("happyPathTestCaseInputProvider")
    void testHappyPath(
            int maxEvents,
            Duration maxTime,
            String inputResource,
            String expectedOutputResource)
            throws IOException, JSONException {
        try (EventProcessorFactory factory = new PickingEventProcessorFactory();
             StreamProcessor processor = factory.createProcessor(maxEvents, maxTime);
             InputStream source = getClass().getResourceAsStream(inputResource);
             ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
            processor.process(source, sink);
            String expectedOutput = loadResource(expectedOutputResource);
            String actualOutput = new String(sink.toByteArray(), StandardCharsets.UTF_8);
            JSONAssert.assertEquals(expectedOutput, actualOutput, JSONCompareMode.STRICT);
        }
    }

    static Stream<Arguments> happyPathTestCaseInputProvider() {
        return Stream.of(
                Arguments.of(
                        100,
                        Duration.ofSeconds(30),
                        "happy-path-input.json-stream",
                        "happy-path-output.json"),

                //Check : Pickers are sorted chronologically (ascending) by their active_since timestamp, breaking ties by ID.
                Arguments.of(
                        100,
                        Duration.ofSeconds(30),
                        "input-same-active-since-for-two-pickers.json-stream",
                        "output-same-active-since-for-two-pickers-sortedBy-pickersId.json"),

                //Check : if maxEvent is 1 then give only 1 result
                Arguments.of(
                        1,
                        Duration.ofSeconds(30),
                        "happy-path-input.json-stream",
                        "output-only-one-result.json"),

                //Check : Result is properly sorted if two picker has same name but different Id
                Arguments.of(
                        100,
                        Duration.ofSeconds(30),
                        "input-pickers-same-name-but-different-Id.json-stream",
                        "output-pickers-same-name-but-different-Id.json"),

                //Check : Result must only includes ambient picks
                Arguments.of(
                        100,
                        Duration.ofSeconds(30),
                        "input-both-ambient-chilled-picks.json-stream",
                        "happy-path-output.json"),

                // Check : If pick items are only chilled then return empty list
                Arguments.of(
                        100,
                        Duration.ofSeconds(10),
                        "input-contains-only-chilled-items.json-stream",
                        "output-zero-result.json"));


    }

    @Test
    void maxEventSizeZeroShouldReturnEmptyString() throws IOException {
        try (EventProcessorFactory factory = new PickingEventProcessorFactory();
             StreamProcessor processor = factory.createProcessor(0, Duration.ofSeconds(30));
             InputStream source = getClass().getResourceAsStream("happy-path-input.json-stream");
             ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
            processor.process(source, sink);
            String actualOutput = new String(sink.toByteArray(), StandardCharsets.UTF_8);
            assertEquals("[]", actualOutput);
        }
    }

    @Test
    void maxRuntimeZeroShouldReturnEmptyString() throws IOException {
        try (EventProcessorFactory factory = new PickingEventProcessorFactory();
             StreamProcessor processor = factory.createProcessor(100, Duration.ofMillis(0));
             InputStream source = getClass().getResourceAsStream("happy-path-input.json-stream");
             ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
            processor.process(source, sink);
            String actualOutput = new String(sink.toByteArray(), StandardCharsets.UTF_8);
            assertEquals("[]", actualOutput);
        }
    }

    @Test
    void maxRuntimeAndMaxEventZeroShouldReturnEmptyString() throws IOException {
        try (EventProcessorFactory factory = new PickingEventProcessorFactory();
             StreamProcessor processor = factory.createProcessor(0, Duration.ofMillis(0));
             InputStream source = getClass().getResourceAsStream("happy-path-input.json-stream");
             ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
            processor.process(source, sink);
            String actualOutput = new String(sink.toByteArray(), StandardCharsets.UTF_8);
            assertEquals("[]", actualOutput);
        }
    }

    @Test
    void testEmptyInputStream() throws IOException {
        try (EventProcessorFactory factory = new PickingEventProcessorFactory();
             StreamProcessor processor = factory.createProcessor(100, Duration.ofSeconds(30));
             InputStream source = getClass().getResourceAsStream("empty-input.json-stream");
             ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
            processor.process(source, sink);
            String actualOutput = new String(sink.toByteArray(), StandardCharsets.UTF_8);
            assertEquals("[]", actualOutput);
        }
    }

    private String loadResource(String resource) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resource);
             Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    /**
     * Verifies that precisely one {@link EventProcessorFactory} can be service-loaded.
     */
    @Test
    void testServiceLoading() {
        Iterator<EventProcessorFactory> factories =
                ServiceLoader.load(EventProcessorFactory.class).iterator();
        assertTrue(factories.hasNext(), "No EventProcessorFactory is service-loaded");
        factories.next();
        assertFalse(factories.hasNext(), "More than one EventProcessorFactory is service-loaded");
    }

}
