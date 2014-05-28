package com.danielecampogiani.mynativephotodiary.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.danielecampogiani.mynativephotodiary.R;
import com.danielecampogiani.mynativephotodiary.fragments.PlacesFragment;
import com.danielecampogiani.mynativephotodiary.fragments.TimelineFragment;
import com.danielecampogiani.mynativephotodiary.persistence.PicturesProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri currentUri;

    private ActionBar actionBar;

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
        if (id == R.id.action_new_picture) {
            takeNewPicture();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){

            if (resultCode==RESULT_OK){

                saveNewPicture();

            }

        }

    }

    private void takeNewPicture(){

        new NewPictureAsyncTask().execute();

    }

    private Uri getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyNativePhotoDiary"  );

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    @SuppressLint("all")
    private void saveNewPicture(){

        LayoutInflater inflater = LayoutInflater.from(this);
        View promptView = inflater.inflate(R.layout.dialog_description_layout,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setCancelable(false);
        final EditText input = (EditText)promptView.findViewById(R.id.userInput);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int id) {


                new SavePictureAsyncTask(currentUri,input.getText().toString()).execute();

            }

        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int id) {
                File file = new File(currentUri.getPath());
                file.delete();

            }

        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();

    }

    private class NewPictureAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... arg0) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            currentUri = getOutputMediaFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT,currentUri);

            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            return true;
        }
    }

    private class SavePictureAsyncTask extends AsyncTask<Void, Integer, Boolean> {

        private final Uri uri;
        private final String description;

        public SavePictureAsyncTask(Uri uri, String description){
            this.uri=uri;
            this.description=description;
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                ExifInterface exif = new ExifInterface(uri.getPath());
                float[] latLong = new float[2];

                exif.getLatLong(latLong);

                ContentResolver cr = getContentResolver();
                ContentValues newValues = new ContentValues();
                newValues.put(PicturesProvider.KEY_DESCRIPTION,description);
                newValues.put(PicturesProvider.KEY_LATITUDE,latLong[0]);
                newValues.put(PicturesProvider.KEY_LONGITUDE, latLong[1]);
                newValues.put(PicturesProvider.KEY_URI,currentUri.getPath());

                cr.insert(PicturesProvider.CONTENT_URI,newValues);

            }catch (IOException e){

            }


            return true;
        }
    }

}


