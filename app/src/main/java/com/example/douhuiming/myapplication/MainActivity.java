package com.example.douhuiming.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final int REQ_CODE_SHOW_ACTIVITY = 100;


    private List<Fragment> list;
    private ViewPager myViewPager;
    private TabFragmentPagerAdapter adapter;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();
        list.add(new SearchFormFragment());
        list.add(new FavoritesFragment());

        getSupportActionBar().setElevation(0);

        myViewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), list);
        myViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.view_pager_tab);
        tabLayout.setupWithViewPager(myViewPager);

        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));


        tabLayout.getTabAt(0).setCustomView(getTabView("SEARCH",R.drawable.search)); ;
        tabLayout.getTabAt(1).setCustomView(getTabView("FAVORITES",R.drawable.heart_fill_white));



    }

    public View getTabView(String text, int tabIcon) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView txt_title = view.findViewById(R.id.txt_title);
        txt_title.setText(text);
        ImageView img_title = view.findViewById(R.id.img_title);
        img_title.setImageResource(tabIcon);
        return view;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

}
