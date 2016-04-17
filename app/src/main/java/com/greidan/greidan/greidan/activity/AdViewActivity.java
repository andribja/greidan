package com.greidan.greidan.greidan.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.greidan.greidan.greidan.model.Ad;
import com.greidan.greidan.greidan.manager.AdManager;
import com.greidan.greidan.greidan.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AdViewActivity extends ActionBarActivity {

    AdManager adManager;

    TextView mTitle;
    TextView mAuthor;
    TextView mTimePosted;
    TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_view);

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


        DateFormat df = new SimpleDateFormat("dd/mm/yyyy");


        mTitle.setText(ad.getTitle());
        mAuthor.setText(String.format(getString(R.string.author), ad.getAuthorName()));
        mTimePosted.setText(String.format(
                getString(R.string.time_posted_with_category),
                df.format(ad.getTimePosted()),
                ad.getCategory()));
        mContent.setText(ad.getContent());
    }
}
