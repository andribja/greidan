package com.greidan.greidan.greidan;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AdViewActivity extends ActionBarActivity {

    AdManager adManager;

    TextView mTitle;
    TextView mAuthor;
    TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_view);

        adManager = new AdManager(this);

        mTitle = (TextView) findViewById(R.id.ad_view_title);
        mAuthor = (TextView) findViewById(R.id.ad_view_author);
        mContent = (TextView) findViewById(R.id.ad_view_content);

        Intent intent = getIntent();
        if(intent.hasExtra("id")) {
            int id = (int) getIntent().getIntExtra("id", -1);
            adManager.fetchAd(id);
        } else {
            Bundle bundle = getIntent().getExtras();
            Ad ad = (Ad) bundle.getSerializable("ad");

            mTitle.setText(ad.getTitle());
            mAuthor.setText(ad.getAuthor().getUsername());
            mContent.setText(ad.getContent());
        }
    }

    public void populateAdView(Ad ad) {
        mTitle.setText(ad.getTitle());
        mAuthor.setText(ad.getAuthor().getUsername());
        mContent.setText(ad.getContent());
    }
}
