package com.app.spotifyapp.Models;

import androidx.annotation.Nullable;

public class TrackDAO {
    public final String Name;
    public final String Id;
    @Nullable
    public final String Img;
    @Nullable
    public final String Artists;
    public final Long Duration;


    public TrackDAO(String name, String id, Long duration, @Nullable String img, @Nullable String artists){
        Name = name;
        Id = id;
        Img = img;
        Duration = duration;
        Artists = artists;
    }

}
