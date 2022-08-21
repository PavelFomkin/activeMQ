package com.jms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Component
public class MessageProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

    @JmsListener(destination = "${jms.destination}")
    public String receiveMessage(Message message) throws JMSException {
        String text = ((TextMessage) message).getText();
        LOGGER.info("Message received is: {}", text);

        return text + " processed.";
    }
}
