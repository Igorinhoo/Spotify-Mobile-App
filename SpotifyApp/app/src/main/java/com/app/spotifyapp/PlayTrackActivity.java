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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotify.android.appremote.api.ImagesApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PlayTrackActivity extends AppCompatActivity {

    private SpotifyAppRemote _SpotifyAppRemote;
    private ImageButton play, skipToNext, skipToPrevious;
    private ImageView trackImage;
    private TextView playTrackName;
    private boolean playing = true;

    TrackProgressBar mTrackProgressBar;
    SeekBar mSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_track);
        play = findViewById(R.id.playTrack);
        skipToPrevious = findViewById(R.id.skipPrevious);
        skipToNext = findViewById(R.id.skipNext);
        playTrackName = findViewById(R.id.playTrackName);
        trackImage = findViewById(R.id.trackImage);

        play.setImageResource(R.drawable.btn_pause);

        Intent intent = getIntent();
//        String href = intent.getStringExtra("TrackHref");

        SpotifyAppRemoteConnector.Connect(PlayTrackActivity.this);
        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();


        _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState ->{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playTrackName.setText(playerState.track.name);
//                                    Log.d("CO SIE DZIEJE", playerState.track.imageUri.toString());
//
                    _SpotifyAppRemote
                            .getImagesApi()
                            .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                            .setResultCallback(
                                    bitmap -> {
                                        trackImage.setImageBitmap(bitmap);
//                                        mImageLabel.setText(
//                                                String.format(
//                                                        Locale.ENGLISH, "%d x %d", bitmap.getWidth(), bitmap.getHeight()));
                                    });


//                    Picasso.get().load(String.valueOf(_SpotifyAppRemote.getImagesApi().getImage(playerState.track.imageUri))).into(trackImage);

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
////                        Log.d("CO SIE DZIEJE", trackDatas.toString());
////                        playTrackName.setText(trackDatas.Name);
//                        Picasso.get().load(trackDatas.Img).into(trackImage);
//
//                    }
//                });
//            }
//
//        });




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



        play.setOnClickListener((view -> {
            Playing();
        }));



        skipToPrevious.setOnClickListener((view ->{
            _SpotifyAppRemote.getPlayerApi().skipPrevious();
        }));
        skipToNext.setOnClickListener((view ->{
            _SpotifyAppRemote.getPlayerApi().skipNext();
        }));


//        trackImage.setOnClickListener((view -> {
//            _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState->{
//
//                new AlertDialog.Builder(this).setTitle("Title").setMessage(new GsonBuilder().setPrettyPrinting().create().toJson(playerState)).create().show();
//            });
//
//        }));
    }

    private void Playing(){
        _SpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerState -> {
                            if (playerState.isPaused) {
                                play.setImageResource(R.drawable.btn_pause);
                                _SpotifyAppRemote
                                        .getPlayerApi()
                                        .resume()
                                        .setResultCallback(
                                                empty -> Toast.makeText(this, "TOAST MAKED", Toast.LENGTH_SHORT).show());
                            } else {
                                play.setImageResource(R.drawable.btn_play);
                                _SpotifyAppRemote
                                        .getPlayerApi()
                                        .pause()
                                        .setResultCallback(
                                                empty -> Toast.makeText(this, "TOAST MAKED", Toast.LENGTH_SHORT).show());
                            }
                        });
    }





    public class TrackProgressBar {
        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;

        public final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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