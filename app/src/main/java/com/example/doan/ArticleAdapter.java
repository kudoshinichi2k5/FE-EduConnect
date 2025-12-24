package com.example.doan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.Article;
import com.example.doan.model.BookmarkCheckResponse;
import com.example.doan.model.BookmarkRequest;
import com.example.doan.utils.TimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private final List<Article> list;
    private final Context context;
    private final String maNguoiDung;

    // Cache bookmark
    private final Map<String, Boolean> bookmarkCache = new HashMap<>();

    public ArticleAdapter(List<Article> list, Context context, String maNguoiDung) {
        this.list = list;
        this.context = context;
        this.maNguoiDung = maNguoiDung;
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
        Article a = list.get(position);
        String id = a.getMaBaiViet();

        // === TITLE ===
        holder.tvTitle.setText(a.getTitle());

        // === ẢNH BÌA ===
        String imageUrl = a.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.uit)
                    .error(R.drawable.uit)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.uit);
        }

        // === CATEGORY BADGE ===
        String category = a.getCategory();
        if (category != null && !category.isEmpty()) {
            holder.tvCategory.setText(formatCategory(category));
        } else {
            holder.tvCategory.setText("Kiến thức");
        }

        // === THỜI GIAN ===
        String timeAgo = TimeUtils.formatTimeAgo(a.getCreatedAt());
        holder.tvTimeAgo.setText(timeAgo.isEmpty() ? "Vừa xong" : timeAgo);

        // === BOOKMARK ICON ===
        holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_border);

        if (bookmarkCache.containsKey(key(id))) {
            updateIcon(holder.ivBookmark, bookmarkCache.get(key(id)));
        } else {
            checkBookmark(id, holder.ivBookmark);
        }

        // === CLICK LISTENERS ===
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, ArticleDetailActivity.class);
            i.putExtra("MA_BAI_VIET", id);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });

        holder.ivBookmark.setOnClickListener(v -> toggleBookmark(id, holder.ivBookmark));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ===== BOOKMARK LOGIC =====

    private void checkBookmark(String id, ImageView iv) {
        if (maNguoiDung.isEmpty()) return;

        ApiClient.getClient().create(ApiService.class)
                .checkBookmark(maNguoiDung, id, "article")
                .enqueue(new Callback<BookmarkCheckResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<BookmarkCheckResponse> call,
                            @NonNull Response<BookmarkCheckResponse> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean saved = response.body().isBookmarked();
                            bookmarkCache.put(key(id), saved);
                            updateIcon(iv, saved);
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<BookmarkCheckResponse> call,
                            @NonNull Throwable t
                    ) {}
                });
    }

    private void toggleBookmark(String id, ImageView iv) {
        if (maNguoiDung.isEmpty()) {
            Toast.makeText(context, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean current = bookmarkCache.getOrDefault(key(id), false);
        BookmarkRequest req = new BookmarkRequest(maNguoiDung, id, "article");

        ApiService api = ApiClient.getClient().create(ApiService.class);

        if (!current) {
            api.addBookmark(req).enqueue(new SimpleCallback(() -> {
                bookmarkCache.put(key(id), true);
                updateIcon(iv, true);
                Toast.makeText(context, "Đã lưu", Toast.LENGTH_SHORT).show();
            }));
        } else {
            api.removeBookmark(req).enqueue(new SimpleCallback(() -> {
                bookmarkCache.put(key(id), false);
                updateIcon(iv, false);
                Toast.makeText(context, "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
            }));
        }
    }

    private void updateIcon(ImageView iv, boolean saved) {
        iv.setImageResource(
                saved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border
        );
    }

    // ===== HELPER =====

    private String formatCategory(String raw) {
        // Map English to Vietnamese
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

    // ===== SIMPLE CALLBACK =====
    static class SimpleCallback implements Callback<Void> {
        Runnable onSuccess;
        SimpleCallback(Runnable r) { onSuccess = r; }

        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful() && onSuccess != null) {
                onSuccess.run();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {}
    }

    // ===== VIEW HOLDER =====
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivBookmark;
        TextView tvTitle, tvCategory, tvTimeAgo;

        ViewHolder(@NonNull View v) {
            super(v);
            ivImage = v.findViewById(R.id.ivArticleImage);
            ivBookmark = v.findViewById(R.id.ivBookmark);
            tvTitle = v.findViewById(R.id.tvArticleTitle);
            tvCategory = v.findViewById(R.id.tvCategory);
            tvTimeAgo = v.findViewById(R.id.tvTimeAgo);
        }
    }
}