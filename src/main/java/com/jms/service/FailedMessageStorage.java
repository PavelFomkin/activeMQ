package com.jms.service;

import com.jms.entity.FailedMessage;
import com.jms.repository.FailedMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FailedMessageStorage {
    @Autowired
    private FailedMessageRepository failedMessageRepository;

    public void saveMessage(FailedMessage failedMessage) {
        failedMessageRepository.save(failedMessage);
    }

    public List<FailedMessage> findAll() {
        return failedMessageRepository.findAll();
    }

    public FailedMessage findById(Integer id) {
        return failedMessageRepository.findById(id);
    }

    public void removeById(Integer id) {
        failedMessageRepository.removeById(id);
    }

    public boolean exists(Integer id) {
        return failedMessageRepository.exists(id);
    }
}
