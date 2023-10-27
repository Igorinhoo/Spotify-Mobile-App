package com.app.spotifyapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spotifyapp.Adapters.TracksRecyclerViewAdapter;
import com.app.spotifyapp.Interfaces.Listeners.OnTrackClickListener;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.PlayTrackActivity;
import com.app.spotifyapp.Repositories.ApiDataProvider;
import com.app.spotifyapp.Services.AddToPlaylistDialog;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentAlbumsTrackListBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AlbumsTrackListFragment extends Fragment {

    private String img = "";
    private SpotifyAppRemote _SpotifyAppRemote;
    private FragmentAlbumsTrackListBinding _binding;
    private String albumId, releaseDate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
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
            albumId = bundle.getString("albumHref");
            releaseDate = bundle.getString("albumReleaseDate");

            getTracks(albumId);
        }
    }


    public void getTracks(String Id){
        final ArrayList<TrackDAO>[] trackData = new ArrayList[]{new ArrayList<>()};

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        _binding.trackListRecycler.setLayoutManager(linearLayoutManager);
        ArrayList<TrackDAO> finalTrackData = new ArrayList<>();

        TracksRecyclerViewAdapter adapter = new TracksRecyclerViewAdapter(finalTrackData, new OnTrackClickListener() {
            @Override
            public void onItemClick(TrackDAO track, int position) {
                if (_SpotifyAppRemote != null) {
                    _SpotifyAppRemote.getPlayerApi().skipToIndex("spotify:album:" + albumId, position);
                    Intent intent = new Intent(getActivity(), PlayTrackActivity.class);
                    intent.putExtra("TrackHref", track.Id);
                    startActivity(intent);
                }
            }
            @Override
            public void onLongClick(TrackDAO track) {
                try {
                    DialogFragment dialog = new AddToPlaylistDialog(requireActivity(), track);
                    dialog.show(getParentFragmentManager(), "Add dialog");
                    Toast.makeText(requireContext(), "Added " + track.Name , Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Not added", Toast.LENGTH_SHORT).show();
                }
            }
        }
        );
        _binding.trackListRecycler.setAdapter(adapter);

        ApiDataProvider api = new ApiDataProvider();
        api.getAlbumTracks(Id, img, tracksData -> requireActivity().runOnUiThread(() -> {
            _binding.tvAlbumLength.setText(String.format("Time: %s min", GetAlbumLength(tracksData)));
            trackData[0].clear();
            trackData[0].addAll(tracksData);
            adapter.UpdateData(trackData[0]);
            adapter.notifyDataSetChanged();


            try {
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                Date date = inputFormat.parse(releaseDate);

                String formattedDate = outputFormat.format(Objects.requireNonNull(date));
                _binding.tvAlbumRelease.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }));

    }

    private String GetAlbumLength(ArrayList<TrackDAO> tracksData){
        long time = tracksData.stream().mapToLong(track -> track.Duration).sum();

        long min = TimeUnit.MILLISECONDS.toMinutes(time);
        long sec = TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));

        return min + ":" + (sec < 10 ? "0" + sec : sec);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}