package com.example.douhuiming.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by douhuiming on 4/18/18.
 */

public class LocationsListViewHolder extends RecyclerView.ViewHolder  {

    TextView placeName;
    TextView placeAddress;
    ImageView placeIcon;
    ImageView IsFav;

    public LocationsListViewHolder(View itemView) {
        super(itemView);
        placeName =  itemView.findViewById(R.id.locationName);
        placeAddress = itemView.findViewById(R.id.locationAddress);
        placeIcon = itemView.findViewById(R.id.locationIcon);
        IsFav = itemView.findViewById(R.id.IsFav);
    }
}
