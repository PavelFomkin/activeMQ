package com.jms.service;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.*;

@Service
public class MessageSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

    @Value("${jms.destination}")
    private String destination;
    private Session session;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ConnectionFactory connectionFactory;

    @SneakyThrows
    public void sendMessage(String message) {
        if (session == null) {
            init();
        }

        TextMessage textMessage = session.createTextMessage();
        textMessage.setText(message);
        TemporaryQueue tempQueue = session.createTemporaryQueue();
        textMessage.setJMSReplyTo(tempQueue);
        jmsTemplate.convertAndSend(destination, textMessage);
        LOGGER.info("Message \"{}\" sent to: {}", message, destination);

        Message receive = session.createConsumer(tempQueue).receive();
        String text = ((TextMessage) receive).getText();
        LOGGER.info("Reply message received is: {}", text);
    }

    @SneakyThrows
    private void init() {
        Connection connection = connectionFactory.createConnection();
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }
}
