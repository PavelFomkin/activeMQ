package com.jms.service;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jms.*;

@Service
public class MessageListenerManager implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerManager.class);

    @Value("${jms.consumerName}")
    private String consumerName;

    @Autowired
    private ConnectionFactory connectionFactory;

    private Connection consumerConnection;

    @SneakyThrows
    public void setMessageListener(MessageListener listener) {
        if (consumerConnection == null) {
            consumerConnection = connectionFactory.createConnection();
            consumerConnection.start();
        }

        Session session = consumerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue sessionQueue = session.createQueue(consumerName);
        MessageConsumer consumer = session.createConsumer(sessionQueue);
        consumer.setMessageListener(listener);
    }

    /**
     * Set up default listeners
     *
     * @throws Exception in case of any exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        setMessageListener(message -> {
            try {
                LOGGER.info("[1 listener] Message received: {}", ((TextMessage)message).getText());
            } catch (JMSException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });

        setMessageListener(message -> {
            try {
                LOGGER.info("[2 listener] Message received: {}", ((TextMessage)message).getText());
            } catch (JMSException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }
}
