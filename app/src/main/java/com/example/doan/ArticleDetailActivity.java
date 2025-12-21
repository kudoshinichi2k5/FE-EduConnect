package com.example.doan;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.Article;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvContent, tvCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvContent = findViewById(R.id.tvDetailContent);
        tvCategory = findViewById(R.id.tvDetailCategory);

        String maBaiViet = getIntent().getStringExtra("MA_BAI_VIET");

        if (maBaiViet == null) {
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
                            tvTitle.setText(article.getTitle());
                            tvContent.setText(article.getContent());
                            tvCategory.setText(article.getCategory());
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
