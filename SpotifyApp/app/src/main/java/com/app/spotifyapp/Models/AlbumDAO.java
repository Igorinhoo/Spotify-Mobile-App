package com.app.spotifyapp.Models;

public class AlbumDAO {
    public String Name;
    public String Uri;
    public String Img;

    public AlbumDAO(String Name, String Uri, String Img){
        this.Name = Name;
        this.Uri = Uri;
        this.Img = Img;
    }
//
//    public ArrayList<TrackDAO> albumToTrack(ArrayList<AlbumDAO> albums){
//        ArrayList<TrackDAO> tracks = new ArrayList<>();
//        for (AlbumDAO album:albums
//             ) {
//            tracks.add(new TrackDAO(album.AlbumName, album.Uri, null, album.AlbumImg));
//        }
//        return tracks;
//    }
}
