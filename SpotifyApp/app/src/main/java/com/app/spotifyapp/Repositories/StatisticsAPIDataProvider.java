package com.app.spotifyapp.Repositories;


import android.util.Log;

import androidx.annotation.NonNull;

import com.app.spotifyapp.Interfaces.Callbacks.TopArtistsCallback;
import com.app.spotifyapp.Interfaces.Callbacks.TopTracksCallback;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.google.gson.Gson;

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


    public final void getTopArtists(String accessToken ,TopArtistsCallback topArtistsCallback) {
                Request request = new Request.Builder()
                        .url("https://api.spotify.com/v1/me/top/artists?limit=50&time_range=long_term")
                        .header("Authorization", "Bearer " + accessToken)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                            return;
                        }
                        try {

                            String name;
                            String uri;
                            String Img;
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
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
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
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
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {
                    String name;
                    String uri;
                    long duration;
                    String Img;
                    String artists;

                    JSONObject json = new JSONObject(response.body().string());

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


    public final void getQUEUE(String accessToken) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/player/queue?market=GB&limit=50")
                .header("Authorization", "Bearer " + accessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler QUEUE", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {


                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
//                    Log.e("QUEUEUEUEUE", json.toString() );
//                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                    Log.e("QUEUE", gson.toJson(gson.fromJson(json.toString(), Object.class)));
                    try {
                        Gson gso = new Gson();
                        QueueResponse queueResponse = gso.fromJson(json.toString(), QueueResponse.class);
                        QueueItem[] queueItems = queueResponse.getQueueItems();
                        Log.e("QUEUEs", Integer.toString(queueItems.length));

                        for (QueueItem queueItem : queueItems) {
                            String songName = queueItem.getTrack();
                            // Do something with the song name
                                                   Log.e("QUEUE", songName);

                        }
//                        CurrentlyPlaying currentlyPlaying = gso.fromJson(json.toString(), CurrentlyPlaying.class);
//                        String songName = currentlyPlaying.getCurrentlyPlaying().getName();
//                        Log.e("QUEUE", songName);

                    }catch(Exception e){
                        Log.e( "onResponsesss: ", e.getMessage() );
                    }
                } catch (Exception e) {
                    Log.e("Call QUEUE Tracks", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }
}




