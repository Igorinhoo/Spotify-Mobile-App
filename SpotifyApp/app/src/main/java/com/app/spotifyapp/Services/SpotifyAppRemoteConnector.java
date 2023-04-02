package com.app.spotifyapp.Services;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyAppRemoteConnector {
    private static final String CLIENT_ID = "8595ceb3423c45aca5775efb610b48b7";
    private static final String REDIRECT_URI = "com.app.SpotifyApp://callback";
    private static SpotifyAppRemote mSpotifyAppRemote;

    public static void Connect(Context context){
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d(context.getAttributionTag(), "Connected! Yay!");
                        System.out.println(mSpotifyAppRemote);

                    }
                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("Nie dzialajace", throwable.getMessage(), throwable);
                    }
        });


    }

    public static SpotifyAppRemote GetAppRemote(){
        return mSpotifyAppRemote;
    }
}
