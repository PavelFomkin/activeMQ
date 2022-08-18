package com.jms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

    @Value("${jms.replyChanel}")
    private String replyChanel;

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "${jms.requestChanel}")
    public void receiveMessage(String message) {
        LOGGER.info("Message received is: {}", message);

        message += ". yes, you are right.";

        jmsTemplate.convertAndSend(replyChanel, message);
        LOGGER.info("Message \"{}\" sent to: {}", message, replyChanel);
    }
}
