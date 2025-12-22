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

public class MentorListFragment extends Fragment {
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentor_list, container, false);
        recyclerView = view.findViewById(R.id.rvMentorList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadDataFromApi();
        return view;
    }

    private void loadDataFromApi() {
        ApiClient.getClient().create(ApiService.class).getAllMentors().enqueue(new Callback<List<Mentor>>() {
            @Override
            public void onResponse(Call<List<Mentor>> call, Response<List<Mentor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MentorAdapter adapter = new MentorAdapter(getContext(), response.body(), mentor -> openDetail(mentor));
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Mentor>> call, Throwable t) {
                if (getContext() != null) Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDetail(Mentor mentor) {
        MentorDetailFragment detailFragment = new MentorDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("mentor_data", mentor);
        detailFragment.setArguments(args);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_container, detailFragment) // ID frame_container của Home
                .addToBackStack(null)
                .commit();
    }
}