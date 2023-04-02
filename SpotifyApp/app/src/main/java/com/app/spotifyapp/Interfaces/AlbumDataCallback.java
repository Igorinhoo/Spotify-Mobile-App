package com.app.spotifyapp.Interfaces;

import com.app.spotifyapp.Models.AlbumDAO;

import java.util.ArrayList;

public interface AlbumDataCallback {
    void onAlbumDataReceived(ArrayList<AlbumDAO> albumData);
}