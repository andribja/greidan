package com.greidan.greidan.greidan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    AdManager adManager;
    ListView mListView;

    //List<Ad> ads;
    HashMap<Integer, Ad> ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long id = (long) getIntent().getLongExtra("id", -1);
        Log.i("AdListActivity", "Got this from intent: " + id);
        adManager = new AdManager(this);
        adManager.fetchAds(0);

        mListView = (ListView) findViewById(R.id.ad_list);
        mListView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("AdListActivityListView", "You clicked Item: " + id + " at position:" + position + ", tag: " + v.getTag());

        Intent intent = new Intent(this, AdViewActivity.class);
        //intent.putExtra("id", (int) v.getTag());
        Bundle bundle = new Bundle();
        bundle.putSerializable("ad", ads.get((int) v.getTag()));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void populateAdList(List<Ad> ads) {
        //this.ads = ads;
        this.ads = new HashMap<Integer, Ad>();
        for(Ad ad: ads) {
            this.ads.put(ad.getId(), ad);
        }

        AdListAdapter arrayAdapter = new AdListAdapter(this, ads);

        mListView.setAdapter(arrayAdapter);
    }

    private class AdListAdapter extends ArrayAdapter<Ad> {

        public AdListAdapter(Context context, List<Ad> ads) {
            super(context, 0, ads);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Ad ad = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.ad_list_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.ad_list_item_title);
            title.setText(ad.getTitle());
            convertView.setTag(ad.getId());

            return convertView;
        }

    }

}
