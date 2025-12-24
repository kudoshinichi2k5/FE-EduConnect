package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.BookmarkCheckResponse;
import com.example.doan.model.BookmarkRequest;
import com.example.doan.model.Opportunity;
import com.example.doan.utils.TimeUtils;

import io.noties.markwon.Markwon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpportunityDetailActivity extends AppCompatActivity {

    ImageView ivBack, ivBookmark, ivDetailImage;
    TextView tvTitle, tvTypeBadge, tvDate, tvDeadline, tvContent;
    Button btnRegister;

    String maTinTuc;
    String linkUrl = "";
    String maNguoiDung;
    boolean isBookmarked = false;

    Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity_detail);

        mapping();

        // Init Markwon
        markwon = Markwon.create(this);

        // Get intent data
        maTinTuc = getIntent().getStringExtra("MA_TIN_TUC");
        if (maTinTuc == null || maTinTuc.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get user ID
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        maNguoiDung = sp.getString("USER_ID", "");

        // Load data
        fetchOpportunityDetail(maTinTuc);
        checkBookmarkStatus(maNguoiDung, maTinTuc);

        // Listeners
        ivBack.setOnClickListener(v -> finish());
        ivBookmark.setOnClickListener(v -> toggleBookmark());
        btnRegister.setOnClickListener(v -> openRegistrationLink());
    }

    private void fetchOpportunityDetail(String id) {
        ApiClient.getClient().create(ApiService.class)
                .getOpportunityById(id)
                .enqueue(new Callback<Opportunity>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Opportunity> call,
                            @NonNull Response<Opportunity> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            displayOpportunity(response.body());
                        } else {
                            Toast.makeText(
                                    OpportunityDetailActivity.this,
                                    "Không tải được chi tiết",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Opportunity> call,
                            @NonNull Throwable t
                    ) {
                        Toast.makeText(
                                OpportunityDetailActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void displayOpportunity(Opportunity o) {

        // ===== TITLE =====
        tvTitle.setText(o.getTitle());

        // ===== ẢNH =====
        if (o.getImageUrl() != null && !o.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(o.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.uit)
                    .error(R.drawable.uit)
                    .into(ivDetailImage);
        } else {
            ivDetailImage.setImageResource(R.drawable.uit);
        }

        // ===== TYPE =====
        String type = o.getType();
        if ("scholarship".equalsIgnoreCase(type)) {
            tvTypeBadge.setText("Học bổng");
            tvTypeBadge.setBackgroundResource(R.drawable.badge_scholarship);
        } else if ("contest".equalsIgnoreCase(type)) {
            tvTypeBadge.setText("Cuộc thi");
            tvTypeBadge.setBackgroundResource(R.drawable.badge_contest);
        } else if ("event".equalsIgnoreCase(type)) {
            tvTypeBadge.setText("Sự kiện");
            tvTypeBadge.setBackgroundResource(R.drawable.badge_event);
        } else {
            tvTypeBadge.setText("Khác");
            tvTypeBadge.setBackgroundResource(R.drawable.badge_default);
        }

        // ===== HIỂN THỊ NÚT ĐĂNG KÝ =====
        if ("scholarship".equalsIgnoreCase(o.getType())
                && o.getContentUrl() != null
                && !o.getContentUrl().isEmpty()) {

            btnRegister.setVisibility(View.VISIBLE);
        } else {
            btnRegister.setVisibility(View.GONE);
        }

        // ===== THỜI GIAN =====
        String timeAgo = TimeUtils.formatTimeAgo(o.getCreatedAt());
        tvDate.setText(timeAgo.isEmpty() ? "Vừa xong" : timeAgo);

        // ===== DEADLINE =====
        if (o.getDeadline() != null && !o.getDeadline().isEmpty()) {
            tvDeadline.setText("Hạn: " + formatDeadline(o.getDeadline()));
            tvDeadline.setVisibility(View.VISIBLE);
        } else {
            tvDeadline.setVisibility(View.GONE);
        }

        // ===== MARKDOWN CONTENT =====
        if (o.getDescription() != null && !o.getDescription().isEmpty()) {
            markwon.setMarkdown(tvContent, o.getDescription());
        } else {
            tvContent.setText("Không có nội dung chi tiết.");
        }

        // ===== LINK =====
        linkUrl = o.getContentUrl();
    }

    // ===== BOOKMARK =====

    private void checkBookmarkStatus(String uid, String id) {
        if (uid.isEmpty()) return;

        ApiClient.getClient().create(ApiService.class)
                .checkBookmark(uid, id, "opportunity")
                .enqueue(new Callback<BookmarkCheckResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<BookmarkCheckResponse> call,
                            @NonNull Response<BookmarkCheckResponse> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            isBookmarked = response.body().isBookmarked();
                            updateBookmarkIcon();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<BookmarkCheckResponse> call,
                            @NonNull Throwable t
                    ) {}
                });
    }

    private void toggleBookmark() {
        if (maNguoiDung.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        BookmarkRequest req = new BookmarkRequest(maNguoiDung, maTinTuc, "opportunity");

        if (!isBookmarked) {
            api.addBookmark(req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(
                        @NonNull Call<Void> call,
                        @NonNull Response<Void> response
                ) {
                    if (response.isSuccessful()) {
                        isBookmarked = true;
                        updateBookmarkIcon();
                        Toast.makeText(OpportunityDetailActivity.this, "Đã lưu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
            });
        } else {
            api.removeBookmark(req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(
                        @NonNull Call<Void> call,
                        @NonNull Response<Void> response
                ) {
                    if (response.isSuccessful()) {
                        isBookmarked = false;
                        updateBookmarkIcon();
                        Toast.makeText(OpportunityDetailActivity.this, "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
            });
        }
    }

    private void updateBookmarkIcon() {
        ivBookmark.setImageResource(
                isBookmarked
                        ? R.drawable.ic_bookmark_filled
                        : R.drawable.ic_bookmark_border
        );
    }

    // ===== OPEN LINK =====

    private void openRegistrationLink() {
        if (linkUrl != null && !linkUrl.isEmpty()) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl)));
            } catch (Exception e) {
                Toast.makeText(this, "Không thể mở link", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không tìm thấy link đăng ký", Toast.LENGTH_SHORT).show();
        }
    }

    // ===== HELPER =====

    private String formatDeadline(String deadline) {
        if (deadline.length() >= 10) {
            return deadline.substring(0, 10).replace("-", "/");
        }
        return deadline;
    }

    private void mapping() {
        ivBack = findViewById(R.id.ivBack);
        ivBookmark = findViewById(R.id.ivBookmark);
        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvTypeBadge = findViewById(R.id.tvTypeBadge);
        tvDate = findViewById(R.id.tvDetailDate);
        tvDeadline = findViewById(R.id.tvDetailDeadline);
        tvContent = findViewById(R.id.tvContent);
        btnRegister = findViewById(R.id.btnRegister);
    }
}
