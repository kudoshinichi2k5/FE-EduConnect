package com.example.doan;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText etEmail;
    Button btnSubmit;
    TextView tvBack;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etForgotEmail);
        btnSubmit = findViewById(R.id.btnForgotSubmit);
        tvBack = findViewById(R.id.tvBackToLogin);

        // Xử lý nút quay lại
        tvBack.setOnClickListener(v -> finish());

        // Xử lý nút Gửi yêu cầu
        btnSubmit.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPassword.this, "Vui lòng nhập Email!", Toast.LENGTH_SHORT).show();
                return;
            }

            resetPassword(email);
        });
    }

    private void resetPassword(String email) {
        btnSubmit.setEnabled(false); // Khóa nút để tránh bấm nhiều lần

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    btnSubmit.setEnabled(true); // Mở lại nút
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPassword.this, "Đã gửi link khôi phục vào Email của bạn. Hãy kiểm tra hộp thư!", Toast.LENGTH_LONG).show();
                        finish(); // Đóng màn hình này, quay về Login
                    } else {
                        Toast.makeText(ForgotPassword.this, "Lỗi: Email không tồn tại hoặc sai định dạng.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}