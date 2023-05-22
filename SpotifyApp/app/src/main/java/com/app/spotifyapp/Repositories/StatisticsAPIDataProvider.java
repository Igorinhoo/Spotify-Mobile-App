package com.app.spotifyapp.Repositories;


import android.util.Log;

import androidx.annotation.NonNull;

import com.app.spotifyapp.Interfaces.Callbacks.TopArtistsCallback;
import com.app.spotifyapp.Interfaces.Callbacks.TopTracksCallback;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.Models.TrackDAO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StatisticsAPIDataProvider{

    private final OkHttpClient client = new OkHttpClient();
    private final ArrayList<ArtistDAO> topArtists = new ArrayList<>();
    private final ArrayList<TrackDAO> topTracks = new ArrayList<>();


    /*private AuthorizationCallback authorizationCallback;

    public void setAuthorizationCallback(AuthorizationCallback callback) {
        this.authorizationCallback = callback;
    }

    public void initiateAuthorization(Activity activity) {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder("8595ceb3423c45aca5775efb610b48b7",
                AuthorizationResponse.Type.TOKEN, "com.app.SpotifyApp://callback");

        builder.setScopes(new String[]{"user-top-read"});
        AuthorizationRequest request = builder.build();

        Intent intent = AuthorizationClient.createLoginActivityIntent(activity, request);
        activity.startActivityForResult(intent, 1);

    }

    public void handleAuthorizationResult(int resultCode, Intent data) {
        Log.e( "handleAuthorizationResult: ", "WORK" );
        if (resultCode == Activity.RESULT_OK && data != null) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            if (authorizationCallback != null) {
                switch (response.getType()) {
                    case TOKEN:
                        String accessToken = response.getAccessToken();
                        Log.e( "handleAuthorizationR ", accessToken );
                        authorizationCallback.onAuthorizationResult(accessToken);
                        break;
                    case ERROR:
                        break;
                    default:
                        break;
                }
            }
        }
    }
*/

    public final void getTopArtists(String accessToken ,TopArtistsCallback topArtistsCallback) {
                Request request = new Request.Builder()
                        .url("https://api.spotify.com/v1/me/top/artists?limit=50")
                        .header("Authorization", "Bearer " + accessToken)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                            return;
                        }
                        try {

                            String name = "";
                            String uri = "";
                            String Img = "";
                            JSONObject json = new JSONObject(response.body().string());
                            try {

                                JSONArray items = json.getJSONArray("items");

                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject artist = items.getJSONObject(i);
                                    name = artist.getString("name");
                                    uri = artist.getString("id");
                                    Img = artist.getJSONArray("images").getJSONObject(0).getString("url");

                                    ArtistDAO arti = new ArtistDAO(name, Img, uri);
                                    topArtists.add(arti);
                                }


                                topArtistsCallback.onTopArtistsDataReceived(topArtists);
                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());

                            }

                        } catch (Exception e) {
                            Log.e("Call Top Artists", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                });
    }


    public final void getTopTracks(String accessToken , TopTracksCallback topTracksCallback) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=50&time_range=long_term")
                .header("Authorization", "Bearer " + accessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {
                    String name = "";
                    String uri = "";
                    long duration = 0L;
                    String Img = "";
                    String artists = "";

                    JSONObject json = new JSONObject(response.body().string());
                    Log.e("JSON TOP TracksTS", json.toString() );
                    try {
                        JSONArray items = json.getJSONArray("items");

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject track = items.getJSONObject(i);
                            name = track.getString("name");
                            uri = track.getString("id");
                            duration = track.getLong("duration_ms");
                            Img = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                            artists = track.getJSONArray("artists").getJSONObject(0).getString("name");

                            TrackDAO tracks = new TrackDAO(name, uri, duration, Img, artists);
                            topTracks.add(tracks);
                        }


                        topTracksCallback.onTopTracksDataReceived(topTracks);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());

                    }

                } catch (Exception e) {
                    Log.e("Call Top Tracks", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }


/*
    public static void authorize(Context context, AuthorizationCallback callback) {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                    "8595ceb3423c45aca5775efb610b48b7",
                    AuthorizationResponse.Type.TOKEN,
                    "com.app.SpotifyApp://callback"
            );

            builder.setScopes(new String[]{"user-top-read"});
            AuthorizationRequest request = builder.build();

            Intent intent = AuthorizationClient.createLoginActivityIntent(context, request);

            // Start the activity using startActivityForResult
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, 65);
            } else {
                // Handle the case where the context is not an Activity
            }
        }*/

}




