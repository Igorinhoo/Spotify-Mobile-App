package com.app.spotifyapp.Adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spotifyapp.Interfaces.Listeners.OnTrackClickListener;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TracksRecyclerViewAdapter extends RecyclerView.Adapter<TracksRecyclerViewAdapter.ViewHolder>{

    private ArrayList<TrackDAO> data;

    private final OnTrackClickListener _listener;

    public TracksRecyclerViewAdapter(Context context, ArrayList<TrackDAO> data, OnTrackClickListener listener){
        this.data = data;
        this._listener = listener;
    }

    public void UpdateData(ArrayList<TrackDAO> newData){
        this.data = newData;
        this.notifyDataSetChanged();
    }

    public String timeToDuration(Long time){
        long min = TimeUnit.MILLISECONDS.toMinutes(time);
        long sec = TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));

        return min + ":" + (sec < 10 ? "0" + sec : sec);
    }

    @NonNull
    @Override
    public TracksRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracklistview,
                parent,
                false);


        return new ViewHolder(view, _listener, data);
    }

    @Override
    public void onBindViewHolder(@NonNull TracksRecyclerViewAdapter.ViewHolder holder, int position) {
        final int currentPosition = position;
        holder.itemView.setOnClickListener(view -> {
            if (_listener != null) {
                _listener.onItemClick(data.get(currentPosition), currentPosition);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            private final Handler longClickHandler = new Handler(Looper.getMainLooper());
            @Override
            public boolean onLongClick(View view) {
                longClickHandler.postDelayed(() -> {
                    if (_listener != null) {
                        _listener.onLongClick(data.get(currentPosition));
                    }
                }, 500);

                return true;
            }
        });

        holder.name.setText(data.get(position).Name);
        holder.duration.setText(timeToDuration(data.get(position).Duration));
        Picasso.get().load(data.get(position).Img).into(holder.image);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private final TextView name, duration;
        private final ImageView image;
        private final OnTrackClickListener _listener;
        private final ArrayList<TrackDAO> _data;

        public ViewHolder(@NonNull View itemView, OnTrackClickListener listener, ArrayList<TrackDAO> data) {
            super(itemView);

            name = itemView.findViewById(R.id.trackName);
            duration = itemView.findViewById(R.id.trackDuration);
            image = itemView.findViewById(R.id.albumImage);

            _listener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            _data = data;
        }

        @Override
        public void onClick(View view) {
            _listener.onItemClick(_data.get(getAdapterPosition()), getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            _listener.onLongClick(_data.get(getAdapterPosition()));
            return true;

        }
    }

}
