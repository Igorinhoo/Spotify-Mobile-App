package com.app.spotifyapp.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spotifyapp.Adapters.TracksRecyclerViewAdapter;
import com.app.spotifyapp.Interfaces.Listeners.OnTrackClickListener;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.PlayTrackActivity;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.PlaylistsAPIProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentPlaylistTrackListBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;

public class PlaylistTrackListFragment extends Fragment {

    String PlaylistID;
    private FragmentPlaylistTrackListBinding _binding;
    private PlaylistsAPIProvider api;
    private SpotifyAppRemote _SpotifyAppRemote;
    private TracksRecyclerViewAdapter adapter;
    private ArrayList<TrackDAO> tracks;


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
        _binding.btnPlayPlaylist.setOnClickListener(view -> _SpotifyAppRemote.getPlayerApi().play("spotify:playlist:" + PlaylistID));

        GetTrackList();
    }

    void GetTrackList(){
        tracks = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        _binding.rvPlaylistTrackList.setLayoutManager(linearLayoutManager);

        adapter = new TracksRecyclerViewAdapter(requireContext(), tracks, new OnTrackClickListener() {
                    @Override
                    public void onItemClick(TrackDAO track) {
                        if (_SpotifyAppRemote != null) {
                            _SpotifyAppRemote.getPlayerApi().play("spotify:track:" + track.Id);
                            Intent intent = new Intent(getActivity(), PlayTrackActivity.class);
                            intent.putExtra("TrackHref", track.Id);
                            startActivity(intent);
                        }
                        Toast.makeText(requireContext(), track.Name, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLongClick(TrackDAO track) {
                        ShowDialog(track);
//                        Toast.makeText(requireContext(), "LONG CLICK", Toast.LENGTH_LONG).show();

                    }
                });
        _binding.rvPlaylistTrackList.setAdapter(adapter);

        try {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
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

    private void ShowDialog(TrackDAO track){
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.playlist_dialog);

        Button remove = dialog.findViewById(R.id.btn_remove);
        remove.setOnClickListener(view -> {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                api.GetPlaylist(PlaylistID, albumData -> {
                    try {
                        requireActivity().runOnUiThread(()->{
                            tracks.remove(track);
                            UpdateRecycler(tracks);
                            api.RemoveFromPlaylist(PlaylistID, albumData.SnapshotID, new String[]{"spotify:track:"+track.Id}, dialog);
                        });
                    }catch (Exception e){
                        Log.e("CAll play .", e.getMessage());
                    }
                });

            }, 600);
        });
        dialog.show();
    }

    /*private void ShowDialog(TrackDAO track){
        PopupMenu popupMenu = new PopupMenu(requireContext(), _binding.rvPlaylistTrackList);
        popupMenu.getMenuInflater().inflate(R.menu.playlist_dialog, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.remove:
                    new Handler().postDelayed(() -> {
                        api.GetPlaylist(PlaylistID, albumData -> {
                            Log.e("onLongClick: .", albumData.Name + " +" + albumData.snapshotID);
                            try {
                                tracks.remove(track);
                                UpdateRecycler(tracks);
                                api.RemoveFromPlaylist(PlaylistID, albumData.snapshotID, new String[]{"spotify:track:"+track.Uri});
                            }catch (Exception e){
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    }, 1000);

                    break;

            }
            return false;
        });
        popupMenu.show();*//*


    }*/

    private void UpdateRecycler(ArrayList<TrackDAO> NewTracks){
            adapter.UpdateData(NewTracks);
            adapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}