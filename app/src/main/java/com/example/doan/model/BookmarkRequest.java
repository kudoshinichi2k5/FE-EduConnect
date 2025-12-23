package com.example.doan.model;

public class BookmarkRequest {
    private String MaNguoiDung;
    private String TargetId;
    private String TargetType;

    public BookmarkRequest(String maNguoiDung, String targetId, String targetType) {
        MaNguoiDung = maNguoiDung;
        TargetId = targetId;
        TargetType = targetType;
    }
}
