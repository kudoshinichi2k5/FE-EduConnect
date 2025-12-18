package com.example.doan;

import android.content.Context; // Import mới
import android.content.SharedPreferences; // Import mới
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView; // Import mới
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView rvArticles;
    LinearLayout btnCohol, btnChat;
    TextView tvHelloUser; // <--- 1. KHAI BÁO BIẾN HIỂN THỊ TÊN

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 2. ÁNH XẠ VIEW
        rvArticles = view.findViewById(R.id.rvArticles);
        btnCohol = view.findViewById(R.id.btnGoToOpportunities);
        btnChat = view.findViewById(R.id.btnGoToChat);
        tvHelloUser = view.findViewById(R.id.tvHelloUser); // <--- Tìm TextView bên layout XML

        // ==================================================================
        // 3. CODE MỚI: ĐỌC TÊN TỪ BỘ NHỚ VÀ HIỂN THỊ
        // ==================================================================

        // Mở file "UserPrefs" (Phải trùng tên với bên Login.java)
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

            // Lấy dữ liệu với key "USERNAME" (Phải trùng key bên Login.java)
            String username = sharedPreferences.getString("USERNAME", "Bạn");

            // Gán lên màn hình
            tvHelloUser.setText("Xin chào, " + username + "! 👋");
        }
        // ==================================================================


        // 4. Setup List Bài viết (Fake data - Giữ nguyên code của bạn)
        rvArticles.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Phương pháp Pomodoro là gì?", "Cách quản lý thời gian hiệu quả cho sinh viên mùa thi."));
        articles.add(new Article("Top 5 kỹ năng mềm cần thiết", "Nhà tuyển dụng tìm kiếm gì ở sinh viên mới ra trường?"));
        articles.add(new Article("Học tiếng Anh qua phim ảnh", "Vừa giải trí vừa nâng trình IELTS hiệu quả."));
        articles.add(new Article("Cách viết CV ấn tượng", "Hướng dẫn chi tiết từng bước để có CV chuẩn chỉnh."));

        ArticleAdapter adapter = new ArticleAdapter(articles);
        rvArticles.setAdapter(adapter);

        // 5. Xử lý sự kiện bấm nút (Giữ nguyên code của bạn)
        btnCohol.setOnClickListener(v -> {
            if (getActivity() instanceof Home) {
                ((Home) getActivity()).switchToTab(R.id.nav_opportunities);
            }
        });

        btnChat.setOnClickListener(v -> {
            if (getActivity() instanceof Home) {
                ((Home) getActivity()).switchToTab(R.id.nav_chatbot);
            }
        });

        return view;
    }
}