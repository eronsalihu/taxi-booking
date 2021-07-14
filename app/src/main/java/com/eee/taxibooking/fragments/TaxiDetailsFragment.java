package com.eee.taxibooking.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.eee.taxibooking.R;

import java.io.InputStream;

public class TaxiDetailsFragment extends Fragment {
    private TextView _phone;
    private String taxiName;
    private String imageURI;
    private String phone_1;
    private String phone_2;
    private String freeCall;

    public TaxiDetailsFragment() {
        // Required empty public constructor
    }

    public TaxiDetailsFragment(String name, String image, String phone_1, String phone_2, String freeCall) {
        this.taxiName = name;
        this.imageURI = image;
        this.phone_1 = phone_1;
        this.phone_2 = phone_2;
        this.freeCall = freeCall;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_taxi_details, container, false);
        TextView name = view.findViewById(R.id.detailName);
        name.setText(taxiName);
        ImageView imageView = view.findViewById(R.id.imageTaxiDetails);
        try {
            AsyncTask<String, Void, Bitmap> execute = new LoadImageTask(imageView)
                    .execute(imageURI);

        } catch (Exception ex) {
            ex.getMessage();
        }

        ImageView closeBtn = view.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(v -> getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up).remove(this).commit());

        _phone = view.findViewById(R.id.number_1);
        _phone.setText(phone_1);

        TextView _phone_ = view.findViewById(R.id.number_2);
        _phone_.setText(phone_2);

        TextView _freeCall = view.findViewById(R.id.freeCall);
        _freeCall.setText(freeCall);

        _phone.setOnClickListener(v -> {
            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        Integer.parseInt("+383"));
            } else {
                String phone_dial = _phone.getText().toString().replaceAll(" ", "");
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_dial));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }

        });

        _phone_.setOnClickListener(v -> {
            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        Integer.parseInt("+383"));
            } else {
                String phone_dial = _phone.getText().toString().replaceAll(" ", "");
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_dial));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }

        });
        _freeCall.setOnClickListener(v -> {
            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        Integer.parseInt("+383"));
            } else {
                String phone_dial = _phone.getText().toString().replaceAll(" ", "");
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_dial));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }

        });
        return view;
    }

    public static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView bmImage;

        public LoadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap myImage = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                myImage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return myImage;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}