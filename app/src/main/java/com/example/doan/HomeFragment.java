package com.example.doan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Ánh xạ
        rvArticles = view.findViewById(R.id.rvArticles);
        btnCohol = view.findViewById(R.id.btnGoToOpportunities);
        btnChat = view.findViewById(R.id.btnGoToChat);

        // 2. Setup List Bài viết (Fake data)
        rvArticles.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Phương pháp Pomodoro là gì?", "Cách quản lý thời gian hiệu quả cho sinh viên mùa thi."));
        articles.add(new Article("Top 5 kỹ năng mềm cần thiết", "Nhà tuyển dụng tìm kiếm gì ở sinh viên mới ra trường?"));
        articles.add(new Article("Học tiếng Anh qua phim ảnh", "Vừa giải trí vừa nâng trình IELTS hiệu quả."));
        articles.add(new Article("Cách viết CV ấn tượng", "Hướng dẫn chi tiết từng bước để có CV chuẩn chỉnh."));

        ArticleAdapter adapter = new ArticleAdapter(articles);
        rvArticles.setAdapter(adapter);

        // 3. Xử lý sự kiện bấm nút

        // Bấm nút "Khám phá Cơ hội" -> Chuyển sang Tab Cơ hội
        btnCohol.setOnClickListener(v -> {
            if (getActivity() instanceof Home) {
                ((Home) getActivity()).switchToTab(R.id.nav_opportunities);
            }
        });

        // Bấm nút "Chat ngay" -> Chuyển sang Tab Chatbot
        btnChat.setOnClickListener(v -> {
            if (getActivity() instanceof Home) {
                ((Home) getActivity()).switchToTab(R.id.nav_chatbot);
            }
        });

        return view;
    }
}