package com.jms.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class QueuesConfig {

    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange("delay.exchange");
    }

    @Bean
    @Qualifier("delayQueue")
    public Queue delayQueue(@Value("${rabbitmq.retry.delay}") Long delay) {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl", delay);
        arguments.put("x-dead-letter-exchange", "my.exchange");
        arguments.put("x-dead-letter-routing-key", "message.test");
        return new Queue("delayQueue", true, false, false, arguments);
    }

    @Bean
    public Binding bindDelayQueueToDelayExchange(@Autowired @Qualifier("delayQueue") Queue delayQueue) {
        return BindingBuilder.bind(delayQueue).to(delayExchange()).with("message.delay");
    }

    @Bean
    public TopicExchange myExchange() {
        return new TopicExchange("my.exchange");
    }

    @Bean
    @Qualifier("processedQueue")
    public Queue processedQueue(@Value("${rabbitmq.dlq.ttl}") Long ttl,
                                @Value("${rabbitmq.message.max.length}") Long maxLength) {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl", ttl);
        arguments.put("x-dead-letter-exchange", "deadLetter.exchange");
        arguments.put("x-dead-letter-routing-key", "message.dlq");
        arguments.put("x-max-length-bytes", maxLength);
        arguments.put("x-overflow", "reject-publish-dlx");
        return new Queue("processedQueue", true, false, false, arguments);
    }

    @Bean
    @Qualifier("emptyQueue")
    public Queue emptyQueue(@Value("${rabbitmq.dlq.ttl}") Long ttl) {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl", ttl);
        arguments.put("x-dead-letter-exchange", "deadLetter.exchange");
        arguments.put("x-dead-letter-routing-key", "message.dlq");
        return new Queue("emptyQueue", true, false, false, arguments);
    }

    @Bean
    public Binding bindProcessedQueueToMyExchange(@Autowired @Qualifier("processedQueue") Queue processedQueue) {
        return BindingBuilder.bind(processedQueue).to(myExchange()).with("message.test");
    }

    @Bean
    public Binding bindEmptyQueueToMyExchange(@Autowired @Qualifier("emptyQueue") Queue emptyQueue) {
        return BindingBuilder.bind(emptyQueue).to(myExchange()).with("message.test");
    }

    @Bean
    public DirectExchange failureExchange() {
        return new DirectExchange("failure.exchange");
    }

    @Bean
    public Queue failureQueue() {
        return new Queue("failureQueue", true);
    }

    @Bean
    public Binding bindFailureQueueToFailureExchange() {
        return BindingBuilder.bind(failureQueue()).to(failureExchange()).with("message.failure");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("deadLetter.exchange");
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("deadLetterQueue", true);
    }

    @Bean
    public Binding bindDeadLetterQueueToDeadLetterExchange() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with("message.dlq");
    }
}
