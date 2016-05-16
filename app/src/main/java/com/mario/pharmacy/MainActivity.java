package com.mario.pharmacy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    DatabaseHelper helper;
    ListView pharmacies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pharmacies = (ListView) findViewById(R.id.lvPharmacies);
        helper = new DatabaseHelper(this);

        pharmacies.setAdapter(new MyListAdapter(this));
        pharmacies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent info = new Intent(view.getContext(), InfoActivity.class);
                TextView name = (TextView) view.findViewById(R.id.tvName);
                info.putExtra("name", name.getText().toString());
                startActivity(info);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent map = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(map);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ViewHolder {
        TextView name, address;
        ImageView image;
    }

    class SingleRow {
        String name, address;
        int arrow;

        SingleRow(String name, String address, int arrow) {
            this.name = name;
            this.address = address;
            this.arrow = arrow;
        }

        public String getName() {
            return name;
        }
    }

    class MyListAdapter extends BaseAdapter {
        DatabaseHelper helper;
        ArrayList<SingleRow> list;
        Context context;

        MyListAdapter(Context c) {
            context = c;
            list = new ArrayList<SingleRow>();
            helper = new DatabaseHelper(context);

            int[] arrow = {R.drawable.list_arrow};

            for (int i = 0; i < helper.getName().size(); i++) {
                list.add(new SingleRow(helper.getName().get(i), helper.getAddress().get(i), arrow[0]));
            }

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_row, parent, false);

                holder = new ViewHolder();

                holder.name = (TextView) convertView.findViewById(R.id.tvName);
                holder.address = (TextView) convertView.findViewById(R.id.tvAddress);
                holder.image = (ImageView) convertView.findViewById(R.id.ivArrow);

                convertView.setTag(holder);

            } else {
                //view is already recycled
                holder = (ViewHolder) convertView.getTag();
            }

            SingleRow temp = list.get(position);

            holder.name.setText(temp.name);
            holder.address.setText(temp.address);
            holder.image.setImageResource(temp.arrow);

            return convertView;
        }

    }
}

