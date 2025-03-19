package ru.otus.java.pro.serialization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SmsData {
    @JsonProperty("chat_sessions")
    private List<ChatSession> chatSessions;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatSession {
        @JsonProperty("chat_id")
        private int chatId;

        @JsonProperty("chat_identifier")
        private String chatIdentifier;

        @JsonProperty("members")
        private List<Member> members;

        @JsonProperty("messages")
        private List<Message> messages;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Member {
        @JsonProperty("first")
        private String firstName;

        @JsonProperty("last")
        private String lastName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        @JsonProperty("belong_number")
        private String belongNumber;

        @JsonProperty("send_date")
        private String sendDate;

        @JsonProperty("text")
        private String text;
    }
}