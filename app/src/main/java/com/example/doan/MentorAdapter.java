package com.example.doan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.doan.model.Mentor; // QUAN TRỌNG: Import model
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class MentorAdapter extends RecyclerView.Adapter<MentorAdapter.MentorViewHolder> {
    private Context context;
    private List<Mentor> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Mentor mentor);
    }

    public MentorAdapter(Context context, List<Mentor> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MentorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mentor, parent, false);
        return new MentorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MentorViewHolder holder, int position) {
        Mentor mentor = list.get(position);
        holder.tvName.setText(mentor.getName());
        holder.tvJob.setText(mentor.getJobTitle());
        if (mentor.getImageUrl() != null && !mentor.getImageUrl().isEmpty()) {
            Glide.with(context).load(mentor.getImageUrl()).into(holder.imgAvatar);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(mentor);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MentorViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvJob;
        CircleImageView imgAvatar;
        public MentorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMentorName);
            tvJob = itemView.findViewById(R.id.tvMentorJob);
            imgAvatar = itemView.findViewById(R.id.imgMentorAvatar);
        }
    }
}