package com.jms.controller;

import com.jms.service.MessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @Autowired
    private MessageSenderService messageSenderService;

    @PostMapping("/message")
    public ResponseEntity<Void> publishMessage(@RequestParam(value = "msg", defaultValue = "no message") String msg) {
        messageSenderService.sendTextMessage(msg);
        return ResponseEntity.ok().build();
    }
}
