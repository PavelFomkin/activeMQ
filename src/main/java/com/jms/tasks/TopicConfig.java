package com.jms.tasks;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {
    @Bean
    public NewTopic topic_1(@Value("${kafka.stream.topic.1}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }
    @Bean
    public NewTopic topic_2(@Value("${kafka.stream.topic.2}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }
    @Bean
    public NewTopic topic_3(@Value("${kafka.stream.topic.3}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }
    @Bean
    public NewTopic topic_4(@Value("${kafka.stream.topic.4}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }
    @Bean
    public NewTopic topic_5(@Value("${kafka.stream.topic.5}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }
    @Bean
    public NewTopic topic_6(@Value("${kafka.stream.topic.6}") String topic) {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }
}
