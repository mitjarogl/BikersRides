package com.moods.bikersrides.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.moods.bikersrides.R;
import com.moods.bikersrides.fragments.HomeFragment;


public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, HomeFragment.newInstance(), "HOME").commit();
            getSupportFragmentManager().addOnBackStackChangedListener(this);
            //Handle when activity is recreated like on orientation Change
            //          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            shouldDisplayHomeUp();
//            Log.d("ENTRY-COUNT", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
        } else {
            //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            shouldDisplayHomeUp();
//            Log.d("ENTRY-COUNT2", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
            return;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_search || super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp() {
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;

        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);

        Log.d("MAIN-ENTRY-COUNT", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }
}
