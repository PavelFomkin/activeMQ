package com.jms.entity;

import lombok.*;
import org.springframework.amqp.core.Message;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString (exclude = "id")
public class FailedMessage implements Serializable {
    private Integer id;
    private String exchange;
    private String rootingKey;
    private String payload;
    private String status;

    public static FailedMessage fromMessage(Message message) {
        FailedMessage failedMessage = new FailedMessage();
        failedMessage.setExchange(message.getMessageProperties().getReceivedExchange());
        failedMessage.setRootingKey(message.getMessageProperties().getReceivedRoutingKey());
        failedMessage.setPayload(new String(message.getBody(), StandardCharsets.UTF_8));
        return failedMessage;
    }
}
