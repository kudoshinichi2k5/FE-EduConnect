package com.example.doan;

import android.content.Context;
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

public class MentorAdapter extends RecyclerView.Adapter<MentorAdapter.MentorViewHolder> {

    private final Context context;
    private final List<Mentor> mentors;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Mentor mentor);
    }

    public MentorAdapter(Context context, List<Mentor> mentors, OnItemClickListener listener) {
        this.context = context;
        this.mentors = mentors;
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
        Mentor mentor = mentors.get(position);

        holder.tvName.setText(mentor.getHoTen());
        holder.tvJob.setText(
                mentor.getChucVu() + " - " + mentor.getNoiLamViec()
        );

        if (mentor.getAnhDaiDien() != null && !mentor.getAnhDaiDien().isEmpty()) {
            Glide.with(context)
                    .load(mentor.getAnhDaiDien())
                    .into(holder.imgAvatar);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(mentor));
    }

    @Override
    public int getItemCount() {
        return mentors.size();
    }

    static class MentorViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvJob;
        CircleImageView imgAvatar;

        MentorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMentorName);
            tvJob = itemView.findViewById(R.id.tvMentorJob);
            imgAvatar = itemView.findViewById(R.id.imgMentorAvatar);
        }
    }
}
