package com.app.spotifyapp.Repositories;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.spotifyapp.Interfaces.Callbacks.AlbumDataCallback;
import com.app.spotifyapp.Interfaces.Callbacks.ArtistDataCallback;
import com.app.spotifyapp.Interfaces.Callbacks.SearchDataCallback;
import com.app.spotifyapp.Interfaces.Callbacks.SingleTrackCallback;
import com.app.spotifyapp.Interfaces.Callbacks.TrackDataCallback;
import com.app.spotifyapp.Interfaces.StringCallback;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.Models.ArtistDAO;
import com.app.spotifyapp.Models.SearchDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.Services.AccessToken;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
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
    private final ArrayList<SearchDAO> search = new ArrayList<>();


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
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                            return;
                        }
                        try {

                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());

                            JSONArray items = json.getJSONArray("items");
                            String previousAlbum = "";
                            for (int i = 0; i < items.length(); i++) {
                                Gson gson = new Gson();
                                AlbumDAO albumResponse = gson.fromJson(items.getJSONObject(i).toString(), AlbumDAO.class);

                                if (!albumResponse.getName().equals(previousAlbum)) {
                                    albums.add(albumResponse);

                                } else {
                                    continue;
                                }
                                previousAlbum = albumResponse.getName();
                            }
                            albumCallback.onAlbumDataReceived(albums);
                        } catch (Exception e) {
                            Log.e("Call Artists Albums", "Not working");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
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

    public final void getAlbumTracks(String Id, String albumImg, TrackDataCallback trackCallback) {
        AccessToken.getInstance().getAccessToken(new StringCallback() {
            @Override
            public void onResponse(String accessToken) {
                Request request = new Request.Builder()
                        .url("https://api.spotify.com/v1/albums/" + Id + "/tracks?market=GB&limit=40")
                        .header("Authorization", "Bearer " + accessToken)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                            return;
                        }
                            String name = "";
                            String id = "";
                            long duration = 0;
                        JSONArray items = new JSONArray();
                        try {
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                            items = json.getJSONArray("items");

                        } catch (Exception e) {
                            Log.e("Call Album Tracks", "Something went wrong: "  + albumImg + " " + e.getMessage());
                        }

                            for (int i = 0; i < items.length(); i++) {
                                try{
                                    JSONObject album = items.getJSONObject(i);
                                    name = album.getString("name");
                                    id = album.getString("id");
                                    duration = album.getLong("duration_ms");
                                }catch (Exception e){
                                    Log.e("JSONResponse: ", e.getMessage());

                                }

                                try{

                                    TrackDAO track = new TrackDAO(name, id, duration, albumImg, null);

                                    tracks.add(track);
                                }catch (Exception e){
                                    Log.e("AddResponse: ", e.getMessage());
                                }
                            }
                            trackCallback.onTrackDataReceived(tracks);

                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
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



    public final void Search(String query, String[] type, Activity activity, SearchDataCallback<SearchDAO> searchCallback) {
        search.clear();

        int totalNotNulls = -1;
        for (String s :type) { if (s != null) totalNotNulls++; }

        StringBuilder d = new StringBuilder();
        for (String s : type) {
            if (s == null) continue;
            d.append(s);
            if (totalNotNulls > 0) {
                d.append("%2C");
                totalNotNulls--;
            }
        }

        AccessToken.getInstance().getAccessToken(new StringCallback() {
            public void onResponse(String accessToken) {
                Request request = new Request.Builder()
                        .url("https://api.spotify.com/v1/search?q="+ query.replace(" ", "+") +"&type=" + d + "&market=GB&limit=20")
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
                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());

                            //TODO DO when searched more than one type it show most accurate items, not like now (first artists, then albums and then tracks)(not sure how to do it now)

                            if (json.has("artists")){
                                GetSearchType(json.getJSONObject("artists"));
                            }
                            if (json.has("albums")) {
                                GetSearchType(json.getJSONObject("albums"));
                            }
                            if (json.has("tracks")){
                                GetSearchType(json.getJSONObject("tracks"));
                            }

                            activity.runOnUiThread(()-> searchCallback.onSearchDataCallback(search));
                        } catch (Exception e) {
                            Log.e("Call Search Tracks", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
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

    private void GetSearchType(JSONObject searchType){
        try {
            String type;
            String name;
            String uri;
            String img;
            String releaseDate = null;
            JSONArray items = searchType.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject searchList = items.getJSONObject(i);

                name = searchList.getString("name");
                uri = searchList.getString("id");
                if (searchList.has("release_date")) {
                    releaseDate = searchList.getString("release_date");
                }
//                releaseDate = searchList.getString("release_date");
                type = searchList.getString("type");
                JSONArray images;
                if (type.equals("track")){
                    images = searchList.getJSONObject("album").getJSONArray("images");
                }else images = searchList.getJSONArray("images");

                img = images.length() > 1 ? images.getJSONObject(1).getString("url") : "https://cdn-icons-png.flaticon.com/512/847/847970.png?w=740&t=st=1689538827~exp=1689539427~hmac=a30042782aac879994a47a8f044a202b3fd84279e924c5e771178141b5278e13";
                search.add(new SearchDAO(type,name, img, uri, releaseDate));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

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
                                JSONArray items = json.getJSONArray("artists");
                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject artist = items.getJSONObject(i);
                                    name = artist.getString("name");
                                    Img = artist.getJSONArray("images").getJSONObject(0).getString("url");
                                    uri = artist.getString("id");
                                    ArtistDAO arti = new ArtistDAO(name, Img, uri);
                                    artists.add(arti);

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
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
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

    public final void getTrack(String href, SingleTrackCallback trackCallback){
        AccessToken.getInstance().getAccessToken(new StringCallback() {
            @Override
            public void onResponse(String accessToken) {


                Request request = new Request.Builder()
                        .url("https://api.spotify.com/v1/tracks/" + href +"?market=GB")
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

                            String name = "";
                            String uri = "";
                            String albumImg = "";
                            long duration = 0L;

                            JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                            try {

                                JSONObject items = json.getJSONObject("album");
                                JSONArray images = items.getJSONArray("images");

                                name = json.getString("name");
                                uri = json.getString("href").substring(34);
                                albumImg = images.getJSONObject(0).getString("url");
                                duration = json.getLong("duration_ms");
                            }catch(Exception e){
                                Log.d("Something wrong", e.getMessage());

                            }
                            TrackDAO track = new TrackDAO(name, uri, duration, albumImg, null);

                            trackCallback.onTrackDataReceived(track);
                        }catch(Exception e){
                            Log.d("Call Track", e.getMessage());

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
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




