package com.example.doan;
public class Opportunity {
    private String title;
    private String description;
    private String date; // Ngày đăng
    private String deadline; // Hạn chót (Mới thêm)
    private String link; // Link đăng ký (Mới thêm)

    // Cập nhật Constructor
    public Opportunity(String title, String description, String date, String deadline, String link) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.deadline = deadline;
        this.link = link;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getDeadline() { return deadline; } // Getter mới
    public String getLink() { return link; } // Getter mới
}