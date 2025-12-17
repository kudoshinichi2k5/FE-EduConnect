package com.example.doan.model;

public class Opportunity {

    private String MaTinTuc;
    private String Title;
    private String Description;
    private String Content_url;
    private String Image_url;
    private String Type;
    private String Deadline;
    private String Created_at;

    public String getMaTinTuc() {
        return MaTinTuc;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getContentUrl() {
        return Content_url;
    }

    public String getImageUrl() {
        return Image_url;
    }

    public String getType() {
        return Type;
    }

    public String getDeadline() {
        return Deadline;
    }

    public String getCreatedAt() {
        return Created_at;
    }
}
