package com.thakurprojects.waterresourcemanager;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener{

    //Our Map
    private GoogleMap mMap;

    // logger object
    Logger logger;

    //To store longitude and latitude from map
    private double longitude;
    private double latitude;
    MarkerOptions markerOptions;
    //LatLng latLng;

    //Buttons
    private ImageButton buttonSave;
    private ImageButton buttonCurrent;
    //private ImageButton buttonView;

    //Address text view
    private TextView _address_view;

    //Google ApiClient
    private GoogleApiClient googleApiClient;

    // Intent
    private String deviceLocation="NON";
    private int REQUEST_CODE=1;
    private final String TAG="map_activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        //Initializing views and adding onclick listeners
        _address_view=(TextView) findViewById(R.id.address_textView);
        buttonSave = (ImageButton) findViewById(R.id.buttonSave);
        buttonCurrent = (ImageButton) findViewById(R.id.buttonCurrent);
        //buttonView = (ImageButton) findViewById(R.id.buttonView);
        buttonSave.setOnClickListener(this);
        buttonCurrent.setOnClickListener(this);
       // buttonView.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    //Getting current location
    private void getCurrentLocation() {
        mMap.clear();
        //Creating a location object
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();

            LatLng latLng1=new LatLng(latitude,longitude);
            getAddress(latLng1);

        }
    }

    //Function to move the map
    private void moveMap() {
        //String to display current latitude and longitude
        String msg = latitude + ", "+longitude;

        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location")); //Adding a title

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Displaying current coordinates in toast
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        // Check if backgound task is running
        Intent intentMessage=new Intent();
        // put the message to return as result in Intent
        intentMessage.putExtra("MESSAGE",deviceLocation);
        // Set The Result in Intent
        setResult(REQUEST_CODE,intentMessage);
        // finish The activity
        finish();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Clearing all the markers
        mMap.clear();

        //Adding a new marker to the current pressed position
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
        // Update location

         deviceLocation=getAddress(latLng);
         _address_view.setText(deviceLocation);

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onClick(View v) {
        if(v == buttonCurrent){
            getCurrentLocation();
            moveMap();
        } else if(v == buttonSave){
            Intent intentMessage=new Intent();
            // put the message to return as result in Intent
            intentMessage.putExtra("MESSAGE",deviceLocation);
            // Set The Result in Intent
            setResult(REQUEST_CODE,intentMessage);
            // finish The activity
            finish();

        }
    }

    // Find address from latiu and long corrdinates.
    private String getAddress(LatLng latLng) {

        String combinedAddress = "NON";
        String address = "NON";
        String city = "NON";
        String state = "NON";
        String country = "NON";
        String postalCode = "NON";

        String addressFragments = "NON";
        String errorMessage = "";

        //set default address
        combinedAddress=latLng.latitude+"#"+latLng.longitude+"#"+address+"#"+city+"#"+state+"#"+country+"#"+postalCode;

        logger.addRecordToLog("Find Address : ");
        logger.addRecordToLog("GEO Cordinate Details : "+latLng.latitude+" : "+latLng.longitude);
        // Find address
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "#Network or I/O Problem";
            logger.addRecordToLog(TAG+" : "+ errorMessage+" : "+ioException);
            return combinedAddress+errorMessage;
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "#Invalid latitude or longitude values";
            logger.addRecordToLog(TAG+" : "+  errorMessage + ". " +
                    "Latitude = " + latLng.latitude +
                    ", Longitude = " +
                    latLng.longitude+" : "+ illegalArgumentException);
            return combinedAddress+errorMessage;
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Address Not Found!!";
                logger.addRecordToLog(TAG+" : "+  errorMessage);
            }

        } else {
            Address address_1 = addresses.get(0);

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            addressFragments="";
            for (int i = 2; i < address_1.getMaxAddressLineIndex(); i++) {
                addressFragments = address_1.getAddressLine(i) + "," + addressFragments;
            }
            // Set Other Values
            address = address_1.getLocality();
            city = address_1.getSubAdminArea();
            state = address_1.getAdminArea();
            country = address_1.getCountryName();
            postalCode=address_1.getPostalCode();

            logger.addRecordToLog(TAG+" : "+ "Address Found!!\n" +" : "+ addressFragments);
            combinedAddress=latLng.latitude+"#"+latLng.longitude+"#"+address+"#"+city+"#"+state+"#"+country+"#"+postalCode;
            logger.addRecordToLog("Combined Address : "+combinedAddress);

        }

        // Return address or error string
        if (addressFragments.equals("NON")) {
            errorMessage="#No Error!!";
            return combinedAddress+errorMessage;
        } else {
            return combinedAddress;
        }


    } // fcn end

}// class end