package com.app.spotifyapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.PlayTrackActivity;
import com.app.spotifyapp.R;
import com.app.spotifyapp.Services.SpotifyAppRemoteConnector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TopTracksRecyclerViewAdapter extends RecyclerView.Adapter<TopTracksRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<TrackDAO> data;

    private final SpotifyAppRemote _SpotifyAppRemote;

    public TopTracksRecyclerViewAdapter(Context context, ArrayList<TrackDAO> data) {
        this.context = context;
        this.data = data;
        _SpotifyAppRemote = SpotifyAppRemoteConnector.GetAppRemote();

    }

    public void UpdateData(ArrayList<TrackDAO> newData) {
        this.data = newData;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopTracksRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_tracks,
                parent,
                false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopTracksRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            if (_SpotifyAppRemote != null) {
                TrackDAO track = data.get(position);
                _SpotifyAppRemote.getPlayerApi().play("spotify:track:" + track.Id);
                Intent intent = new Intent(context, PlayTrackActivity.class);
                intent.putExtra("TrackHref", track.Id);
                context.startActivity(intent);
            }

        });
        holder.place.setText(Integer.toString(position + 1));
        holder.name.setText(data.get(position).Name);
        holder.artists.setText(data.get(position).Artists);
        Picasso.get().load(data.get(position).Img).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, artists, place;
        private final ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            place = itemView.findViewById(R.id.topTrackPosition);
            name = itemView.findViewById(R.id.topTrackName);
            artists = itemView.findViewById(R.id.topTrackArtists);
            image = itemView.findViewById(R.id.topImgImg);
        }

    }

}
