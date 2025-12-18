package com.example.doan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.model.Opportunity;

import java.util.ArrayList;
import java.util.List;

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.ViewHolder> {

    private final List<Opportunity> mList;
    private final Context context;

    public OpportunityAdapter(List<Opportunity> list, Context context) {
        this.mList = list;
        this.context = context;
    }

    // KHÔNG ĐỔI REFERENCE
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

        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());

        holder.tvDate.setText(
                item.getDeadline() != null
                        ? "Hạn chót: " + item.getDeadline()
                        : "Không có hạn chót"
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent =
                    new Intent(context, OpportunityDetailActivity.class);
            intent.putExtra("MA_TIN_TUC", item.getMaTinTuc());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
