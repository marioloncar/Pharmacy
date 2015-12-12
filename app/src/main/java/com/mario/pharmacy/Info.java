package com.mario.pharmacy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * Created by mario on 12/11/15.
 */
public class Info extends AppCompatActivity {

    Bundle extras;
    String name;
    DatabaseHelper helper;
    ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helper = new DatabaseHelper(this);

        extras = getIntent().getExtras();
        name = extras.getString("name");

        data = helper.getData(name);

        System.out.println("DATA ->" + data);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
