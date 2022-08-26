package com.jms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class DelayQueueConfig {

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
}
