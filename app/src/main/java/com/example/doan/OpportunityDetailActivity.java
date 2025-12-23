package com.example.doan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.BookmarkCheckResponse;
import com.example.doan.model.Opportunity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpportunityDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvDate, tvDeadline, tvDesc;
    Button btnRegister;
    ImageView ivBack;

    String maTinTuc;
    String linkUrl = "";
    String maNguoiDung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity_detail);

        mapping();

        maTinTuc = getIntent().getStringExtra("MA_TIN_TUC");
        if (maTinTuc == null) {
            finish();
            return;
        }

        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        maNguoiDung = sp.getString("USER_ID", "");

        fetchOpportunityDetail(maTinTuc);

        ivBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            if (!linkUrl.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl)));
            }
        });
    }

    private void fetchOpportunityDetail(String id) {
        ApiClient.getClient().create(ApiService.class)
                .getOpportunityById(id)
                .enqueue(new Callback<Opportunity>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Opportunity> call,
                            @NonNull Response<Opportunity> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            Opportunity o = response.body();
                            tvTitle.setText(o.getTitle());
                            tvDesc.setText(o.getDescription());
                            tvDate.setText("Ngày đăng: " + o.getCreatedAt());
                            tvDeadline.setText("Hạn chót: " + o.getDeadline());
                            linkUrl = o.getContentUrl();
                        }
                    }

                    @Override public void onFailure(
                            @NonNull Call<Opportunity> call,
                            @NonNull Throwable t
                    ) {}
                });
    }

    private void mapping() {
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvDate = findViewById(R.id.tvDetailDate);
        tvDeadline = findViewById(R.id.tvDetailDeadline);
        tvDesc = findViewById(R.id.tvDetailDesc);
        btnRegister = findViewById(R.id.btnRegister);
        ivBack = findViewById(R.id.ivBack);
    }
}
