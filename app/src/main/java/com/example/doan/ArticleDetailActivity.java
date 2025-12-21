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

    TextView tvTitle, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        tvTitle = findViewById(R.id.tvArticleTitle);
        tvContent = findViewById(R.id.tvArticleContent);

        String articleId = getIntent().getStringExtra("ARTICLE_ID");
        if (articleId == null) {
            finish();
            return;
        }

        loadDetail(articleId);
    }

    private void loadDetail(String id) {
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
                            tvTitle.setText(response.body().getTitle());
                            tvContent.setText(response.body().getContent());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Article> call,
                            @NonNull Throwable t
                    ) {
                        Toast.makeText(
                                ArticleDetailActivity.this,
                                "Lỗi tải chi tiết",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
