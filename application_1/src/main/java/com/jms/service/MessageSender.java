package com.jms.service;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

    @Value("${jms.topic-name}")
    private String topicName;

    @Autowired
    private JmsTemplate jmsTemplate;

    @SneakyThrows
    public void sendMessage(String message) {
        jmsTemplate.convertAndSend(topicName, message);

        LOGGER.info("Message \"{}\" sent to: {}", message, topicName);
    }
}
