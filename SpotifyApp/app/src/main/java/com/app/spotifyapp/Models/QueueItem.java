package com.app.spotifyapp.Models;

import com.google.gson.annotations.SerializedName;

public class QueueItem {
    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }

    public AlbumDAO getAlbum() {
        return album;
    }
    public Long getDuration() {
        return duration;
    }

    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("album")
    private AlbumDAO album;
    @SerializedName("duration")
    private Long duration;
}
