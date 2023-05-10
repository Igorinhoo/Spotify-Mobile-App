    package com.app.spotifyapp.Services;


import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.app.spotifyapp.Interfaces.StringCallback;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccessToken {

    private final OkHttpClient client = new OkHttpClient();
    private static final String CLIENT_ID = "8595ceb3423c45aca5775efb610b48b7";
    private static final String CLIENT_SECRET = "64b2842d19b048b79d1cf634b73d4599";
    private static AccessToken instance;

//    private ActivityResultLauncher<Intent> launcher;




    public static AccessToken getInstance(){
        if(instance == null){
            instance = new AccessToken();
        }
        return instance;
    }

   /* public void getAuthToken(Activity activity, StringCallback callback){

        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder("8595ceb3423c45aca5775efb610b48b7", AuthorizationResponse.Type.TOKEN, "com.app.SpotifyApp://callback");

        builder.setScopes(new String[]{"user-top-read"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(activity, 65, request);


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.get == Activity.RESULT_OK) {
                AuthorizationResponse response = AuthorizationClient.getResponse(result.getResultCode(), result.getData());

                switch (response.getType()) {
                    case TOKEN:
                        String accessToken = response.getAccessToken();
                        Log.e("TOKEN", accessToken);
                        callback.onResponse(accessToken);
                        break;
                    case ERROR:
                        Log.e("Error", "DFJAFN");
                        callback.onFailure(new Exception("Authorization Error"));
                        break;
                    default:
                        break;
                }
            }
        });

        launcher.launch(AuthorizationClient.createLoginActivityIntent(activity,request));
    }*/

    public void getAccessToken(StringCallback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();

        Request accessRequest = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic " + Base64.encodeToString(
                        (CLIENT_ID + ":" + CLIENT_SECRET).getBytes(), Base64.NO_WRAP))
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
