package com.example.doan;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageUtils {

    // Lưu ngôn ngữ đã chọn vào bộ nhớ
    public static void saveLanguage(Context context, String langCode) {
        SharedPreferences prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        prefs.edit().putString("APP_LANG", langCode).apply();
    }

    // Lấy ngôn ngữ đang lưu (mặc định là tiếng Việt "vi")
    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        return prefs.getString("APP_LANG", "vi");
    }

    // Áp dụng ngôn ngữ cho Context (quan trọng để đổi tiếng toàn app)
    public static void loadLocale(Context context) {
        String langCode = getLanguage(context);
        setLocale(context, langCode);
    }

    public static void setLocale(Context context, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}