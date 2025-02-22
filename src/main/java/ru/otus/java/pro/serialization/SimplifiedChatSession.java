package ru.otus.java.pro.serialization;

import lombok.Data;

@Data
public class SimplifiedChatSession {
    private String chatIdentifier;
    private String memberLastName;
    private String belongNumber;
    private String sendDate;
    private String text;
}