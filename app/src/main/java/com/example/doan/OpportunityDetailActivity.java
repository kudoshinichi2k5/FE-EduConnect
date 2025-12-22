// java
package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.BookmarkCheckResponse;
import com.example.doan.model.BookmarkRequest;
import com.example.doan.model.Opportunity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpportunityDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvDate, tvDeadline, tvDesc;
    Button btnRegister;
    ImageView ivBack;

    ImageView ivBookmark;
    boolean isBookmarked = false;

    private String maNguoiDung;

    String maTinTuc;
    String linkUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity_detail);

        mapping();

        // 1. Nhận MaTinTuc từ Intent trước khi gọi API/check status
        maTinTuc = getIntent().getStringExtra("MA_TIN_TUC");
        if (maTinTuc == null || maTinTuc.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy dữ liệu!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Lấy user id
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        maNguoiDung = sp.getString("USER_ID", "");

        // 3. Kiểm tra bookmark (bây giờ maTinTuc đã có)
        checkBookmarkStatus(maNguoiDung, maTinTuc);

        // 4. Đăng ký listener duy nhất cho bookmark -> gọi API
        ivBookmark.setOnClickListener(v -> toggleBookmark());

        // 5. Gọi API lấy chi tiết
        fetchOpportunityDetail(maTinTuc);

        // 6. Back
        ivBack.setOnClickListener(v -> finish());

        // 7. Đăng ký ngay
        btnRegister.setOnClickListener(v -> {
            if (linkUrl != null && !linkUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "Không tìm thấy link đăng ký!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchOpportunityDetail(String id) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getOpportunityById(id)
                .enqueue(new Callback<Opportunity>() {
                    @Override
                    public void onResponse(@NonNull Call<Opportunity> call, @NonNull Response<Opportunity> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Opportunity item = response.body();

                            tvTitle.setText(item.getTitle());
                            tvDesc.setText(item.getDescription());
                            tvDate.setText("Ngày đăng: " + item.getCreatedAt());
                            tvDeadline.setText("Hạn chót: " + item.getDeadline());

                            linkUrl = item.getContentUrl();
                        } else {
                            Toast.makeText(OpportunityDetailActivity.this, "Không tải được chi tiết", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Opportunity> call, @NonNull Throwable t) {
                        Toast.makeText(OpportunityDetailActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkBookmarkStatus(String maNguoiDung, String maTinTuc) {
        if (maNguoiDung == null || maNguoiDung.isEmpty() || maTinTuc == null || maTinTuc.isEmpty()) {
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.checkBookmark(maNguoiDung, maTinTuc)
                .enqueue(new Callback<BookmarkCheckResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BookmarkCheckResponse> call, @NonNull Response<BookmarkCheckResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            isBookmarked = response.body().isBookmarked();
                            ivBookmark.setImageResource(isBookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BookmarkCheckResponse> call, @NonNull Throwable t) { }
                });
    }

    private void toggleBookmark() {
        if (maNguoiDung == null || maNguoiDung.isEmpty() || maTinTuc == null || maTinTuc.isEmpty()) {
            Toast.makeText(this, "Thiếu thông tin người dùng hoặc mục", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        BookmarkRequest request = new BookmarkRequest(maNguoiDung, maTinTuc);

        if (!isBookmarked) {
            apiService.addBookmark(request).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isBookmarked = true;
                        ivBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) { }
            });
        } else {
            apiService.removeBookmark(request).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        isBookmarked = false;
                        ivBookmark.setImageResource(R.drawable.ic_bookmark_border);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) { }
            });
        }
    }

    private void mapping() {
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvDate = findViewById(R.id.tvDetailDate);
        tvDeadline = findViewById(R.id.tvDetailDeadline);
        tvDesc = findViewById(R.id.tvDetailDesc);
        btnRegister = findViewById(R.id.btnRegister);
        ivBack = findViewById(R.id.ivBack);
        ivBookmark = findViewById(R.id.ivBookmark);
    }
}