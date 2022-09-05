package com.jms.producer;

import com.jms.dto.CarCoordinates;
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
public class CarCoordinatesKafkaProducer {
    @Autowired
    private final KafkaProducer<String, CarCoordinates> producer;
    @Value("${kafka.topic}")
    private String topic;

    public void send(CarCoordinates carCoordinates) {
        try {
            ProducerRecord<String, CarCoordinates> record = new ProducerRecord<>(topic, carCoordinates.getNumberplate(), carCoordinates);
            producer.send(record, (recordMetadata, e) -> {
                if (e == null) {
                    log.info("Sent message. Topic: {}, Partition: {}, Offset: {}, Timestamp: {}",
                            recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());
                } else {
                    log.error("Error: " + e.getMessage());
                }
            });
        } finally {
            producer.flush();
        }
    }
}
