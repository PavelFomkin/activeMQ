package com.jms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyConsumer {
    @Autowired
    private final KafkaConsumer<String, String> consumer;

    public List<String> getMessages() {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        consumer.commitSync(); // at-most-once guarantee

        List<String> messages = new ArrayList<>();
        for (ConsumerRecord<String, String> record : records) {
            log.info("Partition: {}, Key: {}, Value: {}, Offset: {}", record.partition(), record.key(), record.value(), record.offset());
            messages.add(record.value());
        }
        return messages;
    }
}
