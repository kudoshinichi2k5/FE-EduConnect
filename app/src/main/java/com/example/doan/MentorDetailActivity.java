package com.example.doan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.Mentor;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MentorDetailActivity extends BaseActivity {

    CircleImageView imgAvatar;
    TextView tvName, tvJob, tvMajor;
    Button btnContact;

    String mentorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_detail);

        imgAvatar = findViewById(R.id.imgDetailAvatar);
        tvName = findViewById(R.id.tvDetailName);
        tvJob = findViewById(R.id.tvDetailJob);
        tvMajor = findViewById(R.id.tvDetailMajor);
        btnContact = findViewById(R.id.btnContact);

        mentorId = getIntent().getStringExtra("MENTOR_ID");
        if (mentorId == null || mentorId.isEmpty()) {
            finish();
            return;
        }

        fetchMentorDetail();
    }

    private void fetchMentorDetail() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getMentorById(mentorId)
                .enqueue(new Callback<Mentor>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Mentor> call,
                            @NonNull Response<Mentor> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            Mentor m = response.body();

                            tvName.setText(m.getHoTen());
                            tvJob.setText(m.getChucVu());
                            tvMajor.setText(m.getChuyenNganh());

                            if (m.getAnhDaiDien() != null) {
                                Glide.with(MentorDetailActivity.this)
                                        .load(m.getAnhDaiDien())
                                        .into(imgAvatar);
                            }

                            btnContact.setOnClickListener(v -> {
                                if (m.getLinkLienHe() != null) {
                                    Intent intent = new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(m.getLinkLienHe())
                                    );
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Mentor> call,
                            @NonNull Throwable t
                    ) {
                        finish();
                    }
                });
    }
}
