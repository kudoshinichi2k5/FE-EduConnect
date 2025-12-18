package com.example.doan.model;
import com.google.gson.annotations.SerializedName;

public class Opportunity {

    @SerializedName("MaTinTuc")
    private String maTinTuc;

    @SerializedName("Title")
    private String title;

    @SerializedName("Description")
    private String description;

    @SerializedName("Content_url")
    private String contentUrl;

    @SerializedName("Image_url")
    private String imageUrl;

    @SerializedName("Type")
    private String type;

    @SerializedName("Deadline")
    private String deadline;

    @SerializedName("Created_at")
    private String createdAt;

    // ===== GETTERS =====
    public String getMaTinTuc() { return maTinTuc; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContentUrl() { return contentUrl; }
    public String getImageUrl() { return imageUrl; }
    public String getType() { return type; }
    public String getDeadline() { return deadline; }
    public String getCreatedAt() { return createdAt; }
}