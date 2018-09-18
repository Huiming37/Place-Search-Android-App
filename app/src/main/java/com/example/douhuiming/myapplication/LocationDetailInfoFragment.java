package com.example.douhuiming.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class LocationDetailInfoFragment extends Fragment {

    private LocationDetail locationsDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        locationsDetail = ((LocationDetailActivity)getActivity()).getLocationDetail();

        TextView placeAddress = getActivity().findViewById(R.id.detailAddress);
        placeAddress.setText(locationsDetail.placeAddress);

        TextView placePhoneNumber = getActivity().findViewById(R.id.detailPhoneNumer);
        if(locationsDetail.placeNumber != null){
            placePhoneNumber.setText(locationsDetail.placeNumber);
        }else{
            getActivity().findViewById(R.id.layout_phone).setVisibility(View.GONE);
        }

        TextView placePriceLevel = getActivity().findViewById(R.id.detailPriceLevel);
        if(locationsDetail.placePriceLevel != null){
            placePriceLevel.setText(locationsDetail.placePriceLevel);
        }else{
            getActivity().findViewById(R.id.layout_priveLevel).setVisibility(View.GONE);
        }


        RatingBar placeRating = getActivity().findViewById(R.id.detailRating);
        if(locationsDetail.placeRating != 0){
            placeRating.setRating(locationsDetail.placeRating);
        }else{
            getActivity().findViewById(R.id.layout_rating).setVisibility(View.GONE);
        }

        TextView placeGooglePage = getActivity().findViewById(R.id.detailGooglePage);
        if(locationsDetail.placeGooglePage != null){
            placeGooglePage.setText(locationsDetail.placeGooglePage);
        }else{
            getActivity().findViewById(R.id.layout_googlePage).setVisibility(View.GONE);
        }


        TextView placeWebsite = getActivity().findViewById(R.id.detailWebsite);
        if(locationsDetail.placeWebsite != null){
            placeWebsite.setText(locationsDetail.placeWebsite);
        }else{
            getActivity().findViewById(R.id.layout_website).setVisibility(View.GONE);
        }
    }

}
