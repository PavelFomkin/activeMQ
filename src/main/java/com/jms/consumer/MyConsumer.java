package com.jms.consumer;

import com.jms.util.UnstableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyConsumer {
    @Autowired
    private final KafkaConsumer<String, String> consumer;
    @Autowired
    private final KafkaProducer<String, String> producer;
    @Value("${kafka.request.topic}")
    private String topic;


    public List<String> getMessages() {
        try {
            producer.beginTransaction();
            log.info("STARTED CONSUMER TRANSACTION");

            // Move offsets to committed ones. Without it .poll() method still read from the end.
            // "auto.offset.reset" consumer property and assign to particular partition doesn't help.
            consumer.committed(consumer.assignment()).forEach(consumer::seek);

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            List<String> messages = UnstableUtils.convertRecords(records);

            if (messages.size() > 0) {
                Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
                for (TopicPartition partition : records.partitions()) {
                    List<ConsumerRecord<String, String>> partitionedRecords = records.records(partition);
                    long offset = partitionedRecords.get(partitionedRecords.size() - 1).offset();
                    offsetsToCommit.put(partition, new OffsetAndMetadata(offset + 1));
                }
                producer.sendOffsetsToTransaction(offsetsToCommit, consumer.groupMetadata());
                log.info("COMMITTED CONSUMER OFFSETS");
            }

            producer.commitTransaction();
            log.info("COMMITTED CONSUMER TRANSACTION");

            return messages;
        } catch (Exception e) {
            producer.abortTransaction();
            log.info("ABORTED CONSUMER TRANSACTION");
            throw new RuntimeException("Consumer error", e);
        } finally {
            producer.flush();
        }
    }
}
