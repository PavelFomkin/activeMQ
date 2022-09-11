package com.jms.tasks;


import com.jms.dto.Employee;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Task4Test {
    public static final String INPUT_TOPIC = "input_topic";
    public static final String OUTPUT_TOPIC = "output_topic";

    @InjectMocks
    private Task4 task4;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(task4, "topic_6", INPUT_TOPIC);
    }

    @Test
    public void successTest() {
        // GIVEN
        final Properties streamsProps = new Properties();
        streamsProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "unit-test");
        streamsProps.put("schema.registry.url", "mock://test");
        String message = "{\"name\":\"John\",\"company\":\"EPAM\",\"position\":\"developer\",\"experience\":5}";
        String expectedResult = "Employee(name=John, company=EPAM, position=developer, experience=5)";

        final StreamsBuilder builder = new StreamsBuilder();
        final Serde<Employee> employeeSerde = task4.employeeSerde();
        KStream<String, Employee> stream = task4.employeeStream(builder, employeeSerde);

        // additional topic to see the result
        stream.to(OUTPUT_TOPIC, Produced.with(Serdes.String(), employeeSerde));
        try (final TopologyTestDriver testDriver = new TopologyTestDriver(builder.build(), streamsProps)) {
            final TestInputTopic<String, String> inputTopic =
                    testDriver.createInputTopic(INPUT_TOPIC, Serdes.String().serializer(), Serdes.String().serializer());
            final TestOutputTopic<String, Employee> outputTopic =
                    testDriver.createOutputTopic(OUTPUT_TOPIC, Serdes.String().deserializer(), employeeSerde.deserializer());

            // WHEN
            inputTopic.pipeInput(null, message);
            Employee employee = outputTopic.readValue();

            // THEN
            assertEquals(expectedResult, employee.toString());
        }
    }

    @Test
    public void failTest_notJsonInput() {
        // GIVEN
        final Properties streamsProps = new Properties();
        streamsProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "unit-test");
        streamsProps.put("schema.registry.url", "mock://test");
        String message = "Wrong input message";

        final StreamsBuilder builder = new StreamsBuilder();
        final Serde<Employee> employeeSerde = task4.employeeSerde();
        task4.employeeStream(builder, employeeSerde);

        try (final TopologyTestDriver testDriver = new TopologyTestDriver(builder.build(), streamsProps)) {
            final TestInputTopic<String, String> inputTopic =
                    testDriver.createInputTopic(INPUT_TOPIC, Serdes.String().serializer(), Serdes.String().serializer());

            // WHEN/THEN
            assertThrows(StreamsException.class, () -> {
                inputTopic.pipeInput(null, message);
            });
        }
    }
}
