package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.LoginResponse;
import com.example.doan.model.RegisterRequest;
import com.example.doan.utils.SessionManager;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnGoogle;
    private TextView tvSignup, tvForgot;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private ApiService apiService;

    // ===== GOOGLE SIGN-IN RESULT =====
    private ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            Task<GoogleSignInAccount> task =
                                    GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                GoogleSignInAccount account =
                                        task.getResult(ApiException.class);
                                firebaseAuthWithGoogle(account.getIdToken());
                            } catch (ApiException e) {
                                Toast.makeText(this,
                                        "Google Sign-In thất bại",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getClient().create(ApiService.class);

        // ===== GOOGLE CONFIG =====
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        initViews();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.loginEmail);
        edtPassword = findViewById(R.id.loginpassword);
        btnLogin = findViewById(R.id.login_button);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvSignup = findViewById(R.id.sign_up);
        tvForgot = findViewById(R.id.forgot_password);

        btnLogin.setOnClickListener(v -> loginEmailPassword());

        btnGoogle.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            googleLauncher.launch(intent);
        });

        tvSignup.setOnClickListener(v ->
                startActivity(new Intent(this, Signup.class)));

        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPassword.class)));
    }

    // ================= EMAIL / PASSWORD =================
    private void loginEmailPassword() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Vui lòng nhập đầy đủ email và mật khẩu",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) return;

                    user.getIdToken(true)
                            .addOnSuccessListener(tokenResult ->
                                    callBackendLogin(tokenResult.getToken()));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Đăng nhập thất bại: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    // ================= GOOGLE AUTH =================
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential =
                GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) return;

                    user.getIdToken(true)
                            .addOnSuccessListener(tokenResult ->
                                    handleGoogleBackendFlow(
                                            user,
                                            tokenResult.getToken()
                                    ));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Firebase Google Auth thất bại",
                                Toast.LENGTH_SHORT).show());
    }

    // ================= GOOGLE → BACKEND =================
    private void handleGoogleBackendFlow(FirebaseUser firebaseUser,
                                         String firebaseToken) {

        String bearerToken = "Bearer " + firebaseToken;

        apiService.login(bearerToken).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    saveSessionAndGoMain(
                            response.body().getUser(),
                            bearerToken
                    );
                }
                else if (response.code() == 404) {
                    // 🔥 CHƯA CÓ PROFILE → TẠO
                    RegisterRequest req = new RegisterRequest(
                            firebaseUser.getUid(),
                            firebaseUser.getEmail(),
                            firebaseUser.getDisplayName()
                    );

                    apiService.register(req).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call,
                                               Response<Void> res) {
                            if (res.isSuccessful()) {
                                callBackendLogin(firebaseToken);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(Login.this,
                                    "Không tạo được profile",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(Login.this,
                        "Không kết nối được server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= BACKEND LOGIN =================
    private void callBackendLogin(String firebaseToken) {
        String bearerToken = "Bearer " + firebaseToken;

        apiService.login(bearerToken).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveSessionAndGoMain(
                            response.body().getUser(),
                            bearerToken
                    );
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(Login.this,
                        "Không kết nối được server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSessionAndGoMain(LoginResponse.User user,
                                      String token) {

        SessionManager session = new SessionManager(this);
        session.saveUser(
                user.getUid(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                token
        );

        // ✅ THÊM THÔNG BÁO
        Toast.makeText(
                Login.this,
                "Đăng nhập thành công: " + user.getUsername(),
                Toast.LENGTH_SHORT
        ).show();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
