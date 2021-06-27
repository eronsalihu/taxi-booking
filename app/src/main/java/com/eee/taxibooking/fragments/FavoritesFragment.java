package com.eee.taxibooking.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eee.taxibooking.R;
import com.eee.taxibooking.adapters.AddressAdapter;
import com.eee.taxibooking.databases.Address;
import com.eee.taxibooking.databases.Database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    FloatingActionButton add;
    Database database;
    ArrayList<String> address_name, address_address, address_city;
    public AddressAdapter addressAdapter;

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
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_in_left);
            transaction.replace(R.id.fragment_explore, addressFragment).addToBackStack(null).commit();
        });

        RecyclerView recyclerView = view.findViewById(R.id.addressesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addressAdapter = new AddressAdapter(getContext());
        addressAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(addressAdapter);

        loadAddressList();


        return view;
    }

    private void loadAddressList() {
        Database db = Database.getDbInstance(getContext().getApplicationContext());
        List<Address> addressList =db.addressDao().getAllAddresses();
        addressAdapter.setAddressList(addressList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100) {
            loadAddressList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}