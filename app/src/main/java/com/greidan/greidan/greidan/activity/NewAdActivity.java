package com.greidan.greidan.greidan.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.greidan.greidan.greidan.model.Ad;
import com.greidan.greidan.greidan.manager.AdManager;
import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.UserManager;

import java.util.Date;

public class NewAdActivity extends ProgressActivity {

    AdManager adManager;
    UserManager userManager;

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

        adManager = new AdManager(this);
        userManager = new UserManager(this);

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

                attemptPostAd(title, content, category);
            }
        });

        mButtonCancel = (Button) findViewById(R.id.new_ad_button_cancel);

        mContainerView = findViewById(R.id.new_ad_container);
        mProgressView = findViewById(R.id.new_ad_progress);
    }

    private void attemptPostAd(String title, String content, String category) {
        // TODO: verify ad content before posting
        // TODO: proper parameters in ad constructor

        showProgress(true);
        newAd = new Ad("", title, content, category, userManager.getLoggedInUsername(), new Date(), new Location("foo"));
        adManager.postAdToServer(newAd);
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
}
