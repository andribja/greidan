package com.greidan.greidan.greidan.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.greidan.greidan.greidan.model.Ad;
import com.greidan.greidan.greidan.manager.AdManager;
import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.UserManager;

import java.util.Date;

public class NewAdActivity extends LocationActivity {

    private static final int MIN_ACCURACY = 20;
    private static final int MIN_FRESHNESS = 60*60*1000;
    private static final int UPDATE_INTERVAL = 3000;
    private static final int MIN_UPDATE_INTERVAL = 1000;

    AdManager mAdManager;
    UserManager mUserManager;

    EditText mTitle;
    EditText mContent;
    Spinner mCategory;
    Button mButtonPost;
    Button mButtonCancel;

    Ad newAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(MIN_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mAdManager = new AdManager(this);
        mUserManager = new UserManager(this);

        mTitle = (EditText) findViewById(R.id.new_ad_title);
        mContent = (EditText) findViewById(R.id.new_ad_content);
        mCategory = (Spinner) findViewById(R.id.new_ad_category);

        mButtonPost = (Button) findViewById(R.id.new_ad_button_post);
        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString();
                String content = mContent.getText().toString();
                String category = mCategory.getSelectedItem().toString();

                attemptPostAd(title, content, category, mCurrentLocation);
            }
        });

        mButtonCancel = (Button) findViewById(R.id.new_ad_button_cancel);

        mContainerView = findViewById(R.id.new_ad_container);
        mProgressView = findViewById(R.id.new_ad_progress);
    }

    private void attemptPostAd(String title, String content, String category, Location location) {
        // TODO: verify ad content before posting

        showProgress(true);
        newAd = new Ad("", title, content, category, mUserManager.getLoggedInUsername(), new Date(), location);
        mAdManager.postAdToServer(newAd);
    }

    @Override
    public void doUponCompletion(Bundle data) {
        boolean success = data.getBoolean("success");
        String message = data.getString("message");
        String id = data.getString("id");

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        if(success) {
            newAd.setId(id);

            Intent intent = new Intent(this, AdViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("ad", newAd);
            intent.putExtras(bundle);

            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        float accuracy = mCurrentLocation.getAccuracy();
        long freshness = System.currentTimeMillis() - mCurrentLocation.getTime();

        if(accuracy < MIN_ACCURACY && freshness < MIN_FRESHNESS) {
            Log.i("NewAdActivity", "Got a nice location: " + mCurrentLocation.toString());
            stopLocationUpdates();
        } else {
            Log.i("NewAdActivity", "Looking for a better location than " + mCurrentLocation.toString());
            startLocationUpdates();
        }
    }
}
