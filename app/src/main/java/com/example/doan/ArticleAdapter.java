package com.example.doan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Dùng thư viện này để load ảnh (nếu sau này có)
import com.example.doan.model.Article;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private final List<Article> articleList;
    private final Context context;

    public ArticleAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gọi layout mới item_article (đã thiết kế lại ở bước trước)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articleList.get(position);

        // 1. TIÊU ĐỀ
        holder.tvTitle.setText(article.getTitle());

        // 2. DANH MỤC (CATEGORY)
        // Kiểm tra nếu có dữ liệu thì hiện, không thì để mặc định "Tin tức"
        if (article.getCategory() != null && !article.getCategory().isEmpty()) {
            holder.tvCategory.setText(article.getCategory());
        } else {
            holder.tvCategory.setText("Kiến thức"); // Mặc định cho đẹp
        }

        // 3. NGÀY ĐĂNG (Model chưa có thì giả lập)
        holder.tvDate.setText("Mới cập nhật");

        // 4. HÌNH ẢNH (Quan trọng)
        // Vì bạn chưa có link ảnh trong model, ta sẽ dùng ảnh mặc định R.drawable.uit
        // Sau này nếu Model có thêm field 'getImageUrl()', bạn chỉ cần bỏ comment dòng dưới:

        // String imageUrl = article.getImageUrl();
        // if (imageUrl != null && !imageUrl.isEmpty()) {
        //     Glide.with(context).load(imageUrl).placeholder(R.drawable.uit).into(holder.imgThumb);
        // } else {
        holder.imgThumb.setImageResource(R.drawable.uit); // Luôn hiện ảnh này cho đẹp
        // }

        // 5. SỰ KIỆN CLICK (Giữ nguyên logic cũ)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("MA_BAI_VIET", article.getMaBaiViet());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    // ViewHolder cập nhật theo layout mới
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvDate;
        ImageView imgThumb;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ đúng các ID trong file item_article.xml mới
            tvTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory); // ID mới
            tvDate = itemView.findViewById(R.id.tvDate);         // ID mới
            imgThumb = itemView.findViewById(R.id.imgArticleThumb); // ID mới
        }
    }
}
