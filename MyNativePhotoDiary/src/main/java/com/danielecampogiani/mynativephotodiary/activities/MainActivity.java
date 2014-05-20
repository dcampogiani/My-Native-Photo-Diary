package com.danielecampogiani.mynativephotodiary.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.danielecampogiani.mynativephotodiary.R;
import com.danielecampogiani.mynativephotodiary.fragments.TimelineFragment;
import com.danielecampogiani.mynativephotodiary.fragments.PlacesFragment;


public class MainActivity extends Activity {

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab timelineTab = actionBar.newTab();
        TabListener timelineListener = new com.danielecampogiani.mynativephotodiary.fragments.TabListener<TimelineFragment>(this,R.id.container, TimelineFragment.class);
        timelineTab.setText("Timeline");
        timelineTab.setContentDescription("Pictures in timeline");
        timelineTab.setTabListener(timelineListener);

        Tab placesTab = actionBar.newTab();
        TabListener placesListener = new com.danielecampogiani.mynativephotodiary.fragments.TabListener<PlacesFragment>(this,R.id.container, PlacesFragment.class);
        placesTab.setText("Places");
        placesTab.setContentDescription("Show Map");
        placesTab.setTabListener(placesListener);


        actionBar.addTab(timelineTab);
        actionBar.addTab(placesTab);

        setContentView(R.layout.activity_main);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}


