package com.app.spotifyapp.Models;

import androidx.annotation.Nullable;

public class AlbumDAO {
    public final String Name;
    public final String Id;
    public final String Img;
    @Nullable
    public String SnapshotID;

    public AlbumDAO(String Name, String Id, String Img, @Nullable String SnapshotID) {
        this.Name = Name;
        this.Id = Id;
        this.Img = Img;
        this.SnapshotID = SnapshotID;
    }
}
