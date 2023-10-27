package com.app.spotifyapp.Models;

import androidx.annotation.Nullable;

public class SearchDAO {
    public String Type;
    public String Name;
    public String Img;
    public String Id;
    @Nullable
    public String ReleaseDate;
    public SearchDAO(){}
    public SearchDAO(String Type,String Name, String Img, String Id, @Nullable String ReleaseDate){
        this.Type = Type;
        this.Name = Name;
        this.Img = Img;
        this.Id = Id;
        this.ReleaseDate = ReleaseDate;
    }


}
