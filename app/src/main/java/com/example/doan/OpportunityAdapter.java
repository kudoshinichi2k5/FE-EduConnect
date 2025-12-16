package com.example.doan;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.ViewHolder> {

    private List<Opportunity> mList;
    private Context context;

    public OpportunityAdapter(List<Opportunity> list, Context context) {
        this.mList = list;
        this.context = context;
    }

    // --- QUAN TRỌNG: Hàm này dùng để cập nhật danh sách khi tìm kiếm ---
    public void setFilteredList(List<Opportunity> filteredList) {
        this.mList = filteredList;
        notifyDataSetChanged(); // Báo cho RecyclerView vẽ lại
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_opportunity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Opportunity item = mList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());
        holder.tvDate.setText("Hạn chót: " + item.getDeadline());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OpportunityDetailActivity.class);
            intent.putExtra("TITLE", item.getTitle());
            intent.putExtra("DESC", item.getDescription());
            intent.putExtra("DATE", item.getDate());
            intent.putExtra("DEADLINE", item.getDeadline());
            intent.putExtra("LINK", item.getLink());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return mList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}