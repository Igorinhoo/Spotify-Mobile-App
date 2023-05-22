package com.app.spotifyapp.Interfaces.Callbacks;

import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.Models.TrackDAO;

import java.util.ArrayList;

public interface TopTracksCallback {
    void onTopTracksDataReceived(ArrayList<TrackDAO> trackData);
}