package com.greidan.greidan.greidan;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class NewAdActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
