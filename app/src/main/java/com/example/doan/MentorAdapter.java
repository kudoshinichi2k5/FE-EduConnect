package com.example.doan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan.model.Mentor;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MentorAdapter extends RecyclerView.Adapter<MentorAdapter.ViewHolder> {

    private final List<Mentor> list;
    private final Context context;

    public MentorAdapter(List<Mentor> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mentor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        Mentor mentor = list.get(position);

        holder.tvName.setText(mentor.getHoTen());
        holder.tvJob.setText(mentor.getChucVu());

        if (mentor.getAnhDaiDien() != null && !mentor.getAnhDaiDien().isEmpty()) {
            Glide.with(context)
                    .load(mentor.getAnhDaiDien())
                    .into(holder.imgAvatar);
        }

        // 👉 CLICK → MỞ DETAIL (GIỐNG Opportunity)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MentorDetailActivity.class);
            intent.putExtra("MENTOR_ID", mentor.getMaMentor());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvJob;
        CircleImageView imgAvatar;

        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvMentorName);
            tvJob = view.findViewById(R.id.tvMentorJob);
            imgAvatar = view.findViewById(R.id.imgMentorAvatar);
        }
    }
}
