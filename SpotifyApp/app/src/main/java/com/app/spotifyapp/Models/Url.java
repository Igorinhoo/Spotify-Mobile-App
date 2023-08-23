package com.app.spotifyapp.Models;

import com.google.gson.annotations.SerializedName;

public class Url {
    public String getUrl() {
        return url;
    }
    public void setUrl(String Url) {
        this.url = Url;
    }


    @SerializedName("url")
    private String url;

}
