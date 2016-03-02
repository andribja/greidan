package com.greidan.greidan.greidan;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewAdActivity extends ActionBarActivity {

    AdManager adManager;

    EditText mTitle;
    EditText mContent;
    Spinner mCategory;

    Button mButtonPost;
    Button mButtonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adManager = new AdManager(this);

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

                adManager.postAdToServer(new Ad(0, title, content, category, new User(0, "foobar", "raboof"), null, new Location("foo")));
            }
        });

        mButtonCancel = (Button) findViewById(R.id.new_ad_button_cancel);
    }

    public void doAfterPost(boolean success, String message, int id) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AdViewActivity.class);
        intent.putExtra("id", id);

    }

}
