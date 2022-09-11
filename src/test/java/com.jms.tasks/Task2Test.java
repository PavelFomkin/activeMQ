package com.jms.tasks;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Task2Test {
    public static final String INPUT_TOPIC = "input_topic";
    public static final String OUTPUT_TOPIC = "output_topic";

    @InjectMocks
    private Task2 task2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(task2, "topic_3", INPUT_TOPIC);
    }

    @Test
    public void successTest() {
        // GIVEN
        final Properties streamsProps = new Properties();
        streamsProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "unit-test");
        streamsProps.put("schema.registry.url", "mock://test");
        String message = "abcd a1234 a0 long_word_with_a_letter word_without_required_letter";
        String expectedResult = "{2=a0, 4=abcd, 5=a1234, 23=long_word_with_a_letter}";

        final StreamsBuilder builder = new StreamsBuilder();
        Map<String, KStream<Integer, String>> splitStreams = task2.splitStreams(builder);
        KStream<Integer, String> mergeStream = task2.mergeStreams(splitStreams);

        // additional topic to see the result
        mergeStream.to(OUTPUT_TOPIC, Produced.with(Serdes.Integer(), Serdes.String()));
        try (final TopologyTestDriver testDriver = new TopologyTestDriver(builder.build(), streamsProps)) {
            final TestInputTopic<String, String> inputTopic =
                    testDriver.createInputTopic(INPUT_TOPIC, Serdes.String().serializer(), Serdes.String().serializer());
            final TestOutputTopic<Integer, String> outputTopic =
                    testDriver.createOutputTopic(OUTPUT_TOPIC, Serdes.Integer().deserializer(), Serdes.String().deserializer());

            // WHEN
            inputTopic.pipeInput(null, message);
            Map<Integer, String> integerStringMap = outputTopic.readKeyValuesToMap();

            // THEN
            assertEquals(expectedResult, integerStringMap.toString());
        }
    }
}
