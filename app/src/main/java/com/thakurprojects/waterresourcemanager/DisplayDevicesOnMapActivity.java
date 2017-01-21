package com.thakurprojects.waterresourcemanager;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DisplayDevicesOnMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String deviceDetails="";

    // Loger
    Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_devices_on_map);

        // Get DEVICE details.
        Intent intent = getIntent();
        deviceDetails = intent.getStringExtra(MainActivity.DEVICE_MAP_ID);

        logger.addRecordToLog("LAT_LNG Details : \n"+deviceDetails);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String[] allDevice = deviceDetails.split("%");
        for (String eachDevice: allDevice) {
            if(!eachDevice.isEmpty()) {
                logger.addRecordToLog(" >> " + eachDevice);
                Double latitude = Double.parseDouble(eachDevice.split("#")[0]);
                Double longitude = Double.parseDouble(eachDevice.split("#")[1]);
                String title=eachDevice.split("#")[2];
                String details=eachDevice.split("#")[3];

                Marker deviceOnMap= mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(title).snippet(details));
                deviceOnMap.showInfoWindow();

            }
        }//for



        //Animating the camera
        LatLng india = new LatLng(21.7679, 78.8718);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(india));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(4));
    }
}
