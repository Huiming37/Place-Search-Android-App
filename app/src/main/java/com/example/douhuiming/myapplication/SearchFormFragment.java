package com.example.douhuiming.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by douhuiming on 4/16/18.
 */

public class SearchFormFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener{

    private HashMap<String,String> typesMap ;
    private ArrayList<LocationOriginal> locationsList ;
    private String nextToken;


    private EditText keyWordEditText;
    private EditText distanceText ;
    private AutoCompleteTextView otherText ;
    private Spinner typeSpinner;
    private TextView checkKeyword ;
    private TextView  checkOtherInput ;
    private RadioButton otherPlace ;
    private RadioButton curPlace ;
    private ProgressDialog progressDialog ;


    private LocationRequest mLocationRequest ;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location currentLocation;


    private Double curLat ;
    private Double curLng ;


    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

    private GoogleApiClient mGoogleApiClient;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_form_fragment, container, false);



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTypeMap();

        getLocationPermission();

        keyWordEditText = getActivity().findViewById(R.id.input_keyword);
        distanceText =  getActivity().findViewById(R.id.input_distance);
        otherText = getActivity().findViewById(R.id.input_other_place);
        typeSpinner = getActivity().findViewById(R.id.choosed_type);
        checkKeyword =  getActivity().findViewById(R.id.checkKeywod);
        checkOtherInput =  getActivity().findViewById(R.id.checkOtherPlace);
        otherPlace = getActivity().findViewById(R.id.otherBtn);
        curPlace = getActivity().findViewById(R.id.curBtn);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();


        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, LAT_LNG_BOUNDS,null);

        otherText.setAdapter( placeAutocompleteAdapter);


        otherText.setEnabled(false);

        RadioGroup radioGroup = (RadioGroup) getActivity().findViewById(R.id.searchBtnGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.otherBtn){
                    otherText.setEnabled(true);
                }else{
                    otherText.setEnabled(false);
                    checkOtherInput.setVisibility(View.GONE);
                }
            }
        });

        locationsList = new ArrayList<>();

        Button searchButton = getActivity().findViewById(R.id.search_button);
        Button clearButton = getActivity().findViewById(R.id.clear_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean keywordFlag = true;
                boolean inputFlat = true;

                String keyword = keyWordEditText.getText().toString();
                if(keyword.trim().length() == 0){
                    checkKeyword.setVisibility(View.VISIBLE);
                    keywordFlag = false;
                }

                String distance = distanceText.getText().toString();

                if(distance.trim().length() == 0){
                    distance = "10";
                }

                String type = typesMap.get((String) typeSpinner.getSelectedItem());

                String otherPlaceInput = "";

                if(otherPlace.isChecked()){

                    otherPlaceInput = otherText.getText().toString();

                    if(otherPlaceInput.trim().length() == 0){
                        checkOtherInput.setVisibility(View.VISIBLE);
                        inputFlat = false;
                    }
                }

                if(keywordFlag && inputFlat){

                    String url;

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Fetch results");
                    progressDialog.show();

                    if(otherPlace.isChecked()){
                        url ="http://travelandentertainm-env.us-west-1.elasticbeanstalk.com/?distance=" +
                                distance+"&keyword="+ URLEncoder.encode(keyword)+"&location="+URLEncoder.encode(otherPlaceInput)+"&type="+type;

                    }else{
                        url ="http://travelandentertainm-env.us-west-1.elasticbeanstalk.com/?distance=" +
                                distance+"&keyword="+ URLEncoder.encode(keyword)+"&lat="+curLat+"&lng=" +curLng+"&type="+type;
                    }

                    Log.i("response",url);

                    //Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getActivity());

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override

                                public void onResponse(String response) {

                                    Log.i("resList",response);

                                    progressDialog.dismiss();

                                    parseLocationsJsonObject(response);

                                    actionsOnUiThread(locationsList);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    queue.add(stringRequest);
                }else{
                    Toast.makeText(getContext(),"please fix all fields with errors", Toast.LENGTH_SHORT).show();
                }

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                curPlace.setChecked(true);
                otherPlace.setChecked(false);
                otherText.setEnabled(false);
                checkOtherInput.setVisibility(View.GONE);
                checkKeyword.setVisibility(View.GONE);
                keyWordEditText.setText("");
                distanceText.setText("");
                otherText.setText("");

            }
        });
    }

    private void actionsOnUiThread(final ArrayList<LocationOriginal> locationsList){

         getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), LocationsShowActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("list",(Serializable)locationsList);//序列化,要注意转化(Serializable)
                bundle.putString("nextToken",nextToken);
                intent.putExtras(bundle);//发送数据
                startActivity(intent);//启动intent
            }
        });

    }

    private void parseLocationsJsonObject(String jsonData){
        locationsList= new ArrayList<>();;
        try{
            JSONObject jsonObject = new JSONObject(jsonData);

            if(jsonObject.has("next_page_token")){
                nextToken =  jsonObject.getString("next_page_token");
            }else {
                nextToken  = "noToken";
            }

            JSONArray locationsArray = jsonObject.getJSONArray("results");

            for(int i = 0; i < locationsArray.length(); i++){
                LocationOriginal locationOriginal = new LocationOriginal();

                locationOriginal.placeID = locationsArray.getJSONObject(i).getString("place_id");
                locationOriginal.placeAddress = locationsArray.getJSONObject(i).getString("vicinity");
                locationOriginal.placeName = locationsArray.getJSONObject(i).getString("name");
                locationOriginal.icon = locationsArray.getJSONObject(i).getString("icon");
                locationsList.add(locationOriginal);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initTypeMap(){
        typesMap = new HashMap<>();
        typesMap.put("Default","default");
        typesMap.put("Airport","airport");
        typesMap.put("Amusement","amusement");
        typesMap.put("Aquarium","aquarium");
        typesMap.put("Art Gallery","art_gallery");
        typesMap.put("Bakery","bakery");
        typesMap.put("Bar","bar");
        typesMap.put("Beauty Salon","beauty_salon");
        typesMap.put("Bowling Alley","bowling_alley");
        typesMap.put("Bus Station","bus_station");
        typesMap.put("Cafe","cafe");
        typesMap.put("Campground","campground");
        typesMap.put("Car Rental","car_rental");
        typesMap.put("Casino","casino");
        typesMap.put("Lodging","lodging");
        typesMap.put("Movie Theater","movie_theater");
        typesMap.put("Museum","museum");
        typesMap.put("Night Club","night_club");
        typesMap.put("Park","park");
        typesMap.put("Parking","parking");
        typesMap.put("Restaurant","restaurant");
        typesMap.put("Shopping Mall","shopping_mall");
        typesMap.put("Stadium","stadium");
        typesMap.put("Subway Station","subway_station");
        typesMap.put("Taxi Stand","taxi_stand");
        typesMap.put("Train station","train_station");
        typesMap.put("Transit Station","transit_station");
        typesMap.put("Travel Agency","travel_agency");
        typesMap.put("Zoo","zoo");
    }


    private LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            currentLocation=locationResult.getLastLocation();
            curLat = currentLocation.getLatitude();
            curLng = currentLocation.getLongitude();


        }
    };


    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current locationOriginal");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());

    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting locationOriginal permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                getDeviceLocation();
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.i("text","show");
//        mGoogleApiClient.stopAutoManage(getActivity());
//        mGoogleApiClient.disconnect();
//    }



}
