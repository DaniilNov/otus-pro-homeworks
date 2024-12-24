package ru.otus.java.pro.http;

import lombok.Data;

import java.util.Map;

@Data
public class HttpRequest {
    private String method;
    private String uri;
    private Map<String, String> headers;
    private Map<String, String> parameters;
}