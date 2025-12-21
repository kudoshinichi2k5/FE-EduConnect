package com.example.doan;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doan.Login; // Import màn hình Login
import com.example.doan.R;

public class SettingsFragment extends Fragment {

    // Khai báo View
    private TextView tvName, tvEmail;
    private TextView btnEditProfile, btnChangePass, btnLanguage, btnAbout;
    private Switch switchNotif;
    private Button btnLogout;
    private ImageView imgAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(view);
        loadUserData();
        setupEvents();

        return view;
    }

    private void initViews(View view) {
        tvName = view.findViewById(R.id.tvSettingName);
        tvEmail = view.findViewById(R.id.tvSettingEmail);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePass = view.findViewById(R.id.btnChangePassword);
        btnLanguage = view.findViewById(R.id.btnLanguage);
        btnAbout = view.findViewById(R.id.btnAbout);

        switchNotif = view.findViewById(R.id.switchNotification);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    // 1. LẤY THÔNG TIN USER TỪ SHAREDPREF HIỆN LÊN GIAO DIỆN
    private void loadUserData() {
        if (getActivity() == null) return;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Lấy tên và email (Giá trị mặc định là chuỗi rỗng nếu chưa có)
        String name = sharedPreferences.getString("USERNAME", "Người dùng");
        String email = sharedPreferences.getString("EMAIL", "user@email.com");

        tvName.setText(name);
        tvEmail.setText(email);

        // (Tùy chọn) Nếu bạn có lưu link Avatar thì dùng Glide load vào imgAvatar ở đây
    }

    private void setupEvents() {

        // Nút chỉnh sửa thông tin
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
            // Sau này bạn StartActivity sang màn hình EditProfileActivity tại đây
        });

        // Nút đổi ngôn ngữ
        btnLanguage.setOnClickListener(v -> showLanguageDialog());

        // Nút thông báo
        switchNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) Toast.makeText(getContext(), "Đã BẬT thông báo", Toast.LENGTH_SHORT).show();
            else Toast.makeText(getContext(), "Đã TẮT thông báo", Toast.LENGTH_SHORT).show();
        });

        // NÚT ĐĂNG XUẤT (QUAN TRỌNG)
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    // Hộp thoại chọn ngôn ngữ
    private void showLanguageDialog() {
        String[] languages = {"Tiếng Việt", "English", "日本語"};

        new AlertDialog.Builder(getContext())
                .setTitle("Chọn ngôn ngữ")
                .setItems(languages, (dialog, which) -> {
                    Toast.makeText(getContext(), "Đã chọn: " + languages[which], Toast.LENGTH_SHORT).show();
                    // Code xử lý đổi Locale ở đây (phức tạp hơn, cần restart app)
                })
                .show();
    }

    // Xác nhận đăng xuất
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Xử lý Logic đăng xuất thật
    private void performLogout() {
        if (getActivity() == null) return;

        // 1. Xóa session trong SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa sạch dữ liệu
        editor.apply();

        // 2. Chuyển về màn hình Login
        Intent intent = new Intent(getActivity(), Login.class);
        // Cờ này để xóa hết các Activity cũ, ngăn người dùng bấm Back để quay lại
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }
}