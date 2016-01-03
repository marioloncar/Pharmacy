package com.mario.pharmacy.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mario.pharmacy.R;

/**
 * Created by mario on 12/21/15.
 */
public class FavoritesFragment extends Fragment {
    ListView favorites;

    public FavoritesFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favorites = (ListView) view.findViewById(R.id.lvFavorites);

        return view;

    }
}
