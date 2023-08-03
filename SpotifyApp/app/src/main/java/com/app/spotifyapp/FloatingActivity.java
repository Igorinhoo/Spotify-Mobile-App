package com.app.spotifyapp;

import static com.spotify.sdk.android.auth.LoginActivity.REQUEST_CODE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.spotifyapp.Fragments.LyricsFragment;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.ActivityFloatingBinding;
import com.app.spotifyapp.databinding.ActivityPlayTrackBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FloatingActivity extends AppCompatActivity {

//    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1001;
    private SpotifyAppRemote _SpotifyAppRemote;

    private ActivityFloatingBinding _binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityFloatingBinding.inflate(getLayoutInflater());
        View view = _binding.getRoot();
//        setTheme(android.R.style.Theme_Dialog);
        setContentView(view);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, 1001);}
//
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT
//        );
//
//        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        windowManager.addView(getWindow().getDecorView(), layoutParams);

//        String TAG = "Artists";
//        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
//        _binding.lyr.setMovementMethod(new ScrollingMovementMethod());
//        _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState ->
//                runOnUiThread(() -> getLyrics(playerState.track.artist.name, playerState.track.name)
//                )));

    }

    private String ChangeTitle(String title){
        title = title.toLowerCase();
        int feat = title.indexOf("(fe");
        if (feat != -1){
            return title.substring(0, feat).replaceAll("[^a-zA-Z0-10]", "");
        }

        return title.replaceAll("[^a-zA-Z0-10]", "");
    }
    private String ChangeName(String name){
        name = name.toLowerCase();
        return name.replaceAll("[$]", "s").replaceAll("[^a-zA-Z0-10]", "");

    }
    public void getLyrics(String artistName, String songTitle) {
        final String title = ChangeTitle(songTitle);
        final String name = ChangeName(artistName);
        String url = "https://www.azlyrics.com/lyrics/" + name + "/" + title + ".html";
        System.out.println(url);

        CompletableFuture.supplyAsync(() -> {
            try {
                return fetchLyricsFromUrl(url);
            } catch (IOException e) {
                Log.e("GeniusAPI", "Error getting lyrics from Genius" + e.getMessage());
                return null;
            }
        }).thenAccept(lyrics -> {
            runOnUiThread(() -> {
                if (lyrics != null) {
                    _binding.lyr.setText(lyrics);
                } else {
                    Log.e("Error Lyrics!+", "There is a problem with lyrics from HTML");
                    Toast.makeText(FloatingActivity.this, "NO Text for this song", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String extractLyricsFromHtml(Document doc) {
        StringBuilder lyrics = new StringBuilder();
        try {
            Element sib = doc.selectFirst("#azmxmbanner").previousElementSibling().previousElementSibling().previousElementSibling();

            String[] lines = Objects.requireNonNull(sib).html().split("<br>");
            for (String line : lines) {
                lyrics.append(Jsoup.parse(line).text()).append("\n");
            }
            lyrics.append("\n");

        }catch (Exception e){
            Log.e("FROM HTML", e.getMessage());
        }
        return lyrics.toString();
    }

    private String fetchLyricsFromUrl(String url) throws IOException {
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
                    .execute();

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
                    .header("Accept-Language", "en-US")
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .cookies(response.cookies())
                    .get();

            return extractLyricsFromHtml(doc);
        } catch (IOException e) {
            Log.e("GeniusAPI", "Error getting lyrics from Genius" + e.getMessage());
        }
        return null;
    }



/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {

            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        windowManager.removeView(getWindow().getDecorView());

    }
}