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
import com.spotify.protocol.types.Image;
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

        alreadyPlayingBox.setOnClickListener((view -> {
            Intent intent = new Intent(MainActivity.this, PlayTrackActivity.class);
            startActivity(intent);
        }));

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


//    @Override
//    public boolean onSupportNavigateUp() {
//        return NavigationUI.navigateUp(navController, (AppBarConfiguration) null)
//                || super.onSupportNavigateUp();
//    }

}