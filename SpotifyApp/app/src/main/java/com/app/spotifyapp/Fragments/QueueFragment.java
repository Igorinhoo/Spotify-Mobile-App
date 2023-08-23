package com.app.spotifyapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.spotifyapp.Adapters.TopTracksRecyclerViewAdapter;
import com.app.spotifyapp.Interfaces.Listeners.OnTrackClickListener;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.PlayTrackActivity;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.StatisticsAPIDataProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentQueueBinding;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;

public class QueueFragment extends Fragment {
    private FragmentQueueBinding _binding;
    private SpotifyAppRemote _SpotifyAppRemote;
    private final ArrayList<TrackDAO> QueueData = new ArrayList<>();

    TopTracksRecyclerViewAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();

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

        adapter = new TopTracksRecyclerViewAdapter(getActivity(), QueueData, new OnTrackClickListener() {
            @Override
            public void onItemClick(TrackDAO track, int position) {
                // TODO: 8/8/2023 Maybe try making own function to play track or playlist
                if (_SpotifyAppRemote != null) {
                    _SpotifyAppRemote.getPlayerApi().play("spotify:track:" + track.Id);
                    Intent intent = new Intent(getActivity(), PlayTrackActivity.class);
                    intent.putExtra("TrackHref", track.Id);
                    startActivity(intent);
                }
            }


            @Override
            public void onLongClick(TrackDAO track) {

            }
        });
        getData();
        _binding.rvQueue.setAdapter(adapter);
    }

    void getData(){
        new StatisticsAPIDataProvider().getQUEUE(MainActivity.getAccessToken(), data->{
            requireActivity().runOnUiThread(()->{
                QueueData.clear();
                QueueData.addAll(data);
                adapter.UpdateData(QueueData);
                adapter.notifyDataSetChanged();
            });

        } );

    }
}