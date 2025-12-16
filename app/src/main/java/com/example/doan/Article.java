package com.example.doan;

public class Article {
    String title;
    String description;
    // int imageResId; // Bạn có thể thêm ảnh nếu muốn

    public Article(String title, String description) {
        this.title = title;
        this.description = description;
    }
    // Getter...
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}