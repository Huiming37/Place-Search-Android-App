package com.example.douhuiming.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocationDetailPhotosFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private LocationDetail locationsDetail;

    private ArrayList<Bitmap> photosBitmap;

    private  RecyclerView recyclerView;

    private TextView noResults;

    private String placeId;

    private GoogleApiClient mGoogleApiClient;

    private int photoNumber;

    private int index;

    View view;

    String TAG ="tag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        index = 0;

        photosBitmap = new ArrayList<>();

        locationsDetail = ((LocationDetailActivity)getActivity()).getLocationDetail();

        placeId = locationsDetail.placeId;

        mGoogleApiClient = ((LocationDetailActivity)getActivity()).getmGoogleApiClient();

        placePhotosAsync();

        Log.e("info","Fragment--onCreat()");
    }



    private void placePhotosAsync() {

        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {


                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                        photoNumber = photoMetadataBuffer.getCount();

                        if (photoMetadataBuffer.getCount() > 0) {
                            for (int i = 0; i < photoMetadataBuffer.getCount(); i++) {
                                photoMetadataBuffer.get(i)
                                        .getScaledPhoto(mGoogleApiClient, 1600,
                                                1600)
                                        .setResultCallback(mDisplayPhotoResultCallback);
                            }
                        }

                        photoMetadataBuffer.release();
                    }
                });
    }



    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            photosBitmap.add(placePhotoResult.getBitmap());
            index ++;

            if(index == photoNumber){

                recyclerView.setAdapter(new photosListAdapter(photosBitmap));

                if(photosBitmap.size() == 0){
                    noResults.setVisibility(View.VISIBLE);
                }else{
                    noResults.setVisibility(View.GONE);
                }

            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.photos_fragment, container, false);
        Log.e("info","Fragment--onCreatView()");
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {


        super.onActivityCreated(savedInstanceState);

        noResults =  view.findViewById(R.id.NoResults);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.photoRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new photosListAdapter(photosBitmap));



        if(photosBitmap.size() == 0){
            noResults.setVisibility(View.VISIBLE);
            Log.e("info","bbbbb");
        }else{
            noResults.setVisibility(View.GONE);
        }


        Log.e("info","Fragment--onActivityCreated()");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e("info","Fragment--onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("info","Fragment--onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("info","Fragment--onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("info","Fragment--onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("info","Fragment--onDestroyView()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
