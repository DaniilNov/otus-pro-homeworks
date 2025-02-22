package ru.otus.java.pro.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        SmsData smsData = objectMapper.readValue(new File("src/main/resources/sms.json"), SmsData.class);

        List<SimplifiedChatSession> simplifiedChatSessions = getSimplifiedChatSessions(smsData);

        Map<String, List<SimplifiedChatSession>> groupedMessages = simplifiedChatSessions.stream()
                .collect(Collectors.groupingBy(SimplifiedChatSession::getBelongNumber));

        groupedMessages.forEach((key, value) -> value.sort(Comparator.comparing(SimplifiedChatSession::getSendDate)));

        List<SimplifiedChatSession> uniqueMessages = groupedMessages.values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(new File("output.json"), uniqueMessages);

        String jsonOutput = objectMapper.writeValueAsString(uniqueMessages);
        System.out.println("JSON Output:");
        System.out.println(jsonOutput);

        XmlMapper xmlMapper = new XmlMapper();
        String xmlOutput = xmlMapper.writeValueAsString(uniqueMessages);
        System.out.println("XML Output:");
        System.out.println(xmlOutput);

        Files.write(Paths.get("output.xml"), xmlOutput.getBytes());
    }

    private static List<SimplifiedChatSession> getSimplifiedChatSessions(SmsData smsData) {
        List<SimplifiedChatSession> simplifiedChatSessions = new ArrayList<>();
        for (SmsData.ChatSession chatSession : smsData.getChatSessions()) {
            String chatIdentifier = chatSession.getChatIdentifier();
            String memberLastName = chatSession.getMembers().get(0).getLastName();
            for (SmsData.Message message : chatSession.getMessages()) {
                SimplifiedChatSession simplifiedChatSession = new SimplifiedChatSession();
                simplifiedChatSession.setChatIdentifier(chatIdentifier);
                simplifiedChatSession.setMemberLastName(memberLastName);
                simplifiedChatSession.setBelongNumber(message.getBelongNumber());
                simplifiedChatSession.setSendDate(message.getSendDate());
                simplifiedChatSession.setText(message.getText());
                simplifiedChatSessions.add(simplifiedChatSession);
            }
        }

        return simplifiedChatSessions;
    }
}