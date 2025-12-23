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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.ViewHolder> {

    private final List<Opportunity> mList;
    private final Context context;
    private final String userId;

    public OpportunityAdapter(List<Opportunity> list, Context context) {
        this.mList = list;
        this.context = context;

        // Lấy userId từ SharedPreferences
        SharedPreferences sp = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.userId = sp.getString("USER_ID", "");
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

        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());

        holder.tvDate.setText(
                item.getDeadline() != null
                        ? "Hạn chót: " + item.getDeadline()
                        : "Không có hạn chót"
        );

        // Check bookmark status cho item này
        checkAndSetBookmarkIcon(holder, item.getMaTinTuc());

        // Click vào card → mở detail
        holder.itemView.setOnClickListener(v -> {
            // Chặn sự kiện click nếu đang click vào bookmark icon
            if (v.getId() != R.id.ivBookmark) {
                Intent intent = new Intent(context, OpportunityDetailActivity.class);
                intent.putExtra("MA_TIN_TUC", item.getMaTinTuc());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        // Click vào bookmark icon → toggle bookmark
        holder.ivBookmark.setOnClickListener(v -> {
            // Ngăn sự kiện click lan sang card
            v.setClickable(true);
            toggleBookmark(holder, item.getMaTinTuc());
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * Kiểm tra bookmark status và set icon tương ứng
     */
    private void checkAndSetBookmarkIcon(ViewHolder holder, String opportunityId) {
        if (userId.isEmpty() || opportunityId == null) {
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.checkBookmark(userId, opportunityId, "opportunity")
                .enqueue(new Callback<BookmarkCheckResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BookmarkCheckResponse> call,
                                           @NonNull Response<BookmarkCheckResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean isBookmarked = response.body().isBookmarked();
                            holder.ivBookmark.setImageResource(
                                    isBookmarked
                                            ? R.drawable.ic_bookmark_filled
                                            : R.drawable.ic_bookmark_border
                            );
                            holder.ivBookmark.setTag(isBookmarked); // Lưu trạng thái vào tag
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BookmarkCheckResponse> call,
                                          @NonNull Throwable t) {
                        // Không làm gì, để icon mặc định
                    }
                });
    }

    /**
     * Toggle bookmark (add/remove)
     */
    private void toggleBookmark(ViewHolder holder, String opportunityId) {
        if (userId.isEmpty() || opportunityId == null) {
            Toast.makeText(context, "Vui lòng đăng nhập để lưu", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        BookmarkRequest request = new BookmarkRequest(userId, opportunityId, "opportunity");

        // Lấy trạng thái hiện tại từ tag
        Boolean isCurrentlyBookmarked = (Boolean) holder.ivBookmark.getTag();
        if (isCurrentlyBookmarked == null) {
            isCurrentlyBookmarked = false;
        }

        if (!isCurrentlyBookmarked) {
            // Add bookmark
            apiService.addBookmark(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call,
                                       @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                        holder.ivBookmark.setTag(true);
                        Toast.makeText(context, "Đã lưu", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lưu thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Remove bookmark
            apiService.removeBookmark(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call,
                                       @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        holder.ivBookmark.setImageResource(R.drawable.ic_bookmark_border);
                        holder.ivBookmark.setTag(false);
                        Toast.makeText(context, "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Bỏ lưu thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

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