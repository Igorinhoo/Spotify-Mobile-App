package com.app.spotifyapp;

import static androidx.navigation.ui.NavigationUI.setupWithNavController;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;



import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;


import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Image;

import java.util.StringJoiner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private TextView alreadyPlayingName, alreadyPlayingArtists;
    private ImageView alreadyPlayingImg;
    private LinearLayout alreadyPlayingBox;
    private SpotifyAppRemote _SpotifyAppRemote;

    private NavController navController;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();

        bottomNavigationView = findViewById(R.id.bottomNav);
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




        alreadyPlayingBox.setOnClickListener((view -> {
            Intent intent = new Intent(MainActivity.this, PlayTrackActivity.class);
            startActivity(intent);
        }));
    }


//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//
//        // Check if result comes from the correct activity
//        if (requestCode == 65) {
//            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
//
//            switch (response.getType()) {
//                // Response was successful and contains auth token
//                case TOKEN:
//                    // Handle successful response
//
////                    Log.e("TOKEN", response.getAccessToken() );
//
//                    break;
//
//                // Auth flow returned an error
//                case ERROR:
//                    // Handle error response
//                    break;
//
//                // Most likely auth flow was cancelled
//                default:
//                    // Handle other cases
//            }
//        }
//    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Load();
    }

    public void ConnectToRemote(){
            SpotifyAppRemoteConnector.Connect(MainActivity.this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
                    if (_SpotifyAppRemote != null) {
                        Load();
                    }
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

