package com.jms.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;
import java.util.Properties;

@Slf4j
@Configuration
@EnableTransactionManagement
public class KafkaConfig {

    @Bean
    public KafkaProducer<String, String> kafkaProducer(@Value("${kafka.bootstrap.server}") String server) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my_transactional_id");
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        producer.initTransactions();
        return producer;
    }

    @Bean
    public KafkaConsumer<String, String> kafkaConsumer(@Value("${kafka.bootstrap.server}") String server,
                                                       @Value("${kafka.group.id}") String groupId,
                                                       @Value("${kafka.request.topic}") String topic) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "30000");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }
}
