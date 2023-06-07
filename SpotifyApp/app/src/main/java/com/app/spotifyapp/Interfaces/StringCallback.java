package com.app.spotifyapp.Interfaces;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class StringCallback implements Callback {

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String responseJson = response.body().string();
        try {
            JSONObject json = new JSONObject(responseJson);
            String accessToken = json.getString("access_token");
            onResponse(accessToken);
        } catch (JSONException e) {
            onFailure(e);
        }
    }

    public abstract void onResponse(String response);
    public abstract void onFailure(Exception e);
}
