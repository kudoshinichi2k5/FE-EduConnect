package com.example.doan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.Article;
import com.example.doan.model.BookmarkCheckResponse;
import com.example.doan.model.BookmarkRequest;
import com.example.doan.utils.TimeUtils;

import io.noties.markwon.Markwon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleDetailActivity extends BaseActivity {

    ImageView ivBack, ivBookmark, ivDetailImage;
    TextView tvTitle, tvCategory, tvDate, tvContent;

    String maBaiViet;
    String maNguoiDung;
    boolean isBookmarked = false;

    Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        mapping();

        // Init Markwon
        markwon = Markwon.create(this);

        // Get intent data
        maBaiViet = getIntent().getStringExtra("MA_BAI_VIET");
        if (maBaiViet == null || maBaiViet.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get user ID
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        maNguoiDung = sp.getString("USER_ID", "");

        // Load data
        fetchArticleDetail(maBaiViet);
        checkBookmarkStatus(maNguoiDung, maBaiViet);

        // Listeners
        ivBack.setOnClickListener(v -> finish());
        ivBookmark.setOnClickListener(v -> toggleBookmark());
    }

    private void fetchArticleDetail(String id) {
        ApiClient.getClient().create(ApiService.class)
                .getArticleById(id)
                .enqueue(new Callback<Article>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Article> call,
                            @NonNull Response<Article> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            displayArticle(response.body());
                        } else {
                            Toast.makeText(
                                    ArticleDetailActivity.this,
                                    "Không tải được chi tiết",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Article> call,
                            @NonNull Throwable t
                    ) {
                        Toast.makeText(
                                ArticleDetailActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void displayArticle(Article a) {

        // ===== TITLE =====
        tvTitle.setText(a.getTitle());

        // ===== ẢNH =====
        if (a.getImageUrl() != null && !a.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(a.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.uit)
                    .error(R.drawable.uit)
                    .into(ivDetailImage);
        } else {
            ivDetailImage.setImageResource(R.drawable.uit);
        }

        // ===== CATEGORY =====
        String category = a.getCategory();
        if (category != null && !category.isEmpty()) {
            tvCategory.setText(formatCategory(category));
        } else {
            tvCategory.setText("Kiến thức");
        }

        // ===== THỜI GIAN =====
        String timeAgo = TimeUtils.formatTimeAgo(a.getCreatedAt());
        tvDate.setText(timeAgo.isEmpty() ? "Vừa xong" : timeAgo);

        // ===== MARKDOWN CONTENT =====
        if (a.getContent() != null && !a.getContent().isEmpty()) {
            markwon.setMarkdown(tvContent, a.getContent());
        } else {
            tvContent.setText("Không có nội dung chi tiết.");
        }
    }

    // ===== BOOKMARK =====

    private void checkBookmarkStatus(String uid, String id) {
        if (uid.isEmpty()) return;

        ApiClient.getClient().create(ApiService.class)
                .checkBookmark(uid, id, "article")
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
        BookmarkRequest req = new BookmarkRequest(maNguoiDung, maBaiViet, "article");

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
                        Toast.makeText(ArticleDetailActivity.this, "Đã lưu", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ArticleDetailActivity.this, "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
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

    // ===== HELPER =====

    private String formatCategory(String raw) {
        switch (raw.toLowerCase()) {
            case "career": return "Nghề nghiệp";
            case "soft-skills": return "Kỹ năng mềm";
            case "university": return "Đại học";
            case "study-abroad": return "Du học";
            case "career-guidance": return "Định hướng";
            case "interview-tips": return "Phỏng vấn";
            default: return raw;
        }
    }

    private void mapping() {
        ivBack = findViewById(R.id.ivBack);
        ivBookmark = findViewById(R.id.ivBookmark);
        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvCategory = findViewById(R.id.tvCategory);
        tvDate = findViewById(R.id.tvDetailDate);
        tvContent = findViewById(R.id.tvContent);
    }
}