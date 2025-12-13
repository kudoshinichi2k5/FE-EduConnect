package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    // Khai báo biến
    EditText tbemail, tbusername, tbpass, tbcfpass;
    Button btnsignup;
    TextView txtlogin;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Khởi tạo Firebase
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//        mapping(); // Ánh xạ
//
//        btnsignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = tbemail.getText().toString().trim();
//                String username = tbusername.getText().toString().trim();
//                String password = tbpass.getText().toString();
//                String cfpassword = tbcfpass.getText().toString();
//
//                // Validate dữ liệu (Giống file mẫu của bạn)
//                if (!validateusername(username)) {
//                    Toast.makeText(Signup.this, "Tên tài khoản phải từ 6 kí tự", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!validateemail(email)) {
//                    Toast.makeText(Signup.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!validatepass(password)) {
//                    Toast.makeText(Signup.this, "Mật khẩu phải từ 6 kí tự", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!password.equals(cfpassword)) {
//                    Toast.makeText(Signup.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Gọi hàm đăng ký Firebase
//                RegisterUser(username, email, password);
//            }
//        });
//
//        txtlogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish(); // Quay lại màn hình Login
//            }
//        });
//    }
//
//    private void mapping() {
//        tbemail = findViewById(R.id.signupemail);
//        tbusername = findViewById(R.id.signupusername);
//        tbpass = findViewById(R.id.signuppassword);
//        tbcfpass = findViewById(R.id.signupcfpassword);
//        btnsignup = findViewById(R.id.signup_button);
//        txtlogin = findViewById(R.id.login);
//    }
//
//    // Các hàm validate (Giữ nguyên style của bạn)
//    private boolean validateemail(String email) {
//        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
//    }
//    private boolean validateusername(String username) {
//        return username.length() >= 6;
//    }
//    private boolean validatepass(String password) {
//        return password.length() >= 6;
//    }
//
//    // Hàm xử lý Đăng ký với Firebase
//    private void RegisterUser(String username, String email, String password) {
//        btnsignup.setEnabled(false); // Chặn bấm liên tục
//
//        // 1. Kiểm tra Username có trùng trong Firestore không
//        db.collection("users").document(username).get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        if (task.getResult().exists()) {
//                            Toast.makeText(Signup.this, "Tên tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
//                            btnsignup.setEnabled(true);
//                        } else {
//                            // 2. Tạo tài khoản Auth
//                            createAuthAccount(username, email, password);
//                        }
//                    } else {
//                        Toast.makeText(Signup.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
//                        btnsignup.setEnabled(true);
//                    }
//                });
//    }
//
//    private void createAuthAccount(String username, String email, String password) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // 3. Lưu thông tin Username vào Firestore
//                        Map<String, Object> userMap = new HashMap<>();
//                        userMap.put("username", username);
//                        userMap.put("email", email);
//
//                        db.collection("users").document(username).set(userMap)
//                                .addOnSuccessListener(unused -> {
//                                    Toast.makeText(Signup.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
//                                    finish(); // Quay về Login
//                                });
//                    } else {
//                        Toast.makeText(Signup.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        btnsignup.setEnabled(true);
//                    }
//                });
    }
}