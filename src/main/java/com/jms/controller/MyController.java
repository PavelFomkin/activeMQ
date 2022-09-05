package com.jms.controller;

import com.jms.consumer.MyConsumer;
import com.jms.Producer.MyProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<?> handleError(RuntimeException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
