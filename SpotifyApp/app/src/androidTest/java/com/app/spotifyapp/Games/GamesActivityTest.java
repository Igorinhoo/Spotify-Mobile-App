package com.app.spotifyapp.Games;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.junit.Assert.*;

import android.util.Log;

import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.Repositories.ApiDataProvider;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GamesActivityTest {
    ArrayList<TrackDAO> tracks = new ArrayList<>();

    Set<TrackDAO> GetAllTracks(String id) {
        ApiDataProvider api = new ApiDataProvider();
        Set<TrackDAO> Tracks = new HashSet<>();

        api.getArtistsAlbums(id, "album", albumsData -> {
            try {

                for (AlbumDAO album : albumsData) {
                    api.getAlbumTracks(album.getId(), album.getImages()[0].getUrl(), Tracks::addAll);
                }

            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        return Tracks;
    }

    @Test
    public void getAllTracks() {
//        CompletableFuture.supplyAsync(() -> {
//            try {
//                return GetAllTracks("5K4W6rqBFWDnAN6FQUkS6x");
//            } catch (Exception e) {
//                Log.e("Lyrics API", "Error getting lyrics from Genius" + e.getMessage());
//                return null;
//            }
//        }).thenAccept(Tracks ->{
//            try {
//                runOnUiThread(() -> {
//                    tracks.addAll(Tracks);
//                });
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        }).thenAccept(some -> {
//            try {
//                runOnUiThread(this::playTrack);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//
//
//        });

        CompletableFuture<Set<TrackDAO>> tracksFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return GetAllTracks("5K4W6rqBFWDnAN6FQUkS6x");
            } catch (Exception e) {
                Log.e("Lyrics API", "Error getting lyrics from Genius" + e.getMessage());
                return null;
            }
        });

        tracksFuture.thenAccept(Tracks -> {
            try {
                runOnUiThread(() -> {
                    tracks.addAll(Tracks);
                    playTrack(); // Call playTrack only when data is ready
                });
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });


    }


    @Test
    public void playTrack() {
        Random rand = new Random();
        int index = rand.nextInt(tracks.size());
        Log.e("PlayTrack: ", String.valueOf(tracks.size()));
        Log.e("PlayTrack: ", tracks.get(index).Name);
//        Log.e("PlayTrack: ", tracks.get(index);

        Log.e("onPlay: ", tracks.get(index).Id);
        tracks.remove(index);
    }
}