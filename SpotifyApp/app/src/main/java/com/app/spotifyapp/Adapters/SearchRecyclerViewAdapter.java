package com.app.spotifyapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spotifyapp.Interfaces.Listeners.OnSearchClickListener;
import com.app.spotifyapp.Models.SearchDAO;
import com.app.spotifyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>{
    private ArrayList<SearchDAO> data;
    private final OnSearchClickListener _listener;

    public SearchRecyclerViewAdapter(ArrayList<SearchDAO> data, OnSearchClickListener listener){
        this.data = data;
        this._listener = listener;
    }

    public void UpdateData(ArrayList<SearchDAO> newData){
        this.data = newData;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchlistview,
                parent,
                false);


        return new ViewHolder(view, _listener, data);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            if (_listener != null) {
                _listener.onItemClick(data.get(position));
            }
        });
        holder.name.setText(data.get(position).Name);
        Picasso.get().load(data.get(position).Img).into(holder.image);
        //TODO:Set on click listener to options
        holder.options.setOnClickListener(null);

    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView name;
        private final ImageView image, options;
        private final OnSearchClickListener _listener;
        private final ArrayList<SearchDAO> _data;

        public ViewHolder(@NonNull View itemView, OnSearchClickListener listener, ArrayList<SearchDAO> data) {
            super(itemView);

            name = itemView.findViewById(R.id.tvSearchName);
            image = itemView.findViewById(R.id.ivSearchImage);
            options = itemView.findViewById(R.id.ivSearchOptions);

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
