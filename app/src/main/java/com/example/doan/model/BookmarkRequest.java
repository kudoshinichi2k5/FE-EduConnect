package com.example.doan.model;

import com.google.gson.annotations.SerializedName;

public class BookmarkRequest {

    @SerializedName("MaNguoiDung")
    private String MaNguoiDung;

    @SerializedName("TargetId")
    private String TargetId;

    @SerializedName("TargetType")
    private String TargetType;

    // Constructor với 3 params
    public BookmarkRequest(String maNguoiDung, String targetId, String targetType) {
        this.MaNguoiDung = maNguoiDung;
        this.TargetId = targetId;
        this.TargetType = targetType;
    }

    // Constructor với 2 params (backward compatible cho code cũ)
    public BookmarkRequest(String maNguoiDung, String targetId) {
        this.MaNguoiDung = maNguoiDung;
        this.TargetId = targetId;
        this.TargetType = "opportunity"; // Default
    }

    // Getters
    public String getMaNguoiDung() {
        return MaNguoiDung;
    }

    public String getTargetId() {
        return TargetId;
    }

    public String getTargetType() {
        return TargetType;
    }

    // Setters
    public void setMaNguoiDung(String maNguoiDung) {
        MaNguoiDung = maNguoiDung;
    }

    public void setTargetId(String targetId) {
        TargetId = targetId;
    }

    public void setTargetType(String targetType) {
        TargetType = targetType;
    }
}