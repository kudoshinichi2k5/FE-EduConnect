package com.example.doan.model;

public class BookmarkItem {

    private String TargetId;
    private String TargetType;
    private String Saved_at;

    // Opportunity fields
    private String oppTitle;
    private String oppDesc;
    private String oppImage;
    private String oppType;
    private String Deadline;

    // Article fields
    private String articleTitle;
    private String Category;
    private String articleImage;

    // Getters
    public String getTargetId() {
        return TargetId;
    }

    public String getTargetType() {
        return TargetType;
    }

    public String getSaved_at() {
        return Saved_at;
    }

    // Opportunity getters
    public String getOppTitle() {
        return oppTitle;
    }

    public String getOppDesc() {
        return oppDesc;
    }

    public String getOppImage() {
        return oppImage;
    }

    public String getOppType() {
        return oppType;
    }

    public String getDeadline() {
        return Deadline;
    }

    // Article getters
    public String getArticleTitle() {
        return articleTitle;
    }

    public String getCategory() {
        return Category;
    }

    public String getArticleImage() {
        return articleImage;
    }

    // Helper methods để lấy title/description chung
    public String getTitle() {
        if ("opportunity".equals(TargetType)) {
            return oppTitle;
        } else if ("article".equals(TargetType)) {
            return articleTitle;
        }
        return "";
    }

    public String getDescription() {
        if ("opportunity".equals(TargetType)) {
            return oppDesc;
        } else if ("article".equals(TargetType)) {
            return Category; // hoặc một trường description khác
        }
        return "";
    }

    public String getImageUrl() {
        if ("opportunity".equals(TargetType)) {
            return oppImage;
        } else if ("article".equals(TargetType)) {
            return articleImage;
        }
        return "";
    }
}