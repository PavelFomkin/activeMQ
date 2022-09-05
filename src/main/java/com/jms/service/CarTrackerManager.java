package com.jms.service;

import com.jms.dto.CarCoordinates;
import com.jms.entity.CarTracker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CarTrackerManager {
    private final ConcurrentHashMap<String, CarTracker> carDistances = new ConcurrentHashMap<>();

    public void process(ConsumerRecords<String, CarCoordinates> records, int consumerNumber) {
        records.partitions().stream()
                .map(records::records)
                .flatMap(Collection::stream)
                .forEach(record -> {
                    carDistances.putIfAbsent(record.key(), new CarTracker());
                    CarTracker carTracker = carDistances.get(record.key());
                    log.info("[Consumer {}][Partition {}] Received car coordinates. Numberplate: {}, Coordinates: {}",
                            consumerNumber, record.partition(), record.key(), record.value());
                    carTracker.track(record.value().getCoordinates());
                    log.info("Distance is updated: Numberplate: {}, Distance: {}", record.key(), carTracker.getDistance());
                });
    }

    public Long getCarDistances(String numberplate) {
        CarTracker carTracker = carDistances.get(numberplate);
        return carTracker != null ? carTracker.getDistance() : null;
    }
}
