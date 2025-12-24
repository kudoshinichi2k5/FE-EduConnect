package com.example.doan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.Article;
import com.example.doan.model.BookmarkCheckResponse;
import com.example.doan.model.BookmarkRequest;
import com.example.doan.utils.TimeUtils;


import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private final List<Article> articleList;
    private final Context context;
    private final String uid;

    private final Map<String, Boolean> bookmarkCache = new HashMap<>();

    public ArticleAdapter(List<Article> articleList, Context context, String uid) {
        this.articleList = articleList;
        this.context = context;
        this.uid = uid;
    }

    private String key(String id) {
        return "article_" + id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);
        String articleId = article.getMaBaiViet();

        // ========= TITLE =========
        holder.tvTitle.setText(article.getTitle());

        // ========= CATEGORY =========
        holder.tvCategory.setText(
                article.getCategory() != null && !article.getCategory().isEmpty()
                        ? article.getCategory()
                        : "Kiến thức"
        );

        // ========= DATE =========
        if (article.getCreatedAt() != null) {
            holder.tvDate.setText(
                    TimeUtils.formatTimeAgo(article.getCreatedAt())
            );
        } else {
            holder.tvDate.setText("");
        }


        // ========= IMAGE =========
        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(article.getImageUrl())
                    .placeholder(R.drawable.uit)
                    .error(R.drawable.uit)
                    .into(holder.imgThumb);
        } else {
            holder.imgThumb.setImageResource(R.drawable.uit);
        }

        // ========= BOOKMARK =========
        holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_border);

        if (bookmarkCache.containsKey(key(articleId))) {
            updateIcon(holder.ivBookmark, bookmarkCache.get(key(articleId)));
        } else {
            checkBookmark(articleId, holder.ivBookmark);
        }

        holder.ivBookmark.setOnClickListener(v ->
                toggleBookmark(articleId, holder.ivBookmark)
        );

        // ========= CLICK DETAIL =========
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("MA_BAI_VIET", articleId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    // ================= BOOKMARK =================

    private void checkBookmark(String articleId, ImageView iv) {
        if (uid == null || uid.isEmpty()) return;

        ApiClient.getClient().create(ApiService.class)
                .checkBookmark(uid, articleId, "article")
                .enqueue(new Callback<BookmarkCheckResponse>() {
                    @Override
                    public void onResponse(Call<BookmarkCheckResponse> call,
                                           Response<BookmarkCheckResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean saved = response.body().isBookmarked();
                            bookmarkCache.put(key(articleId), saved);
                            updateIcon(iv, saved);
                        }
                    }

                    @Override
                    public void onFailure(Call<BookmarkCheckResponse> call, Throwable t) {
                    }
                });
    }

    private void toggleBookmark(String articleId, ImageView iv) {
        if (uid == null || uid.isEmpty()) {
            Toast.makeText(context, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean current = bookmarkCache.getOrDefault(key(articleId), false);
        BookmarkRequest req = new BookmarkRequest(uid, articleId, "article");

        ApiService api = ApiClient.getClient().create(ApiService.class);

        if (!current) {
            api.addBookmark(req).enqueue(new SimpleCallback(() -> {
                bookmarkCache.put(key(articleId), true);
                updateIcon(iv, true);
            }));
        } else {
            api.removeBookmark(req).enqueue(new SimpleCallback(() -> {
                bookmarkCache.put(key(articleId), false);
                updateIcon(iv, false);
            }));
        }
    }

    private void updateIcon(ImageView iv, boolean saved) {
        iv.setImageResource(
                saved ? R.drawable.ic_bookmark_filled
                        : R.drawable.ic_bookmark_border
        );
    }

    // ========= DATE FORMAT =========
    private String formatDate(String raw) {
        try {
            SimpleDateFormat input =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat output =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return output.format(input.parse(raw));
        } catch (Exception e) {
            return "Mới cập nhật";
        }
    }

    static class SimpleCallback implements Callback<Void> {
        Runnable onSuccess;

        SimpleCallback(Runnable r) {
            onSuccess = r;
        }

        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful() && onSuccess != null) {
                onSuccess.run();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvDate;
        ImageView imgThumb, ivBookmark;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgThumb = itemView.findViewById(R.id.imgArticleThumb);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
        }
    }
}
