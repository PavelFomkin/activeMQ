package com.jms.service;

import com.jms.entity.FailedMessage;
import lombok.SneakyThrows;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class MessageReceiver {
    private final static String ATTEMPT_NUMBER = "attemptNumber";
    @Value("${rabbitmq.retry.count}")
    private int numberOfAttempts;
    @Value("${rabbitmq.retry.delay}")
    private int delayTime;
    @Autowired
    private AmqpTemplate template;
    @Autowired
    private FailedMessageStorage storage;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "emptyQueue", durable = "true"),
                    key = "message.test",
                    exchange = @Exchange(name = "my.exchange", type = ExchangeTypes.TOPIC)
            )
    )
    public void listenerWithError(Message message) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        if (!validateMessage(payload)) {
            retryRequest(message);
            return;
        }

        // processing... never happens
        System.out.println("[2] listenerWithError: message processed: " + payload);
    }

    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "processedQueue", durable = "true"),
                    key = "message.test",
                    exchange = @Exchange(name = "my.exchange", type = ExchangeTypes.TOPIC)
            )
    )
    public void listener(Message message) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        // processing...
        System.out.println("[1] listener: message processed: " + payload);
    }

    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "failureQueue", durable = "true"),
                    key = "message.failure",
                    exchange = @Exchange(name = "failure-exchange")
            )
    )
    public void failureListener(FailedMessage message) {
        storage.saveMessage(message);
        System.out.println("[3] failureListener: message \"" + message.getPayload() + "\" persisted.");
    }

    @SneakyThrows
    private void retryRequest(Message message) {
        Integer attemptNumber = message.getMessageProperties().getHeader(ATTEMPT_NUMBER);
        attemptNumber = attemptNumber == null ? 1 : ++attemptNumber;
        if (attemptNumber > numberOfAttempts) {
            System.err.println("[2] listenerWithError: Out of retry attempts. Message sent to failed message queue.");

            template.convertAndSend("failure-exchange", "message.failure", FailedMessage.fromMessage(message));
            return;
        }

        System.out.println("[2] listenerWithError: Message could not be processed, sent to retry, attempt " + attemptNumber);
        message.getMessageProperties().setRedelivered(true);
        message.getMessageProperties().setHeader(ATTEMPT_NUMBER, attemptNumber);
        Thread.sleep(delayTime);
        template.convertAndSend(message.getMessageProperties().getReceivedExchange(),
                message.getMessageProperties().getReceivedRoutingKey(),
                message);
    }

    public boolean validateMessage(String payload) {
        return false;
    }
}
