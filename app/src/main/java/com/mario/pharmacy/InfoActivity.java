package com.mario.pharmacy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by mario on 12/11/15.
 */
public class InfoActivity extends AppCompatActivity {

    Bundle extras;
    String name;
    DatabaseHelper helper;
    ArrayList<String> data;
    ImageView ivPicture;
    TextView tvTitle, tvAddress, tvPhone, tvEmail, tvWorkday, tvSaturday;
    ImageButton ibNavigation, ibPhone, ibEmail;
    int imageName;
    Double latitude, longitude;
    LatLng coordinates;

    private static final int PERMISSION_REQUEST_CODE_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helper = new DatabaseHelper(this);

        ivPicture = (ImageView) findViewById(R.id.ivPicture);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvWorkday = (TextView) findViewById(R.id.tvWorkday);
        tvSaturday = (TextView) findViewById(R.id.tvSaturday);
        ibNavigation = (ImageButton) findViewById(R.id.ibNavigation);
        ibPhone = (ImageButton) findViewById(R.id.ibPhone);
        ibEmail = (ImageButton) findViewById(R.id.ibEmail);

        //Get pharmacy name from previous activity so we can make a query
        extras = getIntent().getExtras();
        name = extras.getString("name");

        //Database query
        data = helper.getData(name);

        //Convert image to int
        imageName = getResources().getIdentifier(data.get(1), "drawable", InfoActivity.this.getPackageName());

        //add data to UI
        tvTitle.setText(name);
        ivPicture.setImageResource(imageName);
        tvAddress.setText(data.get(0));
        tvPhone.setText(data.get(2));
        tvEmail.setText(data.get(3));
        tvWorkday.setText(data.get(4));
        tvSaturday.setText(data.get(5));

        //Parse String to Double so we can create LatLng object
        latitude = Double.parseDouble(data.get(6));
        longitude = Double.parseDouble(data.get(7));

        coordinates = new LatLng(latitude, longitude);


        //handle navigation button click
        ibNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(InfoActivity.this)
                        .setTitle("Navigation")
                        .setMessage("Are you sure you want to start navigation to " + data.get(0) + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent mapsIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?&daddr=" + latitude + "," + longitude));
                                mapsIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(mapsIntent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss
                            }
                        })
                        .setIcon(R.drawable.navigation)
                        .show();
            }
        });

        //Phone
        ibPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(InfoActivity.this)
                        .setTitle("Call")
                        .setMessage("Are you sure you want to call number " + data.get(2) + "?")
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    if (checkPermission(Manifest.permission.CALL_PHONE, getApplicationContext(), InfoActivity.this)) {
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:" + data.get(2).trim()));
                                        startActivity(callIntent);
                                    } else {
                                        requestPermission(Manifest.permission.CALL_PHONE, PERMISSION_REQUEST_CODE_CALL, getApplicationContext(), InfoActivity.this);

                                    }
                                } catch (Exception e) {
                                    Toast.makeText(InfoActivity.this, "Greška",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss
                            }
                        })
                        .setIcon(R.drawable.phone)
                        .show();
            }
        });

        //Email
        ibEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(data.get(3), "nepoznato")) {
                    new AlertDialog.Builder(InfoActivity.this)
                            .setTitle("Email")
                            .setMessage("This pharmacy does not have email!")
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //dismiss
                                }
                            })
                            .setIcon(R.drawable.mail)
                            .show();
                } else {
                    new AlertDialog.Builder(InfoActivity.this)
                            .setTitle("Email")
                            .setMessage("Are you sure you want to send email to " + name + "?")
                            .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent mailIntent = new Intent(Intent.ACTION_SEND);
                                    mailIntent.setType("message/rfc822");
                                    mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{data.get(3)});

                                    try {
                                        startActivity(Intent.createChooser(mailIntent, "Send email..."));
                                    } catch (android.content.ActivityNotFoundException ex) {
                                        Toast.makeText(InfoActivity.this, "You don't have email app installed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //dismiss
                                }
                            })
                            .setIcon(R.drawable.mail)
                            .show();
                }
            }
        });
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

            case PERMISSION_REQUEST_CODE_CALL:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getApplicationContext(), "App does not have permission to make a call!", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
}
