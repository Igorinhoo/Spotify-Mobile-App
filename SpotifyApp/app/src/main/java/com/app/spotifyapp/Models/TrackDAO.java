package com.app.spotifyapp.Models;

import androidx.annotation.Nullable;

public class TrackDAO {
    public String Name;
    public String Uri;
    @Nullable
    public String Img;
    @Nullable
    public String Artists;
    public Long Duration;


    public TrackDAO(String name, String uri, Long duration, @Nullable String img, @Nullable String artists){
        Name = name;
        Uri = uri;
        Img = img;
        Duration = duration;
        Artists = artists;
    }

}
