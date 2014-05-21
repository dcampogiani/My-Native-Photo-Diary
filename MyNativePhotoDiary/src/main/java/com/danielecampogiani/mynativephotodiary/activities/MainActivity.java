package com.danielecampogiani.mynativephotodiary.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.danielecampogiani.mynativephotodiary.R;
import com.danielecampogiani.mynativephotodiary.fragments.TimelineFragment;
import com.danielecampogiani.mynativephotodiary.fragments.PlacesFragment;
import com.danielecampogiani.mynativephotodiary.persistence.PicturesProvider;


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

        ContentResolver cr = getContentResolver();
        /*ContentValues newValues = new ContentValues();
        newValues.put(PicturesProvider.KEY_DESCRIPTION,"desc");
        newValues.put(PicturesProvider.KEY_LATITUDE,11.23);
        newValues.put(PicturesProvider.KEY_LONGITUDE, 12.03);
        newValues.put(PicturesProvider.KEY_URI,"http://www.calcioweb.eu/wp-content/uploads/2013/01/Vieri-Inter.jpg");

        cr.insert(PicturesProvider.CONTENT_URI,newValues);*/

        //cr.delete(PicturesProvider.CONTENT_URI,null,null);


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
        if (id == R.id.action_new_picture) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}


