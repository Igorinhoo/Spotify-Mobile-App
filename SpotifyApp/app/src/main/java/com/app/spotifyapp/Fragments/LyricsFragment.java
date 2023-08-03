package com.app.spotifyapp.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.BaseMovementMethod;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.app.spotifyapp.FloatingActivity;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentLyricsBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public class LyricsFragment extends Fragment {

    private FragmentLyricsBinding _binding;
    private float initialX;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpotifyAppRemote _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
        _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState ->
                requireActivity().runOnUiThread(() -> getLyrics(playerState.track.artist.name, playerState.track.name)
                )));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentLyricsBinding.inflate(inflater, container, false);
        _binding.songLyrics.setMovementMethod(new ScrollingMovementMethod());
        return _binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();

        _binding.songLyrics.setMovementMethod(new ScrollingMovementMethod());

        _binding.songLyrics.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    initialX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - initialX;

                    if (deltaX > 300 || deltaX < -300) {
                        Navigation.findNavController(requireView()).navigate(R.id.action_lyricsFragment_to_trackCoverFragment);
                    }
                    break;
            }
            return true;
        });
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
    private void getLyrics(String artistName, String songTitle) {
        final String title = ChangeTitle(songTitle);
        final String name = ChangeName(artistName);
        String url = "https://www.azlyrics.com/lyrics/" + name + "/" + title + ".html";

        CompletableFuture.supplyAsync(() -> {
            try {
                return fetchLyricsFromUrl(url);
            } catch (IOException e) {
                Log.e("GeniusAPI", "Error getting lyrics from Genius" + e.getMessage());
                return null;
            }
        }).thenAccept(lyrics -> {
            requireActivity().runOnUiThread(() -> {
                if (lyrics != null) {
                    _binding.songLyrics.setText(lyrics);
                } else {
                    Log.e("Error Lyrics!+", "There is a problem with lyrics from HTML");
                    Toast.makeText(requireContext(), "NO Text for this song", Toast.LENGTH_SHORT).show();
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
            Log.e("GeniusAPI", "Error getting lyrics " + e.getMessage());
        }
        return null;
    }



//    private static final String API_BASE_URL = "https://api.genius.com";
//    private static final String API_ACCESS_TOKEN = "dnUj3Cs5M541FM7Tq4rgkB_HjAAdc_-BmcDa4YOrp0UJluq9BP7dKEw-RZakMTvx";
//    public void getLyrics(String artistName, String songTitle) {
//        String url = API_BASE_URL + "/search?q=" + artistName + " " + songTitle;
//        new FetchLyricsTask().execute(url);
//    }
//
//    private String extractLyricsFromHtml(Document doc) {
//        StringBuilder lyrics = new StringBuilder();
//        try {
//            Elements lyricElements = doc.select("div.Lyrics__Container-sc-1ynbvzw-5.Dzxov");
//            for (Element element : lyricElements) {
//                String[] lines = element.html().split("<br>");
//                for (String line : lines) {
//                    lyrics.append(Jsoup.parse(line).text()).append("\n");
//                }
//                lyrics.append("\n");
//            }
//        }catch (Exception e){
//            Log.e("FROM HTML", e.getMessage());
//        }
//
//        return lyrics.toString();
//    }
//
//    private class FetchLyricsTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            OkHttpClient client = new OkHttpClient();
//            try {
//                String url = urls[0];
//
//                Log.e("Lyrics url", url );
//
//                Request request = new Request.Builder()
//                        .url(url)
//                        .addHeader("Authorization", "Bearer " + API_ACCESS_TOKEN)
//                        .addHeader("User-Agent", "Mozilla/5.0")
//                        .build();
//
//                Response response = client.newCall(request).execute();
//
//                String jsonString = response.body().string();
//                JSONObject json = new JSONObject(jsonString);
//                JSONObject responseJson = json.getJSONObject("response");
//
//                JSONObject hitJson = responseJson.getJSONArray("hits").getJSONObject(0);
//                String songId = hitJson.getJSONObject("result").getString("id");
//
//                url = API_BASE_URL + "/songs/" + songId;
//
//                request = new Request.Builder()
//                        .url(url)
//                        .addHeader("Authorization", "Bearer " + API_ACCESS_TOKEN)
//                        .addHeader("User-Agent", "Mozilla/5.0")
//                        .build();
//
//                response = client.newCall(request).execute();
//
//                jsonString = response.body().string();
//                json = new JSONObject(jsonString);
//                JSONObject songJson = json.getJSONObject("response").getJSONObject("song");
//                String lyricsUrl = songJson.getString("url");
//
//                request = new Request.Builder()
//                        .url(lyricsUrl)
//                        .addHeader("User-Agent", "Mozilla/5.0")
//                        .build();
//
//                response = client.newCall(request).execute();
//                String htmlString = response.body().string();
//                return extractLyricsFromHtml(Jsoup.parse(htmlString));
//            } catch (IOException | JSONException e) {
//                Log.e("GeniusAPI", "Error getting lyrics from Genius" + e.getMessage());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String lyrics) {
//            if (lyrics != null) {
//                _binding.songLyrics.setText(lyrics);
//            } else {
//                Log.e("Error Lyrics!+", "There is problem with lyrics form HTML");
//                Toast.makeText(requireContext(), "NO Text for this song", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}