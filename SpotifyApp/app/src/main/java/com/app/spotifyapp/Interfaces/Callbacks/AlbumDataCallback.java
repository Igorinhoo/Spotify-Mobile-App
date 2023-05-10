package com.app.spotifyapp.Interfaces.Callbacks;

import com.app.spotifyapp.Models.AlbumDAO;

import java.util.ArrayList;

public interface AlbumDataCallback {
    void onAlbumDataReceived(ArrayList<AlbumDAO> albumData);
}