package com.example.doan;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.Article;
import com.example.doan.utils.TimeUtils;

import io.noties.markwon.Markwon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleDetailActivity extends AppCompatActivity {

    private ImageView imgDetail;
    private TextView tvTitle, tvCategory, tvDate, tvContent;

    // 🔥 MARKDOWN ENGINE
    private Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // ===== INIT MARKWON =====
        markwon = Markwon.create(this);

        // ===== ÁNH XẠ VIEW =====
        imgDetail = findViewById(R.id.imgDetailArticle);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDate = findViewById(R.id.tvDetailDate);
        tvContent = findViewById(R.id.tvDetailContent);

        // --- MỚI: XỬ LÝ NÚT BACK ---
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Đóng Activity hiện tại -> Tự động quay về trang trước (Trang chủ)
            finish();
        });

        // ===== NHẬN ID BÀI VIẾT =====
        String maBaiViet = getIntent().getStringExtra("MA_BAI_VIET");

        if (maBaiViet == null || maBaiViet.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchArticleDetail(maBaiViet);
    }

    private void fetchArticleDetail(String id) {
        ApiClient.getClient()
                .create(ApiService.class)
                .getArticleById(id)
                .enqueue(new Callback<Article>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Article> call,
                            @NonNull Response<Article> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {

                            Article article = response.body();

                            // ===== TITLE =====
                            tvTitle.setText(article.getTitle());

                            // ===== CATEGORY =====
                            tvCategory.setText(
                                    article.getCategory() != null
                                            ? article.getCategory()
                                            : "Kiến thức"
                            );

                            // ===== THỜI GIAN =====
                            if (article.getCreatedAt() != null) {
                                tvDate.setText(
                                        TimeUtils.formatTimeAgo(article.getCreatedAt())
                                );
                            }

                            // ===== MARKDOWN CONTENT =====
                            if (article.getContent() != null) {
                                markwon.setMarkdown(
                                        tvContent,
                                        article.getContent()
                                );
                            }

                            // ===== IMAGE =====
                            if (article.getImageUrl() != null &&
                                    !article.getImageUrl().isEmpty()) {
                                Glide.with(ArticleDetailActivity.this)
                                        .load(article.getImageUrl())
                                        .placeholder(R.drawable.uit)
                                        .into(imgDetail);
                            } else {
                                imgDetail.setImageResource(R.drawable.uit);
                            }

                        } else {
                            Toast.makeText(
                                    ArticleDetailActivity.this,
                                    "Không tải được bài viết",
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
                                "Lỗi kết nối server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
