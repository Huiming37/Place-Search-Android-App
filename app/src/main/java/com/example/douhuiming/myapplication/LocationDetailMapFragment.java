package com.example.douhuiming.myapplication;

import android.graphics.Color;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationDetailMapFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {

    private LocationDetail locationsDetail;

    private Double lat;

    private Double lng;

    SupportMapFragment mapFragment;

    private AutoCompleteTextView locationInput;

    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

    private GoogleApiClient mGoogleApiClient;

    private GoogleMap googleMap;

    private LatLng destination;

    private LatLng startLocation;

    private int travelMode;

    private String[] travelModes;

    private String serverKey = "AIzaSyDyL7Lqir9RtxOtltgp72-tNdSP4m11NrU";

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    public LocationDetailMapFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        System.out.println("map" + ":onCreatView()");

        locationInput = view.findViewById(R.id.startLocation);

        travelMode = 0;

        travelModes = new String[]{TransportMode.DRIVING,TransportMode.BICYCLING,TransportMode.TRANSIT,TransportMode.WALKING};

        mGoogleApiClient = ((LocationDetailActivity)getActivity()).getmGoogleApiClient();

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, LAT_LNG_BOUNDS,null);

        locationInput.setAdapter(placeAutocompleteAdapter);

        locationInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("click","1");
                geoLocate();
            }
        });


        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);

        if(mapFragment == null){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map,mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return view;
    }

    private void geoLocate(){

        String searchString = locationInput.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());

        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.i("log", "geoLocate: IOException: " + e.getMessage() );
        }


        if(list.size() > 0){
            Address address = list.get(0);

            Log.i("log", "geoLocate: found a location: " + address.getLatitude() +address.getLongitude() );

            startLocation = new LatLng(address.getLatitude(), address.getLongitude());

            drawDirection();

        }
    }



    private void drawDirection(){

        GoogleDirection.withServerKey(serverKey)
            .from(startLocation)
            .to(destination)
            .transportMode(travelModes[travelMode])
            .execute(new DirectionCallback() {

                @Override
                public void onDirectionSuccess(Direction direction, String rawBody) {
                    if(direction.isOK()) {

                        googleMap.clear();

                        Route route = direction.getRouteList().get(0);
                        googleMap.addMarker(new MarkerOptions().position(startLocation));
                        googleMap.addMarker(new MarkerOptions().position(destination));

                        if(travelMode == 2){
                            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getActivity(), stepList, 5, Color.BLUE, 5, Color.BLUE);
                            for (PolylineOptions polylineOption : polylineOptionList) {
                                googleMap.addPolyline(polylineOption);
                            }
                        }else{
                            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.BLUE);
                            googleMap.addPolyline(polylineOptions);
                        }

                        setCameraWithCoordinationBounds(route);
                    }
                }

                @Override
                public void onDirectionFailure(Throwable t) {
                    // Do something here
                }
            });
    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        locationsDetail = ((LocationDetailActivity)getActivity()).getLocationDetail();

        lat = locationsDetail.Lat;

        Log.i("geo",String.valueOf(lat));

        lng = locationsDetail.Lng;

        destination = new LatLng(lat, lng);

        Spinner travelModeSpinner = getActivity().findViewById(R.id.travleMethod);

        travelModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                travelMode = position;

                if(startLocation != null){
                    drawDirection();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng latlng= new LatLng(lat, lng);

        googleMap.addMarker(new MarkerOptions().position(latlng));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination,16));

    }



    private void setCameraWithCoordinationBounds(Route route) {

        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 16));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}



