package com.example.doan;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// Các thư viện cần thiết
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    // Khai báo View
    private TextView tvName, tvEmail;
    // 🔥 MỚI THÊM: Biến hiển thị ngôn ngữ hiện tại
    private TextView tvCurrentLang;

    private LinearLayout btnEditProfile, btnChangePass, btnLanguage, btnAbout, btnLogout;
    private SwitchCompat switchNotif;
    private ImageView imgAvatar;

    // Biến lưu cài đặt
    private SharedPreferences appSettingsPrefs;

    // Launcher để xin quyền thông báo
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    updateNotificationState(true);
                    sendTestNotification();
                } else {
                    switchNotif.setChecked(false);
                    Toast.makeText(getContext(), getString(R.string.toast_need_permission), Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        if (getActivity() != null) {
            appSettingsPrefs = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        }

        initViews(view);
        loadUserData();
        loadSettingsState();

        // 🔥 MỚI THÊM: Cập nhật chữ hiển thị ngôn ngữ ngay khi mở màn hình
        updateLanguageText();

        setupEvents();

        return view;
    }

    private void initViews(View view) {
        tvName = view.findViewById(R.id.tvSettingName);
        tvEmail = view.findViewById(R.id.tvSettingEmail);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        // 🔥 MỚI THÊM: Ánh xạ View hiển thị ngôn ngữ (ID phải khớp file XML)
        tvCurrentLang = view.findViewById(R.id.tvCurrentLang);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePass = view.findViewById(R.id.btnChangePassword);
        btnLanguage = view.findViewById(R.id.btnLanguage);
        btnAbout = view.findViewById(R.id.btnAbout);
        btnLogout = view.findViewById(R.id.btnLogout);

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

    private void loadSettingsState() {
        if (appSettingsPrefs != null) {
            boolean isEnabled = appSettingsPrefs.getBoolean("NOTIF_ENABLED", true);
            switchNotif.setChecked(isEnabled);
        }
    }

    // 🔥 MỚI THÊM: Hàm kiểm tra và hiển thị tên ngôn ngữ
    private void updateLanguageText() {
        if (getContext() == null) return;
        // Lấy mã ngôn ngữ đang lưu (vi, en, zh)
        String langCode = LanguageUtils.getLanguage(getContext());

        switch (langCode) {
            case "en":
                tvCurrentLang.setText("English");
                break;
            case "zh":
                tvCurrentLang.setText("中文"); // Tiếng Trung
                break;
            default:
                tvCurrentLang.setText("Tiếng Việt");
                break;
        }
    }

    private void setupEvents() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnChangePass.setOnClickListener(v -> Toast.makeText(getContext(), getString(R.string.change_password), Toast.LENGTH_SHORT).show());

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });

        btnLanguage.setOnClickListener(v -> showLanguageDialog());

        switchNotif.setOnClickListener(v -> {
            boolean isChecked = switchNotif.isChecked();
            if (isChecked) {
                checkPermissionAndEnable();
            } else {
                updateNotificationState(false);
                Toast.makeText(getContext(), getString(R.string.toast_noti_off), Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

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

        Intent intent = new Intent(getActivity(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(getContext(), "Đang đổi ngôn ngữ...", Toast.LENGTH_SHORT).show();
    }

    // --- CÁC HÀM CŨ GIỮ NGUYÊN ---

    private void checkPermissionAndEnable() {
        if (getContext() == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                updateNotificationState(true);
                sendTestNotification();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            updateNotificationState(true);
            sendTestNotification();
        }
    }

    private void updateNotificationState(boolean isEnabled) {
        if (appSettingsPrefs != null) {
            appSettingsPrefs.edit().putBoolean("NOTIF_ENABLED", isEnabled).apply();
            switchNotif.setChecked(isEnabled);
        }
    }

    private void sendTestNotification() {
        if (getContext() == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "EDU_CONNECT_CHANNEL",
                    "Thông báo EduConnect",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "EDU_CONNECT_CHANNEL")
                .setSmallIcon(R.drawable.ic_notifications_24)
                .setContentTitle(getString(R.string.toast_noti_on))
                .setContentText("EduConnect notification test.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(1, builder.build());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

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