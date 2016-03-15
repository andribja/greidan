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

        mTitle = (TextView) findViewById(R.id.ad_view_title);
        mAuthor = (TextView) findViewById(R.id.ad_view_author);
        mContent = (TextView) findViewById(R.id.ad_view_content);

        Bundle bundle = getIntent().getExtras();
        Ad ad = (Ad) bundle.getParcelable("ad");

        mTitle.setText(ad.getTitle());
        mAuthor.setText(ad.getAuthor().getUsername());
        mContent.setText(ad.getContent());
    }
}
