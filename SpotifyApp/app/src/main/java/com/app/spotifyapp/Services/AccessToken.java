package com.app.spotifyapp.Services;

import android.util.Base64;

import com.app.spotifyapp.Interfaces.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccessToken {
    private final OkHttpClient client = new OkHttpClient();
    private static AccessToken instance;

    public static AccessToken getInstance(){
        if(instance == null){
            instance = new AccessToken();
        }
        return instance;
    }

    public void getAccessToken(String id, String secret ,StringCallback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();

        Request accessRequest = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic " + Base64.encodeToString(
                        (id + ":" + secret).getBytes(), Base64.NO_WRAP))
                .post(requestBody)
                .build();

        client.newCall(accessRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseJson = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseJson);
                    String accessToken = json.getString("access_token");
                    callback.onResponse(accessToken);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call , e);
            }
        });
    }
}
