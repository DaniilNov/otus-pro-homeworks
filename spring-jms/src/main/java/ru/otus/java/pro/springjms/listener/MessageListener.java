package ru.otus.java.pro.springjms.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.otus.java.pro.springjms.model.Message;

@Component
public class MessageListener {

    @JmsListener(destination = "messageQueue")
    public void receiveMessage(Message message) {
        System.out.println("Received <" + message + ">");
    }
}