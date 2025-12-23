package com.example.doan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.doan.model.Mentor;
import de.hdodenhof.circleimageview.CircleImageView;

public class MentorDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_mentor_detail, container, false);

        if (getArguments() != null) {
            Mentor mentor = (Mentor) getArguments().getSerializable("mentor_data");
            if (mentor != null) {
                ((TextView) view.findViewById(R.id.tvDetailName)).setText(mentor.getName());
                ((TextView) view.findViewById(R.id.tvDetailJob)).setText(mentor.getJobTitle());
                ((TextView) view.findViewById(R.id.tvDetailEducation)).setText(mentor.getEducation());
                ((TextView) view.findViewById(R.id.tvDetailAchievement)).setText(mentor.getAchievements());

                CircleImageView avatar = view.findViewById(R.id.imgDetailAvatar);
                if (mentor.getImageUrl() != null) Glide.with(this).load(mentor.getImageUrl()).into(avatar);

                view.findViewById(R.id.btnContactEmail).setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + mentor.getEmail()));
                    try { startActivity(Intent.createChooser(intent, "Email")); } catch (Exception e) {}
                });
            }
        }
        return view;
    }
}