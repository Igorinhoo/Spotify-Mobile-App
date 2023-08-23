package com.app.spotifyapp.Services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spotifyapp.Adapters.AlbumsRecyclerViewAdapter;
import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.PlaylistsAPIProvider;

import java.util.ArrayList;

public class AddToPlaylistDialog extends DialogFragment {

    private final Activity context;
    private final PlaylistsAPIProvider api;
    private final AlbumsRecyclerViewAdapter adapter;
    private final TrackDAO track;

    public AddToPlaylistDialog(Activity context, TrackDAO track){
        this.context = context;
        this.track = track;
        api = new PlaylistsAPIProvider(MainActivity.getAccessToken());

        ArrayList<AlbumDAO> playlists = new ArrayList<>();

        adapter = new AlbumsRecyclerViewAdapter(context, playlists, playlist -> {
            api.AddToPlaylist(MainActivity.getAccessToken(), playlist.getId(), new String[]{"spotify:track:" + this.track.Id});
            dismiss();
        });

        api.GetPlaylists(playlistData -> {
            context.runOnUiThread(() -> {
                playlists.clear();
                playlists.addAll(playlistData);
                adapter.UpdateData(playlistData);
                adapter.notifyDataSetChanged();

            });
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.add_to_playlist, null);

        Button createNewPlaylist = rootView.findViewById(R.id.btnCreateNewPlaylist);
        createNewPlaylist.setOnClickListener(view -> {
            Dialog dialog = new Dialog(context);
            AddPlaylistDialog addPlaylistDialog = new AddPlaylistDialog(dialog);
            addPlaylistDialog.DialogWork(api, track);
            dismiss();
            dialog.show();
        });

        RecyclerView playlists = rootView.findViewById(R.id.rvChoosePlaylist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        playlists.setLayoutManager(linearLayoutManager);

        playlists.setAdapter(adapter);


        builder.setView(rootView);
        return builder.create();
    }

}
