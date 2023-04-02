package com.app.spotifyapp.Interfaces;

import com.app.spotifyapp.Models.TrackDAO;

import java.util.ArrayList;

public interface TrackDataCallback {
    void onTrackDataReceived(ArrayList<TrackDAO> albumData);
}
