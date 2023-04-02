package com.app.spotifyapp.Models;

import androidx.annotation.Nullable;

public class TrackDAO {
    public String Name;
    public String Uri;
    @Nullable
    public String Img;
    public Long Duration;


    public TrackDAO(String name, String uri, Long duration, @Nullable String img){
        Name = name;
        Uri = uri;
        Img = img;
        Duration = duration;
    }

}
