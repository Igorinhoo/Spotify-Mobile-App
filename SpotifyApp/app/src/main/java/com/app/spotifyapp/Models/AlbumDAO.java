package com.app.spotifyapp.Models;

import java.util.ArrayList;

public class AlbumDAO {
    public String AlbumName;
    public String Uri;
    public String AlbumImg;

    public AlbumDAO(String AlbumName, String Uri, String AlbumImg){
        this.AlbumName = AlbumName;
        this.Uri = Uri;
        this.AlbumImg = AlbumImg;
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
