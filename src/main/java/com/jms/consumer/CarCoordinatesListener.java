package com.jms.consumer;

import com.jms.service.CarTrackerManager;
import com.jms.dto.CarCoordinates;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class CarCoordinatesListener {
    @Autowired
    private CarTrackerManager manager;
    @Autowired
    private KafkaConsumer<String, CarCoordinates> consumer_1;
    @Autowired
    private KafkaConsumer<String, CarCoordinates> consumer_2;
    @Autowired
    private KafkaConsumer<String, CarCoordinates> consumer_0;

    @Scheduled(fixedDelay = 1000)
    private void getCoordinatesWithConsumer_0() {
        ConsumerRecords<String, CarCoordinates> records = consumer_0.poll(Duration.ofMillis(100));
        manager.process(records, 0);
    }

    @Scheduled(fixedDelay = 1000)
    private void getCoordinatesWithConsumer_1() {
        ConsumerRecords<String, CarCoordinates> records = consumer_1.poll(Duration.ofMillis(100));
        manager.process(records, 1);
    }

    @Scheduled(fixedDelay = 1000)
    private void getCoordinatesWithConsumer_2() {
        ConsumerRecords<String, CarCoordinates> records = consumer_2.poll(Duration.ofMillis(100));
        manager.process(records, 2);
    }
}
