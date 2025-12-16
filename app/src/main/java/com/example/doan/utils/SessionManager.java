package com.example.doan.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "edu_connect_session";

    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_UID = "uid";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TOKEN = "firebase_token";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ===== SAVE USER SESSION =====
    public void saveUser(String uid,
                         String email,
                         String username,
                         String role,
                         String token) {

        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // ===== CHECK LOGIN =====
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // ===== GETTERS =====
    public String getUid() {
        return prefs.getString(KEY_UID, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    // Kiểm tra đã có profile backend chưa
    public boolean hasProfile() {
        return prefs.getString(KEY_UID, null) != null;
    }

    // ===== CLEAR SESSION (LOGOUT) =====
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
