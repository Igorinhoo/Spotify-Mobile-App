package com.app.spotifyapp.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.app.spotifyapp.R;
import com.app.spotifyapp.Repositories.LyricsProvider;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class FloatingWindowService extends Service implements LyricsProvider.LyricsCallback {
    private WindowManager windowManager;
    private View floatingWindowView;
    private TextView text;
    private static final int FOREGROUND_NOTIFICATION_ID = 1;

    @Override
    public void onLyricsFetched(String lyrics) {
        text.setText(lyrics);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        floatingWindowView = LayoutInflater.from(this).inflate(R.layout.floating_window_layout, null);

        text = floatingWindowView.findViewById(R.id.tvFloatingText);

        SpotifyAppRemote _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();
        _SpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback((playerState -> {
            LyricsProvider lyrics = LyricsProvider.GetInstance();
            lyrics.getLyrics(playerState.track.artist.name, playerState.track.name, this);
            }
        ));

        int widthInDp = 300;
        int heightInDp = 300;
        float scale = getResources().getDisplayMetrics().density;
        int widthInPixels = (int) (widthInDp * scale + 0.5f);
        int heightInPixels = (int) (heightInDp * scale + 0.5f);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                widthInPixels,
                heightInPixels,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingWindowView, params);

        floatingWindowView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        params.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingWindowView, params);
                        return true;
                    default:
                        return false;
                }
            }


            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

        });


        floatingWindowView.findViewById(R.id.btnCloseFloating).setOnClickListener(view -> {
            stopService(intent);
            // TODO: 8/23/2023 Mayby this: 
//            stopSelf();
        });

        floatingWindowView.findViewById(R.id.btnFloatingUp).setOnClickListener(view -> {
            scrollPosition -= 20;
            text.scrollTo(0, scrollPosition);
        });

        floatingWindowView.findViewById(R.id.btnFloatingDown).setOnClickListener(view -> {
            scrollPosition += 20;
            text.scrollTo(0, scrollPosition);
        });

        startForeground(FOREGROUND_NOTIFICATION_ID, buildForegroundNotification());

        return START_STICKY;
    }

    private int scrollPosition = 0;
    private Notification buildForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL")
                .setContentTitle("Floating Window Service")
                .setContentText("Floating window is active")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CHANNEL",
                    "Floating Window Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        return builder.build();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingWindowView != null) {
            windowManager.removeView(floatingWindowView);
        }
    }


}