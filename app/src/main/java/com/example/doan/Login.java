package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
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

// ============================================================================
// MÀN HÌNH ĐĂNG NHẬP (LOGIN ACTIVITY)
// Chức năng chính:
// 1. Đăng nhập bằng Email/Password (Qua Firebase Auth)
// 2. Đăng nhập bằng Google (Qua Google Sign-In & Firebase)
// 3. Đồng bộ dữ liệu với Backend Server (API)
// ============================================================================
public class Login extends AppCompatActivity {

    // Khai báo biến giao diện
    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnGoogle;
    private TextView tvSignup, tvForgot;

    // Khai báo biến xử lý logic
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private ApiService apiService;

    // ========================================================================
    // 1. XỬ LÝ KẾT QUẢ TRẢ VỀ TỪ GOOGLE (LAUNCHER)
    // ========================================================================
    // Khi người dùng chọn tài khoản Google xong, kết quả sẽ trả về đây
    private ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            Task<GoogleSignInAccount> task =
                                    GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                // Lấy được tài khoản Google thành công
                                GoogleSignInAccount account =
                                        task.getResult(ApiException.class);
                                // Chuyển tiếp sang bước xác thực với Firebase
                                firebaseAuthWithGoogle(account.getIdToken());
                            } catch (ApiException e) {
                                Toast.makeText(this, "Google Sign-In thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

    // ========================================================================
    // 2. KHỞI TẠO (ON CREATE)
    // ========================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo các dịch vụ
        firebaseAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getClient().create(ApiService.class);

        // Cấu hình đăng nhập Google
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id)) // Cần ID này để lấy Token
                        .requestEmail()
                        .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Ánh xạ giao diện và sự kiện
        initViews();
    }

    // ========================================================================
    // 3. ÁNH XẠ VIEW & BẮT SỰ KIỆN (SETUP UI)
    // ========================================================================
    private void initViews() {
        edtEmail = findViewById(R.id.loginEmail);
        edtPassword = findViewById(R.id.loginpassword);
        btnLogin = findViewById(R.id.login_button);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvSignup = findViewById(R.id.sign_up);
        tvForgot = findViewById(R.id.forgot_password);

        // Sự kiện: Bấm nút Đăng nhập thường
        btnLogin.setOnClickListener(v -> loginEmailPassword());

        // Sự kiện: Bấm nút Đăng nhập Google
        btnGoogle.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            googleLauncher.launch(intent);
        });

        // Sự kiện: Chuyển màn hình Đăng ký & Quên mật khẩu
        tvSignup.setOnClickListener(v -> startActivity(new Intent(this, Signup.class)));
        tvForgot.setOnClickListener(v -> startActivity(new Intent(this, ForgotPassword.class)));
    }

    // ========================================================================
    // 4. LOGIC ĐĂNG NHẬP EMAIL / PASSWORD
    // ========================================================================
    private void loginEmailPassword() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Bước 1: Kiểm tra rỗng
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bước 2: Gửi lên Firebase Auth để kiểm tra
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) return;

                    // Bước 3: Lấy ID Token từ Firebase để gửi cho Backend
                    user.getIdToken(true).addOnSuccessListener(tokenResult ->
                            callBackendLogin(tokenResult.getToken()));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // ========================================================================
    // 5. LOGIC ĐĂNG NHẬP GOOGLE (FIREBASE AUTH)
    // ========================================================================
    private void firebaseAuthWithGoogle(String idToken) {
        // Tạo chứng thực từ Token Google
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        // Đăng nhập vào Firebase bằng chứng thực này
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) return;

                    // Lấy ID Token mới nhất từ Firebase
                    user.getIdToken(true).addOnSuccessListener(tokenResult ->
                            handleGoogleBackendFlow(user, tokenResult.getToken()));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Firebase Google Auth thất bại", Toast.LENGTH_SHORT).show());
    }

    // ========================================================================
    // 6. XỬ LÝ BACKEND (GOOGLE FLOW) - PHỨC TẠP NHẤT
    // Logic: Login thử -> Nếu lỗi 404 (chưa có acc) -> Register -> Login lại
    // ========================================================================
    private void handleGoogleBackendFlow(FirebaseUser firebaseUser, String firebaseToken) {
        String bearerToken = "Bearer " + firebaseToken;

        // Gọi API Login
        apiService.login(bearerToken).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // TRƯỜNG HỢP 1: Đã có tài khoản -> Đăng nhập thành công
                if (response.isSuccessful() && response.body() != null) {
                    saveSessionAndGoMain(response.body().getUser(), bearerToken);
                }
                // TRƯỜNG HỢP 2: Chưa có tài khoản (Lỗi 404) -> Tự động Đăng ký
                else if (response.code() == 404) {
                    RegisterRequest req = new RegisterRequest(
                            firebaseUser.getUid(),
                            firebaseUser.getEmail(),
                            firebaseUser.getDisplayName()
                    );
                    // Gọi API Đăng ký
                    apiService.register(req).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> res) {
                            if (res.isSuccessful()) {
                                // Đăng ký xong -> Gọi lại hàm Login lần nữa
                                callBackendLogin(firebaseToken);
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(Login.this, "Không tạo được profile", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(Login.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========================================================================
    // 7. XỬ LÝ BACKEND (NORMAL FLOW) - GỌI API LOGIN
    // ========================================================================
    private void callBackendLogin(String firebaseToken) {
        String bearerToken = "Bearer " + firebaseToken;

        apiService.login(bearerToken).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveSessionAndGoMain(response.body().getUser(), bearerToken);
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(Login.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========================================================================
    // 8. LƯU DỮ LIỆU & CHUYỂN MÀN HÌNH (FINAL STEP)
    // ========================================================================
    private void saveSessionAndGoMain(LoginResponse.User user, String token) {

        String finalName = "Bạn"; // Giá trị mặc định phòng hờ

        // --- BƯỚC A: TÍNH TOÁN TÊN HIỂN THỊ (Logic thông minh) ---
        // 1. Ưu tiên lấy Username từ Server API trả về
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            finalName = user.getUsername();
        }
        // 2. Nếu server rỗng, thử lấy DisplayName từ Google/Firebase
        else {
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fbUser != null && fbUser.getDisplayName() != null && !fbUser.getDisplayName().isEmpty()) {
                finalName = fbUser.getDisplayName();
            }
            // 3. Cuối cùng mới lấy Email cắt ra (ví dụ: nam@gmail.com -> nam)
            else if (user.getEmail() != null && user.getEmail().contains("@")) {
                finalName = user.getEmail().split("@")[0];
            }
        }

        // --- BƯỚC B: LƯU VÀO BỘ NHỚ ---

        // 1. Lưu vào SessionManager (Dùng cho toàn bộ app quản lý phiên)
        SessionManager session = new SessionManager(this);
        session.saveUser(user.getUid(), user.getEmail(), finalName, user.getRole(), token);

        // 2. Lưu vào SharedPreferences (Dùng riêng cho HomeFragment đọc tên hiển thị)
        // Key "UserPrefs" và "USERNAME" phải khớp với bên HomeFragment
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Log.d("LOGIN_DEBUG", "Đang lưu tên: " + finalName); // Log để kiểm tra
        editor.putString("USERNAME", finalName);
        String emailToSave = (user.getEmail() != null) ? user.getEmail() : "";
        editor.putString("EMAIL", emailToSave);
        editor.apply();

        // --- BƯỚC C: CHUYỂN TRANG ---
        Toast.makeText(Login.this, "Xin chào: " + finalName, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, Home.class);
        // Cờ này để xóa Login khỏi lịch sử, bấm Back không quay lại Login được
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }
}