package com.jms.tasks;

import com.jms.dto.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Objects;

@Slf4j
@Configuration
public class Task4 {

    @Value("${kafka.stream.topic.6}")
    private String topic_6;

    @Bean
    public KStream<String, Employee> employeeStream(StreamsBuilder kStreamBuilder,
                                                   Serde<Employee> customSerde) {
        KStream<String, Employee> stream = kStreamBuilder.stream(topic_6, Consumed.with(Serdes.String(), customSerde));
        stream.filter((key, value) -> Objects.nonNull(value))
                .peek((key, value) -> log.warn("[task4]: {}", value));

        return stream;
    }

    @Bean
    public Serde<Employee> employeeSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Employee.class));
    }
}
