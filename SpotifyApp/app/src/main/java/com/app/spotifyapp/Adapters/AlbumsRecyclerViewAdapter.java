package com.app.spotifyapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spotifyapp.Interfaces.Listeners.OnAlbumClickListener;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlbumsRecyclerViewAdapter extends RecyclerView.Adapter<AlbumsRecyclerViewAdapter.ViewHolder>{
    private ArrayList<AlbumDAO> data;

    private final OnAlbumClickListener _listener;

    public AlbumsRecyclerViewAdapter(ArrayList<AlbumDAO> data, OnAlbumClickListener listener){
        this.data = data;
        this._listener = listener;
    }

    public void UpdateData(ArrayList<AlbumDAO> newData){
        this.data = newData;
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public AlbumsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.albumlistview,
                parent,
                false);


        return new ViewHolder(view, _listener, data);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            if (_listener != null) {
                _listener.onItemClick(data.get(position));
            }
        });

        holder.name.setText(data.get(position).getName());
        String img;
        try{
            img = data.get(position).getImages()[0].getUrl();
        }catch (Exception e){
            img = "https://tinyurl.com/2p8pzm3e";
        }

        Picasso.get().load(img).into(holder.image);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView name;
        private final ImageView image;
        private final OnAlbumClickListener _listener;
        private final ArrayList<AlbumDAO> _data;

        public ViewHolder(@NonNull View itemView, OnAlbumClickListener listener, ArrayList<AlbumDAO> data) {
            super(itemView);

            name = itemView.findViewById(R.id.albumName);
            image = itemView.findViewById(R.id.albumImage);
            _listener = listener;
            itemView.setOnClickListener(this);
            _data = data;
        }

        @Override
        public void onClick(View view) {
            _listener.onItemClick(_data.get(getAdapterPosition()));
        }
        


    }

}
