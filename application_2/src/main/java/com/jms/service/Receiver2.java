package com.jms.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver2 {

    @JmsListener(destination = "${jms.topic-name}", containerFactory = "jmsContainerFactory")
    public void receiveMessage(String message) {
        System.out.println("[2] Message received is: " + message);
    }
}
