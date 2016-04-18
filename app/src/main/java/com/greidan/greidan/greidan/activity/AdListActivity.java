package com.greidan.greidan.greidan.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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

public class AdListActivity extends LocationActivity {

    private static final String TAG = "AdListActivity";

    private static final int MIN_ACCURACY = 100;
    private static final int UPDATE_TIMEOUT = 10000;
    private static final int MIN_FRESHNESS = 60*60*1000;
    private static final int UPDATE_INTERVAL = 3000;
    private static final int MIN_UPDATE_INTERVAL = 1000;
    private static final int DEFAULT_SEEK = 30;

    AdManager mAdManager;
    ListView mListView;
    SeekBar mRadiusSeek;
    TextView mRangeLabel;

    HashMap<String, Ad> ads;

    String mCategory;
    double mRadius;
    long mLastRefreshTime;

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

        mContainerView = findViewById(R.id.ad_list_container);
        mProgressView = findViewById(R.id.ad_list_progress);

        mAdManager = new AdManager(this);

        mRadiusSeek = (SeekBar) findViewById(R.id.ad_list_radius_seek);
        mRadiusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "New seekbar value " + progress);
                mRadius = 0.1 * Math.exp(progress/10.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showProgress(true);
                refresh();
            }
        });

        mRangeLabel = (TextView) findViewById(R.id.ad_list_range_label);

        mListView = (ListView) findViewById(R.id.ad_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AdListActivity.this, AdViewActivity.class);
                Bundle bundle = new Bundle();
                String adID = (String) view.getTag();
                bundle.putParcelable("ad", ads.get(adID));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mCategory = getIntent().getStringExtra("category");

        mRadiusSeek.setProgress(DEFAULT_SEEK);

        showProgress(true);
    }

    private void refresh() {

        showProgress(true);

        Log.i(TAG, "Fresh fresh");

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
                Log.i(TAG, "Location update timed out");
                Log.i(TAG, "Refreshing with a location accuracy: " + accuracy + " and freshness: " + freshness);
                mAdManager.fetchAds(mCategory, mCurrentLocation, mRadius);
            } else {
                startLocationUpdates();
                Toast.makeText(this, "Aquiring location", Toast.LENGTH_SHORT).show();
            }
        } else {
            stopLocationUpdates();
            Log.i(TAG, "Refreshing with a location accuracy: " + accuracy + " and freshness: " + freshness);
            mAdManager.fetchAds(mCategory, mCurrentLocation, mRadius);
        }
    }

    @Override
    public void doUponCompletion(Bundle data) {
        List<Ad> ads = data.getParcelableArrayList("ads");
        mLastRefreshTime = System.currentTimeMillis();
        populateAdList(ads);

        if(ads.size() > 0) {
            mRangeLabel.setText(getString(R.string.radius_label, String.format("%.2f", mRadius)));
        } else {
            mRangeLabel.setText(getString(R.string.ad_list_nothing_found, String.format("%.2f", mRadius)));
        }

        if(data.getBoolean("error")) {
            Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
        }

        showProgress(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        refresh();
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

        // Prevent unnecessary server requests if e.g. returning from an AdViewActivity
        if(System.currentTimeMillis() - mLastRefreshTime > 1000*60*60*5) {
            refresh();
        }
    }

    private void populateAdList(List<Ad> ads) {
        this.ads = new HashMap<>();
        for(Ad ad: ads) {
            this.ads.put(ad.getId(), ad);
        }

        mListView.setAdapter(new AdListAdapter(this, ads));
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
