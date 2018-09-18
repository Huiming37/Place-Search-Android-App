package com.example.douhuiming.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class reviewListAdapter extends RecyclerView.Adapter {

    private ArrayList<Review> data;

    private Context context;

    public reviewListAdapter(@NonNull ArrayList<Review> data ,Context context) {

        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reviews_fragment_item, parent, false);
        return new reviewListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Review review = data.get(position);

        ((reviewListViewHolder) holder).text.setText(review.reviewText);
        ((reviewListViewHolder) holder).name .setText(review.reviewName);
        Picasso.get().load(review.reviewProfileUrl).into(((reviewListViewHolder) holder).photo);
        ((reviewListViewHolder) holder).rating.setRating(review.reviewRating);
        ((reviewListViewHolder) holder).time.setText(review.reviewTime);

        ((reviewListViewHolder) holder).singleRivew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Uri uri = Uri.parse(review.reviewUrl ); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



}
