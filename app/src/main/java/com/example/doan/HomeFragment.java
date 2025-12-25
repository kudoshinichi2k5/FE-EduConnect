package com.example.doan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.Article;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvArticles;
    private LinearLayout btnCohol, btnChat, btnFindMentor, btnBookmark;
    private TextView tvHelloUser;

    private ArticleAdapter articleAdapter;
    private final List<Article> articleList = new ArrayList<>();

    // 🔥 USER ID dùng cho bookmark
    private String uid = "";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvArticles = view.findViewById(R.id.rvArticles);
        btnCohol = view.findViewById(R.id.btnGoToOpportunities);
        btnChat = view.findViewById(R.id.btnGoToChat);
        btnFindMentor = view.findViewById(R.id.btnFindMentor);
        tvHelloUser = view.findViewById(R.id.tvHelloUser);
        btnBookmark = view.findViewById(R.id.btnGoToBookmark);

        // ===== LẤY USER INFO =====
        if (getActivity() != null) {
            SharedPreferences prefs =
                    getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

            // Lấy tên người dùng
            String username = prefs.getString("USERNAME", "Bạn");

            // --- SỬA ĐỔI Ở ĐÂY: Dùng getString có tham số (%s) ---
            // Nó sẽ tự lấy chuỗi "Xin chào, %s! 👋" hoặc "Hello, %s! 👋" tùy ngôn ngữ
            tvHelloUser.setText(getString(R.string.hello_user, username));

            uid = prefs.getString("USER_ID", "");
        }

        // ===== SETUP RECYCLERVIEW =====
        rvArticles.setLayoutManager(new LinearLayoutManager(getContext()));

        // 🔥 TRUYỀN UID VÀO ADAPTER
        articleAdapter = new ArticleAdapter(articleList, requireContext(), uid);
        rvArticles.setAdapter(articleAdapter);

        loadArticlesFromApi();

        // ===== NAVIGATION =====
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

        // ===== SỰ KIỆN CLICK NÚT MENTOR =====
        btnFindMentor.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, new MentorFragment())
                        .addToBackStack(null) // QUAN TRỌNG
                        .commit();
            }
        });

        // ===== SỰ KIỆN CLICK NÚT BOOKMARK =====
        btnBookmark.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, new BookmarkFragment())
                        .addToBackStack(null) // QUAN TRỌNG
                        .commit();
            }
        });

        return view;
    }

    // ===== LOAD ARTICLE =====
    private void loadArticlesFromApi() {
        ApiClient.getClient()
                .create(ApiService.class)
                .getAllArticles()
                .enqueue(new Callback<List<Article>>() {

                    @Override
                    public void onResponse(
                            Call<List<Article>> call,
                            Response<List<Article>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            articleList.clear();
                            articleList.addAll(response.body());
                            articleAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Article>> call, Throwable t) {
                    }
                });
    }
}
