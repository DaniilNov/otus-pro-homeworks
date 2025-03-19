package ru.otus.java.pro.serialization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplifiedChatSession {
    private String chatIdentifier;
    private String memberLastName;
    private String belongNumber;
    private String sendDate;
    private String text;
}