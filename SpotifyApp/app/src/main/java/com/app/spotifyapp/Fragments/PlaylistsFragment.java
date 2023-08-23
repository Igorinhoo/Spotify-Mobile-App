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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spotifyapp.Adapters.AlbumsRecyclerViewAdapter;
import com.app.spotifyapp.Genres.GenresActivity;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.PlaylistsAPIProvider;
import com.app.spotifyapp.Services.AddPlaylistDialog;
import com.app.spotifyapp.databinding.FragmentPlaylistsBinding;

import java.util.ArrayList;

public class PlaylistsFragment extends Fragment {


    private FragmentPlaylistsBinding _binding;
    private PlaylistsAPIProvider api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            api = new PlaylistsAPIProvider(MainActivity.getAccessToken());
        }catch (Exception e){
            Log.e("EXCE", e.getMessage() );
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        GetPlaylists();

        _binding.btnAddPlaylist.setOnClickListener(view -> {
            Dialog dialog = new Dialog(requireContext());
            AddPlaylistDialog addPlaylistDialog = new AddPlaylistDialog(dialog);
            addPlaylistDialog.DialogWork(api);
            dialog.show();
            Toast.makeText(requireContext(), "Adding...", Toast.LENGTH_SHORT).show();
        });
    }
    private void GetPlaylists(){
        ArrayList<AlbumDAO> playlists = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        _binding.rvPlaylists.setLayoutManager(linearLayoutManager);

        AlbumsRecyclerViewAdapter adapter = new AlbumsRecyclerViewAdapter(requireContext(), playlists, album -> {
            Bundle bundle = new Bundle();
            bundle.putString("playlistID", album.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_playlists_to_playlistTrackListFragment, bundle);

            Toast.makeText(requireContext(), album.getName(), Toast.LENGTH_LONG).show();
        });
        _binding.rvPlaylists.setAdapter(adapter);

//        try {
//            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                api.GetPlaylists(playlistData -> {
                    requireActivity().runOnUiThread(() -> {
                        playlists.clear();
                        playlists.addAll(playlistData);
                        adapter.UpdateData(playlistData);
                        adapter.notifyDataSetChanged();

                    });
                });
//            }, 1000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}