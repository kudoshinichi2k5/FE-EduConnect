package com.example.doan.model;

/**
 * Body gửi lên API /user/register
 */
public class RegisterRequest {

    private String uid;
    private String email;
    private String username;

    public RegisterRequest(String uid, String email, String username) {
        this.uid = uid;
        this.email = email;
        this.username = username;
    }
}
