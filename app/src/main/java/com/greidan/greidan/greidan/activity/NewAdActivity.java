package com.greidan.greidan.greidan.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.greidan.greidan.greidan.util.RealPathUtil;
import com.greidan.greidan.greidan.model.Ad;
import com.greidan.greidan.greidan.manager.AdManager;
import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.UserManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewAdActivity extends LocationActivity {

    private static final String TAG = "NewAdActivity";
    private static final int MIN_ACCURACY = 20;
    private static final int MIN_FRESHNESS = 60*60*1000;
    private static final int UPDATE_INTERVAL = 3000;
    private static final int MIN_UPDATE_INTERVAL = 1000;
    private static final int SELECT_PICTURE = 1;

    AdManager mAdManager;
    UserManager mUserManager;

    EditText mTitle;
    EditText mContent;
    Spinner mCategory;
    Button mButtonPost;
    Button mButtonCancel;
    ImageView mImagePicker;

    LinearLayout mImagesLayout;

    Ad newAd;
    String imagePath;
    String pendingImagePath;
    List<String> imagePaths;

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
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mContainerView = findViewById(R.id.new_ad_container);
        mProgressView = findViewById(R.id.new_ad_progress);

        mImagePicker = (ImageView) findViewById(R.id.new_ad_image);
        mImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        imagePaths = new ArrayList<>();

        mImagesLayout = (LinearLayout) findViewById(R.id.new_ad_image_layout);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
//            imagePath = RealPathUtil.getRealPathFromURI_API19(this, selectedImageUri);
            pendingImagePath = RealPathUtil.getRealPathFromURI_API19(this, selectedImageUri);

            Log.i(TAG, "Image path: " + pendingImagePath);

            mImagePicker.setImageBitmap(BitmapFactory.decodeFile(pendingImagePath));
//            addImage(imagePath);
        }
    }

    // For adding multiple pictures
    private void addImage(String path) {
        imagePaths.add(path);

        ImageView imageView = new ImageView(this);
        imageView.setPadding(2, 2, 2, 2);
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        mImagesLayout.addView(imageView);

    }

    private void attemptPostAd(String title, String content, String category, Location location) {
        // TODO: verify ad content before posting

        showProgress(true);
        newAd = new Ad("", title, content, category, mUserManager.getLoggedInUsername(), new Date(), location);

        if(pendingImagePath != null) {
            mAdManager.uploadImage(new File(pendingImagePath));
        } else {
            mAdManager.postAdToServer(newAd);
        }
    }

    @Override
    public void doUponCompletion(Bundle response) {
        Log.i(TAG, "doUponCompletion, imagePath=" + imagePath);
        boolean success = response.getBoolean("success");
        String message = response.getString("message");

        if(pendingImagePath != null) {
            // Image upload pending
            if(message.equals("Upload successful")) {
                // Image upload successful
                newAd.setExtFilename(response.getString("extFilename"));
                imagePath = pendingImagePath;
                pendingImagePath = null;
                mAdManager.postAdToServer(newAd);
            } else {
                // Image upload unsuccessful
                // TODO: handle this
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        } else {
            // No image upload pending, upload ad
            String id = response.getString("id");

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            if(response.getBoolean("error")) {
                showProgress(false);
            }

            if(success) {
                newAd.setId(id);

                // TODO: save image to local storage

                Intent intent = new Intent(this, AdViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("ad", newAd);
                intent.putExtras(bundle);

                startActivity(intent);
                finish();
            }
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
