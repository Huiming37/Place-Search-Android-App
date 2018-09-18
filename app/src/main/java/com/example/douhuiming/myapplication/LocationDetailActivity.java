package com.example.douhuiming.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationDetailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private LocationDetail locationsDetail;

    private List<Fragment> list;
    private ViewPager myViewPager;
    private TabFragmentPagerAdapter adapter;

    private SharedPreferences sp;

    private MenuItem favItem;

    private SharedPreferences.Editor editor;

    private Context context;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail);

        getSupportActionBar().setElevation(0);

        context = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("favourites", Context.MODE_PRIVATE);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        editor = sp.edit();

        Intent intent = this.getIntent();

        locationsDetail = (LocationDetail) intent.getSerializableExtra("locationDetail");
        getSupportActionBar().setTitle(locationsDetail.placeName);

        list = new ArrayList<>();
        list.add(new LocationDetailInfoFragment());
        list.add(new LocationDetailPhotosFragment());
        list.add(new LocationDetailMapFragment());
        list.add(new LocationDetailReviewFragment());

        myViewPager = (ViewPager) findViewById(R.id.detail_view_pager);
        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), list);
        myViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.detail_view_pager_tab);
        tabLayout.setupWithViewPager(myViewPager);

        tabLayout.getTabAt(0).setCustomView(getTabView("INFO",R.drawable.info)); ;
        tabLayout.getTabAt(1).setCustomView(getTabView("PHOTOS",R.drawable.photos));
        tabLayout.getTabAt(2).setCustomView(getTabView("MAP",R.drawable.map)); ;
        tabLayout.getTabAt(3).setCustomView(getTabView("REVIEWS",R.drawable.reviews));

        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_acitivity_menu,menu);

        favItem = menu.findItem(R.id.ic_fav);

        if(sp.contains(locationsDetail.placeId)){
            favItem.setIcon(R.drawable.heart_fill_white);
        }else{
            favItem.setIcon(R.drawable.heart_outline_black);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else if(item.getItemId() == R.id.ic_twitter){

            String str = "http://twitter.com/intent/tweet?text=Check out " + locationsDetail.placeName + " located at "
                    + locationsDetail.placeAddress + ". Website: " + locationsDetail.placeWebsite + " #TravelAndEntertainmentSearch";

            Uri uri = Uri.parse(str);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);

        }else if(item.getItemId() == R.id.ic_fav){

            if(sp.contains(locationsDetail.placeId)){
                favItem.setIcon(R.drawable.heart_outline_black);
                editor.remove(locationsDetail.placeId);
                editor.commit();

                Toast toast=Toast.makeText(this,locationsDetail.placeName +
                        " was removed from favorites",Toast.LENGTH_LONG);
                showMyToast(toast, 800);

            }else{
                favItem.setIcon(R.drawable.heart_fill_white);
                StringBuilder sb = new StringBuilder();

                sb.append(locationsDetail.locationOriginal.placeName).append("?");
                sb.append(locationsDetail.locationOriginal.placeAddress).append("?");
                sb.append(locationsDetail.locationOriginal.icon);

                editor.putString(locationsDetail.placeId,sb.toString());

                editor.commit();
                Toast toast=Toast.makeText(this,locationsDetail.placeName +
                        " was added to favorites",Toast.LENGTH_LONG);
                showMyToast(toast, 800);
            }
        }
        return super.onOptionsItemSelected(item);
    }

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


    public View getTabView(String text, int tabIcon) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab_detail, null);
        TextView txt_title = view.findViewById(R.id.txt_title);
        txt_title.setText(text);
        ImageView img_title = view.findViewById(R.id.img_title);
        img_title.setImageResource(tabIcon);
        return view;
    }


    public LocationDetail getLocationDetail(){
        return locationsDetail;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
