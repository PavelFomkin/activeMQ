package com.jms.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    @JmsListener(destination = "myTopic", containerFactory = "jmsContainerFactory")
    public void receiveMessage(String message) {
        System.out.println("[1] Message received is: " + message);
    }
}
