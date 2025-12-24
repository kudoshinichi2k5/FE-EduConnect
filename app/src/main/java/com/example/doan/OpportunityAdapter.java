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
import com.example.doan.model.BookmarkCheckResponse;
import com.example.doan.model.BookmarkRequest;
import com.example.doan.model.Opportunity;
import com.example.doan.utils.TimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.ViewHolder> {

    private final List<Opportunity> list;
    private final Context context;
    private final String maNguoiDung;

    // Cache bookmark
    private final Map<String, Boolean> bookmarkCache = new HashMap<>();

    public OpportunityAdapter(List<Opportunity> list, Context context) {
        this.list = list;
        this.context = context;

        SharedPreferences sp = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        maNguoiDung = sp.getString("USER_ID", "");
    }

    private String key(String id) {
        return "opportunity_" + id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_opportunity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Opportunity o = list.get(position);
        String id = o.getMaTinTuc();

        // === TITLE & DESCRIPTION ===
        holder.tvTitle.setText(o.getTitle());
        holder.tvDesc.setText(o.getDescription());

        // === ẢNH BÌA ===
        String imageUrl = o.getImageUrl();
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

        // === TYPE BADGE ===
        String type = o.getType();
        if ("scholarship".equalsIgnoreCase(type)) {
            holder.tvTypeBadge.setText("Học bổng");
            holder.tvTypeBadge.setBackgroundResource(R.drawable.badge_scholarship);
        } else if ("contest".equalsIgnoreCase(type)) {
            holder.tvTypeBadge.setText("Cuộc thi");
            holder.tvTypeBadge.setBackgroundResource(R.drawable.badge_contest);
        } else if ("event".equalsIgnoreCase(type)) {
            holder.tvTypeBadge.setText("Sự kiện");
            holder.tvTypeBadge.setBackgroundResource(R.drawable.badge_event);
        } else {
            holder.tvTypeBadge.setText("Khác");
            holder.tvTypeBadge.setBackgroundResource(R.drawable.badge_default);
        }

        // === THỜI GIAN ===
        String timeAgo = TimeUtils.formatTimeAgo(o.getCreatedAt());
        holder.tvTimeAgo.setText(timeAgo.isEmpty() ? "Vừa xong" : timeAgo);

        // === DEADLINE ===
        String deadline = o.getDeadline();
        if (deadline != null && !deadline.isEmpty()) {
            holder.tvDeadline.setText("Hạn: " + formatDeadline(deadline));
            holder.tvDeadline.setVisibility(View.VISIBLE);
        } else {
            holder.tvDeadline.setVisibility(View.GONE);
        }

        // === BOOKMARK ICON ===
        holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_border);

        if (bookmarkCache.containsKey(key(id))) {
            updateIcon(holder.ivBookmark, bookmarkCache.get(key(id)));
        } else {
            checkBookmark(id, holder.ivBookmark);
        }

        // === CLICK LISTENERS ===
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, OpportunityDetailActivity.class);
            i.putExtra("MA_TIN_TUC", id);
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
                .checkBookmark(maNguoiDung, id, "opportunity")
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
        BookmarkRequest req = new BookmarkRequest(maNguoiDung, id, "opportunity");

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

    // ===== HELPER: Format deadline =====
    private String formatDeadline(String deadline) {
        // Deadline format: "2025-12-31 23:59:59" hoặc ISO
        try {
            if (deadline.length() >= 10) {
                return deadline.substring(0, 10).replace("-", "/");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deadline;
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
        TextView tvTitle, tvDesc, tvTypeBadge, tvTimeAgo, tvDeadline;

        ViewHolder(@NonNull View v) {
            super(v);
            ivImage = v.findViewById(R.id.ivOpportunityImage);
            ivBookmark = v.findViewById(R.id.ivBookmark);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvDesc = v.findViewById(R.id.tvDescription);
            tvTypeBadge = v.findViewById(R.id.tvTypeBadge);
            tvTimeAgo = v.findViewById(R.id.tvTimeAgo);
            tvDeadline = v.findViewById(R.id.tvDeadline);
        }
    }
}