package com.app.spotifyapp.Repositories;


import android.app.Dialog;
import android.util.Log;

import androidx.annotation.Nullable;

import com.app.spotifyapp.Interfaces.APIProviders.PlaylistsAPI;
import com.app.spotifyapp.Interfaces.Callbacks.AlbumDataCallback;
import com.app.spotifyapp.Interfaces.Callbacks.SingleAlbumCallback;
import com.app.spotifyapp.Interfaces.Callbacks.StringCallback;
import com.app.spotifyapp.Interfaces.Callbacks.TrackDataCallback;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.Models.Url;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlaylistsAPIProvider implements PlaylistsAPI {
    private final OkHttpClient client = new OkHttpClient();
    private final String AccessToken;
    private String USER_ID = "";

    public PlaylistsAPIProvider(String access){
        AccessToken = access;
        GetUser(access);
    }
    public void GetUser(String accessToken){
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
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
                    JSONObject json = new JSONObject(response.body().string());
//                    Log.e("ID", json.getString("id"));
                    USER_ID = json.getString("id");
                } catch (Exception e) {
                    Log.e("Call QUEUE Tracks", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    @Override
    public void CreatePlaylist(String Name, String Description, boolean Public) {
        JSONObject playlistData = new JSONObject();

        try {
            playlistData.put("name", Name);
            playlistData.put("description", Description);
            playlistData.put("public", Public);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), playlistData.toString());
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/users/" + USER_ID + "/playlists")
                .header("Authorization", "Bearer " + AccessToken)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {


                    JSONObject json = new JSONObject(response.body().string());
                    Log.e("ID", json.getString("id"));

                } catch (Exception e) {
                    Log.e("Call Create Playlists", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }


    public void CreatePlaylist(String Name, String Description, boolean Public, StringCallback callback) {
        JSONObject playlistData = new JSONObject();

        try {
            playlistData.put("name", Name);
            playlistData.put("description", Description);
            playlistData.put("public", Public);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), playlistData.toString());
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/users/" + USER_ID + "/playlists")
                .header("Authorization", "Bearer " + AccessToken)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {


                    JSONObject json = new JSONObject(response.body().string());
                    Log.e("ID", json.getString("id"));
                    callback.onStringReceived(json.getString("id"));
                } catch (Exception e) {
                    Log.e("Call Create Playlists", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    @Override
    public void GetPlaylists(AlbumDataCallback callback) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/playlists")
                .header("Authorization", "Bearer " + AccessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {
                    ArrayList<AlbumDAO> playlists = new ArrayList<>();

                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray items = json.getJSONArray("items");
                    for(int i = 0; i< items.length(); i++){
                        Gson gson = new Gson();
                        AlbumDAO playlistResponse = gson.fromJson(items.getJSONObject(i).toString(), AlbumDAO.class);

//                        AlbumDAO playlist = new AlbumDAO(albumJson.getString("name"), albumJson.getString("id"),
//                                albumJson.getJSONArray("images").getJSONObject(0).getString("url"), albumJson.getString("snapshot_id"));
                        playlists.add(playlistResponse);
                    }
                    callback.onAlbumDataReceived(playlists);
                } catch (Exception e) {
                    Log.e("Call Get Playlists", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    @Override
    public void GetPlaylist(String playlistID, SingleAlbumCallback callback) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/playlists/"+playlistID+"?market=GB")
                .header("Authorization", "Bearer " + AccessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {
                    Gson gson = new Gson();
                    AlbumDAO albumResponse = gson.fromJson(response.body().string(), AlbumDAO.class);

//                    JSONObject json = new JSONObject(response.body().string());
//                    AlbumDAO playlist = new AlbumDAO(json.getString("name"), json.getString("id"), json.getJSONArray("images").getJSONObject(0).getString("url"),
//                            json.getString("snapshot_id"));
//                    Log.e("onResponse: ", );

                    callback.onAlbumDataReceived(albumResponse);
                } catch (Exception e) {
                    Log.e("Call Get Playlist", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    @Override
    public void GetPlaylistItems(String PlaylistID, TrackDataCallback callback) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/playlists/" + PlaylistID + "/tracks?market=GB&limit=50")
                .header("Authorization", "Bearer " + AccessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {
                    ArrayList<TrackDAO> tracks = new ArrayList<>();
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray items = json.getJSONArray("items");
                    for(int i = 0; i< items.length(); i++){
                        JSONObject playlist = items.getJSONObject(i);
                        JSONObject track = playlist.getJSONObject("track");
                        TrackDAO trackDAO = new TrackDAO(track.getString("name"), track.getString("id"), track.getLong("duration_ms"),
                                track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url"), null);
                        tracks.add(trackDAO);

                    }

                    callback.onTrackDataReceived(tracks);
                } catch (Exception e) {
                    Log.e("Call Get Playlists Items", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    @Override
    public void AddToPlaylist(String AccessToken, String PlaylistID, String[] TrackURI) {
        JSONObject playlistData = new JSONObject();
        JSONArray tracksArray = new JSONArray();
        for (String s : TrackURI) {
            tracksArray.put(s);
        }
        try {
            playlistData.put("uris", tracksArray);
            playlistData.put("position", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), playlistData.toString());

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/playlists/" + PlaylistID + "/tracks")
                .header("Authorization", "Bearer " + AccessToken)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {
                    JSONObject json = new JSONObject(response.body().string());

                } catch (Exception e) {
                    Log.e("Call Create Playlists", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    @Override
    public void RemoveFromPlaylist(String PlaylistID, String snapshotID, String[] TracksURI, Dialog dialog) {
        JSONArray tracksArray = new JSONArray();
        JSONObject playlistData = new JSONObject();
        try {
            for (String s : TracksURI) {
                JSONObject trackObject = new JSONObject();
                trackObject.put("uri", s);
                tracksArray.put(trackObject);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("RemoveFromPlaylist: ", tracksArray.toString());
        Log.e("RemoveFromPlaylistID: ", snapshotID);
        try {
            playlistData.put("tracks", tracksArray);
            playlistData.put("snapshot_id", snapshotID);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), playlistData.toString());

        Request request = new Request.Builder()
            .url("https://api.spotify.com/v1/playlists/" + PlaylistID + "/tracks")
            .header("Authorization", "Bearer " + AccessToken)
            .delete(requestBody)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("DAMAGE Handler", "API request failed with code " + response.code() + ": " + Objects.requireNonNull(response.body()).string());
                    return;
                }
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("Call Create Playlists", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }
}



/*class QUEUE{
//    public Object currently_playing;
    @SerializedName("queue")

    public Queue[] queue;
}
class Queue{
    public Album album;
    @SerializedName("name")
    public String names;
}

class Album{

}
//*/
//record QueueResponse(@SerializedName("queue") QueueItem[] queueItems) { }
//
//record QueueItem(@SerializedName("name") String name, @SerializedName("id") String Id, @Nullable @SerializedName("album") Album album,
//                 @Nullable String Artists, @SerializedName("duration") Long Duration){}
//
//record Album(@SerializedName("name") String name, @SerializedName("id") String Id, @SerializedName("images") Url[] images){}
//
//record Url(@SerializedName("url") String url){}


class QueueResponse {
    @SerializedName("queue")
    private QueueItem[] queueItems;

    public QueueItem[] getQueueItems() {
        return queueItems;
    }

}

class QueueItem {
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Album getAlbum() {
        return album;
    }

//    public String getArtists() {
//        return artists;
//    }

    public Long getDuration() {
        return duration;
    }

    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("album")
    private Album album;
//    private String artists;
    @SerializedName("duration")
    private Long duration;


}

class Album {
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Url[] getImages() {
        return images;
    }

    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("images")
    private Url[] images;


}







//class QueueResponse {
//    @SerializedName("queue")
//    private QueueItem[] queueItems;
//
//    public QueueItem[] getQueueItems() {
//        return queueItems;
//    }
//}
//
//class QueueItem {
//    @SerializedName("name")
//    private String track;
//
//    @SerializedName("id")
//    public String Id;
//
//    @Nullable
//    public String Img;
//
//    @Nullable
//    public String Artists;
//    @SerializedName("duration")
//    public Long Duration;
//
//    public String getTrack() {
//        return track;
//    }
//}
//
//class QueueAlbum{
//
//}
//class CurrentlyPlaying {
//    private Track currently_playing;
//
//    public Track getCurrentlyPlaying() {
//        return currently_playing;
//    }
//}
//
//
//class Track {
//    private String name;
//
//    public String getName() {
//        return name;
//    }
//}