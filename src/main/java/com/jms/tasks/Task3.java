package com.jms.tasks;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Configuration
public class Task3 {

    @Value("${kafka.stream.topic.4}")
    private String topic_4;

    @Value("${kafka.stream.topic.5}")
    private String topic_5;

    @Bean
    @Qualifier("stream_3_1")
    public KStream<Long, String> firstStream(StreamsBuilder kStreamBuilder) {
        KStream<String, String> stream = kStreamBuilder.stream(topic_4, Consumed.with(Serdes.String(), Serdes.String()));
        return stream.filter((key, value) -> Objects.nonNull(value) && value.contains(":"))
                .map((key, value) -> retrieveKey(value))
                .peek((key, value) -> log.warn("[task3 topic1] + { key: {}, value: {} }", key, value));
    }

    @Bean
    @Qualifier("stream_3_2")
    public KStream<Long, String> secondStream(StreamsBuilder kStreamBuilder) {
        KStream<String, String> stream = kStreamBuilder.stream(topic_5, Consumed.with(Serdes.String(), Serdes.String()));
        return stream.filter((key, value) -> Objects.nonNull(value) && value.contains(":"))
                .map((key, value) -> retrieveKey(value))
                .peek((key, value) -> log.warn("[task3 topic2] + { key: {}, value: {} }", key, value));

    }

    @Bean
    public KStream<Long, String> joinedStream(
            @Qualifier("stream_3_1") KStream<Long, String> firstStream,
            @Qualifier("stream_3_2") KStream<Long, String> secondStream) {
        return firstStream.join(secondStream,
                        (value1, value2) -> value1 + " + " + value2,
                        JoinWindows.ofTimeDifferenceAndGrace(Duration.ofMinutes(1), Duration.ofSeconds(30)),
                        StreamJoined.with(Serdes.Long(), Serdes.String(), Serdes.String()))
                .peek((key, value) -> log.warn("[task3 joinedTopic] { key: {}, value: {} }", key, value));
    }

    private KeyValue<Long, String> retrieveKey(String value) {
        int index = value.indexOf(":");
        return KeyValue.pair(
                Long.valueOf(value.substring(0, index)),
                value.substring(index + 1));
    };
}
