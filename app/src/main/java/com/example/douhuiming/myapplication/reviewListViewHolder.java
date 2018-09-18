package com.example.douhuiming.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class reviewListViewHolder extends RecyclerView.ViewHolder {

    ImageView photo;
    TextView time;
    RatingBar rating;
    TextView name;
    TextView text;
    LinearLayout singleRivew;


    public reviewListViewHolder(View itemView) {
        super(itemView);
        photo = itemView.findViewById(R.id.review_photo);
        time = itemView.findViewById(R.id.review_time);
        rating = itemView.findViewById(R.id.review_rating);
        name = itemView.findViewById(R.id.review_name);
        text = itemView.findViewById(R.id.review_text);
        singleRivew = itemView.findViewById(R.id.singleReview);

    }


}
