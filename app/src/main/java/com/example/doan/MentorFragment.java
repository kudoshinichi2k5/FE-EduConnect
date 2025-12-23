package com.example.doan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private RecyclerView rvMentorList;
    private MentorAdapter adapter;

    // giống Opportunity: 1 list hiển thị
    private final List<Mentor> mentorList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_mentor, container, false);

        rvMentorList = view.findViewById(R.id.rvMentorList);
        rvMentorList.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MentorAdapter(mentorList, requireContext());
        rvMentorList.setAdapter(adapter);

        fetchAllMentors();
        return view;
    }

    private void fetchAllMentors() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getAllMentors().enqueue(new Callback<List<Mentor>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<Mentor>> call,
                    @NonNull Response<List<Mentor>> response
            ) {
                Log.d("MENTOR_API", "Code: " + response.code());
                Log.d("MENTOR_API", "Body: " + response.body());

                if (response.isSuccessful() && response.body() != null) {
                    mentorList.clear();
                    mentorList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(),
                            "Không tải được mentor",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<Mentor>> call,
                    @NonNull Throwable t
            ) {
                Toast.makeText(requireContext(),
                        "Lỗi kết nối server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
