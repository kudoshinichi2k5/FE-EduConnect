package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.LoginResponse;
import com.example.doan.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvSignup, tvForgot;

    private FirebaseAuth firebaseAuth;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        SessionManager sessionManager = new SessionManager(this);
//
//        // ✅ Kiểm tra auto login NGAY LẬP TỨC
//        if (sessionManager.isLoggedIn()) {
//            startActivity(new Intent(Login.this, MainActivity.class));
//            finish();
//            return;
//        }

        setContentView(R.layout.activity_login);

        // ===== ÁNH XẠ VIEW =====
        edtEmail = findViewById(R.id.loginEmail);
        edtPassword = findViewById(R.id.loginpassword);
        btnLogin = findViewById(R.id.login_button);
        tvSignup = findViewById(R.id.sign_up);
        tvForgot = findViewById(R.id.forgot_password);

        // ===== INIT FIREBASE =====
        firebaseAuth = FirebaseAuth.getInstance();

        // ===== INIT API =====
        apiService = ApiClient.getClient().create(ApiService.class);

        // ===== LOGIN =====
        btnLogin.setOnClickListener(v -> login());

        // ===== CHUYỂN SANG SIGNUP =====
        tvSignup.setOnClickListener(v ->
                startActivity(new Intent(Login.this, Signup.class)));

        // ===== QUÊN MẬT KHẨU =====
        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(Login.this, ForgotPassword.class)));
    }

    private void login() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Vui lòng nhập đầy đủ email và mật khẩu",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // ===== 1️⃣ LOGIN FIREBASE =====
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) return;

                    // ===== 2️⃣ LẤY FIREBASE ID TOKEN =====
                    user.getIdToken(true)
                            .addOnSuccessListener(result -> {
                                String token = result.getToken();
                                callBackendLogin(token);
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Đăng nhập thất bại: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void callBackendLogin(String firebaseToken) {
        String bearerToken = "Bearer " + firebaseToken;

        apiService.login(bearerToken).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse.User user = response.body().getUser();

                    // ===== SAVE SESSION =====
                    SessionManager sessionManager = new SessionManager(Login.this);
                    sessionManager.saveUser(
                            user.getUid(),
                            user.getEmail(),
                            user.getUsername(),
                            user.getRole(),
                            bearerToken
                    );

                    Toast.makeText(Login.this,
                            "Xin chào " + user.getUsername(),
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();

                } else if (response.code() == 404) {

                    Toast.makeText(Login.this,
                            "Tài khoản chưa tạo profile, vui lòng đăng ký",
                            Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(Login.this,
                            "Lỗi backend: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN_API", t.getMessage());
                Toast.makeText(Login.this,
                        "Không kết nối được server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
