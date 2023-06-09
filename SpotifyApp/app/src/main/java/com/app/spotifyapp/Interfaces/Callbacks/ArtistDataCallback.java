package com.app.spotifyapp.Interfaces.Callbacks;

import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.Models.TrackDAO;

import java.util.ArrayList;

public interface ArtistDataCallback {
    void onArtistsDataReceived(ArrayList<ArtistDAO> artistsData);
}
