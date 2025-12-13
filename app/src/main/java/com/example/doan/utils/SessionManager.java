package com.example.doan.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "edu_connect_session";

    private static final String KEY_UID = "uid";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TOKEN = "token";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ===== SAVE USER =====
    public void saveUser(String uid, String email, String name, String role, String token) {
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // ===== GETTERS =====
    public String getUid() {
        return prefs.getString(KEY_UID, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    // ===== CHECK LOGIN =====
    public boolean isLoggedIn() {
        return getUid() != null && getToken() != null;
    }

    // ===== LOGOUT =====
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
