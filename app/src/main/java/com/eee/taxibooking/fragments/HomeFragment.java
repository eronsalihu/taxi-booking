package com.eee.taxibooking.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.eee.taxibooking.R;
import com.eee.taxibooking.activities.LogInActivity;
import com.eee.taxibooking.models.TaxiCluster;
import com.eee.taxibooking.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DrawerLayout drawerLayout;
    private SearchView searchView;
    private CircleImageView profileImage;
    private TextView name;
    private TextView email;
    private ClusterManager<TaxiCluster> clusterManager;

    public HomeFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        SupportMapFragment mapFragment1 = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment1.getMapAsync(this);

        FloatingActionButton floatingActionButton = v.findViewById(R.id.fabBtn);
        floatingActionButton.setOnClickListener(task -> {
            int intent = getActivity().getIntent().getIntExtra("Place Number", 0);
            if (intent == 0) {

                // Zoom into users location
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        centreMapOnLocation(location, 18);
                        locationManager.removeUpdates(this);
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centreMapOnLocation(lastKnownLocation, 18);
                } else {

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });

        drawerLayout = v.findViewById(R.id.drawer_layout);
        NavigationView navigationView = v.findViewById(R.id.navigationView);
        View header = navigationView.getHeaderView(0);
        navigationView.bringToFront();
        profileImage = header.findViewById(R.id.profile_image_header);
        email = header.findViewById(R.id.user_profile_email_header);
        name = header.findViewById(R.id.user_profile_name_header);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//        Log.v("LogID", uid);

        DatabaseReference databaseReference = database.getReference().child("User").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                assert user != null;
                name.setText(user.getFullName());
                email.setText(user.getEmail());
                FirebaseUser _user = FirebaseAuth.getInstance().getCurrentUser();
                if (_user != null) {
                    if (_user.getPhotoUrl() != null) {
                        Glide.with(getActivity())
                                .load(_user.getPhotoUrl())
                                .into(profileImage);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.logOut) {
                FirebaseAuth.getInstance().signOut();
                AuthUI.getInstance().signOut(getContext());
                sentUserToLoginUi();
            }
            if (id == R.id.viewProfile) {

                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.profile);

            }
            return true;
        });


        FloatingActionButton menuBtn = v.findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(task -> drawerLayout.openDrawer(Gravity.START));


        searchView = v.findViewById(R.id.searchView);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                // below line is to create a list of address
                // where we will store the list of all address.
                List<Address> addressList = null;

                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(getActivity());
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    Address address = addressList.get(0);

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // on below line we are adding marker to that position.
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                    // below line is to animate camera to that position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);

        return v;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.style_json));

        clusterManager = new ClusterManager<>(getContext(), mMap);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        setUpCluster();
        clusterManager.cluster();

        mMap.setOnMapClickListener(latLng -> {
            MarkerOptions markerOptions = new MarkerOptions();

            // Setting the position for the marker
            markerOptions.position(latLng);

            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title(latLng.latitude + " : " + latLng.longitude);

            // Clears the previously touched position
//                googleMap.clear();

            // Animating to the touched position
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Placing a marker on the touched position
//                googleMap.addMarker(markerOptions);

        });

//        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                googleMap.clear();
//
//            }
//        });

        int intent = getActivity().getIntent().getIntExtra("Place Number", 0);
        if (intent == 0) {

            // Zoom into users location
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centreMapOnLocation(location, 17);
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(lastKnownLocation, 15);
            } else {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(lastKnownLocation, 17);

            }
        }
    }

    public void centreMapOnLocation(Location location, float zoom) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(cameraUpdate);
    }

    private void sentUserToLoginUi() {

        Intent intent = new Intent(getActivity(), LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

    private void setUpCluster() {
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<>(getContext(), mMap);
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        for (double i = 42.2171; i < 42.8914; i += 0.3) {
            for (double j = 20.4285; j < 21.1655; j += 0.3) {
                TaxiCluster cluster = new TaxiCluster(i, j, "Title " + i, "Snippet " + i);
                clusterManager.addItem(cluster);

            }
        }

        for (double i = 42.5389; i < 42.6549; i += 0.1) {
            for (double j = 21.3914; j < 21.6193; j += 0.1) {
                TaxiCluster cluster = new TaxiCluster(i, j, "Title " + i, "Snippet " + i);
                clusterManager.addItem(cluster);

            }
        }
        for (double i = 42.4892; i < 42.6121; i += 0.2) {
            for (double j = 21.1148; j < 21.2052; j += 0.2) {
                TaxiCluster cluster = new TaxiCluster(i, j, "Title " + i, "Snippet " + i);
                clusterManager.addItem(cluster);

            }
        }

        for (double i = 42.3141; i < 42.4242; i += 0.02) {
            for (double j = 21.0485; j < 21.2338; j += 0.03) {
                TaxiCluster cluster = new TaxiCluster(i, j, "Title " + i, "Snippet " + i);
                clusterManager.addItem(cluster);

            }
        }

        for (double i = 42.6377; i < 42.6737; i += 0.03) {
            for (double j = 21.0165; j < 21.2423; j += 0.03) {
                TaxiCluster cluster = new TaxiCluster(i, j, "Title " + i, "Snippet " + i);
                clusterManager.addItem(cluster);

            }
        }

        for (double i = 42.3943; i < 42.7567; i += 0.3) {
            for (double j = 20.8504; j < 21.2274; j += 0.3) {
                TaxiCluster cluster = new TaxiCluster(i, j, "Title " + i, "Snippet " + i);
                clusterManager.addItem(cluster);

            }
        }

        for (double i = 42.5987; i < 43.1480; i += 0.3) {
            for (double j = 20.1297; j < 20.8376; j += 0.3) {
                TaxiCluster cluster = new TaxiCluster(i, j, "Title " + i, "Snippet " + i);
                clusterManager.addItem(cluster);

            }
        }

        for (double i = 42.3560; i < 42.8498; i += 0.3) {
            for (double j = 20.5309; j < 21.2291; j += 0.3) {
                TaxiCluster cluster = new TaxiCluster(i, j, "Title " + i, "Snippet " + i);
                clusterManager.addItem(cluster);

            }
        }

        for (double i = 42.4090; i < 42.6586; i += 0.3) {
            for (double j = 21.2771; j < 21.6299; j += 0.3) {
                TaxiCluster cluster = new TaxiCluster(i, j, "Title " + i, "Snippet " + i);
                clusterManager.addItem(cluster);

            }
        }

        for (double i = 42.4719; i < 42.5554; i += 0.3) {
            for (double j = 20.9615; j < 21.4027; j += 0.3) {
                TaxiCluster cluster = new TaxiCluster(i, j, "Title " + i, "Snippet " + i);
                clusterManager.addItem(cluster);

            }
        }
    }

}
