package com.example.douhuiming.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationDetail implements Serializable {

    public String placeId;

    public String placeName;

    public String placeNumber;

    public String placeAddress;

    public ArrayList<Review> placeReviews;

    public String placePriceLevel;

    public float placeRating;

    public String placeGooglePage;

    public String placeWebsite;

    public Double Lat;

    public Double Lng;

    public String placeState;

    public String placeCity;

    public LocationOriginal locationOriginal;


}
