package com.example.doan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.ViewHolder> {

    private static final String TAG = "OpportunityAdapter";

    private final List<Opportunity> mList;
    private final Context context;
    private final String maNguoiDung;

    // Cache trạng thái bookmark để tránh gọi API liên tục
    private final Map<String, Boolean> bookmarkCache = new HashMap<>();

    public OpportunityAdapter(List<Opportunity> list, Context context) {
        this.mList = list;
        this.context = context;

        // Lấy user ID từ SharedPreferences
        SharedPreferences sp = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.maNguoiDung = sp.getString("USER_ID", "");
    }

    public void updateData(List<Opportunity> newList) {
        mList.clear();
        mList.addAll(newList);
        notifyDataSetChanged();
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
        Opportunity item = mList.get(position);
        String maTinTuc = item.getMaTinTuc();

        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());

        holder.tvDate.setText(
                item.getDeadline() != null
                        ? "Hạn chót: " + item.getDeadline()
                        : "Không có hạn chót"
        );

        // Kiểm tra bookmark status (từ cache hoặc API)
        if (bookmarkCache.containsKey(maTinTuc)) {
            updateBookmarkIcon(holder.ivBookmark, bookmarkCache.get(maTinTuc));
        } else {
            checkBookmarkStatus(maTinTuc, holder.ivBookmark);
        }

        // Click vào card -> xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OpportunityDetailActivity.class);
            intent.putExtra("MA_TIN_TUC", maTinTuc);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // Click vào bookmark icon -> toggle bookmark
        holder.ivBookmark.setOnClickListener(v -> {
            toggleBookmark(maTinTuc, holder.ivBookmark);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    // ===== BOOKMARK LOGIC =====

    /**
     * Kiểm tra trạng thái bookmark từ server
     */
    private void checkBookmarkStatus(String maTinTuc, ImageView ivBookmark) {
        if (maNguoiDung.isEmpty()) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.checkBookmark(maNguoiDung, maTinTuc, "opportunity")
                .enqueue(new Callback<BookmarkCheckResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<BookmarkCheckResponse> call,
                            @NonNull Response<BookmarkCheckResponse> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean isBookmarked = response.body().isBookmarked();
                            bookmarkCache.put(maTinTuc, isBookmarked);
                            updateBookmarkIcon(ivBookmark, isBookmarked);
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<BookmarkCheckResponse> call,
                            @NonNull Throwable t
                    ) {
                        Log.e(TAG, "Check bookmark failed: " + t.getMessage());
                    }
                });
    }

    /**
     * Toggle bookmark (thêm/xóa)
     */
    private void toggleBookmark(String maTinTuc, ImageView ivBookmark) {
        if (maNguoiDung.isEmpty()) {
            Toast.makeText(context, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        BookmarkRequest request = new BookmarkRequest(maNguoiDung, maTinTuc, "opportunity");

        boolean currentStatus = bookmarkCache.getOrDefault(maTinTuc, false);

        if (!currentStatus) {
            // Thêm bookmark
            apiService.addBookmark(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(
                        @NonNull Call<Void> call,
                        @NonNull Response<Void> response
                ) {
                    if (response.isSuccessful()) {
                        bookmarkCache.put(maTinTuc, true);
                        updateBookmarkIcon(ivBookmark, true);
                        Toast.makeText(context, "Đã lưu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Lỗi khi lưu", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Xóa bookmark
            apiService.removeBookmark(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(
                        @NonNull Call<Void> call,
                        @NonNull Response<Void> response
                ) {
                    if (response.isSuccessful()) {
                        bookmarkCache.put(maTinTuc, false);
                        updateBookmarkIcon(ivBookmark, false);
                        Toast.makeText(context, "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Lỗi khi bỏ lưu", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Update icon bookmark
     */
    private void updateBookmarkIcon(ImageView ivBookmark, boolean isBookmarked) {
        ivBookmark.setImageResource(
                isBookmarked
                        ? R.drawable.ic_bookmark_filled
                        : R.drawable.ic_bookmark_border
        );
    }

    // ===== VIEW HOLDER =====

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDate;
        ImageView ivBookmark;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
        }
    }
}