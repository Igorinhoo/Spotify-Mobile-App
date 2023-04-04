package com.app.spotifyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.spotifyapp.Interfaces.SingleTrackCallback;
import com.app.spotifyapp.Interfaces.TrackDataCallback;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.Repositories.ApiDataProvider;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.ActivityPlayTrackBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.android.appremote.api.ImagesApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PlayTrackActivity extends AppCompatActivity {

    private SpotifyAppRemote _SpotifyAppRemote;
    private ActivityPlayTrackBinding _binding;

    TrackProgressBar mTrackProgressBar;
    SeekBar mSeekBar;
    TextView durationStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityPlayTrackBinding.inflate(getLayoutInflater());
        View view = _binding.getRoot();
        setContentView(view);


        _binding.playTrack.setImageResource(R.drawable.btn_pause);

        Intent intent = getIntent();

        SpotifyAppRemoteConnector.Connect(PlayTrackActivity.this);
        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();


        _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState ->{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringJoiner names = new StringJoiner(", ");
                    _binding.playTrackName.setText(playerState.track.name);
                    for (Artist artist : playerState.track.artists) {
                        names.add(artist.name);
                    }
                    _binding.playTrackArtists.setText(names.toString());
                    _binding.durationEnd.setText(timeToDuration(playerState.track.duration));

                    _SpotifyAppRemote
                            .getImagesApi()
                            .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                            .setResultCallback(
                                    bitmap -> {
                                        _binding.trackImage.setImageBitmap(bitmap);
                                    });
                }
            });
        }));


//        ApiDataProvider api = new ApiDataProvider();
//        api.getTrack(href, new SingleTrackCallback() {
//            @Override
//            public void onTrackDataReceived(TrackDAO trackDatas) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////
////                        playTrackName.setText(trackDatas.Name);
//                        Picasso.get().load(trackDatas.Img).into(trackImage);
//
//                    }
//                });
//            }
//
//        });

        durationStart = findViewById(R.id.durationStart);
        mSeekBar = findViewById(R.id.seek_to_bar);
        mSeekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mSeekBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        TrackProgressBar mTrackProgressBar = new TrackProgressBar(mSeekBar);

        _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState ->{
            if (playerState.playbackSpeed > 0) {
                mTrackProgressBar.unpause();
            } else {
                mTrackProgressBar.pause();
            }
            mSeekBar.setMax((int) playerState.track.duration);
            mTrackProgressBar.setDuration(playerState.track.duration);
            mTrackProgressBar.update(playerState.playbackPosition);
            mSeekBar.setEnabled(true);
        });



        _binding.playTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Playing();
            }
        });




        _binding.skipPrevious.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                _SpotifyAppRemote.getPlayerApi().skipPrevious();
            }
        });
        _binding.skipNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                _SpotifyAppRemote.getPlayerApi().skipNext();
            }
        });

    }

    private void Playing(){
        _SpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerState -> {
                            if (playerState.isPaused) {
                                _binding.playTrack.setImageResource(R.drawable.btn_pause);
                                _SpotifyAppRemote
                                        .getPlayerApi()
                                        .resume();
//                                        .setResultCallback(
//                                                empty -> Toast.makeText(this, "TOAST MAKED", Toast.LENGTH_SHORT).show());
                            } else {
                                _binding.playTrack.setImageResource(R.drawable.btn_play);
                                _SpotifyAppRemote
                                        .getPlayerApi()
                                        .pause();
//                                        .setResultCallback(
//                                                empty -> Toast.makeText(this, "TOAST MAKED", Toast.LENGTH_SHORT).show());
                            }
                        });
    }

    public String timeToDuration(Long time){
        long min = TimeUnit.MILLISECONDS.toMinutes(time);
        long sec = TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));

        String result = min + ":" + (sec < 10 ? "0" + sec : sec);
        return result;
    }




    public class TrackProgressBar {
        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;


        public final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int totalSeconds = progress / 1000; // convert progress to seconds
                        int minutes = totalSeconds / 60;
                        int seconds = totalSeconds % 60;
                        String time = String.format("%02d:%02d", minutes, seconds); // format time as mm:ss
                        durationStart.setText(time);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        pause();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        _SpotifyAppRemote
                                .getPlayerApi()
                                .seekTo(seekBar.getProgress());

                    }
                };

        public final Runnable mSeekRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        int progress = mSeekBar.getProgress();
                        mSeekBar.setProgress(progress + LOOP_DURATION);
                        mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
                    }
                };

        public TrackProgressBar(SeekBar seekBar) {
            mSeekBar = seekBar;
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
            mHandler = new Handler();
        }

        public void setDuration(long duration) {
            mSeekBar.setMax((int) duration);
        }

        public void update(long progress) {
            mSeekBar.setProgress((int) progress);
        }

        public void pause() {
            mHandler.removeCallbacks(mSeekRunnable);
        }

        public void unpause() {
            mHandler.removeCallbacks(mSeekRunnable);
            mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
        }
    }
}