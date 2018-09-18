package com.example.douhuiming.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

public class LocationDetailReviewFragment extends Fragment {

    private LocationDetail locationsDetail;

    private ArrayList<Review> placeReviews;

    private ArrayList<Review> tempPlaceReviews;

    private ArrayList<Review> yelpReviews;

    private ArrayList<Review> tempYelpReviews;

    private  RecyclerView recyclerView;

    private TextView noResults  ;

    private  int reviewType;

    private  int sortType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reviewType =0;
        sortType = 0;

        locationsDetail = ((LocationDetailActivity)getActivity()).getLocationDetail();
        placeReviews = new ArrayList<>();
        placeReviews =  locationsDetail.placeReviews;
        tempPlaceReviews = new ArrayList<>(placeReviews);

        int index = 0;
        String addressForYelp = locationsDetail.placeAddress;

        for(int i = 0; i < addressForYelp.length(); i++){
            if(addressForYelp.charAt(i) == ','){
                index = i;
                break;
            }
        }

        addressForYelp = addressForYelp.substring(0,index);

        String url ="http://travelandentertainm-env.us-west-1.elasticbeanstalk.com/?city="
                +  URLEncoder.encode(locationsDetail.placeCity) + "&address1=" + URLEncoder.encode(addressForYelp)
                + "&state=" + locationsDetail.placeState + "&name=" + URLEncoder.encode(locationsDetail.placeName) +"&country=US";

        Log.i("response",url);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override

