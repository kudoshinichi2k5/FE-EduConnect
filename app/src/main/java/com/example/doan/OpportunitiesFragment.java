package com.example.doan;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OpportunitiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private OpportunityAdapter adapter;
    private SearchView searchView;
    private List<Opportunity> mList; // Danh sách gốc

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opportunities, container, false);

        recyclerView = view.findViewById(R.id.rvOpportunities);
        searchView = view.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 1. Tạo dữ liệu gốc
        mList = new ArrayList<>();
        mList.add(new Opportunity("Học bổng Doanh nghiệp kì Fall 2025", "Học bổng 100% học phí...", "13/12/2025", "30/01/2026", "https://daihoc.fpt.edu.vn"));
        mList.add(new Opportunity("Tuyển thực tập sinh Android/Java", "FPT Software tuyển dụng 50 Fresher...", "10/12/2025", "15/01/2026", "https://fpt-software.com"));
        mList.add(new Opportunity("Cuộc thi Hackathon EduTech", "Giải thưởng 100 triệu...", "05/12/2025", "20/12/2025", "https://devpost.com"));
        mList.add(new Opportunity("Hội thảo AI Google", "Gặp gỡ chuyên gia Google...", "01/12/2025", "10/12/2025", "https://google.com"));

        // 2. Gán Adapter ban đầu
        adapter = new OpportunityAdapter(mList, getContext());
        recyclerView.setAdapter(adapter);

        // 3. Bắt sự kiện gõ chữ trong ô tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Mỗi khi gõ 1 chữ, hàm này sẽ chạy
                filterList(newText);
                return true;
            }
        });

        return view;
    }

    // Hàm lọc danh sách
    private void filterList(String text) {
        List<Opportunity> filteredList = new ArrayList<>();

        // Vòng lặp kiểm tra từng tin
        for (Opportunity item : mList) {
            // Kiểm tra xem Tiêu đề có chứa từ khóa không (chuyển về chữ thường để so sánh)
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            // Nếu không tìm thấy
            Toast.makeText(getContext(), "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
        } else {
            // Cập nhật Adapter
            adapter.setFilteredList(filteredList);
        }
    }
}