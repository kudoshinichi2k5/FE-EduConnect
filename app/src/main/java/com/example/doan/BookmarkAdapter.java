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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.BookmarkItem;
import com.example.doan.model.BookmarkRequest;
import com.example.doan.utils.TimeUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private final List<BookmarkItem> list;
    private final Context context;

    public BookmarkAdapter(List<BookmarkItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType
    ) {
        // DÙNG CHUNG layout item_opportunity (đẹp nhất)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_opportunity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position
    ) {
        BookmarkItem item = list.get(position);

        // ===== TITLE & DESCRIPTION =====
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());

        // ===== IMAGE =====
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.uit)
                    .error(R.drawable.uit)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.uit);
        }

        // ===== TYPE BADGE =====
        if ("opportunity".equals(item.getTargetType())) {
            holder.tvTypeBadge.setText("Cơ hội");
            holder.tvTypeBadge.setBackgroundResource(R.drawable.badge_scholarship);
            holder.tvDeadline.setVisibility(View.VISIBLE);
        } else {
            holder.tvTypeBadge.setText("Bài viết");
            holder.tvTypeBadge.setBackgroundResource(R.drawable.badge_default);
            holder.tvDeadline.setVisibility(View.GONE);
        }

        // ===== TIME =====
        String timeAgo = TimeUtils.formatTimeAgo(item.getSaved_at());
        holder.tvTimeAgo.setText(
                timeAgo.isEmpty() ? "Vừa lưu" : "Đã lưu " + timeAgo
        );

        // ===== BOOKMARK ICON (LUÔN FILLED) =====
        holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_filled);

        // ===== CLICK ITEM → DETAIL =====
        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if ("opportunity".equals(item.getTargetType())) {
                intent = new Intent(context, OpportunityDetailActivity.class);
                intent.putExtra("MA_TIN_TUC", item.getTargetId());
            } else {
                intent = new Intent(context, ArticleDetailActivity.class);
                intent.putExtra("MA_BAI_VIET", item.getTargetId());
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // ===== CLICK BOOKMARK → REMOVE =====
        holder.ivBookmark.setOnClickListener(v ->
                removeBookmark(item, holder.getAdapterPosition())
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ================= REMOVE BOOKMARK =================

    private void removeBookmark(BookmarkItem item, int position) {
        BookmarkRequest req = new BookmarkRequest(
                getUserId(),
                item.getTargetId(),
                item.getTargetType()
        );

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.removeBookmark(req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(
                    @NonNull Call<Void> call,
                    @NonNull Response<Void> response
            ) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                } else {
                    Toast.makeText(context,
                            "Không thể xoá bookmark",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<Void> call,
                    @NonNull Throwable t
            ) {
                Toast.makeText(context,
                        "Lỗi kết nối",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserId() {
        return context.getSharedPreferences(
                "UserPrefs", Context.MODE_PRIVATE
        ).getString("USER_ID", "");
    }

    // ================= VIEW HOLDER =================

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage, ivBookmark;
        TextView tvTitle, tvDesc, tvTypeBadge, tvTimeAgo, tvDeadline;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivOpportunityImage);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDescription);
            tvTypeBadge = itemView.findViewById(R.id.tvTypeBadge);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
        }
    }
}
