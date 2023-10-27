package com.app.spotifyapp.Repositories;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LyricsProvider {
    private Activity activity;

    private LyricsProvider(){}
    private LyricsProvider(Activity activity){
        this.activity = activity;
    }

    private static LyricsProvider instance;
    public static LyricsProvider GetInstance(){
        if (instance == null) instance = new LyricsProvider();
        return instance;
    }
    public static LyricsProvider GetInstance(Activity Activity){
        if (instance == null) instance = new LyricsProvider(Activity);
        return instance;
    }

    public interface LyricsCallback {
        void onLyricsFetched(String lyrics);
    }

    public void GetLyrics(String artistName, String songTitle, LyricsCallback callback){
        boolean isAZ = activity.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isAZ", true);

        String name = isAZ ? "AZ" : "Genius";
//        Toast.makeText(activity, name, Toast.LENGTH_SHORT).show();

        if(isAZ){
            ALyrics lyr = new ALyrics();
            lyr.getLyrics(artistName, songTitle, callback);
            return;
        }
        BLyrics lyr = new BLyrics();
        lyr.getLyrics(artistName, songTitle, callback);
    }



    private class ALyrics{
        private String ChangeTitleAZ(String title){
            title = title.toLowerCase();
            int feat = title.indexOf("(fe");
            if (feat != -1){
                return title.substring(0, feat).replaceAll("[^a-zA-Z0-10]", "");
            }

            return title.replaceAll("[^a-zA-Z0-10]", "");
        }
        private String ChangeNameAZ(String name){
            name = name.toLowerCase();
            return name.replaceAll("[$]", "s").replaceAll("[^a-zA-Z0-10]", "");

        }

        public void getLyrics(String artistName, String songTitle, LyricsCallback callback) {
            final String title = ChangeTitleAZ(songTitle);
            final String name = ChangeNameAZ(artistName);
            String url = "https://www.azlyrics.com/lyrics/" + name + "/" + title + ".html";

            CompletableFuture.supplyAsync(() -> {
                try {
                    return fetchLyricsFromUrl(url);
                } catch (IOException e) {
                    Log.e("Lyrics API", "Error getting lyrics from Genius" + e.getMessage());
                    return null;
                }
            }).thenAccept(lyrics -> activity.runOnUiThread(() -> {
                if (lyrics != null) {
                    callback.onLyricsFetched(lyrics);
                } else {
                    Log.e("Error Lyrics!+", "There is a problem with lyrics from HTML");
                }
            }));
        }
        private String extractLyricsFromHtml(Document doc) {
            StringBuilder lyrics = new StringBuilder();
            try {
                Element sib = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(doc.selectFirst("#azmxmbanner")).previousElementSibling()).previousElementSibling()).previousElementSibling();

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
                Log.e("Lyrics API", "Error getting lyrics " + e.getMessage());
            }
            return null;
        }


    }

//


    private class BLyrics{
        private String ChangeTitleGenius(String title){
            int feat = title.indexOf("(fe");
            if (feat != -1){
                return title.substring(0, feat).replaceAll("[^a-zA-Z0-9\\s]", "");
            }

            return title.replaceAll("[^a-zA-Z0-9\\s]", "");
        }
        public void getLyrics(String artistName, String songTitle, LyricsCallback callback) {
            String title = ChangeTitleGenius(songTitle);

            String url = "https://api.genius.com/search?q=" + artistName + " " + title;

            CompletableFuture.supplyAsync(() -> {
                try {
                    return fetchLyricsFromUrl(url);
                } catch (IOException e) {
                    Log.e("Lyrics API", "Error getting lyrics from Genius" + e.getMessage());
                    return null;
                }
            }).thenAccept(lyrics -> activity.runOnUiThread(() -> {
                if (lyrics != null) {
                    callback.onLyricsFetched(lyrics);
                } else {
                    Log.e("Error Lyrics!+", "There is a problem with lyrics from HTML");
                }
            }));
        }

        private String fetchLyricsFromUrl(String url) throws IOException {
            try {

                final String API_ACCESS_TOKEN = "CafXhpr0AObVJRXEdsnZX3kikE594izLUZq9oGWUJfh2cYrjeXXTNrf42C4kSKHx";
                Connection.Response searchResponse = Jsoup.connect(url)
                        .header("Authorization", "Bearer " + API_ACCESS_TOKEN)
                        .userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
                        .header("Accept-Language", "en-US")
                        .header("Accept-Encoding", "gzip,deflate,sdch")
                        .ignoreContentType(true)
                        .execute();

                Log.e( "getLcs: ",  url);

                String jsonString = searchResponse.body();

                JSONObject js = new JSONObject(jsonString);
                String lyricsUrl = js.getJSONObject("response").getJSONArray("hits").getJSONObject(0).getJSONObject("result").getString("url");

                Connection.Response lyricsResponse = Jsoup.connect(lyricsUrl)
                        .header("Authorization", "Bearer " + API_ACCESS_TOKEN)
                        .header("User-Agent", "Mozilla/5.0")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                        .header("Accept-Language", "en-US")
                        .ignoreContentType(true)
                        .execute();

                Document dov = lyricsResponse.parse();

                Log.e( "getLyrics: ",  lyricsUrl);


                return extractLyricsFromHtml(dov);

            } catch (IOException | JSONException e) {
                Log.e("Lyrics API", "Error getting lyrics from Genius: " + e.getMessage());
                return null;
            }


        }
        private String extractLyricsFromHtml(Document doc) {
            StringBuilder lyrics = new StringBuilder();
            try {
                Elements lyricElements = doc.select("div.Lyrics__Container-sc-1ynbvzw-1.kUgSbL");
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

    }
}
