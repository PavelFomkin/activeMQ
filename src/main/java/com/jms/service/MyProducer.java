package com.jms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyProducer {
    @Autowired
    private final KafkaProducer<String, String> producer;
    @Value("${kafka.request.topic}")
    private String topic;

    public void send(String message) {
        ProducerRecord<String,String> record = new ProducerRecord<>(topic, message);
        producer.send(record, (recordMetadata, e) -> {
            if (e == null) {
                log.info("Message is sent. Topic: {}, Partition: {}, Offset: {}, Timestamp: {}",
                        recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());
            } else {
                log.error("Error: " + e.getMessage());
            }
        });
        producer.flush();
    }
}
