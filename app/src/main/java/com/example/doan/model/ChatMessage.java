package com.example.doan.model;

public class ChatMessage {
    private String message;
    private boolean isUser; // true: Người dùng, false: Bot

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() { return message; }
    public boolean isUser() { return isUser; }
}