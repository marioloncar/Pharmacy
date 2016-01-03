package com.mario.pharmacy.tabs;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mario.pharmacy.DatabaseHelper;
import com.mario.pharmacy.InfoActivity;
import com.mario.pharmacy.MapsActivity;
import com.mario.pharmacy.R;

import java.util.ArrayList;

/**
 * Created by mario on 12/21/15.
 */
public class ListFragment extends Fragment {
    ListView pharmacies;


    public ListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        pharmacies = (ListView) view.findViewById(R.id.lvPharmacies);

        pharmacies.setAdapter(new MyListAdapter(getActivity().getApplicationContext()));
        pharmacies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent info = new Intent(view.getContext(), InfoActivity.class);
                TextView name = (TextView) view.findViewById(R.id.tvName);
                info.putExtra("name", name.getText().toString());
                startActivity(info);
            }
        });

        return view;
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
