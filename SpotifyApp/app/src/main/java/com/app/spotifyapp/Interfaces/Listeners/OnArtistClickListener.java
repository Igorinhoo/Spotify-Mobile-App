package com.app.spotifyapp.Interfaces.Listeners;

import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.TrackDAO;

public interface OnArtistClickListener {
    void onItemClick(ArtistDAO artist);
}
