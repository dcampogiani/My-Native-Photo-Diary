package com.danielecampogiani.mynativephotodiary.fragments;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.IntentSender;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielecampogiani.mynativephotodiary.R;
import com.danielecampogiani.mynativephotodiary.persistence.PicturesProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PlacesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,  GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private Cursor pictures;
    private GoogleMap map;
    private LocationClient locationClient;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationClient = new LocationClient(getActivity(), this, this);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_places, container, false);
        MapFragment f = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map = f.getMap();
        map.setMyLocationEnabled(true);
        locationClient.connect();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapFragment f = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
        locationClient.disconnect();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0,null,this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{PicturesProvider.KEY_ID,PicturesProvider.KEY_LATITUDE,PicturesProvider.KEY_LONGITUDE,PicturesProvider.KEY_URI};
        return new CursorLoader(getActivity(), PicturesProvider.CONTENT_URI,projection,null,null,PicturesProvider.KEY_ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        pictures=data;
        addMarkers();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        pictures=null;
    }

    private void addMarkers(){

        map.clear();
        Log.i("PlacesFragment","addMarkers()");

        new AddMarkersAsyncTask().execute(); // start the background processing

    }

    @Override
    public void onConnected(Bundle bundle) {

        Location current = locationClient.getLastLocation();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(current.getLatitude(), current.getLongitude()), 15));

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        }
    }

    private class AddMarkersAsyncTask extends AsyncTask<Void, Void, List<MarkerOptions>> {
        @Override
        protected List<MarkerOptions> doInBackground(Void... arg0) {
            // do background processing and return the appropriate result
            // ...

            ArrayList<MarkerOptions> result = new ArrayList<MarkerOptions>();

            if (pictures!=null && pictures.moveToFirst()){


                do{
                    Double lat = pictures.getDouble(pictures.getColumnIndex(PicturesProvider.KEY_LATITUDE));
                    Double lon = pictures.getDouble(pictures.getColumnIndex(PicturesProvider.KEY_LONGITUDE));

                    Bitmap fullImage = BitmapFactory.decodeFile(pictures.getString(pictures.getColumnIndex(PicturesProvider.KEY_URI)));
                    if (fullImage==null)
                        continue;
                    Bitmap image = Bitmap.createScaledBitmap(fullImage,fullImage.getWidth()/5, fullImage.getHeight()/5,false);

                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(image);
                    MarkerOptions options = new MarkerOptions().position(new LatLng(lat,lon)).icon(icon);
                    //Log.i("PlacesFragment",pictures.getString(pictures.getColumnIndex(PicturesProvider.KEY_URI)));
                    //map.addMarker(options);
                    result.add(options);

                }
                while (pictures.moveToNext());


            }

            return result;
        }

        @Override
        protected void onPostExecute(List<MarkerOptions> result) {
            for (MarkerOptions current : result)
                map.addMarker(current);
        }

    }

}
