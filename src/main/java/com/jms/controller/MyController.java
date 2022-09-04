package com.jms.controller;

import com.jms.service.MyConsumer;
import com.jms.service.MyProducer;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyController {
    @Autowired
    private MyProducer producer;
    @Autowired
    private MyConsumer consumer;

    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@RequestBody String message) {
        if (Strings.isBlank(message)) {
            return ResponseEntity.badRequest().build();
        }

        producer.send(message);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages")
    public ResponseEntity<List<String>> getMessages() {
        return ResponseEntity.ok(consumer.getMessages());
    }
}
