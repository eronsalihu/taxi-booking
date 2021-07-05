package com.eee.taxibooking.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    SupportMapFragment mapFragment;
    LocationManager locationManager;
    LocationListener locationListener;
    FloatingActionButton floatingActionButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FloatingActionButton menuBtn;
    SearchView searchView;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private CircleImageView profileImage;
    private TextView name;
    private TextView email;
    private StorageReference storageReference;
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
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        floatingActionButton = v.findViewById(R.id.fabBtn);
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
        navigationView = v.findViewById(R.id.navigationView);
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
                        Glide.with(getContext())
                                .load(_user.getPhotoUrl())
                                .into(profileImage);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.logOut) {
                    FirebaseAuth.getInstance().signOut();
                    AuthUI.getInstance().signOut(getContext());
                    sentUserToLoginUi();
                }
                ProfileFragment profileFragment = new ProfileFragment();
                if (id == R.id.viewProfile) {
                    NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.profile);

                }
                return true;
            }
        });


        menuBtn = v.findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(task -> {
            drawerLayout.openDrawer(Gravity.START);
        });


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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        setUpClusterer();
        clusterManager.cluster();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
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

            }
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

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void sentUserToLoginUi() {

        Intent intent = new Intent(getActivity(), LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

    private void setUpClusterer() {
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<TaxiCluster>(getContext(), mMap);
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = 42.6026;
        double lng = 20.9030;
        // Add ten cluster items in close proximity, for purposes of this example.
//        for (int i = 0; i < 10; i++) {
//            double offset = i / 60d;
//            lat = lat + offset;
//            lng = lng + offset;
//            TaxiCluster cluster_one = new TaxiCluster(42.3702+offset, 21.1483+offset, "Title " + i, "Snippet " + i);
//            TaxiCluster cluster_two = new TaxiCluster(42.6629+offset , 21.1655+offset, "Title " + i, "Snippet " + i);
//            TaxiCluster cluster_three = new TaxiCluster(42.2171+offset, 20.7436+offset, "Title " + i, "Snippet " + i);
//            TaxiCluster cluster_four = new TaxiCluster(42.3844+offset, 20.4285+offset, "Title " + i, "Snippet " + i);
//            TaxiCluster cluster_five = new TaxiCluster(42.8914+offset, 20.8660+offset, "Title " + i, "Snippet " + i);
//            TaxiCluster cluster_six = new TaxiCluster(42.6593+offset, 20.2887+offset, "Title " + i, "Snippet " + i);
//            TaxiCluster cluster_seven = new TaxiCluster(42.4635+offset, 21.4694+offset, "Title " + i, "Snippet " + i);
//
//            clusterManager.addItem(cluster_one) ;
//            clusterManager.addItem(cluster_two) ;
//            clusterManager.addItem(cluster_three);
//            clusterManager.addItem(cluster_four) ;
//            clusterManager.addItem(cluster_five) ;
//            clusterManager.addItem(cluster_six) ;
//            clusterManager.addItem(cluster_seven);
//
//            }
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
