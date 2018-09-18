package com.example.douhuiming.myapplication;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.GeoDataClient;

import java.util.ArrayList;
import java.util.List;

public class photosListAdapter extends RecyclerView.Adapter {

    private ArrayList<Bitmap> data;

    public photosListAdapter (@NonNull ArrayList<Bitmap> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photos_fragment_item, parent, false);
        return new photosListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Bitmap bitmap = data.get(position);

        ((photosListViewHolder) holder).photo.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
