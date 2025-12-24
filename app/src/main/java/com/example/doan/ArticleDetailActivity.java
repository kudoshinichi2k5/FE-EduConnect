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


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleDetailActivity extends AppCompatActivity {

    ImageView imgArticle;
    TextView tvTitle, tvContent, tvCategory;
    TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // ===== MAP VIEW =====
        imgArticle = findViewById(R.id.imgDetailArticle);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvContent = findViewById(R.id.tvDetailContent);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDate = findViewById(R.id.tvDetailDate);

        // ===== GET ID =====
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

                            // ===== TEXT =====
                            tvTitle.setText(article.getTitle());
                            tvContent.setText(article.getContent());
                            tvCategory.setText(
                                    article.getCategory() != null
                                            ? article.getCategory()
                                            : "Kiến thức"
                            );

                            if (article.getCreatedAt() != null) {
                                tvDate.setText(
                                        TimeUtils.formatTimeAgo(article.getCreatedAt())
                                );
                            }

                            // ===== IMAGE =====
                            if (article.getImageUrl() != null &&
                                    !article.getImageUrl().isEmpty()) {

                                Glide.with(ArticleDetailActivity.this)
                                        .load(article.getImageUrl())
                                        .placeholder(R.drawable.uit)
                                        .error(R.drawable.uit)
                                        .into(imgArticle);
                            } else {
                                imgArticle.setImageResource(R.drawable.uit);
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
