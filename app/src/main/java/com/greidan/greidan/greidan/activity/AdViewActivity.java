package com.greidan.greidan.greidan.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greidan.greidan.greidan.model.Ad;
import com.greidan.greidan.greidan.manager.AdManager;
import com.greidan.greidan.greidan.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AdViewActivity extends ProgressActivity {

    private static final String TAG = "AdViewActivity";

    AdManager adManager;

    TextView mTitle;
    TextView mAuthor;
    TextView mTimePosted;
    TextView mContent;

    LinearLayout mImagesLayout;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_view);

        adManager = new AdManager(this);

        Bundle bundle = getIntent().getExtras();
        final Ad ad = (Ad) bundle.getParcelable("ad");

        mTitle = (TextView) findViewById(R.id.ad_view_title);
        mAuthor = (TextView) findViewById(R.id.ad_view_author);
        mAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdViewActivity.this, UserProfileActivity.class);
                intent.putExtra("username", ad.getAuthorName());
                startActivity(intent);
            }
        });
        mTimePosted = (TextView) findViewById(R.id.ad_view_time_posted);
        mContent = (TextView) findViewById(R.id.ad_view_content);


        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");


        mTitle.setText(ad.getTitle());
        mAuthor.setText(String.format(getString(R.string.author), ad.getAuthorName()));
        mTimePosted.setText(String.format(
                getString(R.string.time_posted_with_category),
                df.format(ad.getTimePosted()),
                ad.getCategory()));
        mContent.setText(ad.getContent());

        mImagesLayout = (LinearLayout) findViewById(R.id.ad_view_image_layout);

        if(ad.getExtFilename() != null) {
            adManager.fetchImageBytes(ad.getExtFilename());
        }
    }

    @Override
    public void doUponCompletion(Bundle response) {
        Log.i(TAG, "doUponCompletion");

        if(response.containsKey("bytes")) {
            byte[] bytes = response.getByteArray("bytes");

            ImageView imageView = new ImageView(this);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            imageView.setAdjustViewBounds(true);
            imageView.setLayoutParams(new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));

            mImagesLayout.addView(imageView);

            // TODO: cache images on device?
        }
    }
}
