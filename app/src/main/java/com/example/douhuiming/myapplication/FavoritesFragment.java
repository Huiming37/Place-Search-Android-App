package com.example.douhuiming.myapplication;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by douhuiming on 4/16/18.
 */

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;

    private SharedPreferences sp;

    private ArrayList<LocationOriginal> locationsList;

    private SharedPreferences.Editor editor;

    private LocationDetail locationsDetail ;

    private TextView noResults  ;

    private String TAG = "tag";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_fragment, container, false);

        noResults = view.findViewById(R.id.NoResults);

        System.out.println(TAG + ":onActivityCreated()");

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println(TAG + ":onActivityCreated()");
    }


    @Override
    public void onStart() {

        super.onStart();

        locationsList = new ArrayList<>();

        locationsDetail = new LocationDetail();

        sp = getActivity().getSharedPreferences("favourites", Context.MODE_PRIVATE);

        editor = sp.edit();

        Map<String, ?> allEntries = sp.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            LocationOriginal locationOriginal = new LocationOriginal();
            String key = entry.getKey();
            String str = entry.getValue().toString();
            String[] strArray= str.split("\\?");
            Log.i("fav",str);
            locationOriginal.placeID = key;
            locationOriginal.placeName = strArray[0];
            locationOriginal.placeAddress =strArray[1];
            locationOriginal.icon = strArray[2];
            locationsList.add(locationOriginal);

        }

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.FavortesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new FavoritesAdapter(locationsList,onRecyclerviewItemClickListener));

        if(locationsList.size() == 0){
            noResults.setVisibility(View.VISIBLE);
        }else{
            noResults.setVisibility(View.GONE);
        }

        System.out.println(TAG + ":onStart()");
    }



    public OnRecyclerviewItemClickListener onRecyclerviewItemClickListener = new OnRecyclerviewItemClickListener() {

        @Override
        public void onItemClickListener(View v,String viewName, int position) {

            String placeId = locationsList.get(position).placeID;

            String placeName = locationsList.get(position).placeName;

            if(viewName.equals("favBtn")){

                editor.remove(placeId);
                editor.commit();


                Toast toast=Toast.makeText(getActivity(),placeName +
                        " was removed from favorites",Toast.LENGTH_LONG);
                showMyToast(toast, 800);

                locationsList.remove(position);

                if(locationsList.size() == 0){
                    noResults.setVisibility(View.VISIBLE);
                }else{
                    noResults.setVisibility(View.GONE);
                }

                recyclerView.setAdapter(new FavoritesAdapter(locationsList,onRecyclerviewItemClickListener));

            }else{

                Log.i("test",placeId);

                String url ="http://hw9backend-env.us-west-1.elasticbeanstalk.com/?placeId=" + placeId;

                //Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getActivity());

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override

                            public void onResponse(String response) {

                                Log.i("response",response);

                                parseLocationJsonObject(response);

                                actionsOnUiThread();

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(stringRequest);
            }
        }
    };

    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }


    private void actionsOnUiThread(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), LocationDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("locationDetail",(Serializable)locationsDetail);//序列化,要注意转化(Serializable)
                intent.putExtras(bundle);//发送数据
                startActivity(intent);//启动intent
            }
        });

    }


    private void parseLocationJsonObject(String jsonData){

        try{

            JSONObject jsonObject = new JSONObject(jsonData);

            JSONObject results =  new JSONObject(jsonObject.getString("result"));

            JSONArray reviews = results.getJSONArray("reviews");

            JSONArray locationComponents = results.getJSONArray("address_components");


            for(int i = 0; i < locationComponents.length(); i++){

                JSONObject locationComponent =  locationComponents.getJSONObject(i);

                JSONArray locationComponentType = locationComponent.getJSONArray("types");

                if(locationComponentType.getString(0).equals("locality") ){

                    locationsDetail.placeCity = locationComponent.getString("short_name");

                    System.out.println(locationsDetail.placeCity);

                }

                if(locationComponentType.getString(0).equals("administrative_area_level_1") ){

                    locationsDetail.placeState = locationComponent.getString("short_name");
                    System.out.println(locationsDetail.placeState);
                }

            }

            locationsDetail.placeReviews = new ArrayList<>();

            for(int i = 0 ; i < reviews.length(); i++){

                JSONObject reviewObject =  reviews.getJSONObject(i);
                Review review = new Review();

                review.reviewName = reviewObject.getString("author_name");
                review.reviewProfileUrl = reviewObject.getString("profile_photo_url");
                review.reviewRating = reviewObject.getInt("rating");

                review.reviewText = reviewObject.getString("text");
                review.reviewUrl = reviewObject.getString("author_url");

                review.reviewTime = timeFormChange(reviewObject.getLong("time"));

                locationsDetail.placeReviews.add(review);

            }

            JSONObject geo =  new JSONObject(results.getString("geometry"));

            JSONObject location =  new JSONObject(geo.getString("location"));

            locationsDetail.Lat = location.getDouble("lat");

            locationsDetail.Lng = location.getDouble("lng");


            locationsDetail.placeId = results.getString("place_id");


            locationsDetail.placeName = results.getString("name");

            locationsDetail.placeNumber = results.getString("formatted_phone_number");

            locationsDetail.placeAddress = results.getString("formatted_address");

            locationsDetail.placePriceLevel = "";
            for(int i = 0; i < results.getInt("price_level"); i++){
                locationsDetail.placePriceLevel +="$";
            }

            locationsDetail.placeRating = results.getLong("rating");

            locationsDetail.placeGooglePage = results.getString("url");

            locationsDetail.placeWebsite = results.getString("website");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String timeFormChange(Long seconds) {

        long millis = seconds * 1000;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
        return formattedDate;

    }

}
