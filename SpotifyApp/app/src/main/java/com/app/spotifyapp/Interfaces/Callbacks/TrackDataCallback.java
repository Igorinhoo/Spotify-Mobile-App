package com.app.spotifyapp.Interfaces.Callbacks;

import com.app.spotifyapp.Models.TrackDAO;

import java.util.ArrayList;

public interface TrackDataCallback {
    void onTrackDataReceived(ArrayList<TrackDAO> albumData);
}
