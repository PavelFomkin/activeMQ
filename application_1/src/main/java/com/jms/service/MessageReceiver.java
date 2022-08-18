package com.jms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);

    @JmsListener(destination = "${jms.replyChanel}")
    public void receiveMessage(String message) {
        LOGGER.info("Reply received: \"{}\"", message);
    }
}
