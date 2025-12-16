package com.example.doan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OpportunityDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvDate, tvDeadline, tvDesc;
    Button btnRegister;
    ImageView ivBack;
    String linkUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity_detail);

        mapping();

        // 1. Nhận dữ liệu từ Intent (Do Adapter gửi qua)
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("TITLE");
            String desc = intent.getStringExtra("DESC");
            String date = intent.getStringExtra("DATE");
            String deadline = intent.getStringExtra("DEADLINE");
            linkUrl = intent.getStringExtra("LINK"); // Lưu link lại để dùng cho nút bấm

            // Hiển thị lên giao diện
            tvTitle.setText(title);
            tvDesc.setText(desc);
            tvDate.setText("Ngày đăng: " + date);
            tvDeadline.setText("Hạn chót: " + deadline);
        }

        // 2. Xử lý nút Back
        ivBack.setOnClickListener(v -> finish());

        // 3. Xử lý nút Đăng ký -> Mở trình duyệt web
        btnRegister.setOnClickListener(v -> {
            if (linkUrl != null && !linkUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "Không tìm thấy link đăng ký!", Toast.LENGTH_SHORT).show();
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