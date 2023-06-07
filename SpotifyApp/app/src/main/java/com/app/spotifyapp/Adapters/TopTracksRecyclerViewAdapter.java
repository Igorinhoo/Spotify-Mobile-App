package com.app.spotifyapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spotifyapp.Interfaces.Listeners.OnTopTrackClickListener;
import com.app.spotifyapp.Models.TrackDAO;
import com.app.spotifyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TopTracksRecyclerViewAdapter extends RecyclerView.Adapter<TopTracksRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<TrackDAO> data;

    private OnTopTrackClickListener _listener;

    public TopTracksRecyclerViewAdapter(Context context, ArrayList<TrackDAO> data, OnTopTrackClickListener listener) {
        this.context = context;
        this.data = data;
        this._listener = listener;
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


        return new ViewHolder(view, _listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TopTracksRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_listener != null) {
                    _listener.onItemClick(position);
                }
            }
        });
        holder.place.setText(Integer.toString( position + 1));
        holder.name.setText(data.get(position).Name);
        holder.artists.setText(data.get(position).Artists);
        Picasso.get().load(data.get(position).Img).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, artists, place;
        private ImageView image;
        private final OnTopTrackClickListener _listener;

        public ViewHolder(@NonNull View itemView, OnTopTrackClickListener listener) {
            super(itemView);

            place = itemView.findViewById(R.id.topTrackPosition);
            name = itemView.findViewById(R.id.topTrackName);
            artists = itemView.findViewById(R.id.topTrackArtists);
            image = itemView.findViewById(R.id.topImgImg);

            _listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            _listener.onItemClick(getAdapterPosition());
        }
    }

}
