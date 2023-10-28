package com.app.spotifyapp.Models;

public class ArtistFirebaseDAO {
    public String Name;
    public String Img;
    public String Uri;

    public ArtistFirebaseDAO(){}
    public ArtistFirebaseDAO(String Name, String Img, String Uri){
        this.Name = Name;
        this.Img = Img;
        this.Uri = Uri;
    }

    public ArtistDAO ChangeFirebaseToBase(){
        return new ArtistDAO(this.Name, this.Img, this.Uri);
    }
}
