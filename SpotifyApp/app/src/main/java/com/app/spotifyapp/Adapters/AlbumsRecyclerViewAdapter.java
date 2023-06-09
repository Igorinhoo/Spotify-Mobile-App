package com.app.spotifyapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spotifyapp.Interfaces.OnAlbumClickListener;
import com.app.spotifyapp.Models.AlbumDAO;
import com.app.spotifyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlbumsRecyclerViewAdapter extends RecyclerView.Adapter<AlbumsRecyclerViewAdapter.ViewHolder>{
    private Context context;
    private ArrayList<AlbumDAO> data;

    private OnAlbumClickListener _listener;

    public AlbumsRecyclerViewAdapter(Context context, ArrayList<AlbumDAO> data, OnAlbumClickListener listener){
        this.context = context;
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


        return new ViewHolder(view, _listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_listener != null) {
                    _listener.onItemClick(position);
                }
            }
        });

        holder.name.setText(data.get(position).AlbumName);
        Picasso.get().load(data.get(position).AlbumImg).into(holder.image);
//        data.get(position).getThird()
    }
    @Override
    public int getItemCount() {
        return data.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name;
        private ImageView image;
        private OnAlbumClickListener _listener;

        public ViewHolder(@NonNull View itemView, OnAlbumClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.albumName);
            image = itemView.findViewById(R.id.albumImage);
            _listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            _listener.onItemClick(getAdapterPosition());
        }
    }

}
