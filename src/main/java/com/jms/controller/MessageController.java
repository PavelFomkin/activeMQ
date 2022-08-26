package com.jms.controller;

import com.jms.entity.FailedMessage;
import com.jms.service.FailedMessageStorage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MessageController {
    @Autowired
    private FailedMessageStorage storage;
    @Autowired
    private AmqpTemplate template;

    @PostMapping("/message")
    public ResponseEntity<Void> sendToQueue(@RequestBody String message) {
        template.convertAndSend("my.exchange", "message.test", message);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/messages/failed")
    public ResponseEntity<?> sendToQueue(@RequestParam(value = "id") Integer id) {
        if (id == null || !storage.exists(id)) {
            return ResponseEntity.badRequest().body("Invalid id");
        }

        FailedMessage failedMessage = storage.findById(id);
        storage.removeById(failedMessage.getId());
        template.convertAndSend(failedMessage.getExchange(), failedMessage.getRootingKey(), failedMessage.getPayload());
        System.out.println("Message with id " + id + " removed from failed queue and resent.");
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/messages/failed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FailedMessage>> getFailedMessages() {
        return ResponseEntity.ok(storage.findAll());
    }
}
