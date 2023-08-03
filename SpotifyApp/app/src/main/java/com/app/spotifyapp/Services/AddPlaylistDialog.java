package com.app.spotifyapp.Services;

import android.app.Dialog;
import android.widget.EditText;
import android.widget.Switch;

import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.PlaylistsAPIProvider;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AddPlaylistDialog {

    private EditText name, description;
    private SwitchMaterial aSwitch;
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
}
