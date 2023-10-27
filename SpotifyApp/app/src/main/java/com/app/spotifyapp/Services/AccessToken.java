    package com.app.spotifyapp.Services;


    import android.util.Base64;

    import androidx.annotation.NonNull;

    import com.app.spotifyapp.Interfaces.StringCallback;

    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.IOException;
    import java.util.Objects;

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

    public static AccessToken getInstance(){
        if(instance == null){
            instance = new AccessToken();
        }
        return instance;
    }

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
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseJson = Objects.requireNonNull(response.body()).string();
                try {
                    JSONObject json = new JSONObject(responseJson);
                    String accessToken = json.getString("access_token");
                    callback.onResponse(accessToken);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call , e);
            }
        });
    }
}
