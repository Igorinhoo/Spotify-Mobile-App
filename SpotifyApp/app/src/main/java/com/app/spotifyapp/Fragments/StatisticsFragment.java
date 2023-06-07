package com.app.spotifyapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spotifyapp.Adapters.TopArtistsRecyclerViewAdapter;
import com.app.spotifyapp.Adapters.TopTracksRecyclerViewAdapter;
import com.app.spotifyapp.Interfaces.Callbacks.TopArtistsCallback;
import com.app.spotifyapp.Interfaces.Callbacks.TopTracksCallback;
import com.app.spotifyapp.Interfaces.Listeners.OnTopArtistClickListener;
import com.app.spotifyapp.Interfaces.Listeners.OnTopTrackClickListener;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.PlayTrackActivity;
import com.app.spotifyapp.Repositories.StatisticsAPIDataProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentStatisticsBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;


public class StatisticsFragment extends Fragment{

    private ArrayList<ArtistDAO> ArtistData = new ArrayList<>();
    private ArrayList<TrackDAO> TrackData = new ArrayList<>();
    private SpotifyAppRemote _SpotifyAppRemote;

    private FragmentStatisticsBinding _binding;
    private StatisticsAPIDataProvider api;
    private static final LinearLayout.LayoutParams weigth1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
    private static final LinearLayout.LayoutParams weigth0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0, 0);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = new StatisticsAPIDataProvider();
        weigth0.setMargins(8,8,8,8);
        weigth1.setMargins(8,8,8,8);

        api.getTopTracks(MainActivity.getAccessToken(), new TopTracksCallback() {
            @Override
            public void onTopTracksDataReceived(ArrayList<TrackDAO> trackData) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TrackData.addAll(trackData);
                    }
                });
            }
        });
        api.getTopArtists(MainActivity.getAccessToken() ,new TopArtistsCallback() {
            @Override
            public void onTopArtistsDataReceived(ArrayList<ArtistDAO> artistsData) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArtistData.addAll(artistsData);
                    }
                });
            }
        });
        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false);

        return _binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        api.getQUEUE(MainActivity.getAccessToken());


        _binding.tracks.setOnClickListener((view -> {
            _binding.topItemsRecycler.setLayoutParams(weigth0);
            _binding.topTracksRecycler.setLayoutParams(weigth1);

            _binding.topTracksRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
            TopTracksRecyclerViewAdapter adapter = new TopTracksRecyclerViewAdapter(getActivity(), TrackData, new OnTopTrackClickListener() {
                @Override
                public void onItemClick(int position) {
                    Log.e("onItemClick: ", TrackData.get(position).Name);
                    if (_SpotifyAppRemote != null) {
                        _SpotifyAppRemote.getPlayerApi().play("spotify:track:" + TrackData.get(position).Uri);
                        Intent intent = new Intent(getActivity(), PlayTrackActivity.class);
                        intent.putExtra("TrackHref", TrackData.get(position).Uri);
                        startActivity(intent);
                    }
                }
            });
            _binding.topTracksRecycler.setAdapter(adapter);
        }));


        _binding.artists.setOnClickListener(view -> {
            _binding.topItemsRecycler.setLayoutParams(weigth1);
            _binding.topTracksRecycler.setLayoutParams(weigth0);

            _binding.topItemsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
            TopArtistsRecyclerViewAdapter adapter = new TopArtistsRecyclerViewAdapter(getActivity(), ArtistData, new OnTopArtistClickListener() {
                @Override
                public void onItemClick(int position) {
                    Log.e("onItemClick: ", ArtistData.get(position).Name);
                    Toast.makeText(requireContext(), ArtistData.get(position).Name, Toast.LENGTH_SHORT).show();
                }
            });
            _binding.topItemsRecycler.setAdapter(adapter);
        });





    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}