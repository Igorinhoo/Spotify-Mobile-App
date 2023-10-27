package com.app.spotifyapp;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.ActivityPlayTrackBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Artist;

import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.playTrackContainerView);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        durationStart = findViewById(R.id.durationStart);
        mSeekBar = findViewById(R.id.seek_to_bar);
        mSeekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));
        mSeekBar.getIndeterminateDrawable().setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));
        mTrackProgressBar = new TrackProgressBar(mSeekBar);

        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();

        setPlaybackImage();
    }

    @Override
    protected void onStart() {
        super.onStart();

        _binding.btnChangeLyrics.setOnClickListener((view -> {
            boolean isAZ = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .getBoolean("isAZ", true);
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isAZ", !isAZ).apply();
        }));


        _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState ->
            runOnUiThread(() -> {
                StringJoiner names = new StringJoiner(", ");
                _binding.playTrackName.setText(playerState.track.name);
                for (Artist artist : playerState.track.artists) {
                    names.add(artist.name);
                }
                _binding.playTrackArtists.setText(names.toString());
                _binding.durationEnd.setText(timeToDuration(playerState.track.duration));
            })
        ));
        trackBar();

        _binding.playTrack.setOnClickListener(view -> Playing());
        _binding.skipPrevious.setOnClickListener(view -> _SpotifyAppRemote.getPlayerApi().skipPrevious());
        _binding.skipNext.setOnClickListener(view -> _SpotifyAppRemote.getPlayerApi().skipNext());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.playTrackContainerView);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        _binding.btnQUEUE.setOnClickListener(view -> {

            if(Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.trackCoverFragment){
                navController.navigate(R.id.action_trackCoverFragment_to_queueFragment);
                return;
            }
            navController.navigate(R.id.action_lyricsFragment_to_queueFragment);
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.queueFragment) {
                _binding.btnQUEUE.setEnabled(false);
                _binding.btnQUEUE.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.queue_unabled));
            } else {
                _binding.btnQUEUE.setEnabled(true);
                _binding.btnQUEUE.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.queue));
            }
        });
    }

    private void trackBar(){
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
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        trackBar();
        setPlaybackImage();
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.e("ON STOP","PLAY TRACK");

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
                _binding.playTrack.setImageResource(R.drawable.play_50);
                _SpotifyAppRemote
                        .getPlayerApi()
                        .pause();
            }else{
                _binding.playTrack.setImageResource(R.drawable.pause_50);
                _SpotifyAppRemote
                        .getPlayerApi()
                        .resume();
            }
        });
    }

    public String timeToDuration(Long time){
        long min = TimeUnit.MILLISECONDS.toMinutes(time);
        long sec = TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));

        return min + ":" + (sec < 10 ? "0" + sec : sec);
    }

    private void setPlaybackImage(){
        isPlayingAsync(isPlaying-> {
                    _binding.playTrack.setImageResource(isPlaying ? R.drawable.pause_50 : R.drawable.play_50);
                    _binding.playTrack.setBackgroundResource(android.R.color.transparent);
                }
        );
    }


    public class TrackProgressBar {
        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;


        public final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int totalSeconds = progress / 1000;
                        int minutes = totalSeconds / 60;
                        int seconds = totalSeconds % 60;
                        durationStart.setText(String.format(Locale.US,"%01d:%02d", minutes, seconds));
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
            mHandler = new Handler(Looper.getMainLooper());
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