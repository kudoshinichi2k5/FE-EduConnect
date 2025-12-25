package com.example.doan;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        // Đây là hàm quan trọng nhất để ép ngôn ngữ ngay từ lúc khởi tạo
        String langCode = LanguageUtils.getLanguage(newBase); // Lấy ngôn ngữ đã lưu
        Context context = updateBaseContextLocale(newBase, langCode);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Không cần gọi loadLocale ở đây nữa vì đã xử lý ở attachBaseContext
    }

    // Hàm hỗ trợ tạo Context mới với ngôn ngữ đã chọn
    private Context updateBaseContextLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}