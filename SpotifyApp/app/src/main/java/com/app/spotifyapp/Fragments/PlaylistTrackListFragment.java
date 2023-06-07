package com.app.spotifyapp.Fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.spotifyapp.Adapters.AlbumsRecyclerViewAdapter;
import com.app.spotifyapp.Adapters.TracksRecyclerViewAdapter;
import com.app.spotifyapp.Interfaces.Listeners.OnTrackClickListener;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.PlayTrackActivity;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.PlaylistsAPIProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentPlaylistTrackListBinding;
import com.app.spotifyapp.databinding.FragmentPlaylistsBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;

public class PlaylistTrackListFragment extends Fragment {

    String PlaylistID;
    private FragmentPlaylistTrackListBinding _binding;
    private PlaylistsAPIProvider api;
    private SpotifyAppRemote _SpotifyAppRemote;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = new PlaylistsAPIProvider(MainActivity.getAccessToken());
        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        _binding = FragmentPlaylistTrackListBinding.inflate(inflater, container, false);

        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            PlaylistID = bundle.getString("playlistID");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        _binding.btnPlayPlaylist.setOnClickListener(view ->{
            _SpotifyAppRemote.getPlayerApi().play("spotify:playlist:" + PlaylistID);
        });

        GetTrackList();
    }

    void GetTrackList(){
        ArrayList<TrackDAO> tracks = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        _binding.rvPlaylistTrackList.setLayoutManager(linearLayoutManager);

        TracksRecyclerViewAdapter adapter = new TracksRecyclerViewAdapter(requireContext(), tracks, new OnTrackClickListener() {
                    @Override
                    public void onItemClick(TrackDAO track) {
                        if (_SpotifyAppRemote != null) {
                            _SpotifyAppRemote.getPlayerApi().play("spotify:track:" + track.Uri);
                            Intent intent = new Intent(getActivity(), PlayTrackActivity.class);
                            intent.putExtra("TrackHref", track.Uri);
                            startActivity(intent);
                        }
                        Toast.makeText(requireContext(), track.Name, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLongClick(TrackDAO track) {

                    }
                });

        _binding.rvPlaylistTrackList.setAdapter(adapter);


        try {
            new Handler().postDelayed(() -> {
                api.GetPlaylistItems(PlaylistID, trackListData -> {
                    requireActivity().runOnUiThread(() -> {
                        tracks.clear();
                        tracks.addAll(trackListData);
                        adapter.UpdateData(trackListData);

                        adapter.notifyDataSetChanged();

                    });
                });
            }, 1000);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}