package com.app.spotifyapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.PlayTrackActivity;
import com.app.spotifyapp.Repositories.ApiDataProvider;
import com.app.spotifyapp.Repositories.PlaylistsAPIProvider;
import com.app.spotifyapp.Services.AddPlaylistDialog;
import com.app.spotifyapp.Services.AddToPlaylistDialog;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentAlbumsTrackListBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AlbumsTrackListFragment extends Fragment {

    private String img = "";
    private SpotifyAppRemote _SpotifyAppRemote;

    private FragmentAlbumsTrackListBinding _binding;
    private String albumId;
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
            getTracks(albumId);
        }
    }


    public void getTracks(String Id){
        final ArrayList<TrackDAO>[] trackData = new ArrayList[]{new ArrayList<>()} ;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        _binding.trackListRecycler.setLayoutManager(linearLayoutManager);
        ArrayList<TrackDAO> finalTrackData = new ArrayList<>();

        TracksRecyclerViewAdapter adapter = new TracksRecyclerViewAdapter(getActivity(), finalTrackData, new OnTrackClickListener() {
            @Override
            public void onItemClick(TrackDAO track, int position) {
                if (_SpotifyAppRemote != null) {
                    _SpotifyAppRemote.getPlayerApi().skipToIndex("spotify:album:"+ albumId, position);
//                    _SpotifyAppRemote.getPlayerApi().play("spotify:track:" + track.Id);
                    Intent intent = new Intent(getActivity(), PlayTrackActivity.class);
                    intent.putExtra("TrackHref", track.Id);
                    startActivity(intent);
                }
            }
            @Override
            public void onLongClick(TrackDAO track) {
                PlaylistsAPIProvider api = new PlaylistsAPIProvider(MainActivity.getAccessToken());
                try {
                    // TODO: 8/21/2023 Change that playlist that it will be saved to is selected from list
//                    Dialog dialog = new Dialog(requireContext());
//                    AddToPlaylistDialog addPlaylistDialog = new AddToPlaylistDialog(dialog, requireActivity());
//                    addPlaylistDialog.DialogWork();
//                    dialog.show();

                    DialogFragment dialog = new AddToPlaylistDialog(requireActivity(), track);
                    dialog.show(getParentFragmentManager(), "Add dialog");

//                    api.AddToPlaylist(MainActivity.getAccessToken(), "7hnrA0Ngd2yNhRdHSWYklL", new String[]{"spotify:track:" + track.Id});
                    Toast.makeText(requireContext(), "Added " + track.Name , Toast.LENGTH_SHORT).show();
                    Log.e("onLongClick: ", track.Name);
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
            _binding.tvAlbumLength.setText(GetAlbumLength(tracksData));
            trackData[0].clear();
            trackData[0].addAll(tracksData);
            adapter.UpdateData(trackData[0]);
            adapter.notifyDataSetChanged();
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