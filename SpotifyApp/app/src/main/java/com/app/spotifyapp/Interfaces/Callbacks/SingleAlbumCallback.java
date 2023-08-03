package com.app.spotifyapp.Interfaces.Callbacks;

import com.app.spotifyapp.Models.AlbumDAO;

public interface SingleAlbumCallback {
    void onAlbumDataReceived(AlbumDAO albumData);

}
