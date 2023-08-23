package com.app.spotifyapp.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.app.spotifyapp.R;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.app.spotifyapp.databinding.FragmentTrackCoverBinding;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;


public class TrackCoverFragment extends Fragment {
    // TODO: 8/2/2023 Try to do it without this fragment

    private FragmentTrackCoverBinding _binding;
    private float initialX;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentTrackCoverBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart() {
        super.onStart();

        final SpotifyAppRemote _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();



        _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState ->
                requireActivity().runOnUiThread(() -> {
                    _SpotifyAppRemote
                            .getImagesApi()
                            .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                            .setResultCallback(
                                    bitmap -> _binding.trackImage.setImageBitmap(bitmap));
                })
        ));

        _binding.trackImage.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    initialX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - initialX;

                    if (deltaX > 400 || deltaX < -400) {
                        Navigation.findNavController(requireView()).navigate(R.id.action_trackCoverFragment_to_lyricsFragment);
                    }
                    break;
            }
            return true;
        });

    }
}