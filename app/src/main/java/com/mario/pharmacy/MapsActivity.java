package com.mario.pharmacy;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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


    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Make database
        helper = new DatabaseHelper(this);

        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getApplicationContext(), MapsActivity.this)) {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getApplicationContext(), MapsActivity.this);
        }

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getApplicationContext(), MapsActivity.this)) {
            mMap.setMyLocationEnabled(true);
        }
        checkConnectivity();
    }

    public void checkConnectivity() {
        if (!isNetworkAvailable()) {
            connectionAlert();
        } else {
            setUpMap();
        }
    }

    private void setUpMap() {
        LatLng rijeka = new LatLng(45.347050, 14.422648);
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
                    .snippet(address.get(i) + " (Click for info)")
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("marker", 100, 100))));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rijeka, 12));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra("name", marker.getTitle());
                startActivity(intent);
            }
        });
    }




    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    private void locationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("Location services disabled");
        builder.setMessage("Google Maps needs access to your location. Please turn on location access to get your current location.");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                //builder.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void connectionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("No internet connection");
        builder.setMessage("Please check your internet connection and try again.");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkConnectivity(); //ponovno provjeravanje
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNeutralButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void requestPermission(String strPermission, int perCode, Context context, Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{strPermission}, perCode);
    }

    public static boolean checkPermission(String strPermission, Context context, Activity activity) {
        int result = ContextCompat.checkSelfPermission(context, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getApplicationContext(), "App can not access your location!", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
}
