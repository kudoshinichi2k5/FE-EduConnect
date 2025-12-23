package com.example.doan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.doan.model.Mentor;

import de.hdodenhof.circleimageview.CircleImageView;

public class MentorDetailActivity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.activity_mentor_detail, container, false);

        Mentor mentor = (Mentor) getArguments().getSerializable("mentor");
        if (mentor == null) return view;

        CircleImageView imgAvatar = view.findViewById(R.id.imgDetailAvatar);
        TextView tvName = view.findViewById(R.id.tvDetailName);
        TextView tvJob = view.findViewById(R.id.tvDetailJob);
        TextView tvMajor = view.findViewById(R.id.tvDetailMajor);
        Button btnContact = view.findViewById(R.id.btnContact);

        tvName.setText(mentor.getHoTen());
        tvJob.setText(mentor.getChucVu() + " - " + mentor.getNoiLamViec());
        tvMajor.setText(mentor.getChuyenNganh());

        if (mentor.getAnhDaiDien() != null && !mentor.getAnhDaiDien().isEmpty()) {
            Glide.with(this).load(mentor.getAnhDaiDien()).into(imgAvatar);
        }

        btnContact.setOnClickListener(v -> {
            if (mentor.getLinkLienHe() != null && !mentor.getLinkLienHe().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mentor.getLinkLienHe()));
                startActivity(intent);
            }
        });

        return view;
    }
}
