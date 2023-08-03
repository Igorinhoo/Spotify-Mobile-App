package com.app.spotifyapp.Models;

public class SearchDAO {
    public String Type;
    public String Name;
    public String Img;
    public String Id;

    public SearchDAO(){}
    public SearchDAO(String Type,String Name, String Img, String Id){
        this.Type = Type;
        this.Name = Name;
        this.Img = Img;
        this.Id = Id;
    }


}
