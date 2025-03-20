package ru.otus.java.pro.springjms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.java.pro.springjms.service.MessageService;

@RestController
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam String text) {
        messageService.sendMessage(text);
        return "Message sent!";
    }
}