package com.example.doan; // <--- ĐỔI TÊN PACKAGE CỦA BẠN Ở ĐÂY

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    // Khai báo biến
    private EditText etUsername, etPassword;
    private Button btnLogin, btnGoogle;
    private TextView tvForgotPass, tvRegister;

    // Firebase & Google
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Đảm bảo tên file layout đúng là activity_login

//        // 1. Khởi tạo Firebase
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//        // 2. Cấu hình Google Sign In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id)) // Lấy từ google-services.json
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        // 3. Ánh xạ View
//        mapping();
//
//        // 4. Sự kiện nút Đăng nhập
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username = etUsername.getText().toString().trim();
//                String password = etPassword.getText().toString().trim();
//
//                if (username.isEmpty() || password.isEmpty()) {
//                    Toast.makeText(Login.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                loginWithUsername(username, password);
//            }
//        });
//
//        // 5. Sự kiện nút Google
//        btnGoogle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });
//
//        // 6. Chuyển sang Đăng ký
//        tvRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Login.this, Signup.class);
//                startActivity(intent);
//            }
//        });
//
//        // 7. Chuyển sang Quên mật khẩu
//        tvForgotPass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Login.this, ForgotPassword.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    // Hàm ánh xạ ID từ XML sang Java
//    private void mapping() {
//        etUsername = findViewById(R.id.email_username); // ID trong XML
//        etPassword = findViewById(R.id.loginpassword);
//        btnLogin = findViewById(R.id.login_button);
//        btnGoogle = findViewById(R.id.btnGoogle);
//        tvForgotPass = findViewById(R.id.forgot_password);
//        tvRegister = findViewById(R.id.sign_up);
//    }
//
//    // Logic Đăng nhập bằng Username
//    private void loginWithUsername(String username, String password) {
//        // Bước 1: Tìm Email từ Username trong Firestore
//        db.collection("users").document(username).get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            // Bước 2: Tìm thấy Email -> Đăng nhập vào Firebase Auth
//                            String email = document.getString("email");
//                            performFirebaseAuth(email, password);
//                        } else {
//                            Toast.makeText(Login.this, "Tên đăng nhập không tồn tại!", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(Login.this, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    // Logic Đăng nhập vào Firebase Auth
//    private void performFirebaseAuth(String email, String password) {
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(Login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
//                        // Chuyển sang màn hình chính
//                        Intent intent = new Intent(Login.this, MainActivity.class);
//                        startActivity(intent);
//                        finish(); // Đóng màn hình Login lại
//                    } else {
//                        Toast.makeText(Login.this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    // Nhận kết quả từ Google Sign In
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Login thành công, giờ xác thực với Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account.getIdToken());
//            } catch (ApiException e) {
//                Toast.makeText(this, "Lỗi Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void firebaseAuthWithGoogle(String idToken) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(Login.this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(Login.this, MainActivity.class));
//                        finish();
//                    } else {
//                        Toast.makeText(Login.this, "Lỗi Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }
}