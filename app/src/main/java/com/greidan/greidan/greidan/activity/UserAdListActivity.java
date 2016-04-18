package com.greidan.greidan.greidan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.AdManager;
import com.greidan.greidan.greidan.manager.UserManager;
import com.greidan.greidan.greidan.model.Ad;

import java.util.HashMap;
import java.util.List;

public class UserAdListActivity extends ProgressActivity {
    // This is basically a copy of AdListActivity minus the location slider
    // Maybe consider reusing components?

    AdManager mAdManager;
    UserManager mUserManager;

    ListView mAdList;

    HashMap<String, Ad> ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ad_list);

        mContainerView = findViewById(R.id.user_ad_list_container);
        mProgressView = findViewById(R.id.user_ad_list_progress);

        mAdList = (ListView) findViewById(R.id.user_ad_list);
        mAdList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UserAdListActivity.this, AdViewActivity.class);
                Bundle bundle = new Bundle();
                String adID = (String) view.getTag();
                bundle.putParcelable("ad", ads.get(adID));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mAdManager = new AdManager(this);
        mUserManager = new UserManager(this);

        mAdManager.fetchAdsByUsername(mUserManager.getLoggedInUsername());
    }

    private void populateAdList(List<Ad> ads) {
        this.ads = new HashMap<>();
        for(Ad ad: ads) {
            this.ads.put(ad.getId(), ad);
        }

        mAdList.setAdapter(new AdListAdapter(this, ads));
    }

    @Override
    public void doUponCompletion(Bundle data) {
        List<Ad> adlist = data.getParcelableArrayList("ads");
        populateAdList(adlist);
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