                    public void onResponse(String response) {

                        Log.i("response",response);
                        parseLocationsJsonObject(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    private void parseLocationsJsonObject(String jsonData){

        yelpReviews = new ArrayList<>();

        try{

            JSONObject jsonObject = new JSONObject(jsonData);

            JSONArray reviewsArray = jsonObject.getJSONArray("reviews");


            for(int i = 0; i < reviewsArray.length(); i++){

                JSONObject reviewObject =  reviewsArray.getJSONObject(i);

                Review review = new Review();

                JSONObject reviewUserObject = reviewObject.getJSONObject("user");


                review.reviewName = reviewUserObject.getString("name");
                review.reviewProfileUrl = reviewUserObject.getString("image_url");


                review.reviewTime =reviewObject.getString("time_created");
                review.reviewRating = reviewObject.getInt("rating");

                review.reviewText = reviewObject.getString("text");
                review.reviewUrl = reviewObject.getString("url");

                yelpReviews.add(review);
            }
            tempYelpReviews = new ArrayList<>(yelpReviews);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reviews_fragment, container, false);
        noResults = view.findViewById(R.id.NoResults);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.reviewRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new reviewListAdapter(placeReviews,getActivity()));

        if(placeReviews.size() == 0){
            noResults.setVisibility(View.VISIBLE);
        }else{
            noResults.setVisibility(View.GONE);
        }

        Spinner orderSpinner = getActivity().findViewById(R.id.sortType);

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(reviewType == 0){
                    if(placeReviews.size() != 0){
                        switch (position){
                            case 0:{
                                recyclerView.setAdapter(new reviewListAdapter(placeReviews,getActivity()));
                                sortType = 0;
                            }break;
                            case 1:{
                                Collections.sort(tempPlaceReviews,Review.RatingdecreasingComparator);
                                recyclerView.setAdapter(new reviewListAdapter(tempPlaceReviews,getActivity()));
                                sortType = 1;
                            }break;
                            case 2:{
                                Collections.sort(tempPlaceReviews,Review.RatingIncreasingComparator);
                                recyclerView.setAdapter(new reviewListAdapter(tempPlaceReviews,getActivity()));
                                sortType = 2;
                            }break;
                            case 3:{
                                Collections.sort(tempPlaceReviews,Review.TimedecreasingComparator);
                                recyclerView.setAdapter(new reviewListAdapter(tempPlaceReviews,getActivity()));
                                sortType = 3;
                            }break;
                            case 4:{
                                Collections.sort(tempPlaceReviews,Review.TimeIncreasingComparator);
                                recyclerView.setAdapter(new reviewListAdapter(tempPlaceReviews,getActivity()));
                                sortType = 4;
                            }break;
                        }

                    }else{
                        recyclerView.setAdapter(new reviewListAdapter(placeReviews,getActivity()));
                        noResults.setVisibility(View.VISIBLE);
                    }

                }else{
                    if(yelpReviews.size() != 0){
                        switch (position){
                            case 0:{
                                recyclerView.setAdapter(new reviewListAdapter(yelpReviews,getActivity()));
                                sortType = 0;
                            }break;
                            case 1:{
                                Collections.sort(tempYelpReviews,Review.RatingdecreasingComparator);
                                recyclerView.setAdapter(new reviewListAdapter(tempYelpReviews,getActivity()));
                                sortType = 1;
                            }break;
                            case 2:{
                                Collections.sort(tempYelpReviews,Review.RatingIncreasingComparator);
                                recyclerView.setAdapter(new reviewListAdapter(tempYelpReviews,getActivity()));
                                sortType = 2;
                            }break;
                            case 3:{
                                Collections.sort(tempYelpReviews,Review.TimedecreasingComparator);
                                recyclerView.setAdapter(new reviewListAdapter(tempYelpReviews,getActivity()));
                                sortType = 3;
                            }break;
                            case 4:{
                                Collections.sort(tempYelpReviews,Review.TimeIncreasingComparator);
                                recyclerView.setAdapter(new reviewListAdapter(tempYelpReviews,getActivity()));
                                sortType = 4;
                            }break;
                        }

                    }else{
                        recyclerView.setAdapter(new reviewListAdapter(yelpReviews,getActivity()));
                        noResults.setVisibility(View.VISIBLE);
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner typeSpinner = getActivity().findViewById(R.id.googleOrYelp);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{
                        if(placeReviews.size() != 0){

                            switch (sortType){
                                case 0:{
                                    recyclerView.setAdapter(new reviewListAdapter(placeReviews,getActivity()));

                                }break;
                                case 1:{
                                    Collections.sort(tempPlaceReviews,Review.RatingdecreasingComparator);
                                    recyclerView.setAdapter(new reviewListAdapter(tempPlaceReviews,getActivity()));

                                }break;
                                case 2:{
                                    Collections.sort(tempPlaceReviews,Review.RatingIncreasingComparator);
                                    recyclerView.setAdapter(new reviewListAdapter(tempPlaceReviews,getActivity()));

                                }break;
                                case 3:{
                                    Collections.sort(tempPlaceReviews,Review.TimedecreasingComparator);
                                    recyclerView.setAdapter(new reviewListAdapter(tempPlaceReviews,getActivity()));

                                }break;
                                case 4:{
                                    Collections.sort(tempPlaceReviews,Review.TimeIncreasingComparator);
                                    recyclerView.setAdapter(new reviewListAdapter(tempPlaceReviews,getActivity()));

                                }break;
                            }


                        }else{
                            recyclerView.setAdapter(new reviewListAdapter(placeReviews,getActivity()));
                            noResults.setVisibility(View.VISIBLE);

                        }

                        reviewType = 0;

                    }break;
                    case 1:{
                        if(yelpReviews.size() != 0){
                            switch (sortType){
                                case 0:{
                                    recyclerView.setAdapter(new reviewListAdapter(yelpReviews,getActivity()));
                                }break;
                                case 1:{
                                    Collections.sort(tempYelpReviews,Review.RatingdecreasingComparator);
                                    recyclerView.setAdapter(new reviewListAdapter(tempYelpReviews,getActivity()));
                                }break;
                                case 2:{
                                    Collections.sort(tempYelpReviews,Review.RatingIncreasingComparator);
                                    recyclerView.setAdapter(new reviewListAdapter(tempYelpReviews,getActivity()));
                                }break;
                                case 3:{
                                    Collections.sort(tempYelpReviews,Review.TimedecreasingComparator);
                                    recyclerView.setAdapter(new reviewListAdapter(tempYelpReviews,getActivity()));
                                }break;
                                case 4:{
                                    Collections.sort(tempYelpReviews,Review.TimeIncreasingComparator);
                                    recyclerView.setAdapter(new reviewListAdapter(tempYelpReviews,getActivity()));
                                }break;
                            }

                        }else{
                            recyclerView.setAdapter(new reviewListAdapter(yelpReviews,getActivity()));
                            noResults.setVisibility(View.VISIBLE);

                        }

                        reviewType = 1;
                    }break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


}
