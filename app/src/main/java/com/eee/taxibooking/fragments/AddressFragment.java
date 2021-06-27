package com.eee.taxibooking.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.eee.taxibooking.R;
import com.eee.taxibooking.databases.Address;
import com.eee.taxibooking.databases.Database;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class AddressFragment extends Fragment {

    private TextInputLayout _name;
    private TextInputLayout _address;
    private TextInputLayout _city;
    private ImageView backBtn;

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


        _name = view.findViewById(R.id.name_for_address);
        _address = view.findViewById(R.id.address);
        _city = view.findViewById(R.id.city);

        FavoritesFragment favoritesFragment = new FavoritesFragment();

        backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_slide_in_left, R.anim.slide_out_right).remove(this).commit();

        });


        MaterialButton saveAddress = view.findViewById(R.id.saveAddress);
        saveAddress.setOnClickListener(v -> {
            String name = _name.getEditText().getText().toString();
            String address = _address.getEditText().getText().toString();
            String city = _city.getEditText().getText().toString();

            saveNewAddress(name,address,city);

//            FragmentManager fm = getFragmentManager();
//            FragmentTransaction transaction = fm.beginTransaction();
//            transaction.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.slide_out_right);
//            transaction.replace(R.id.fragment_explore, favoritesFragment).addToBackStack(null).commit();
            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_slide_in_left, R.anim.slide_out_right).remove(this).commit();

            Snackbar.make(getView(), "Address Added.", Snackbar.LENGTH_LONG).show();

//            Toast.makeText(getActivity(),"Added",Toast.LENGTH_SHORT).show();


        });

        return view;
    }

    private void saveNewAddress(String name, String _address,String city) {
        Database db  = Database.getDbInstance(getContext().getApplicationContext());

        Address address = new Address();
        address.name = name;
        address.address =_address;
        address.city = city;
        db.addressDao().insertAddress(address);

    }

}