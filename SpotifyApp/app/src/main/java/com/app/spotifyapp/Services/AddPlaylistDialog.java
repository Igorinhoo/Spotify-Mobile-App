package com.app.spotifyapp.Services;

import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.app.spotifyapp.MainActivity;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.PlaylistsAPIProvider;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AddPlaylistDialog {

    private EditText name, description;
    private Switch aSwitch;
    private final Dialog dialog;
    public AddPlaylistDialog(Dialog dialog){
        this.dialog = dialog;
    }

    public void DialogWork(PlaylistsAPIProvider api){
        dialog.setContentView(R.layout.add_playlist);
        name = dialog.findViewById(R.id.playlistName);
        description = dialog.findViewById(R.id.playlistDescription);
        aSwitch = dialog.findViewById(R.id.playlistPublic);
        dialog.findViewById(R.id.addingCancel).setOnClickListener(view -> dialog.cancel());
        dialog.findViewById(R.id.adder).setOnClickListener(view -> {
            String Name = name.getText().toString();
            String Description = description.getText().toString();
            boolean Public = aSwitch.isChecked();

            api.CreatePlaylist(Name, Description, Public);
            dialog.dismiss();
        });
    }

    public void DialogWork(PlaylistsAPIProvider api, TrackDAO track){
        dialog.setContentView(R.layout.add_playlist);
        name = dialog.findViewById(R.id.playlistName);
        description = dialog.findViewById(R.id.playlistDescription);
        aSwitch = dialog.findViewById(R.id.playlistPublic);
        dialog.findViewById(R.id.addingCancel).setOnClickListener(view -> dialog.cancel());
        dialog.findViewById(R.id.adder).setOnClickListener(view -> {
            String Name = name.getText().toString();
            String Description = description.getText().toString();
            boolean Public = aSwitch.isChecked();

            api.CreatePlaylist(Name, Description, Public, data -> api.AddToPlaylist(MainActivity.getAccessToken(), data, new String[]{"spotify:track:" + track.Id}));
            dialog.dismiss();
        });
    }
}
