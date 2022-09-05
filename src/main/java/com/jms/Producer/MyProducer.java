package com.jms.Producer;

import com.jms.util.UnstableUtils;
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
        try {
            producer.beginTransaction();
            log.info("STARTED PRODUCER TRANSACTION");

            ProducerRecord<String,String> record = new ProducerRecord<>(topic, message);
            producer.send(record, (recordMetadata, e) -> {
                if (e == null) {
                    log.info("Sent message. Topic: {}, Partition: {}, Offset: {}, Timestamp: {}",
                            recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());
                } else {
                    log.error("Error: " + e.getMessage());
                }
            });
            UnstableUtils.playWithFortuna();

            producer.commitTransaction();
            log.info("COMMITTED PRODUCER TRANSACTION");
        } catch (Exception e) {
            producer.abortTransaction();
            log.info("ABORTED PRODUCER TRANSACTION");
            throw new RuntimeException("Producer error");
        } finally {
            producer.flush();
        }
    }
}
