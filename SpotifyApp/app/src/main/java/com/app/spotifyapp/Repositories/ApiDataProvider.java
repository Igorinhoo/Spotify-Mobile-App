package com.app.spotifyapp.Repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.app.spotifyapp.Interfaces.Callbacks.AlbumDataCallback;
import com.app.spotifyapp.Interfaces.Callbacks.ArtistDataCallback;
import com.app.spotifyapp.Interfaces.Callbacks.TopArtistsCallback;
import com.app.spotifyapp.Interfaces.StringCallback;
import com.app.spotifyapp.Interfaces.Callbacks.TrackDataCallback;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.Services.AccessToken;
import com.spotify.protocol.types.Artist;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ApiDataProvider {

    private final OkHttpClient client = new OkHttpClient();

    private final ArrayList<AlbumDAO> albums = new ArrayList<>();
    private final ArrayList<ArtistDAO> artists = new ArrayList<>();
    private final ArrayList<TrackDAO> tracks = new ArrayList<>();
    private final ArrayList<ArtistDAO> topArtists = new ArrayList<>();


    public final void getArtistsAlbums(String data, String scope, AlbumDataCallback albumCallback) {
        AccessToken.getInstance().getAccessToken(new StringCallback() {
            @Override
            public void onResponse(String accessToken) {
                Request request = new Request.Builder()
                        .url("https://api.spotify.com/v1/artists/" + data + "/albums?market=PL&limit=50&include_groups=" + scope)
                        .header("Authorization", "Bearer " + accessToken)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + response.body().string());
                            return;
                        }
                        try {
                            String albumName = "";
                            String albumUri = "";
                            String albumImg = "";
                            JSONObject json = new JSONObject(response.body().string());
                            JSONArray items = json.getJSONArray("items");
                            String previousAlbum = "";
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject albumJson = items.getJSONObject(i);
                                JSONArray images = albumJson.optJSONArray("images");
                                if (images != null) {
                                    albumImg = images.getJSONObject(1).getString("url");
                                } else {
                                    Log.e("ARTIST NAME PROBLEMS", "No artists found for album " + albumJson.getString("name"));
                                }
                                albumName = albumJson.getString("name");
                                albumUri = albumJson.getString("href").substring(34);
                                if (!albumName.equals(previousAlbum)) {
                                    AlbumDAO album = new AlbumDAO(albumName, albumUri, albumImg);
                                    albums.add(album);

                                } else {
                                    continue;
                                }
                                previousAlbum = albumName;
                            }
                            albumCallback.onAlbumDataReceived(albums);
                        } catch (Exception e) {
                            Log.e("Call Artists Albums", "Nie dziala");
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public final void getAlbumTracks(String href, String albumImg, TrackDataCallback trackCallback) {
        AccessToken.getInstance().getAccessToken(new StringCallback() {
            @Override
            public void onResponse(String accessToken) {
                Request request = new Request.Builder()
                        .url(href)
                        .header("Authorization", "Bearer " + accessToken)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + response.body().string());
                            return;
                        }
                        try {
                            String name = "";
                            String uri = "";
                            long duration = 0L;

                            JSONObject json = new JSONObject(response.body().string());
                            JSONArray items = json.getJSONArray("items");

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject album = items.getJSONObject(i);
                                name = album.getString("name");
                                uri = album.getString("href").substring(34);
                                duration = album.getLong("duration_ms");
                                TrackDAO track = new TrackDAO(name, uri, duration, albumImg);
                                tracks.add(track);
                            }
                            trackCallback.onTrackDataReceived(tracks);
                        } catch (Exception e) {
                            Log.e("Call Album Tracks", e.getMessage());
                        }

                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public final void getArtists(List<String> data, ArtistDataCallback artistsCallback) {
        AccessToken.getInstance().getAccessToken(new StringCallback() {
            @Override
            public void onResponse(String accessToken) {

                String href = String.join(",", data);
                Request request = new Request.Builder()
                        .url("https://api.spotify.com/v1/artists?ids=" + href)
                        .header("Authorization", "Bearer " + accessToken)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + response.body().string());
                            return;
                        }
                        try {
                            String name = "";
                            String uri = "";
                            String Img = "";
                            JSONObject json = new JSONObject(response.body().string());
                            try {
                                JSONArray items = json.getJSONArray("artists");
                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject artist = items.getJSONObject(i);
                                    name = artist.getString("name");
                                    Img = artist.getJSONArray("images").getJSONObject(0).getString("url");
                                    uri = artist.getString("id");
                                    ArtistDAO track = new ArtistDAO(name, Img, uri);
                                    artists.add(track);

                                }
                                artistsCallback.onArtistsDataReceived(artists);

                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());

                            }

                        } catch (Exception e) {
                            Log.e("Call Artists", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }


    public final void getTopArtists(TopArtistsCallback topArtistsCallback) {
        AccessToken.getInstance().getAccessToken(new StringCallback() {
            @Override
            public void onResponse(String accessToken) {
                Request request = new Request.Builder()
                        .url("https://api.spotify.com/v1/me/top/artists?limit=50")
                        .header("Authorization", "Bearer " + accessToken)
                        .build();

                Log.e("ACESS", accessToken );
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
                            String Img = "";
                            JSONObject json = new JSONObject(response.body().string());
                            Log.e("JSON TOP ARTS", json.toString() );
                            try {

                                JSONArray items = json.getJSONArray("items");

                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject artist = items.getJSONObject(i);
                                    name = artist.getString("name");
                                    uri = artist.getString("id");
                                    Img = artist.getJSONArray("images").getJSONObject(0).getString("url");

//                                    System.out.println(name);
                                    ArtistDAO track = new ArtistDAO(name, Img, uri);
                                    topArtists.add(track);
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

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });


    }
}


//
//    public final void getTrack(String href, SingleTrackCallback trackCallback){
//        AccessToken.getInstance().getAccessToken(CLIENT_ID, CLIENT_SECRET ,new StringCallback() {
//            @Override
//            public void onResponse(String accessToken) {
//
//
//                Request request = new Request.Builder()
//                        .url("https://api.spotify.com/v1/tracks/" + href +"?market=GB")
//                        .header("Authorization", "Bearer " + accessToken)
//                        .build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (!response.isSuccessful()) {
//                            Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + response.body().string());
//                            return;
//                        }
////                        ArrayList<String> trackview = new ArrayList<>() ;
//
//                        try {
//
//                            String name = "";
//                            String uri = "";
//                            String albumImg = "";
//                            Long duration = 0L;
//
//                            JSONObject json = new JSONObject(response.body().string());
//                            try {
//
//                                JSONObject items = json.getJSONObject("album");
//                                JSONArray images = items.getJSONArray("images");
//                                Log.d("Call Track __________+++++++++++++++++__________", items.toString());
//
////
////                                JSONObject album = items.getJSONObject();
//                                name = json.getString("name");
//                                uri = json.getString("href").substring(34);
//                                albumImg = images.getJSONObject(0).getString("url");
////                                duration = album.getLong("duration_ms");
//                            }catch(Exception e){
//                                Log.d("COs nie dzila", e.getMessage());
//
//                            }
//                                TrackDAO track = new TrackDAO(name, uri, 0L, albumImg);
//
//                                Log.d("Call Track ____________________++", track.Name);
//                                Log.d("Call Track __________+++++++++++++++++__________", albumImg);
//
//                            trackCallback.onTrackDataReceived(track);
//
//
//                        }catch(Exception e){
//                            Log.d("Call Track", e.getMessage());
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                    }
//                });
//            }
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//
//            }
//        });
//    }
//}
