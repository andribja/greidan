package com.greidan.greidan.greidan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.greidan.greidan.greidan.model.Ad;
import com.greidan.greidan.greidan.manager.AdManager;
import com.greidan.greidan.greidan.R;

import java.util.HashMap;
import java.util.List;

public class AdListActivity extends ProgressActivity implements AdapterView.OnItemClickListener {

    AdManager adManager;
    ListView mListView;

    HashMap<String, Ad> ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String category = getIntent().getStringExtra("category");

        adManager = new AdManager(this);
        adManager.fetchAds(category);

        mListView = (ListView) findViewById(R.id.ad_list);
        mListView.setOnItemClickListener(this);

        mContainerView = findViewById(R.id.ad_list);
        mProgressView = findViewById(R.id.ad_list_progress);
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("AdListActivityListView", "You clicked Item: " + id + " at position:" + position + ", tag: " + v.getTag());

        Intent intent = new Intent(this, AdViewActivity.class);
        Bundle bundle = new Bundle();
        String adID = (String) v.getTag();
        bundle.putParcelable("ad", ads.get(adID));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void populateAdList(List<Ad> ads) {
        this.ads = new HashMap<>();
        for(Ad ad: ads) {
            this.ads.put(ad.getId(), ad);
        }

        AdListAdapter arrayAdapter = new AdListAdapter(this, ads);
        mListView.setAdapter(arrayAdapter);
    }

    @Override
    public void doUponCompletion(Bundle data) {
        List<Ad> ads = data.getParcelableArrayList("ads");

        populateAdList(ads);

        showProgress(false);
    }

    private class AdListAdapter extends ArrayAdapter<Ad> {

        public AdListAdapter(Context context, List<Ad> ads) {
            super(context, 0, ads);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Ad ad = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_basic, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.ad_list_item_title);
            title.setText(ad.getTitle());
            convertView.setTag(ad.getId());

            return convertView;
        }

    }

}