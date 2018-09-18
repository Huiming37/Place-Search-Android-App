package com.example.douhuiming.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class photosListViewHolder extends RecyclerView.ViewHolder{

    ImageView photo;

    public photosListViewHolder(View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.photoItem);
    }
}
