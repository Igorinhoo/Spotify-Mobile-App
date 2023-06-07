package com.app.spotifyapp;

import static androidx.navigation.ui.NavigationUI.setupWithNavController;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;


import com.app.spotifyapp.Repositories.PlaylistsAPIProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Image;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.StringJoiner;

import kotlin.Pair;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private TextView alreadyPlayingName, alreadyPlayingArtists;
    private ImageView alreadyPlayingImg;
    private LinearLayout alreadyPlayingBox;
    private SpotifyAppRemote _SpotifyAppRemote;

    private NavController navController;

    private ActivityResultLauncher<Intent> launcher;
    private static String accessToken;
    public static String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder("8595ceb3423c45aca5775efb610b48b7", AuthorizationResponse.Type.TOKEN, "com.app.SpotifyApp://callback");

        builder.setScopes(new String[]{"user-top-read", "user-read-playback-state", "user-read-email",
                "user-read-private", "playlist-read-private", "playlist-read-collaborative",
                "playlist-modify-private", "playlist-modify-public"});
        AuthorizationRequest request = builder.build();

        Intent intent = AuthorizationClient.createLoginActivityIntent(this, request);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                AuthorizationResponse response = AuthorizationClient.getResponse(result.getResultCode(), result.getData());

                switch (response.getType()) {
                    case TOKEN:
                        accessToken = response.getAccessToken();
                        Log.e("TOKEN", accessToken );
                        break;
                    case ERROR:
                        Log.e("Error", "DFJAFN" );
                        break;
                    default:
                        break;
                }
            }
        });
        launcher.launch(intent);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        alreadyPlayingBox = findViewById(R.id.alreadyPlayingBox);
        alreadyPlayingName = findViewById(R.id.alreadyPlayingName);
        alreadyPlayingArtists = findViewById(R.id.alreadyPlayingArtists);
        alreadyPlayingImg = findViewById(R.id.alreadyPlayingImg);

        ConnectToRemote();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("ONSTART", "MAIN");

        Load();

        JSONObject playlistData = new JSONObject();
        try {
            playlistData.put("tracks", new String[]{"raz", "dwa", "trzy"});
            playlistData.put("snapshot_id", "Dont know");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e( "onStart: ", playlistData.toString());

        alreadyPlayingBox.setOnClickListener((view -> {
            Intent intent = new Intent(MainActivity.this, PlayTrackActivity.class);
            startActivity(intent);
        }));
    }

    public static String getAccessToken(){
        if (accessToken != null) return accessToken;
        throw new NullPointerException();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Load();
    }

    public void ConnectToRemote(){
            SpotifyAppRemoteConnector.Connect(MainActivity.this);
            new Handler().postDelayed(() -> {
                _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
                if (_SpotifyAppRemote != null) {
                    Load();
                }
            }, 3000);

    }

    private void Load(){
            if (_SpotifyAppRemote !=null) {
                _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState -> {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StringJoiner names = new StringJoiner(", ");
                            for (Artist artist : playerState.track.artists) {
                                names.add(artist.name);
                            }
                            alreadyPlayingArtists.setText(names.toString());
                            alreadyPlayingName.setText(playerState.track.name);
                            _SpotifyAppRemote
                                    .getImagesApi()
                                    .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                                    .setResultCallback(
                                            bitmap -> {
                                                alreadyPlayingImg.setImageBitmap(bitmap);
                                            });
                        }
                    });
                }));
            }
//            _SpotifyAppRemote.getContentApi().
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("ONSTOP","MAIN");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpotifyAppRemote.disconnect(_SpotifyAppRemote);
        Log.e("ONDESTROY","MAIN");
    }

}

