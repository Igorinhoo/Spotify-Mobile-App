package com.app.spotifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.app.spotifyapp.Fragments.FirstFragment;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Delayed;

import kotlinx.coroutines.Delay;


public class MainActivity extends AppCompatActivity {

    private TextView alreadyPlayingName;
    private ImageView alreadyPlayingImg;
    private LinearLayout alreadyPlayingBox;
    private SpotifyAppRemote _SpotifyAppRemote;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();


        alreadyPlayingBox = findViewById(R.id.alreadyPlayingBox);
        alreadyPlayingName = findViewById(R.id.alreadyPlayingName);
        alreadyPlayingImg = findViewById(R.id.alreadyPlayingImg);
        ConnectToRemote();

//        alreadyPlayingName.setOnClickListener((view -> {
//            Intent intent = new Intent(MainActivity.this, ActionVIew.class);
//            startActivity(intent);
//        }));
        try {
            Log.e("__SpotifyAppRemote", Boolean.toString(_SpotifyAppRemote.isConnected()));
        }catch(Exception e){
            Log.e("GETING CONNECTION", e.getMessage());
        }
//        subscribeToPlayerStateUpdates();

        alreadyPlayingBox.setOnClickListener((view -> {
            Intent intent = new Intent(MainActivity.this, PlayTrackActivity.class);
            startActivity(intent);
        }));


        Load();
    }


    public void ConnectToRemote(){
        SpotifyAppRemoteConnector.Connect(MainActivity.this);
        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
    }

    private void Load(){
        if (_SpotifyAppRemote != null) {
            Log.e("__SpotifyAppRemote", Boolean.toString(_SpotifyAppRemote.isConnected()));

            _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState ->{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alreadyPlayingName.setText(playerState.track.name);
                    }
                });
            }));
        }
    }

    private int _numRetries = 0;
    private final int MAX_RETRIES = 3;

    private void subscribeToPlayerStateUpdates() {
        Log.e("GETING CONNECTION", "NO CIE DZIJEES");

        if (_numRetries >= MAX_RETRIES) {
            // Maximum number of retries exceeded, show error message or take other action
            return;
        }

        if (_SpotifyAppRemote == null) {
            // Wait for a short delay before retrying
            new Handler().postDelayed(() -> subscribeToPlayerStateUpdates(), 1000);
            _numRetries++;
            return;
        }

        _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState ->{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alreadyPlayingName.setText(playerState.track.name);
                }
            });
        }));
    }



//    @Override
//    public boolean onSupportNavigateUp() {
//        return NavigationUI.navigateUp(navController, (AppBarConfiguration) null)
//                || super.onSupportNavigateUp();
//    }

}