package com.app.spotifyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.spotifyapp.Fragments.StatisticsFragment;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.ActivityPlayTrackBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        durationStart = findViewById(R.id.durationStart);
        mSeekBar = findViewById(R.id.seek_to_bar);

        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();

        isPlayingAsync(isPlaying->{
            _binding.playTrack.setImageResource(isPlaying ? R.drawable.btn_pause : R.drawable.btn_play);
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


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

                    getLyrics(playerState.track.name, playerState.track.artist.name);


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
                Log.e("BINDING PLAY", "CLICKED");
                Playing();
            }
        });

        _binding.skipPrevious.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                _SpotifyAppRemote.getPlayerApi().skipPrevious();
                Log.e("BACK", "DONE");

            }
        });

        _binding.skipNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                _SpotifyAppRemote.getPlayerApi().skipNext();
            }
        });

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




    }




    private static final String TAG = "GeniusAPI";
    private static final String API_BASE_URL = "https://api.genius.com";
    private static final String API_ACCESS_TOKEN = "dnUj3Cs5M541FM7Tq4rgkB_HjAAdc_-BmcDa4YOrp0UJluq9BP7dKEw-RZakMTvx";

    public void getLyrics(String artistName, String songTitle) {
        String url = API_BASE_URL + "/search?q=" + artistName + " " + songTitle;
        new FetchLyricsTask().execute(url);
    }

    private String extractLyricsFromHtml(Document doc) {
        StringBuilder lyrics = new StringBuilder();
        try {
            Elements lyricElements = doc.select("div.Lyrics__Container-sc-1ynbvzw-5.Dzxov");
            for (Element element : lyricElements) {
                String[] lines = element.html().split("<br>");
                for (String line : lines) {
                    lyrics.append(Jsoup.parse(line).text()).append("\n");
                }
                lyrics.append("\n");
            }
        }catch (Exception e){
            Log.e("FROM HTML", e.getMessage());
        }

        return lyrics.toString();
    }

    private class FetchLyricsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();
            try {
                String url = urls[0];

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + API_ACCESS_TOKEN)
                        .addHeader("User-Agent", "Mozilla/5.0")
                        .build();

                Response response = client.newCall(request).execute();

                String jsonString = response.body().string();
                JSONObject json = new JSONObject(jsonString);
                JSONObject responseJson = json.getJSONObject("response");

                JSONObject hitJson = responseJson.getJSONArray("hits").getJSONObject(0);
                String songId = hitJson.getJSONObject("result").getString("id");

                // Build the request URL for the song lyrics
                url = API_BASE_URL + "/songs/" + songId;

                // Build the request headers
                request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + API_ACCESS_TOKEN)
                        .addHeader("User-Agent", "Mozilla/5.0")
                        .build();

                // Execute the request
                response = client.newCall(request).execute();

                // Parse the response JSON for the song lyrics
                jsonString = response.body().string();
                json = new JSONObject(jsonString);
                JSONObject songJson = json.getJSONObject("response").getJSONObject("song");
                String lyricsUrl = songJson.getString("url");

                // Build the request URL for the song lyrics page
                request = new Request.Builder()
                        .url(lyricsUrl)
                        .addHeader("User-Agent", "Mozilla/5.0")
                        .build();

                response = client.newCall(request).execute();
                String htmlString = response.body().string();
                return extractLyricsFromHtml(Jsoup.parse(htmlString));
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error getting lyrics from Genius" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lyrics) {
            if (lyrics != null) {
                _binding.songLyrics.setText(lyrics);
            } else {
                Log.e("Error Lyrics!+", "There is problem with lyrics form HTML");
                Toast.makeText(PlayTrackActivity.this, "NO Text for this song", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onStop(){
        super.onStop();
        Log.e("ONSTOP","PLAYTRACK");

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
                _binding.playTrack.setImageResource(R.drawable.btn_play);
                _SpotifyAppRemote
                        .getPlayerApi()
                        .pause();
                Log.e("PAUSE", "DONE");
            }else{
                _binding.playTrack.setImageResource(R.drawable.btn_pause);
                _SpotifyAppRemote
                        .getPlayerApi()
                        .resume();
                Log.e("RESUME", "DONE");
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