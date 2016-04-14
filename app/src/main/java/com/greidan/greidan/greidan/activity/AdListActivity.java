package com.greidan.greidan.greidan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.greidan.greidan.greidan.model.Ad;
import com.greidan.greidan.greidan.manager.AdManager;
import com.greidan.greidan.greidan.R;

import java.util.HashMap;
import java.util.List;

public class AdListActivity extends LocationActivity implements AdapterView.OnItemClickListener{

    private static final int MIN_ACCURACY = 20;
    private static final int UPDATE_TIMEOUT = 10000;
    private static final int MIN_FRESHNESS = 60*60*1000;
    private static final int UPDATE_INTERVAL = 3000;
    private static final int MIN_UPDATE_INTERVAL = 1000;

    AdManager mAdManager;
    ListView mListView;
    SeekBar mRadiusSeek;

    HashMap<String, Ad> ads;

    String mCategory;
    double mRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up Google Services API object for location services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(MIN_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mContainerView = findViewById(R.id.ad_list);
        mProgressView = findViewById(R.id.ad_list_progress);

        mAdManager = new AdManager(this);

        mListView = (ListView) findViewById(R.id.ad_list);
        mListView.setOnItemClickListener(this);

        mRadiusSeek = (SeekBar) findViewById(R.id.ad_list_radius_seek);
        mRadiusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRadius = Math.exp(progress/1.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                refresh();
            }
        });

        mCategory = getIntent().getStringExtra("category");
        mRadius = 100;
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Intent intent = new Intent(this, AdViewActivity.class);
        Bundle bundle = new Bundle();
        String adID = (String) v.getTag();
        bundle.putParcelable("ad", ads.get(adID));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void refresh() {
        showProgress(true);

        // Get the last known location
        updateLocation();

        float accuracy = Float.MAX_VALUE;
        long freshness = Long.MAX_VALUE;

        if(mCurrentLocation != null) {
            accuracy = mCurrentLocation.getAccuracy();
            freshness = System.currentTimeMillis() - mCurrentLocation.getTime();
        } else {
            Toast.makeText(this, "No location", Toast.LENGTH_SHORT).show();
        }

        // Try to get as good a location as we can
        if(accuracy > MIN_ACCURACY || freshness > MIN_FRESHNESS) {
            if(locationUpdatesEnabled && System.currentTimeMillis() - currentUpdateStart > UPDATE_TIMEOUT) {
                stopLocationUpdates();
                Log.i("AdListActivity", "Location update timed out");
                Log.i("AdListActivity", "Refreshing with a location accuracy: " + accuracy + " and freshness: " + freshness);
                mAdManager.fetchAds(mCategory, mCurrentLocation, mRadius);
            } else {
                startLocationUpdates();
            }
        } else {
            stopLocationUpdates();
            Log.i("AdListActivity", "Refreshing with a location accuracy: " + accuracy + " and freshness: " + freshness);
            mAdManager.fetchAds(mCategory, mCurrentLocation, mRadius);
        }
    }

    @Override
    public void doUponCompletion(Bundle data) {
        List<Ad> ads = data.getParcelableArrayList("ads");
        populateAdList(ads);

        showProgress(false);
    }

    @Override
    protected void handleLocationUpdate() {
        refresh();
    }

    private void populateAdList(List<Ad> ads) {
        this.ads = new HashMap<>();
        for(Ad ad: ads) {
            this.ads.put(ad.getId(), ad);
        }

        AdListAdapter arrayAdapter = new AdListAdapter(this, ads);
        mListView.setAdapter(arrayAdapter);
    }

    private class AdListAdapter extends ArrayAdapter<Ad> {

        public AdListAdapter(Context context, List<Ad> ads) {
            super(context, 0, ads);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Ad ad = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_basic, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.ad_list_item_title);
            title.setText(ad.getTitle());
            convertView.setTag(ad.getId());

            return convertView;
        }
    }
}
