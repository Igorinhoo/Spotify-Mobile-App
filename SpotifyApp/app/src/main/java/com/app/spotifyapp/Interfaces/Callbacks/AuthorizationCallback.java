package com.app.spotifyapp.Interfaces.Callbacks;

import com.spotify.sdk.android.auth.AuthorizationResponse;

public interface AuthorizationCallback {
    void onAuthorizationResult(String response);
}
