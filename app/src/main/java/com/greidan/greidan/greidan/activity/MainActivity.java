package com.greidan.greidan.greidan.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.AdManager;
import com.greidan.greidan.greidan.manager.UserManager;

import java.util.List;

public class MainActivity extends ProgressActivity {

    AdManager adManager;
    UserManager userManager;

    LinearLayout mSideBar;
    ListView mSideBarList;
    DrawerLayout mDrawerLayout;
    Button mButtonNewPost;
    ListView mCategoryList;

    ImageView mSidebarImage;
    TextView mSideBarUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adManager = new AdManager(this);
        userManager = new UserManager(this);

        if(!userManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        mContainerView = findViewById(R.id.main_container);
        mProgressView = findViewById(R.id.main_progress);

        mCategoryList = (ListView) findViewById(R.id.main_categories);
        mCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String category = (String) ((ListView) view.getParent()).getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, AdListActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        //showProgress(true);
        adManager.fetchCategories();

        // Set up sidebar
        mSideBar = (LinearLayout) findViewById(R.id.main_sidebar);
        mSideBarList = (ListView) findViewById(R.id.main_sidebar_list);
        mSideBarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context ctx = MainActivity.this;
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(ctx, UserProfileActivity.class);
                        break;
                    // TODO: implement other list items
                }

                if(intent != null) {
                    startActivity(intent);
                }
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mSidebarImage = (ImageView) findViewById(R.id.sidebar_image);
        mSideBarUsername = (TextView) findViewById(R.id.sidebar_username);

        // Set up buttons
        mButtonNewPost = (Button) findViewById(R.id.button_new_post);
        mButtonNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewAdActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String profileImagePath = userManager.getProfileImagePath();

        if(profileImagePath != null) {
            mSidebarImage.setImageBitmap(BitmapFactory.decodeFile(profileImagePath));
        } else {
            mSidebarImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, null));
        }

        mSideBarUsername.setText(userManager.getLoggedInUsername());
    }

    @Override
    public void doUponCompletion(Bundle data) {
        List<String> categories = (List<String>)data.getSerializable("categories");

        mCategoryList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_basic, categories));

        showProgress(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.logout) {
            userManager.logout();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
