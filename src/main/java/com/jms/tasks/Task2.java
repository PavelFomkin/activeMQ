package com.jms.tasks;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class Task2 {

    @Value("${kafka.stream.topic.3}")
    private String topic_3;

    @Bean
    @Qualifier("splitStreams")
    public Map<String, KStream<Integer, String>> splitStreams(StreamsBuilder kStreamBuilder) {
        KStream<String, String> stream = kStreamBuilder.stream(topic_3, Consumed.with(Serdes.String(), Serdes.String()));
        return stream
                .filter((key, value) -> Objects.nonNull(value)) // could be null?
                .filterNot((key, value) -> value.isEmpty())
                .flatMap((key, value) -> Arrays.stream(value.split(" "))
                            .map(str -> KeyValue.pair(str.length(), str))
                            .collect(Collectors.toList()))
                .peek((key, value) -> log.warn("[task2] { key: {}, value: {} }", key, value))
                .split(Named.as("words-"))
                .branch((key, value) -> key < 10, Branched.as("short"))
                .defaultBranch(Branched.as("long"));
    }

    @Bean
    public KStream<Integer, String> mergeStreams(@Qualifier("splitStreams") Map<String, KStream<Integer, String>> splitStreams) {
        KStream<Integer, String> shortWords = splitStreams.get("words-short").filter((key, value) -> value.contains("a"));
        KStream<Integer, String> longWords = splitStreams.get("words-long").filter((key, value) -> value.contains("a"));

        return shortWords.merge(longWords)
                .peek((key, value) -> log.warn("[task2 mergedStreams] { key: {}, value: {} }", key, value));
    }
}
