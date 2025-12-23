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
import com.example.doan.model.Mentor;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MentorFragment extends Fragment {

    private static final String TAG = "MentorFragment";

    private RecyclerView rvMentorList;
    private SearchView searchMentor;
    private MentorAdapter adapter;

    // GIỐNG Opportunities
    private final List<Mentor> fullList = new ArrayList<>();
    private final List<Mentor> displayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_mentor, container, false);

        rvMentorList = view.findViewById(R.id.rvMentorList);
        searchMentor = view.findViewById(R.id.searchMentor);

        rvMentorList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MentorAdapter(displayList, requireContext());
        rvMentorList.setAdapter(adapter);

        // LOAD ALL MENTOR
        fetchAllMentors();

        // SEARCH (CLIENT-SIDE) – GIỐNG Opportunities
        searchMentor.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        return view;
    }

    // ================= API =================

    private void fetchAllMentors() {
        Log.d(TAG, "fetchAllMentors()");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Mentor>> call = apiService.getAllMentors();

        Log.d(TAG, "API URL: " + call.request().url());

        call.enqueue(new Callback<List<Mentor>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<Mentor>> call,
                    @NonNull Response<List<Mentor>> response
            ) {
                Log.d(TAG, "onResponse - Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    fullList.clear();
                    fullList.addAll(response.body());

                    displayList.clear();
                    displayList.addAll(fullList);

                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "Mentor size: " + displayList.size());
                } else {
                    Toast.makeText(
                            requireContext(),
                            "Không tải được mentor",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<Mentor>> call,
                    @NonNull Throwable t
            ) {
                Log.e(TAG, "onFailure", t);
                Toast.makeText(
                        requireContext(),
                        "Lỗi kết nối server",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // ================= SEARCH =================

    private void applySearch(String keyword) {
        displayList.clear();

        if (keyword == null || keyword.trim().isEmpty()) {
            displayList.addAll(fullList);
        } else {
            String lower = keyword.toLowerCase();
            for (Mentor m : fullList) {
                if ((m.getHoTen() != null && m.getHoTen().toLowerCase().contains(lower)) ||
                        (m.getChuyenNganh() != null && m.getChuyenNganh().toLowerCase().contains(lower)) ||
                        (m.getNoiLamViec() != null && m.getNoiLamViec().toLowerCase().contains(lower))) {

                    displayList.add(m);
                }
            }
        }

        adapter.notifyDataSetChanged();
        Log.d(TAG, "Search result: " + displayList.size());
    }
}
