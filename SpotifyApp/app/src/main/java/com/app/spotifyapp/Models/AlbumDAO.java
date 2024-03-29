package com.app.spotifyapp.Models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class AlbumDAO {
    public String getName() {
        return name;
    }

    @Nullable
    public String getReleaseDate() {
        return releaseDate;
    }
    public String getId() {return id;}

    public Url[] getImages() {
        return images;
    }

    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("images")
    private Url[] images;

    @Nullable
    @SerializedName("release_date")
    private String releaseDate;
    @Nullable
    @SerializedName("snapshot_id")
    public String SnapshotID;

    public AlbumDAO(){}

    public AlbumDAO(String Name, String Id, Url[] Img, @Nullable String SnapshotID) {
        this.name = Name;
        this.id = Id;
        this.images = Img;
        this.SnapshotID = SnapshotID;
    }

    public AlbumDAO(String Name, String Id, String Img, @Nullable String SnapshotID) {
        this.name = Name;
        this.id = Id;
        Url url = new Url();
        url.setUrl(Img);
        this.images = new Url[]{url};
        this.SnapshotID = SnapshotID;
    }
}
