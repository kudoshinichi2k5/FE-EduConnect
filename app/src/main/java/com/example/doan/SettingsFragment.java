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
import android.widget.LinearLayout; // 1. QUAN TRỌNG: Dùng LinearLayout cho các nút
import android.widget.TextView;
import android.widget.Toast;

// 2. QUAN TRỌNG: Phải dùng thư viện này cho Switch đẹp
import androidx.appcompat.widget.SwitchCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    // Khai báo View
    private TextView tvName, tvEmail;

    // Các nút chức năng (Giờ là LinearLayout)
    private LinearLayout btnEditProfile, btnChangePass, btnLanguage, btnAbout, btnLogout;

    // Switch (Sửa thành SwitchCompat)
    private SwitchCompat switchNotif;

    private ImageView imgAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nếu thiếu file bg_white_rounded_card.xml thì dòng này sẽ gây crash.
        // Nhớ đảm bảo bạn đã tạo file drawable đó nhé!
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(view);
        loadUserData();
        setupEvents();

        return view;
    }

    private void initViews(View view) {
        // Ánh xạ TextView
        tvName = view.findViewById(R.id.tvSettingName);
        tvEmail = view.findViewById(R.id.tvSettingEmail);

        // Ánh xạ Avatar
        imgAvatar = view.findViewById(R.id.imgAvatar);

        // Ánh xạ các nút LinearLayout
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePass = view.findViewById(R.id.btnChangePassword); // ID trong XML là btnChangePassword
        btnLanguage = view.findViewById(R.id.btnLanguage);
        btnAbout = view.findViewById(R.id.btnAbout);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Ánh xạ Switch
        switchNotif = view.findViewById(R.id.switchNotification);
    }

    private void loadUserData() {
        if (getActivity() == null) return;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("USERNAME", "Người dùng");
        String email = sharedPreferences.getString("EMAIL", "user@email.com");
        tvName.setText(name);
        tvEmail.setText(email);
    }

    private void setupEvents() {
        // Sự kiện click cho các LinearLayout
        btnEditProfile.setOnClickListener(v -> {
            // Chuyển sang màn hình EditProfileActivity
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnChangePass.setOnClickListener(v -> Toast.makeText(getContext(), "Chức năng đổi mật khẩu", Toast.LENGTH_SHORT).show());

        btnAbout.setOnClickListener(v -> Toast.makeText(getContext(), "Phiên bản EduConnect 1.0", Toast.LENGTH_SHORT).show());

        btnLanguage.setOnClickListener(v -> showLanguageDialog());

        switchNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) Toast.makeText(getContext(), "Đã BẬT thông báo", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getContext(), "Đã TẮT thông báo", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLanguageDialog() {
        String[] languages = {"Tiếng Việt", "English", "日本語"};
        new AlertDialog.Builder(getContext())
                .setTitle("Chọn ngôn ngữ")
                .setItems(languages, (dialog, which) ->
                        Toast.makeText(getContext(), "Đã chọn: " + languages[which], Toast.LENGTH_SHORT).show()
                )
                .show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        if (getActivity() == null) return;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }
}