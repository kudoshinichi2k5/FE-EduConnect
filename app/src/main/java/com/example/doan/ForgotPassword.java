package com.example.doan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends BaseActivity {

    private EditText edtEmail;
    private Button btnSubmit;
    private TextView tvBack;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.etForgotEmail);
        btnSubmit = findViewById(R.id.btnForgotSubmit);
        tvBack = findViewById(R.id.tvBackToLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSubmit.setOnClickListener(v -> resetPassword());
        tvBack.setOnClickListener(v -> finish());
    }

    private void resetPassword() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this,
                    "Vui lòng nhập email",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                "Đã gửi email đặt lại mật khẩu",
                                Toast.LENGTH_LONG).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Lỗi: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}
