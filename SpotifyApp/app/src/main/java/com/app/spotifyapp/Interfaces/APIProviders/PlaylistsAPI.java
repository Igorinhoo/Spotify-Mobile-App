package com.app.spotifyapp.Interfaces.APIProviders;

import com.app.spotifyapp.Interfaces.Callbacks.AlbumDataCallback;
import com.app.spotifyapp.Interfaces.Callbacks.TrackDataCallback;

public interface PlaylistsAPI {
    void CreatePlaylist(String Name, String Description, boolean Public);

    void GetPlaylists(AlbumDataCallback callback);

    void GetPlaylistItems(String PlaylistID, TrackDataCallback callback);
    void AddToPlaylist(String AccessToken, String PlaylistID, String[] TrackURI);
    void RemoveFromPlaylist(String PlaylistID, String[] TracksURI);

}
