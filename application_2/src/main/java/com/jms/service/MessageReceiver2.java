package com.jms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver2.class);

    @JmsListener(destination = "${jms.topic-name}", containerFactory = "jmsDurableContainerFactory")
    public void receiveMessage(String message) {
        LOGGER.info("[Receiver 2] Message received is: {}", message);
    }
}