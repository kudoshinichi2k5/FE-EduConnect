package com.example.doan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

// THÊM CÁC IMPORT NÀY
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView; // Import thêm CardView cho nút camera

import com.bumptech.glide.Glide; // Import Glide
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class EditProfileActivity extends BaseActivity {

    private ImageView btnBack, imgAvatarEdit;
    private TextInputEditText edtFullName, edtPhone, edtEmail, edtDob;
    private AutoCompleteTextView dropdownGender;
    private Button btnUpdate;
    // Thêm khai báo cho nút bấm camera
    private CardView btnChangeAvatar;

    // Uri để lưu đường dẫn ảnh vừa chọn
    private Uri selectedImageUri = null;

    // --- KHỞI TẠO BỘ LẮNG NGHE KẾT QUẢ CHỌN ẢNH ---
    // Đây là cách mới thay cho startActivityForResult đã cũ
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // Lấy đường dẫn ảnh (Uri) người dùng đã chọn
                    selectedImageUri = result.getData().getData();

                    if (selectedImageUri != null) {
                        // Dùng Glide để load ảnh đó vào ImageView và cắt tròn
                        Glide.with(this)
                                .load(selectedImageUri)
                                .circleCrop() // Tự động cắt ảnh thành hình tròn
                                .into(imgAvatarEdit);
                    }
                }
            }
    );
    // --------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        setupGenderDropdown();
        loadUserData();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgAvatarEdit = findViewById(R.id.imgAvatarEdit); // Ánh xạ ImageView avatar
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar); // Ánh xạ nút camera mới

        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtDob = findViewById(R.id.edtDob);
        dropdownGender = findViewById(R.id.dropdownGender);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void setupGenderDropdown() {
        String[] genders = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        dropdownGender.setAdapter(adapter);
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        String name = sharedPreferences.getString("USERNAME", "");
        String email = sharedPreferences.getString("EMAIL", "");
        String phone = sharedPreferences.getString("PHONE", "");
        String dob = sharedPreferences.getString("DOB", "");
        String gender = sharedPreferences.getString("GENDER", "");

        // TODO: Nếu bạn có lưu đường dẫn ảnh cũ trong SharedPreferences, hãy load nó ở đây bằng Glide
        // Ví dụ: String avatarUriStr = sharedPreferences.getString("AVATAR_URI", "");
        // if (!avatarUriStr.isEmpty()) { Glide.with(this).load(Uri.parse(avatarUriStr)).circleCrop().into(imgAvatarEdit); }

        edtFullName.setText(name);
        edtEmail.setText(email);
        edtPhone.setText(phone);
        edtDob.setText(dob);
        if (!gender.isEmpty()) {
            dropdownGender.setText(gender, false);
        }
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        edtDob.setOnClickListener(v -> showDatePicker());
        btnUpdate.setOnClickListener(v -> saveUserData());

        // --- SỰ KIỆN BẤM NÚT CAMERA ---
        btnChangeAvatar.setOnClickListener(v -> openGallery());
    }

    // Hàm mở thư viện ảnh
    private void openGallery() {
        // Tạo Intent để mở thư viện ảnh
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Khởi chạy bộ lắng nghe đã tạo ở trên
        pickImageLauncher.launch(intent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            edtDob.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void saveUserData() {
        String newName = edtFullName.getText().toString().trim();
        String newPhone = edtPhone.getText().toString().trim();
        String newDob = edtDob.getText().toString().trim();
        String newGender = dropdownGender.getText().toString();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Họ tên không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("USERNAME", newName);
        editor.putString("PHONE", newPhone);
        editor.putString("DOB", newDob);
        editor.putString("GENDER", newGender);

        // --- LƯU Ý QUAN TRỌNG VỀ ẢNH ---
        // Hiện tại code này chỉ hiển thị ảnh lên cho đẹp.
        // Để LƯU lại ảnh này lâu dài, bạn cần lưu cái `selectedImageUri.toString()` vào SharedPreferences
        // HOẶC (tốt hơn) là upload ảnh này lên Server/Firebase Storage.
        if (selectedImageUri != null) {
            // Tạm thời lưu URI cục bộ (chỉ hoạt động trên máy này)
            editor.putString("AVATAR_URI", selectedImageUri.toString());
        }

        editor.apply();

        Toast.makeText(this, "Đã cập nhật thông tin!", Toast.LENGTH_SHORT).show();
        finish();
    }
}