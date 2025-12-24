package com.example.doan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // 1. Thêm import này
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.BookmarkItem;
import com.example.doan.model.BookmarkListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkFragment extends Fragment {

    RecyclerView rvBookmarks;
    List<BookmarkItem> list = new ArrayList<>();
    BookmarkAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        // --- 1. XỬ LÝ NÚT BACK (MỚI) ---
        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Kiểm tra lịch sử Fragment
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                // Nếu có trang trước -> Quay lại
                getParentFragmentManager().popBackStack();
            } else {
                // Nếu không có (đứng trơ trọi) -> Về trang chủ
                // LƯU Ý: R.id.frame_container là ID khung chứa trong activity_main.xml của bạn
                // Hãy thay đổi nếu ID của bạn khác (vd: R.id.fragment_container)
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new HomeFragment())
                        .commit();
            }
        });

        // --- 2. SETUP RECYCLERVIEW (GIỮ NGUYÊN) ---
        rvBookmarks = view.findViewById(R.id.rvBookmarks);
        rvBookmarks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookmarkAdapter(list, requireContext());
        rvBookmarks.setAdapter(adapter);

        // Load dữ liệu lần đầu
        loadBookmarks();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load lại mỗi khi quay lại màn hình này (để cập nhật nếu có thay đổi)
        loadBookmarks();
    }


    // --- 3. GỌI API (GIỮ NGUYÊN CẤU TRÚC CŨ) ---
    private void loadBookmarks() {
        SharedPreferences sp =
                requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String uid = sp.getString("USER_ID", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getBookmarksByUser(uid)
                .enqueue(new Callback<BookmarkListResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<BookmarkListResponse> call,
                            @NonNull Response<BookmarkListResponse> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            list.clear();
                            list.addAll(response.body().getBookmarks());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<BookmarkListResponse> call,
                            @NonNull Throwable t
                    ) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(),
                                    "Không tải được bookmark",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}