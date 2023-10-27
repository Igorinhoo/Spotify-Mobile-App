package com.app.spotifyapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.LyricsProvider;
import com.app.spotifyapp.Services.FloatingWindowService;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentLyricsBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;


public class LyricsFragment extends Fragment implements LyricsProvider.LyricsCallback {

    private FragmentLyricsBinding _binding;
    private float initialX;

    @Override
    public void onLyricsFetched(String lyrics) {
        _binding.songLyrics.setText(lyrics);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpotifyAppRemote _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
        _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState ->
                requireActivity().runOnUiThread(() -> {
                    LyricsProvider lyrics = LyricsProvider.GetInstance(requireActivity());
                    lyrics.GetLyrics(playerState.track.artist.name, playerState.track.name, this);
                })));
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

        _binding.btnToFloating.setOnClickListener(view -> {
            if (Settings.canDrawOverlays(requireContext())) {
                StartFloatingLyrics();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + requireActivity().getPackageName()));
                startActivityForResult(intent, 1001);
            }
        });

        _binding.songLyrics.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    initialX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - initialX;

                    if (deltaX > 300 || deltaX < -300) {
                        try {
                            Navigation.findNavController(requireView()).navigate(R.id.action_lyricsFragment_to_trackCoverFragment);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
            }
            return true;
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (Settings.canDrawOverlays(requireContext())) {
                StartFloatingLyrics();
            }
        }
    }

    private void StartFloatingLyrics(){
        Intent intent = new Intent(requireContext(), FloatingWindowService.class);
        requireActivity().startForegroundService(intent);
    }



}