package com.app.spotifyapp.Games;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.ApiDataProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.ActivityGamesBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class GamesActivity extends AppCompatActivity {
    private final ArrayList<TrackDAO> tracks = new ArrayList<>();
    private String AlreadyPlaying = "";
    private ActivityGamesBinding _binding;
    private SpotifyAppRemote _SpotifyAppRemote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityGamesBinding.inflate(getLayoutInflater());
        View view = _binding.getRoot();
        setContentView(view);

        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();

        CompletableFuture<Set<TrackDAO>> tracksFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return GetAllTracks(getIntent().getStringExtra("ID"));
            } catch (Exception e) {
                Log.e("Lyrics API", "Error getting lyrics from Genius" + e.getMessage());
                return null;
            }
        });

        tracksFuture.thenAccept(Tracks -> {
            try {
                tracks.addAll(Tracks);
                PlayTrack();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Set<TrackDAO> GetAllTracks(String id) {
        ApiDataProvider api = new ApiDataProvider();
        CopyOnWriteArraySet<TrackDAO> Tracks = new CopyOnWriteArraySet<>();
        CompletableFuture<Void> allAlbumsProcessed = new CompletableFuture<>();

        api.getArtistsAlbums(id, "album,single", albumsData -> {
            try {
                runOnUiThread(() -> {
                    AtomicInteger remainingAlbums = new AtomicInteger(albumsData.size());
                    for (AlbumDAO album : albumsData) {
                        api.getAlbumTracks(album.getId(), album.getImages()[0].getUrl(), tracks -> {
                            Tracks.addAll(tracks);
                            if (remainingAlbums.decrementAndGet() == 0) {
                                allAlbumsProcessed.complete(null);
                            }
                        });
                    }
                });
            } catch (Throwable e) {
                Log.e("GetAllTracks: ", e.getMessage());
            }
        });

        allAlbumsProcessed.join();

        return Tracks;
    }


    @Override
    protected void onStart() {
        super.onStart();
        _binding.gamesPlayTrack.setOnClickListener(view -> Playing());
        _binding.gamesSkipNext.setOnClickListener(view -> SkipNext());

        _binding.btnReveal.setOnClickListener(view -> _binding.etGamesSongName.setText(AlreadyPlaying));
    }

    void PlayTrack(){
        Random rand = new Random();
        int index = rand.nextInt(tracks.size());
        _SpotifyAppRemote.getPlayerApi().play("spotify:track:" +tracks.get(index).Id);
        AlreadyPlaying = tracks.get(index).Name;
        tracks.remove(index);
    }

    void SkipNext(){
        _binding.gamesPlayTrack.setImageResource(R.drawable.btn_pause);
        PlayTrack();
    }

    private void isPlayingAsync(Consumer<Boolean> callback) {
        _SpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            if (playerState != null) {
                callback.accept(!playerState.isPaused);
            } else {
                callback.accept(false);
            }
        });
    }
    private void Playing(){
        isPlayingAsync(isPlaying ->{
            if (isPlaying){
                _binding.gamesPlayTrack.setImageResource(R.drawable.btn_play);
                _SpotifyAppRemote
                        .getPlayerApi()
                        .pause();
            }else{
                _binding.gamesPlayTrack.setImageResource(R.drawable.btn_pause);
                _SpotifyAppRemote
                        .getPlayerApi()
                        .resume();
            }
        });
    }
}