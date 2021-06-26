package com.eee.taxibooking.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.eee.taxibooking.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FavoritesFragment extends Fragment {
    FloatingActionButton add;
    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        add = view.findViewById(R.id.addAddressFab);
        AddressFragment addressFragment = new AddressFragment();
        add.setOnClickListener(v -> {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_in_right);
            transaction.replace(R.id.fragment_explore, addressFragment).addToBackStack(null).commit();
        });

        return view;
    }
}