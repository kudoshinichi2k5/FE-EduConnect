package com.example.doan.model;

/**
 * Model nhận response từ API /user/login
 */
public class LoginResponse {

    private String message;
    private User user;

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        private String uid;
        private String email;
        private String username;
        private String role;
        private String school;
        private String avatar;

        public String getUid() {
            return uid;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }

        public String getSchool() {
            return school;
        }

        public String getAvatar() {
            return avatar;
        }
    }
}
