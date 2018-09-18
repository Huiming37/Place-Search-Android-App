package com.example.douhuiming.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by douhuiming on 4/18/18.
 */

public class LocationsShowActivity extends AppCompatActivity {

    private ArrayList<LocationOriginal> locationsList;

    private ArrayList<LocationOriginal> firstPageLocationsList;

    private int currentPage;

    private String nextToken;

    private ArrayList<String> nextTokens;

    private LocationDetail locationsDetail ;

    private RecyclerView recyclerView;

    private Button prevButton;

    private Button nextButton;

    private SharedPreferences sp;

    private SharedPreferences.Editor editor;

    private TextView noResults;

    private int clickPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations_lists);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Results");


        noResults = findViewById(R.id.NoResults);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);

        addListenForPrevButton();
        addListenForNextButton();


        Intent intent = this.getIntent();

        locationsDetail = new LocationDetail();

        locationsList = (ArrayList<LocationOriginal>) intent.getSerializableExtra("list");//获取list方式

        sp = getSharedPreferences("favourites", Context.MODE_PRIVATE);

        editor = sp.edit();


        firstPageLocationsList = new ArrayList<>(locationsList);
        nextToken = intent.getStringExtra("nextToken");
        nextTokens = new ArrayList<>();
        if(!nextToken.equals("noToken")){
            nextTokens.add(nextToken);
        }else{
            nextButton.setEnabled(false);
        }

        currentPage = 0;
        prevButton.setEnabled(false);
        setupUI();
    }


    @Override
    protected void onStart() {
        super.onStart();

        recyclerView.setAdapter(new LocationsListAdapter(locationsList,onRecyclerviewItemClickListener));

    }

    private void setupUI() {

        recyclerView = (RecyclerView) findViewById(R.id.locationListsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LocationsListAdapter(locationsList,onRecyclerviewItemClickListener));

        if(locationsList.size() == 0){
            noResults.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            noResults.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void addListenForPrevButton(){

         prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentPage != 1){

                    String url ="http://travelandentertainm-env.us-west-1.elasticbeanstalk.com/?pagetoken="+  nextTokens.get(currentPage-2) ;

                    Log.i("response",url);

                    //Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue( LocationsShowActivity.this);

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override

                                public void onResponse(String response) {

                                    parseLocationsJsonObjectForList(response);

                                    actionsOnUiThreadForPreButton(locationsList);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    queue.add(stringRequest);
                }else{
                    currentPage --;
                    locationsList = new ArrayList<>(firstPageLocationsList);
                    recyclerView.setAdapter(new LocationsListAdapter(locationsList,onRecyclerviewItemClickListener));
                    nextButton.setEnabled(true);
                    prevButton.setEnabled(false);
                }
            }
        });
    }

    private void addListenForNextButton(){

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url ="http://travelandentertainm-env.us-west-1.elasticbeanstalk.com/?pagetoken="+ nextTokens.get(currentPage) ;

                Log.i("response",url);

                //Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue( LocationsShowActivity.this);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override

                            public void onResponse(String response) {

                                parseLocationsJsonObjectForList(response);

                                actionsOnUiThreadForNextButton(locationsList);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(stringRequest);
            }
        });
    }


    private void parseLocationsJsonObjectForList(String jsonData){
        locationsList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(jsonData);

            if(jsonObject.has("next_page_token")){
                nextToken =  jsonObject.getString("next_page_token");
            }else {
                nextToken  = "noToken";
            }

            JSONArray locationsArray = jsonObject.getJSONArray("results");

            Log.i("array", locationsArray.length()+"" );
            for(int i = 0; i < locationsArray.length(); i++){
                LocationOriginal locationOriginal = new LocationOriginal();

                locationOriginal.placeID = locationsArray.getJSONObject(i).getString("place_id");
                Log.i("list", locationOriginal.placeID);

                locationOriginal.placeAddress = locationsArray.getJSONObject(i).getString("vicinity");
                locationOriginal.placeName = locationsArray.getJSONObject(i).getString("name");
                locationOriginal.icon = locationsArray.getJSONObject(i).getString("icon");
                locationsList.add(locationOriginal);
                Log.i("size",locationsList.size()+"");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void actionsOnUiThreadForPreButton(final ArrayList<LocationOriginal> locationOriginals){
        LocationsShowActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("size", locationOriginals.size()+"");
                recyclerView.setAdapter(new LocationsListAdapter(locationOriginals,onRecyclerviewItemClickListener));

                nextButton.setEnabled(true);
                currentPage --;

            }
        });
    }

    private void actionsOnUiThreadForNextButton(final ArrayList<LocationOriginal> locationOriginals){

        LocationsShowActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("size", locationOriginals.size()+"");
                recyclerView.setAdapter(new LocationsListAdapter(locationOriginals,onRecyclerviewItemClickListener));

                currentPage ++;
                prevButton.setEnabled(true);

                if(!nextToken.equals("noToken")){
                    nextButton.setEnabled(true);
                    nextTokens.add(nextToken);
                }else{
                    nextButton.setEnabled(false);
                }

            }
        });
    }



    public String timeFormChange(Long seconds) {

        long millis = seconds * 1000;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
        return formattedDate;

    }


    public OnRecyclerviewItemClickListener onRecyclerviewItemClickListener = new OnRecyclerviewItemClickListener() {

        @Override
        public void onItemClickListener(View v,String viewName, int position) {

            clickPosition = position;

            String placeId = locationsList.get(position).placeID;

            String placeName = locationsList.get(position).placeName;

            if(viewName.equals("favBtn")){

                if(sp.contains(placeId)){
                    editor.remove(placeId);
                    editor.commit();
                    Toast toast=Toast.makeText(LocationsShowActivity.this,placeName +
                            " was removed from favorites",Toast.LENGTH_LONG);
                    showMyToast(toast, 800);

                }else{
                    StringBuilder sb = new StringBuilder();

                    sb.append(locationsList.get(position).placeName).append("?");
                    sb.append(locationsList.get(position).placeAddress).append("?");
                    sb.append(locationsList.get(position).icon);


                    editor.putString(placeId,sb.toString());
                    editor.commit();
                    Toast toast=Toast.makeText(LocationsShowActivity.this,placeName +
                            " was added to favorites",Toast.LENGTH_LONG);
                    showMyToast(toast, 800);
                }
                recyclerView.setAdapter(new LocationsListAdapter(locationsList,onRecyclerviewItemClickListener));

            }else{
                Log.i("test",placeId);

                final ProgressDialog progressDialog = new ProgressDialog(LocationsShowActivity.this);
                progressDialog.setMessage("Fetch results");
                progressDialog.show();

                String url ="http://hw9backend-env.us-west-1.elasticbeanstalk.com/?placeId=" + placeId;

                //Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(LocationsShowActivity.this);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override

                            public void onResponse(String response) {

                                Log.i("response",response);

                                progressDialog.dismiss();

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

        LocationsShowActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(LocationsShowActivity.this, LocationDetailActivity.class);
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

            JSONArray locationComponents = results.getJSONArray("address_components");

            for(int i = 0; i < locationComponents.length(); i++){

                JSONObject locationComponent =  locationComponents.getJSONObject(i);
                JSONArray locationComponentType = locationComponent.getJSONArray("types");
                if(locationComponentType.getString(0).equals("locality") ){
                    locationsDetail.placeCity = locationComponent.getString("short_name");
                }

                if(locationComponentType.getString(0).equals("administrative_area_level_1") ){
                    locationsDetail.placeState = locationComponent.getString("short_name");
                    System.out.println(locationsDetail.placeState);
                }
            }

            JSONObject geo =  new JSONObject(results.getString("geometry"));
            JSONObject location =  new JSONObject(geo.getString("location"));
            locationsDetail.Lat = location.getDouble("lat");
            locationsDetail.Lng = location.getDouble("lng");


            locationsDetail.placeId = results.getString("place_id");
            locationsDetail.placeName = results.getString("name");

            if(results.has("formatted_phone_number") ){
                locationsDetail.placeNumber = results.getString("formatted_phone_number");
            }else{
                locationsDetail.placeNumber = null;
            }


            if(results.has("formatted_address") ){
                locationsDetail.placeAddress = results.getString("formatted_address");
            }else{
                locationsDetail.placeAddress= null;
            }

            if(results.has("price_level") ){
                locationsDetail.placePriceLevel = "";
                for(int i = 0; i < results.getInt("price_level"); i++){
                    locationsDetail.placePriceLevel +="$";
                }
            }else{
                locationsDetail.placePriceLevel= null;
            }

            if(results.has("rating") ){
                locationsDetail.placeRating = results.getLong("rating");
            }else{
                locationsDetail.placeRating = 0;
            }

            if(results.has("url") ){
                locationsDetail.placeGooglePage = results.getString("url");
            }else{
                locationsDetail.placeGooglePage = null;
            }

            if(results.has("website") ){
                locationsDetail.placeWebsite = results.getString("website");
            }else{
                locationsDetail.placeWebsite = null;
            }

            locationsDetail.placeReviews = new ArrayList<>();
            if(results.has("reviews")){
                JSONArray reviews = results.getJSONArray("reviews");
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
            }

            locationsDetail.locationOriginal = locationsList.get(clickPosition);

        }catch(Exception e){
            e.printStackTrace();

            Log.i("message","bbbbb");
        }
    }

}


