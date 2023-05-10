package com.app.spotifyapp.Interfaces.Callbacks;

import com.app.spotifyapp.Models.TrackDAO;

import java.util.ArrayList;

public interface SingleTrackCallback {
    void onTrackDataReceived(TrackDAO trackData);

}
