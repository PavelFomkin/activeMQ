package com.jms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
public class UnstableUtils {
    public static List<String> convertRecords(ConsumerRecords<String, String> records) {
        playWithFortuna();
        return records.partitions().stream()
                .map(records::records)
                .flatMap(Collection::stream)
                .peek(record -> log.info("Received message: Partition: {}, Key: {}, Value: {}, Offset: {}",
                        record.partition(), record.key(), record.value(), record.offset()))
                .map(ConsumerRecord::value)
                .collect(Collectors.toList());
    }

    /**
     * Randomly throw an exception
     * */
    public static void playWithFortuna() {
        if (new Random().nextInt(10) % 2 == 0) {
            throw new RuntimeException("Ooops, something went wrong.");
        }
    }
}
