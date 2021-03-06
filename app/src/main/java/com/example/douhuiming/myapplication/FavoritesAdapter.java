

package com.example.douhuiming.myapplication;

        import android.graphics.drawable.Drawable;
        import android.support.annotation.NonNull;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.squareup.picasso.Picasso;

        import java.util.List;

/**
 * Created by douhuiming on 4/18/18.
 */

public class FavoritesAdapter  extends RecyclerView.Adapter implements View.OnClickListener {

    private List<LocationOriginal> data;

    private OnRecyclerviewItemClickListener mOnRecyclerviewItemClickListener;

    public FavoritesAdapter (@NonNull List<LocationOriginal> data, OnRecyclerviewItemClickListener mOnRecyclerviewItemClickListener) {
        this.data = data;
        this.mOnRecyclerviewItemClickListener = mOnRecyclerviewItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.locations_list_item, parent, false);

        view.setOnClickListener(this);
        view.findViewById(R.id.IsFav).setOnClickListener(this);

        return new LocationsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LocationOriginal locationOriginal = data.get(position);

        ((LocationsListViewHolder) holder).itemView.setTag(position);
        ((LocationsListViewHolder) holder).IsFav.setTag(position);

        ((LocationsListViewHolder) holder).placeName.setText(locationOriginal.placeName);
        ((LocationsListViewHolder) holder).placeAddress.setText(locationOriginal.placeAddress);
        Picasso.get().load(locationOriginal.icon).into(((LocationsListViewHolder) holder).placeIcon);

        Drawable redDrawable = ((LocationsListViewHolder) holder).IsFav.getContext().getResources().getDrawable(R.drawable.heart_fill_red);
        ((LocationsListViewHolder) holder).IsFav.setImageDrawable(redDrawable);

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    @Override
    public void onClick(View v) {

        int position = (int) v.getTag();

        switch (v.getId()){
            case R.id.IsFav:
                mOnRecyclerviewItemClickListener.onItemClickListener(v, "favBtn", ((int) v.getTag()));

                break;
            default:
                mOnRecyclerviewItemClickListener.onItemClickListener(v, "recyclerItem", ((int) v.getTag()));

                break;
        }

    }
}

