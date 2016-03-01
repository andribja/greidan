package com.greidan.greidan.greidan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AdListActivity extends ActionBarActivity {

    AdManager adManager;
    ListView mListView;

    Button[] mButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long id = (long) getIntent().getLongExtra("id", -1);
        Log.i("AdListActivity", "Got this from intent: " + id);

        adManager = new AdManager(this);

        mListView = (ListView) findViewById(R.id.ad_list);
    }

    public void populateAdList(List<String> titles) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.ad_list_item,
                titles
        );

        mListView.setAdapter(arrayAdapter);
    }

}
