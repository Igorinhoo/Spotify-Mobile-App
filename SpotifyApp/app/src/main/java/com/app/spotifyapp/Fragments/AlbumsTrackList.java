package com.app.spotifyapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.spotifyapp.Adapters.TracksRecyclerViewAdapter;
import com.app.spotifyapp.Interfaces.OnTrackClickListener;
import com.app.spotifyapp.Interfaces.Callbacks.TrackDataCallback;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.PlayTrackActivity;
import com.app.spotifyapp.Repositories.ApiDataProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentAlbumsTrackListBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;

public class AlbumsTrackList extends Fragment {

    private final ArrayList<String> hrefs = new ArrayList<>() ;
    private final ArrayList<String> duration = new ArrayList<>();
    private String img = "";
    private SpotifyAppRemote _SpotifyAppRemote;

    private FragmentAlbumsTrackListBinding _binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConnectToRemote();
        _binding = FragmentAlbumsTrackListBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {

            img = bundle.getString("albumImg");
            _binding.album.setText(bundle.getString("selectedAlbum"));
            getTracks(bundle.getString("albumHref"));
        }
    }

    public void ConnectToRemote(){
//        SpotifyAppRemoteConnector.Connect(getActivity());
        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
    }

    public void getTracks(String href){
        final ArrayList<TrackDAO>[] trackData = new ArrayList[]{new ArrayList<>()} ;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        _binding.trackListRecycler.setLayoutManager(linearLayoutManager);
        ArrayList<TrackDAO> finalTrackData = new ArrayList<>();

        String finalHref = "";

        TracksRecyclerViewAdapter adapter = new TracksRecyclerViewAdapter(getActivity(), finalTrackData, new OnTrackClickListener() {
            @Override
            public void onItemClick(int position) {
                if (_SpotifyAppRemote != null) {
                    _SpotifyAppRemote.getPlayerApi().play("spotify:track:" + hrefs.get(position));
                    Intent intent = new Intent(getActivity(), PlayTrackActivity.class);
                    intent.putExtra("TrackHref" ,hrefs.get(position));
                    startActivity(intent);
                }
            }
        });
        _binding.trackListRecycler.setAdapter(adapter);

        ApiDataProvider api = new ApiDataProvider();
        finalHref = "https://api.spotify.com/v1/albums/" + href +"/tracks?market=GB&limit=40";

        api.getAlbumTracks(finalHref, img, new TrackDataCallback() {
            @Override
            public void onTrackDataReceived(ArrayList<TrackDAO> trackDatas) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        trackData[0].clear();
                        trackData[0].addAll(trackDatas);
                        adapter.UpdateData(trackData[0]);


                        for (TrackDAO item : trackDatas
                        ) {
                            hrefs.add(item.Uri);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }

        });

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}


// Subscribe to PlayerState
//        mSpotifyAppRemote.getPlayerApi()
//                .subscribeToPlayerState()
//                .setEventCallback(playerState -> {
//                    final Track track = playerState.track;
//                    if (track != null) {
//                        Log.d("MainActivity", track.name + " by " + track.artist.name);
////                        Toast.makeText(MainActivity.this, track.name + " by " + track.artist.name, Toast.LENGTH_LONG).show();
//                    }
//                    else{
//                        Log.d("MainActivity", "Not working");
//
//                    }
//
//                });