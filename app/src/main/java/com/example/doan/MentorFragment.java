package com.example.doan;

import android.os.Bundle;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MentorFragment extends Fragment {

    private RecyclerView rvMentorList;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_mentor, container, false);

        rvMentorList = view.findViewById(R.id.rvMentorList);
        rvMentorList.setLayoutManager(new LinearLayoutManager(getContext()));

        loadMentors();

        return view;
    }

    private void loadMentors() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getAllMentors().enqueue(new Callback<List<Mentor>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<Mentor>> call,
                    @NonNull Response<List<Mentor>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    MentorAdapter adapter = new MentorAdapter(
                            getContext(),
                            response.body(),
                            mentor -> openMentorDetail(mentor)
                    );
                    rvMentorList.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Không tải được mentor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<Mentor>> call,
                    @NonNull Throwable t
            ) {
                Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMentorDetail(Mentor mentor) {
        MentorDetailActivity fragment = new MentorDetailActivity();

        Bundle bundle = new Bundle();
        bundle.putSerializable("mentor", mentor);
        fragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
