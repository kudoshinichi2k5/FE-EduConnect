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

import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.Opportunity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpportunitiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private OpportunityAdapter adapter;
    private SearchView searchView;

    // Danh sách gốc từ backend
    private List<Opportunity> mList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_opportunities, container, false);

        recyclerView = view.findViewById(R.id.rvOpportunities);
        searchView = view.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OpportunityAdapter(mList, getContext());
        recyclerView.setAdapter(adapter);

        // Gọi API lấy dữ liệu
        fetchOpportunities();

        // Search client-side
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        return view;
    }

    /**
     * Gọi API GET /api/opportunity
     */
    private void fetchOpportunities() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getAllOpportunities().enqueue(new Callback<List<Opportunity>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<Opportunity>> call,
                    @NonNull Response<List<Opportunity>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    mList.clear();
                    mList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(),
                            "Không tải được dữ liệu cơ hội",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<Opportunity>> call,
                    @NonNull Throwable t
            ) {
                Toast.makeText(getContext(),
                        "Lỗi kết nối server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Lọc danh sách theo tiêu đề (client-side)
     */
    private void filterList(String text) {
        List<Opportunity> filteredList = new ArrayList<>();

        for (Opportunity item : mList) {
            if (item.getTitle() != null &&
                    item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.setFilteredList(filteredList);
    }
}
