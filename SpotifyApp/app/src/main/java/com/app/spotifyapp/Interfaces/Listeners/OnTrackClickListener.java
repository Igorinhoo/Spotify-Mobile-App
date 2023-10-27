package com.app.spotifyapp.Interfaces.Listeners;

import com.app.spotifyapp.Models.TrackDAO;

public interface OnTrackClickListener {
    void onItemClick(TrackDAO track, int position);
    void onLongClick(TrackDAO track);
}
