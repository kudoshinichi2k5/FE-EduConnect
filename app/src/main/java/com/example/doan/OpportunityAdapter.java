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

import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.BookmarkCheckResponse;
import com.example.doan.model.BookmarkRequest;
import com.example.doan.model.Opportunity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpportunityAdapter
        extends RecyclerView.Adapter<OpportunityAdapter.ViewHolder> {

    private final List<Opportunity> list;
    private final Context context;
    private final String maNguoiDung;

    // Cache bookmark
    private final Map<String, Boolean> bookmarkCache = new HashMap<>();

    public OpportunityAdapter(List<Opportunity> list, Context context) {
        this.list = list;
        this.context = context;

        SharedPreferences sp =
                context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        maNguoiDung = sp.getString("USER_ID", "");
    }

    private String key(String id) {
        return "opportunity_" + id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_opportunity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position
    ) {
        Opportunity o = list.get(position);
        String id = o.getMaTinTuc();

        holder.tvTitle.setText(o.getTitle());
        holder.tvDesc.setText(o.getDescription());
        holder.tvDate.setText(
                o.getDeadline() != null
                        ? "Hạn chót: " + o.getDeadline()
                        : "Không có hạn"
        );

        // RESET icon trước (tránh lỗi recycle)
        holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_border);

        if (bookmarkCache.containsKey(key(id))) {
            updateIcon(holder.ivBookmark, bookmarkCache.get(key(id)));
        } else {
            checkBookmark(id, holder.ivBookmark);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, OpportunityDetailActivity.class);
            i.putExtra("MA_TIN_TUC", id);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });

        holder.ivBookmark.setOnClickListener(v ->
                toggleBookmark(id, holder.ivBookmark)
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ===== BOOKMARK =====

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
            Toast.makeText(context, "Vui lòng đăng nhập",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean current = bookmarkCache.getOrDefault(key(id), false);
        BookmarkRequest req =
                new BookmarkRequest(maNguoiDung, id, "opportunity");

        ApiService api = ApiClient.getClient().create(ApiService.class);

        if (!current) {
            api.addBookmark(req).enqueue(new SimpleCallback(() -> {
                bookmarkCache.put(key(id), true);
                updateIcon(iv, true);
            }));
        } else {
            api.removeBookmark(req).enqueue(new SimpleCallback(() -> {
                bookmarkCache.put(key(id), false);
                updateIcon(iv, false);
            }));
        }
    }

    private void updateIcon(ImageView iv, boolean saved) {
        iv.setImageResource(
                saved
                        ? R.drawable.ic_bookmark_filled
                        : R.drawable.ic_bookmark_border
        );
    }

    static class SimpleCallback implements Callback<Void> {
        Runnable onSuccess;
        SimpleCallback(Runnable r) { onSuccess = r; }

        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) onSuccess.run();
        }

        @Override public void onFailure(Call<Void> call, Throwable t) {}
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDate;
        ImageView ivBookmark;

        ViewHolder(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvDesc = v.findViewById(R.id.tvDescription);
            tvDate = v.findViewById(R.id.tvDate);
            ivBookmark = v.findViewById(R.id.ivBookmark);
        }
    }
}
