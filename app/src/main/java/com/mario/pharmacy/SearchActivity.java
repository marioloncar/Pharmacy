package com.mario.pharmacy;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mario on 12/27/15.
 */
public class SearchActivity extends AppCompatActivity {
    EditText etSearch;
    ImageButton ibSearch;
    ListView lvSearch;
    private SearchView searchView;
    private MyListAdapter defaultAdapter;
    private ArrayList<String> nameList;
    DatabaseHelper helper;
    String send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etSearch = (EditText) findViewById(R.id.etSearch);
        ibSearch = (ImageButton) findViewById(R.id.ibSearch);
        lvSearch = (ListView) findViewById(R.id.lvSearch);

        helper = new DatabaseHelper(this);
        nameList = new ArrayList<>();

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send = etSearch.getText().toString();
                lvSearch.setAdapter(new MyListAdapter(getApplicationContext()));
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
        ArrayList<String> pharmacyName;
        Context context;

        MyListAdapter(Context c) {
            context = c;
            list = new ArrayList<SingleRow>();
            pharmacyName = new ArrayList<>();
            helper = new DatabaseHelper(context);
            pharmacyName = helper.getByInput(send);

            int[] arrow = {R.drawable.list_arrow};

            for (int i = 0; i < 5; i++) {
                list.add(new SingleRow(pharmacyName.get(i), helper.getAddress().get(i), arrow[0]));
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