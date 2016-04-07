package com.greidan.greidan.greidan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.greidan.greidan.greidan.R;
import com.greidan.greidan.greidan.manager.AdManager;
import com.greidan.greidan.greidan.manager.UserManager;

import java.util.List;

public class MainActivity extends ProgressActivity implements AdapterView.OnItemClickListener {

    AdManager adManager;
    UserManager userManager;

    Button mButtonNewPost;
    ListView mCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adManager = new AdManager(this);
        userManager = new UserManager(this);

        mCategoryList = (ListView) findViewById(R.id.main_categories);
        mCategoryList.setOnItemClickListener(this);

        mContainerView = findViewById(R.id.main_container);
        mProgressView = findViewById(R.id.main_progress);

        //showProgress(true);
        adManager.fetchCategories();

        if(!userManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        mButtonNewPost = (Button) findViewById(R.id.button_new_post);
        mButtonNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewAdActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        String category = (String) ((ListView) v.getParent()).getItemAtPosition(position);

        Intent intent = new Intent(this, AdListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
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
