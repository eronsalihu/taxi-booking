package com.eee.taxibooking.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.eee.taxibooking.R;
import com.eee.taxibooking.models.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private TextView username;
    private TextView user_email;
    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView gender;
    private TextView birthDay;
    private CircleImageView profileImage;
    private StorageReference storageReference;
    private LinearLayout camera;
    private LinearLayout storage_import;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;


    private static final int IMAGE_REQUEST = 2;

    public ProfileFragment() {
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
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

//        String userEmail = getActivity().getIntent().getStringExtra("email");

        profileImage = v.findViewById(R.id.profile_image);
        user_email = v.findViewById(R.id.user_profile_email);
        username = v.findViewById(R.id.tv_user_name);
        name = v.findViewById(R.id.user_profile_name);
        email = v.findViewById(R.id.tv_user_email);
        phone = v.findViewById(R.id.tv_user_phone);
        gender = v.findViewById(R.id.tv_user_gender);
        birthDay = v.findViewById(R.id.tv_user_dateOfBirth);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//        Log.v("LogID", uid);

        DatabaseReference databaseReference = database.getReference().child("User").child(uid);
//        Log.v("UserKey", String.valueOf(databaseReference));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.progress_dialog,null));
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    username.setText(user.getFullName());
                    user_email.setText(user.getEmail());
                    name.setText(user.getFullName().substring(0, 5));
                    email.setText(user.getEmail());
                    phone.setText(user.getPhone());
                    gender.setText(user.getGender());
                    birthDay.setText(user.getDateOfBirth());
                    alertDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        EditProfileFragment editProfileFragment = new EditProfileFragment();

        MaterialButton editBtn = v.findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(task -> {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
            transaction.replace(R.id.profile_fragment, editProfileFragment).addToBackStack(null).commit();


        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(profileImage);
            }
        }
        profileImage.setOnClickListener(task -> {
            final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
            dialog.setContentView(R.layout.bottom_sheet);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();

            camera = dialog.findViewById(R.id.camera_import);
            camera.setOnClickListener(v1 -> {
                handleImageClick();
                dialog.dismiss();

            });
            ;
            storage_import = dialog.findViewById(R.id.storage_import);
            storage_import.setOnClickListener(v1 -> {
                openImage();
                dialog.dismiss();
            });
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10001) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profileImage.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }

        if (requestCode == IMAGE_REQUEST && requestCode == RESULT_OK) {
            imageUri = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(getActivity().getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
                uploadImage();
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    //CAMERA UPLOAD
    public void handleImageClick() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, 10001);
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        storageReference = FirebaseStorage.getInstance().getReference()
                .child("profileImages").child(uid + ".jpeg");

        storageReference.putBytes(byteArrayOutputStream.toByteArray()).addOnSuccessListener(taskSnapshot -> getDownloadURL(storageReference));
    }

    private void getDownloadURL(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> setUserProfileURL(uri));
    }

    private void setUserProfileURL(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request).addOnSuccessListener(aVoid -> {

        });
    }

    //STORAGE UPLOAD
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private void uploadImage() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        storageReference = FirebaseStorage.getInstance().getReference()
                .child("profileImages").child(uid + ".jpeg");

        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> Log.d("IMAGE","Upload Successfully"));
    }
}