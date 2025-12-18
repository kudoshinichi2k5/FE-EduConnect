package com.example.doan;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity_detail);

        mapping();

        // 1. Nhận MaTinTuc từ Intent
        maTinTuc = getIntent().getStringExtra("MA_TIN_TUC");
        if (maTinTuc == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Gọi API lấy chi tiết
        fetchOpportunityDetail(maTinTuc);

        // 3. Back
        ivBack.setOnClickListener(v -> finish());

        // 4. Đăng ký ngay
        btnRegister.setOnClickListener(v -> {
            if (linkUrl != null && !linkUrl.isEmpty()) {
                Intent browserIntent =
                        new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this,
                        "Không tìm thấy link đăng ký!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Gọi API GET /api/opportunity/{id}
     */
    private void fetchOpportunityDetail(String id) {
        ApiService apiService =
                ApiClient.getClient().create(ApiService.class);

        apiService.getOpportunityById(id)
                .enqueue(new Callback<Opportunity>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Opportunity> call,
                            @NonNull Response<Opportunity> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            Opportunity item = response.body();

                            tvTitle.setText(item.getTitle());
                            tvDesc.setText(item.getDescription());
                            tvDate.setText("Ngày đăng: " + item.getCreatedAt());
                            tvDeadline.setText("Hạn chót: " + item.getDeadline());

                            linkUrl = item.getContentUrl();
                        } else {
                            Toast.makeText(
                                    OpportunityDetailActivity.this,
                                    "Không tải được chi tiết",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Opportunity> call,
                            @NonNull Throwable t
                    ) {
                        Toast.makeText(
                                OpportunityDetailActivity.this,
                                "Lỗi kết nối server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
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
