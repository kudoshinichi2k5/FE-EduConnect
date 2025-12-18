package com.example.doan;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpportunitiesFragment extends Fragment {

    private static final String TAG = "OpportunitiesFragment";

    private RecyclerView recyclerView;
    private OpportunityAdapter adapter;
    private SearchView searchView;
    private ChipGroup chipGroup;

    // TÁCH 2 LIST: fullList để search, displayList để hiển thị
    private final List<Opportunity> fullList = new ArrayList<>();
    private final List<Opportunity> displayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_opportunities, container, false);

        // Init views
        recyclerView = view.findViewById(R.id.rvOpportunities);
        searchView = view.findViewById(R.id.searchView);
        chipGroup = view.findViewById(R.id.chipGroupFilter);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new OpportunityAdapter(displayList, requireActivity());
        recyclerView.setAdapter(adapter);

        // Load tất cả opportunities lúc đầu
        fetchAll();

        // Search listener (client-side search)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applySearch(newText);
                return true;
            }
        });

        // Filter listener (backend API)
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Clear search khi đổi filter
            searchView.setQuery("", false);
            searchView.clearFocus();

            // Log để debug
            Log.d(TAG, "Chip clicked: " + checkedId);

            if (checkedId == R.id.chipAll) {
                Log.d(TAG, "Fetching ALL opportunities");
                fetchAll();
            } else if (checkedId == R.id.chipScholarship) {
                Log.d(TAG, "Fetching SCHOLARSHIP opportunities");
                fetchByType("scholarship");
            } else if (checkedId == R.id.chipContest) {
                Log.d(TAG, "Fetching CONTEST opportunities");
                fetchByType("contest");
            } else if (checkedId == R.id.chipEvent) {
                Log.d(TAG, "Fetching EVENT opportunities");
                fetchByType("event");
            }
        });

        return view;
    }

    // ================= API CALLS =================

    /**
     * Lấy tất cả opportunities
     */
    private void fetchAll() {
        Log.d(TAG, "fetchAll() called");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Opportunity>> call = apiService.getAllOpportunities();

        Log.d(TAG, "API URL: " + call.request().url());

        call.enqueue(new OpportunityCallback());
    }

    /**
     * Lọc theo type: scholarship, contest, event
     */
    private void fetchByType(String type) {
        Log.d(TAG, "fetchByType() called with type: " + type);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Opportunity>> call = apiService.getOpportunitiesByType(type);

        Log.d(TAG, "API URL: " + call.request().url());

        call.enqueue(new OpportunityCallback());
    }

    // ================= COMMON CALLBACK =================

    /**
     * Callback chung cho cả fetchAll và fetchByType
     */
    private class OpportunityCallback implements Callback<List<Opportunity>> {
        @Override
        public void onResponse(
                @NonNull Call<List<Opportunity>> call,
                @NonNull Response<List<Opportunity>> response
        ) {
            Log.d(TAG, "onResponse - Code: " + response.code());

            if (response.isSuccessful() && response.body() != null) {
                List<Opportunity> data = response.body();
                Log.d(TAG, "Data received: " + data.size() + " items");

                // Log chi tiết các items
                for (int i = 0; i < data.size(); i++) {
                    Opportunity opp = data.get(i);
                    Log.d(TAG, "Item " + i + ": " + opp.getTitle() + " - Type: " + opp.getType());
                }

                // Update fullList
                fullList.clear();
                fullList.addAll(data);

                // Update displayList
                displayList.clear();
                displayList.addAll(fullList);

                // Notify adapter
                adapter.notifyDataSetChanged();

                Log.d(TAG, "Adapter updated. DisplayList size: " + displayList.size());
            } else {
                Log.e(TAG, "Response not successful. Code: " + response.code());
                Toast.makeText(getContext(),
                        "Lỗi tải dữ liệu: " + response.code(),
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(
                @NonNull Call<List<Opportunity>> call,
                @NonNull Throwable t
        ) {
            Log.e(TAG, "onFailure: " + t.getMessage(), t);
            Toast.makeText(getContext(),
                    "Lỗi kết nối: " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ================= CLIENT-SIDE SEARCH =================

    /**
     * Search trên fullList (client-side)
     */
    private void applySearch(String keyword) {
        Log.d(TAG, "applySearch() with keyword: " + keyword);

        displayList.clear();

        if (keyword == null || keyword.trim().isEmpty()) {
            displayList.addAll(fullList);
        } else {
            String lowerKeyword = keyword.toLowerCase();
            for (Opportunity o : fullList) {
                if (o.getTitle() != null &&
                        o.getTitle().toLowerCase().contains(lowerKeyword)) {
                    displayList.add(o);
                }
            }
        }

        adapter.notifyDataSetChanged();
        Log.d(TAG, "Search result: " + displayList.size() + " items");
    }
}