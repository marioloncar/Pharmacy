package com.mario.pharmacy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseHelper helper;
    ArrayList<String> latitudeArray, longitudeArray, name, address;
    double latitude, longitude;
    LatLng coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        helper = new DatabaseHelper(this);

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
        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng rijeka = new LatLng(45.329904, 14.438916);
        latitudeArray = helper.getLatitude();
        longitudeArray = helper.getLongitude();
        name = helper.getName();
        address = helper.getAddress();

        for (int i = 0; i < latitudeArray.size(); i++) {
            latitude = Double.parseDouble(latitudeArray.get(i));
            longitude = Double.parseDouble(longitudeArray.get(i));
            coordinates = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(name.get(i))
                    .snippet(address.get(i))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rijeka, 15));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), Info.class);
                intent.putExtra("name", marker.getTitle());
                startActivity(intent);
            }
        });
    }
}
