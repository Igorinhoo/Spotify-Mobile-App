package com.app.spotifyapp.Games;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Toast;

import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.ApiDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

public class GamesActivity extends AppCompatActivity {

    private final Hashtable<String, ArrayList<TrackDAO>> albums = new Hashtable<>();
    short i = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);



        ApiDataProvider api = new ApiDataProvider();
        api.getArtistsAlbums("5K4W6rqBFWDnAN6FQUkS6x", "album", albumsData -> runOnUiThread(() -> {
            for (AlbumDAO album: albumsData) {
                ArrayList<TrackDAO> tracks = new ArrayList<>();
                api.getAlbumTracks(album.getId(), album.getImages()[0].getUrl(), tracks::addAll);
                albums.put(Short.toString(i) + album.getName(), tracks);
                i++;
            }


        }));

    }


    private int AlbumIndex;
    private int TrackIndex;
    private String TrackName;
    private void DrawATrack(){
        Random rand = new Random();

        AlbumIndex = rand.nextInt(i);
        ArrayList<TrackDAO> tracks = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            for (String name: Collections.list(albums.keys())) {
                if (name.startsWith(Integer.toString(AlbumIndex))){
                    tracks = albums.get(name);
                }
            }
        }
        TrackIndex = rand.nextInt(tracks.size());
        TrackName = tracks.get(TrackIndex).Name;
        System.out.println(TrackName);
    }

    private void Play(){
        DrawATrack();

        if (((EditText) findViewById(R.id.etGamesSongName)).getText().toString().equals(TrackName)){
            Toast.makeText(this, "You won", Toast.LENGTH_SHORT).show();

            return;
        }


//        System.err.println(albums);
    }

    @Override
    protected void onStart() {
        super.onStart();


        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() ->{
//            System.out.println(albums.toString());
            Play();
        }, 5000);
    }
}