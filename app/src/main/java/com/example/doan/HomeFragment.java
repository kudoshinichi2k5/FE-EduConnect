package com.example.doan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private LinearLayout btnCohol, btnChat;
    private TextView tvHelloUser;

    private ArticleAdapter articleAdapter;
    private final List<Article> articleList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ================== ÁNH XẠ ==================
        rvArticles = view.findViewById(R.id.rvArticles);
        btnCohol = view.findViewById(R.id.btnGoToOpportunities);
        btnChat = view.findViewById(R.id.btnGoToChat);
        tvHelloUser = view.findViewById(R.id.tvHelloUser);

        // ================== HIỂN THỊ TÊN USER ==================
        if (getActivity() != null) {
            SharedPreferences prefs =
                    getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String username = prefs.getString("USERNAME", "Bạn");
            tvHelloUser.setText("Xin chào, " + username + "! 👋");
        }

        // ================== SETUP ARTICLE LIST ==================
        rvArticles.setLayoutManager(new LinearLayoutManager(getContext()));
        rvArticles.setNestedScrollingEnabled(false);

        articleAdapter = new ArticleAdapter(articleList, requireContext());
        rvArticles.setAdapter(articleAdapter);

        loadArticlesFromApi();

        // ================== BUTTON EVENTS ==================
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

    // ================== CALL API ==================
    private void loadArticlesFromApi() {
        ApiClient.getClient()
                .create(ApiService.class)
                .getAllArticles()
                .enqueue(new Callback<List<Article>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<List<Article>> call,
                            @NonNull Response<List<Article>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            articleList.clear();
                            articleList.addAll(response.body());
                            articleAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(
                                    getContext(),
                                    "Không tải được bài viết",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<Article>> call,
                            @NonNull Throwable t
                    ) {
                        Toast.makeText(
                                getContext(),
                                "Lỗi kết nối server",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
