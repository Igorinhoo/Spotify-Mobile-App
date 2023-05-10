package com.app.spotifyapp.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.spotifyapp.databinding.FragmentStatisticsBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding _binding;

    private String accessToken;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false);

//        AuthorizationRequest.Builder builder =
//                new AuthorizationRequest.Builder("8595ceb3423c45aca5775efb610b48b7", AuthorizationResponse.Type.TOKEN, "com.app.SpotifyApp://callback");
//
//        builder.setScopes(new String[]{"user-top-read"});
//        AuthorizationRequest request = builder.build();
//
//
//        Intent intent = AuthorizationClient.createLoginActivityIntent(requireActivity(), request);
//
//            launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    AuthorizationResponse response = AuthorizationClient.getResponse(result.getResultCode(), result.getData());
//
//                    switch (response.getType()) {
//                        case TOKEN:
//                            accessToken = response.getAccessToken();
//                            Log.e("TOKEN", accessToken );
//                            break;
//                        case ERROR:
//                            Log.e("Error", "DFJAFN" );
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            });
//
//            launcher.launch(intent);
//
//


        return _binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        /*ApiDataProvider api = new ApiDataProvider();

        _binding.stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                api.getTopArtists(new TopArtistsCallback() {
                    @Override
                    public void onTopArtistsDataReceived(ArrayList<ArtistDAO> artistsData) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("COS", artistsData.toString());
                                for (ArtistDAO artist : artistsData
                                ) {
                                    Log.e("Artist", artist.Name + " " + artist.Uri + " " + artist.Img);

                                }
                            }
                        });
                    }
                });



                }
        });*/





    }


/*
    private static final String TAG = "GeniusAPI";
    private static final String API_BASE_URL = "https://api.genius.com";
    private static final String API_ACCESS_TOKEN = "dnUj3Cs5M541FM7Tq4rgkB_HjAAdc_-BmcDa4YOrp0UJluq9BP7dKEw-RZakMTvx";

    public void getLyrics(String artistName, String songTitle) {
        String url = API_BASE_URL + "/search?q=" + artistName + " " + songTitle;
        new FetchLyricsTask().execute(url);
    }

    private String extractLyricsFromHtml(Document doc) {
        StringBuilder lyrics = new StringBuilder();
        Elements lyricElements = doc.select("div.Lyrics__Container-sc-1ynbvzw-5.Dzxov");
        for (Element element : lyricElements) {
            String[] lines = element.html().split("<br>");
            for (String line : lines) {
                lyrics.append(Jsoup.parse(line).text()).append("\n");
            }
            lyrics.append("\n");
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
                _binding.lyric.setText(lyrics);
            } else {
                Log.e("Error Lyrics!+", "There is problem with lyrics form HTML");
            }
        }
    }*/

}