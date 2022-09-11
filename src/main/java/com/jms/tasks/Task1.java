package com.jms.tasks;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class Task1 {

    @Value("${kafka.stream.topic.1}")
    private String initialTopic;

    @Value("${kafka.stream.topic.2}")
    private String targetTopic;

    @Bean
    public KStream<String, String> initialStream(StreamsBuilder kStreamBuilder) {
        KStream<String, String> stream = kStreamBuilder.stream(initialTopic, Consumed.with(Serdes.String(), Serdes.String()));
        stream.peek((key, value) -> log.warn("[task1 topic1]: {}", value))
                .to(targetTopic);

        return stream;
    }

    @Bean
    public KStream<String, String> targetStream(StreamsBuilder kStreamBuilder) {
        KStream<String, String> stream = kStreamBuilder.stream(targetTopic, Consumed.with(Serdes.String(), Serdes.String()));
        stream.peek((key, value) -> log.warn("[task1 topic2]: {}", value));

        return stream;
    }
}
