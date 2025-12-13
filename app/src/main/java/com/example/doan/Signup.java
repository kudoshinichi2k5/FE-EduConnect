package com.example.doan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.RegisterRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtConfirm;
    private Button btnSignup;
    private TextView tvLogin;

    private FirebaseAuth firebaseAuth;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // ===== ÁNH XẠ =====
        edtName = findViewById(R.id.etFullName);
        edtEmail = findViewById(R.id.signupemail);
        edtPassword = findViewById(R.id.signuppassword);
        edtConfirm = findViewById(R.id.signupcfpassword);
        btnSignup = findViewById(R.id.signup_button);
        tvLogin = findViewById(R.id.login);

        firebaseAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getClient().create(ApiService.class);

        btnSignup.setOnClickListener(v -> signup());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void signup() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();
        String confirm = edtConfirm.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // ===== 1️⃣ CREATE USER FIREBASE =====
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) return;

                    // ===== 2️⃣ REGISTER PROFILE BACKEND =====
                    RegisterRequest request =
                            new RegisterRequest(user.getUid(), email, name);

                    apiService.register(request).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(Signup.this,
                                        "Đăng ký thành công, vui lòng đăng nhập",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(Signup.this,
                                        "Lỗi backend: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(Signup.this,
                                    "Không kết nối được server",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Đăng ký thất bại: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}
