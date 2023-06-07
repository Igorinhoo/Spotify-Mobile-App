package com.app.spotifyapp.Services;

import android.app.Dialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;

import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.PlaylistsAPIProvider;

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
//            Log.e("DialogWork: ", Name + " " + Description + " <" + Public);
        });
    }
}
