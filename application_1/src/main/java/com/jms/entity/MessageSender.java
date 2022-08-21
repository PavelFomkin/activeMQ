package com.jms.entity;

import lombok.SneakyThrows;

import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class MessageSender {
    private final MessageProducer messageProducer;
    private final Session session;

    public MessageSender(MessageProducer messageProducer, Session session) {
        this.messageProducer = messageProducer;
        this.session = session;
    }

    @SneakyThrows
    public void sendTextMessage(String message) {
        TextMessage textMessage = session.createTextMessage(message);
        messageProducer.send(textMessage);
    }
}
