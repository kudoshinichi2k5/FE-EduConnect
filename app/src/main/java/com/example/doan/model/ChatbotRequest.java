package com.example.doan.model;

public class ChatbotRequest {
    private String question;

    public ChatbotRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}