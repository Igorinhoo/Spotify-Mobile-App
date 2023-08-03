package com.app.spotifyapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spotifyapp.Adapters.TopArtistsRecyclerViewAdapter;
import com.app.spotifyapp.Adapters.TopTracksRecyclerViewAdapter;
import com.app.spotifyapp.Interfaces.Listeners.OnTrackClickListener;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.PlayTrackActivity;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.StatisticsAPIDataProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentStatisticsBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;


public class StatisticsFragment extends Fragment{

    private final ArrayList<ArtistDAO> ArtistData = new ArrayList<>();
    private final ArrayList<TrackDAO> TrackData = new ArrayList<>();
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

        api.getTopTracks(MainActivity.getAccessToken(), trackData -> requireActivity().runOnUiThread(() -> TrackData.addAll(trackData)));
        api.getTopArtists(MainActivity.getAccessToken() , artistsData -> requireActivity().runOnUiThread(() -> ArtistData.addAll(artistsData)));
        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

//        api.getQUEUE(MainActivity.getAccessToken());

        _binding.tracks.setOnClickListener((view -> {
            _binding.topItemsRecycler.setLayoutParams(weigth0);
            _binding.topTracksRecycler.setLayoutParams(weigth1);

            _binding.topTracksRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
            TopTracksRecyclerViewAdapter adapter = new TopTracksRecyclerViewAdapter(getActivity(), TrackData, new OnTrackClickListener() {
                @Override
                public void onItemClick(TrackDAO track) {
                    Log.e("onItemClick: ", track.Name);
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
            _binding.topTracksRecycler.setAdapter(adapter);
        }));


        _binding.artists.setOnClickListener(view -> {
            _binding.topItemsRecycler.setLayoutParams(weigth1);
            _binding.topTracksRecycler.setLayoutParams(weigth0);

            _binding.topItemsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
            TopArtistsRecyclerViewAdapter adapter = new TopArtistsRecyclerViewAdapter(getActivity(), ArtistData, artist -> {
                Bundle bundle = new Bundle();
                bundle.putString("artistUri", artist.Id);
                bundle.putString("artistName", artist.Name);
                bundle.putString("artistImg", artist.Img);

                Navigation.findNavController(requireView()).navigate(R.id.action_statistics_to_artistAlbumsFragment, bundle);
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