package com.app.spotifyapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spotifyapp.Adapters.TopTracksRecyclerViewAdapter;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.Repositories.StatisticsAPIDataProvider;
import com.app.spotifyapp.databinding.FragmentQueueBinding;

import java.util.ArrayList;

public class QueueFragment extends Fragment {
    private FragmentQueueBinding _binding;
    private final ArrayList<TrackDAO> QueueData = new ArrayList<>();

    private TopTracksRecyclerViewAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentQueueBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        _binding.rvQueue.setLayoutManager(new LinearLayoutManager(requireActivity()));

        adapter = new TopTracksRecyclerViewAdapter(getActivity(), QueueData);

        getData();
        _binding.rvQueue.setAdapter(adapter);
    }

    private void getData(){
        new StatisticsAPIDataProvider().getQUEUE(MainActivity.getAccessToken(), data-> requireActivity().runOnUiThread(()->{
            QueueData.clear();
            QueueData.addAll(data);
            adapter.UpdateData(QueueData);
            adapter.notifyDataSetChanged();
        }));

    }
}