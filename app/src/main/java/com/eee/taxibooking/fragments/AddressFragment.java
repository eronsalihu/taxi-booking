package com.eee.taxibooking.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eee.taxibooking.R;
import com.eee.taxibooking.databases.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;

public class AddressFragment extends Fragment {

    private TextInputLayout _name;
    private TextInputLayout _address;
    private TextInputLayout _city;

    DatabaseHelper databaseHelper;

    public AddressFragment() {
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
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        databaseHelper = new DatabaseHelper(getContext());

        _name = view.findViewById(R.id.name_for_address);
        _address = view.findViewById(R.id.address);
        _city = view.findViewById(R.id.city);



        MaterialButton saveAddress = view.findViewById(R.id.saveAddress);
        saveAddress.setOnClickListener(v -> {
            String name = _name.getEditText().getText().toString();
            String address = _name.getEditText().getText().toString();
            String city = _name.getEditText().getText().toString();

            boolean insertData = databaseHelper.addData(name,address,city);
            if (insertData == true){
                Toast.makeText(getActivity(),"Added",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(),"danger",Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }
}