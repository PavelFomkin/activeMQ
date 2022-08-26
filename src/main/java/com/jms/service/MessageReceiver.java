package com.jms.service;

import com.jms.entity.FailedMessage;
import lombok.SneakyThrows;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class MessageReceiver {
    private final static String ATTEMPT_NUMBER = "attemptNumber";
    @Value("${rabbitmq.retry.count}")
    private int numberOfAttempts;
    @Autowired
    private AmqpTemplate template;
    @Autowired
    private FailedMessageStorage storage;

    @RabbitListener(queues = "emptyQueue")
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
    @RabbitListener(queues = "processedQueue")
    public void listener(Message message) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        // processing...
        Thread.sleep(1000);
        System.out.println("[1] listener: message processed: " + payload);
    }

    @RabbitListener(queues = "failureQueue")
    public void failureListener(FailedMessage failedMessage) {
        failedMessage.setStatus("FAILED");
        storage.saveMessage(failedMessage);
        System.out.println("[3] failureListener: message \"" + failedMessage + "\" persisted.");
    }

    @RabbitListener(queues = "deadLetterQueue")
    public void deadLetterListener(Message message) {
        FailedMessage failedMessage = FailedMessage.fromMessage(message);
        failedMessage.setStatus("DEAD");
        storage.saveMessage(failedMessage);
        System.out.println("[4] deadLetterListener: message \"" + failedMessage + "\" persisted.");
    }

    @SneakyThrows
    private void retryRequest(Message message) {
        Integer attemptNumber = message.getMessageProperties().getHeader(ATTEMPT_NUMBER);
        attemptNumber = attemptNumber == null ? 1 : ++attemptNumber;
        if (attemptNumber > numberOfAttempts) {
            System.err.println("[2] listenerWithError: Out of retry attempts. Message sent to failed message queue.");

            template.convertAndSend("failure.exchange", "message.failure", FailedMessage.fromMessage(message));
            return;
        }

        System.out.println("[2] listenerWithError: Message could not be processed, sent to retry, attempt " + attemptNumber);
        message.getMessageProperties().setRedelivered(true);
        message.getMessageProperties().setHeader(ATTEMPT_NUMBER, attemptNumber);
        template.convertAndSend("delay.exchange", "message.delay", message);
    }

    public boolean validateMessage(String payload) {
        return false;
    }
}
