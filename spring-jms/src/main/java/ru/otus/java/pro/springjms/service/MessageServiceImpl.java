package ru.otus.java.pro.springjms.service;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.otus.java.pro.springjms.model.Message;

import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final JmsTemplate jmsTemplate;

    public MessageServiceImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String text) {
        Message message = new Message(UUID.randomUUID(), text);
        jmsTemplate.convertAndSend("messageQueue", message);
    }
}