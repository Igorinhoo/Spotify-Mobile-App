package com.app.spotifyapp.Repositories;


import android.util.Log;

import androidx.annotation.NonNull;

import com.app.spotifyapp.Interfaces.Callbacks.TopArtistsCallback;
import com.app.spotifyapp.Interfaces.Callbacks.TopTracksCallback;
import com.app.spotifyapp.Interfaces.Callbacks.TrackDataCallback;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.QueueItem;
import com.app.spotifyapp.Models.QueueResponse;
import com.app.spotifyapp.Models.StatisticsTerm;
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


    public final void getTopArtists(String accessToken, StatisticsTerm term, TopArtistsCallback topArtistsCallback) {
        String statsTerm = GetTerm(term);
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?limit=50&time_range=" + statsTerm)
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
                    final ArrayList<ArtistDAO> topArtists = new ArrayList<>();

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


    public final void getTopTracks(String accessToken, StatisticsTerm term, TopTracksCallback topTracksCallback) {
        String statsTerm = GetTerm(term);
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=50&time_range=" + statsTerm)
                .header("Authorization", "Bearer " + accessToken)
                .build();
        Log.e("getTopTracks: ", statsTerm);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {

                    final ArrayList<TrackDAO> topTracks = new ArrayList<>();
                    String name;
                    String uri;
                    long duration;
                    String Img;
                    String artists;

                    JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());

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
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
    }

    private String GetTerm(StatisticsTerm term){
        switch (term){
            case LONG_TERM:
                return "long_term";
            case MEDIUM_TERM:
                return "medium_term";
            case SHORT_TERM:
                return "short_term";
        }
        return "long_term";
    }


    public final void getQUEUE(String accessToken, TrackDataCallback callback) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/player/queue?market=GB&limit=50")
                .header("Authorization", "Bearer " + accessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler QUEUE", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {

                    String a = Objects.requireNonNull(response.body()).string();
                    JSONObject json = new JSONObject(Objects.requireNonNull(a));
                    Log.e("onResponse: ", a);
                    try {
                        Gson gso = new Gson();
                        QueueResponse queueResponse = gso.fromJson(json.toString(), QueueResponse.class);
                        QueueItem[] queueItems = queueResponse.getQueueItems();

                        ArrayList<TrackDAO> queue = new ArrayList<>();

                        for (QueueItem queueItem : queueItems) {
                            String songName = queueItem.getName();

                            Log.e("QUEUE", songName + " " + queueItems.length);

                            queue.add(new TrackDAO(queueItem.getName(), queueItem.getId(), queueItem.getDuration(), queueItem.getAlbum().getImages()[0].getUrl(), null));
                        }
                        callback.onTrackDataReceived(queue);
                    }catch(Exception e){
                        Log.e( "Went wrong get Queue: ", e.getMessage());
                    }
                } catch (Exception e) {
                    Log.e("Call QUEUE Tracks", e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
    }
}




