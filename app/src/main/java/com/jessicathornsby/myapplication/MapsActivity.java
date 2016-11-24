package com.jessicathornsby.myapplication;


import android.support.v4.app.ActivityCompat;
import android.os.Build;
import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.SupportMapFragment;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


//Since it’s the easiest way of adding a map to your project, I’m going to stick with using a MapFragment//


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        LocationListener {


    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Marker mLocationMarker;
    Location mLastLocation;
    LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }






        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()




                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);


    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;


    public boolean checkLocationPermission(){


//In Android 6.0 and higher you need to request permissions at runtime, and the user has the ability to grant or deny each permission. Users can also revoke a previously-granted permission at any time, so your app must always check that it has access to each permission, before trying to perform actions that require that permission. Here, we’re using ContextCompat.checkSelfPermission to check whether this app currently has the ACCESS_COARSE_LOCATION permission//


        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)


//If your app does have access to COARSE_LOCATION, then this method will return PackageManager.PERMISSION_GRANTED//


                != PackageManager.PERMISSION_GRANTED) {




            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {


                // If your app doesn’t have this permission, then you’ll need to request it by calling the ActivityCompat.requestPermissions method//


                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);


            } else {

//Request the permission by launching Android’s standard permissions dialog. If you want to provide any additional information, such as why your app requires this particular permission, then you’ll need to add this information before calling requestPermission//


                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();




    }


    @Override
    protected void onPause() {
        super.onPause();


    }




    @Override
    public void onMapReady(GoogleMap googleMap) {




        mMap = googleMap;


        //Specify what kind of map you want to display. In this example I’m sticking with the classic, “Normal” map//


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();


//Although the user’s location will update automatically on a regular basis, you can also give your users a way of triggering a location update manually. Here, we’re adding a ‘My Location’ button to the upper-right corner of our app; when the user taps this button, the camera will update and center on the user’s current location//


                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }
    protected synchronized void buildGoogleApiClient() {


//Use the GoogleApiClient.Builder class to create an instance of the Google Play Services API client//


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


//Connect to Google Play Services, by calling the connect() method//


        mGoogleApiClient.connect();
    }


    @Override


//If the connect request is completed successfully, the onConnected(Bundle) method will be invoked and any queued items will be executed//


    public void onConnected(Bundle bundle) {


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


//Retrieve the user’s last known location//


            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


//onConnectionSuspended is called when the client gets disconnected from Google Play Services. At this point, all requests are cancelled and no outstanding listeners will be executed. Note, the Google API client will attempt to restore the connection automatically, so this isn’t something you need to worry about//


    @Override
    public void onConnectionSuspended(int i) {


    }


//Displaying multiple ‘current location’ markers is only going to confuse your users! To make sure there’s only ever one marker onscreen at a time, I’m using mLocationMarker.remove to clear all markers whenever the user’s location changes//


    @Override
    public void onLocationChanged(Location location) {


        mLastLocation = location;
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }


//To help preserve the device’s battery life, you’ll typically want to use removeLocationUpdates to suspend location updates when your app is no longer visible onscreen//




        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


    }


//Once the user has granted or denied your permission request, the Activity’s onRequestPermissionsResult method will be called, and the system will pass the results of the ‘grant permission’ dialog, as an int//


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                // If the request is cancelled, the result array will be empty (0)//




                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



//If the user has granted your permission request, then your app can now perform all its location-related tasks, including displaying the user’s location on the map//




                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }


                } else {


                    //If the user has denied your permission request, then at this point you may want to disable any functionality that depends on this permission//


                }
                return;
            }




        }
    }


}