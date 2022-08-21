package com.jms.service;

import com.jms.entity.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jms.*;

@Service
public class MessageSenderServiceImpl implements MessageSenderService, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderServiceImpl.class);

    @Value("${jms.topic-name}")
    private String topicName;

    @Autowired
    private ConnectionFactory connectionFactory;
    private MessageSender messageSender;

    @Override
    public void sendTextMessage(String message) {
        messageSender.sendTextMessage(message);
        LOGGER.info("Message \"{}\" sent to: {}", message, topicName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Connection senderConnection = connectionFactory.createConnection();
        senderConnection.start();

        Session senderSession = senderConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topicDestination = senderSession.createTopic(topicName);
        MessageProducer producer = senderSession.createProducer(topicDestination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        messageSender = new MessageSender(producer, senderSession);
    }
}
