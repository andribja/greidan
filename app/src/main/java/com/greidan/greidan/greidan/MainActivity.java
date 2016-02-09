package com.greidan.greidan.greidan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private Button mButtonHandywork;
    private Button mButtonRides;
    private Button mButtonTools;
    private Button mButtonProgramming;
    private Button mButtonOther;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonHandywork = (Button) findViewById(R.id.button_handywork);
        mButtonRides = (Button) findViewById(R.id.button_rides);
        mButtonTools = (Button) findViewById(R.id.button_tools);
        mButtonProgramming = (Button) findViewById(R.id.button_programming);
        mButtonOther = (Button) findViewById(R.id.button_other);

        mButtonHandywork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO

//                Toast.makeText(MainActivity.this, "Foobar", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, AdListActivity.class);
                startActivity(intent);
            }
        });

        mButtonRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        mButtonTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        mButtonProgramming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        mButtonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
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

        return super.onOptionsItemSelected(item);
    }
}
