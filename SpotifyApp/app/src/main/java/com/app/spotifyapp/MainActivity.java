package com.app.spotifyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.app.spotifyapp.Genres.GenresActivity;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Image;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;


public class MainActivity extends AppCompatActivity {

    private TextView alreadyPlayingName, alreadyPlayingArtists;
    private ImageView alreadyPlayingImg;
    private LinearLayout alreadyPlayingBox;
    private SpotifyAppRemote _SpotifyAppRemote;
    private static String accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        CheckIfFirstRun();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setItemIconTintList(null);

        GetAuthToken(bottomNavigationView);


        alreadyPlayingBox = findViewById(R.id.alreadyPlayingBox);
        alreadyPlayingName = findViewById(R.id.alreadyPlayingName);
        alreadyPlayingArtists = findViewById(R.id.alreadyPlayingArtists);
        alreadyPlayingImg = findViewById(R.id.alreadyPlayingImg);

        ConnectToRemote();
    }

    private void GetAuthToken(BottomNavigationView bottomNavigationView){
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder("8595ceb3423c45aca5775efb610b48b7", AuthorizationResponse.Type.TOKEN, "com.app.SpotifyApp://callback");

        builder.setScopes(new String[]{"user-top-read", "user-read-playback-state", "user-read-email",
                "user-read-private", "playlist-read-private", "playlist-read-collaborative",
                "playlist-modify-private", "playlist-modify-public"});
        AuthorizationRequest request = builder.build();


        AtomicBoolean isEnabled = new AtomicBoolean(true);

        Intent intent = AuthorizationClient.createLoginActivityIntent(this, request);
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                AuthorizationResponse response = AuthorizationClient.getResponse(result.getResultCode(), result.getData());
                Menu menu = bottomNavigationView.getMenu();
                MenuItem statisticsItem = menu.findItem(R.id.statistics);
                MenuItem playlistItem = menu.findItem(R.id.playlists);
                isEnabled.set(false);

                switch (response.getType()) {
                    case TOKEN:
                        accessToken = response.getAccessToken();
                        Log.e("TOKEN", accessToken);

                        break;
                    case ERROR:

                        statisticsItem.setEnabled(isEnabled.get());
                        playlistItem.setEnabled(isEnabled.get());
                        Toast.makeText(MainActivity.this, "You can't get into statistics or playlists. Try exit offline mode or enable internet!", Toast.LENGTH_LONG).show();
                        Log.e("Error", "Problem with getting scoped Token");
                        break;
                    default:
                        statisticsItem.setEnabled(isEnabled.get());
                        playlistItem.setEnabled(isEnabled.get());
                        Toast.makeText(MainActivity.this, "You can't get into statistics or playlists. Try exit offline mode or enable internet!", Toast.LENGTH_LONG).show();

                        Log.e("DEFAULT", " scoped Token");

                        break;
                }

            }else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                Log.e("Cancelled", "Login window was canceled");
            }

        });
        launcher.launch(intent);
    }



    private void CheckIfFirstRun(){
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            startActivity(new Intent(MainActivity.this, GenresActivity.class));
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();
    }
    public static String getAccessToken(){
        if (accessToken != null) return accessToken;
        throw new NullPointerException();
    }

    public void ConnectToRemote(){
            SpotifyAppRemoteConnector.Connect(MainActivity.this);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
                if (_SpotifyAppRemote != null) {
                    Load();
                }
            }, 3000);

    }

    private void Load(){
            if (_SpotifyAppRemote !=null) {
                _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState -> runOnUiThread(() -> {
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
                                    bitmap -> alreadyPlayingImg.setImageBitmap(bitmap));
                })));
            }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("ON START", "MAIN");

        Load();

        alreadyPlayingBox.setOnClickListener((view -> {
            Intent intent = new Intent(MainActivity.this, PlayTrackActivity.class);
            startActivity(intent);
        }));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("ON STOP","MAIN");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpotifyAppRemote.disconnect(_SpotifyAppRemote);
        Log.e("ON DESTROY","MAIN");
    }

}

