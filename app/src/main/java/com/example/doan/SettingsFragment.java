package com.example.doan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    // Khai báo View
    private TextView tvName, tvEmail;
    private TextView tvCurrentLang; // Biến hiển thị ngôn ngữ

    // Chỉ giữ lại các nút còn tồn tại trong XML
    private LinearLayout btnEditProfile, btnLanguage, btnAbout, btnLogout;
    private ImageView imgAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(view);
        loadUserData();

        // Cập nhật chữ hiển thị ngôn ngữ (Tiếng Việt / English / 中文)
        updateLanguageText();

        setupEvents();

        return view;
    }

    private void initViews(View view) {
        // Ánh xạ Text & Image
        tvName = view.findViewById(R.id.tvSettingName);
        tvEmail = view.findViewById(R.id.tvSettingEmail);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvCurrentLang = view.findViewById(R.id.tvCurrentLang);

        // Ánh xạ các nút bấm (Đã xóa btnChangePass và switchNotification)
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLanguage = view.findViewById(R.id.btnLanguage);
        btnAbout = view.findViewById(R.id.btnAbout);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        if (getActivity() == null) return;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("USERNAME", "Người dùng");
        String email = sharedPreferences.getString("EMAIL", "user@email.com");
        tvName.setText(name);
        tvEmail.setText(email);
    }

    // Hàm hiển thị ngôn ngữ hiện tại
    private void updateLanguageText() {
        if (getContext() == null) return;
        String langCode = LanguageUtils.getLanguage(getContext());

        switch (langCode) {
            case "en":
                tvCurrentLang.setText("English");
                break;
            case "zh":
                tvCurrentLang.setText("中文");
                break;
            default:
                tvCurrentLang.setText("Tiếng Việt");
                break;
        }
    }

    private void setupEvents() {
        // 1. Chỉnh sửa thông tin
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // 2. Đổi ngôn ngữ
        btnLanguage.setOnClickListener(v -> showLanguageDialog());

        // 3. Về ứng dụng
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });

        // 4. Đăng xuất
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    // --- LOGIC ĐỔI NGÔN NGỮ ---
    private void showLanguageDialog() {
        final String[] languages = {"Tiếng Việt", "English", "中文 (Tiếng Trung)"};

        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.dialog_language_title))
                .setItems(languages, (dialog, which) -> {
                    String selectedCode = "vi";
                    switch (which) {
                        case 0: selectedCode = "vi"; break;
                        case 1: selectedCode = "en"; break;
                        case 2: selectedCode = "zh"; break;
                    }
                    changeAppLanguage(selectedCode);
                })
                .show();
    }

    private void changeAppLanguage(String langCode) {
        if (getContext() == null || getActivity() == null) return;

        LanguageUtils.saveLanguage(getContext(), langCode);
        LanguageUtils.setLocale(getContext(), langCode);

        // Restart về Home để áp dụng ngôn ngữ mới
        Intent intent = new Intent(getActivity(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(getContext(), "Đang đổi ngôn ngữ...", Toast.LENGTH_SHORT).show();
    }

    // --- LOGIC ĐĂNG XUẤT ---
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.dialog_logout_title))
                .setMessage(getString(R.string.dialog_logout_message))
                .setPositiveButton(getString(R.string.btn_confirm), (dialog, which) -> performLogout())
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void performLogout() {
        if (getActivity() == null) return;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(getContext(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
    }
}