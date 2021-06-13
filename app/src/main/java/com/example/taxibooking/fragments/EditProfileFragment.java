package com.example.taxibooking.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.taxiBooking.R;
import com.example.taxibooking.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class EditProfileFragment extends Fragment{

    private TextInputLayout username;
    private TextInputLayout email;
    private TextInputLayout phone;
    private TextInputLayout gender;
    private TextInputLayout birthDay;
    private AlertDialog alertDialog;

    public EditProfileFragment() {
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
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        username = v.findViewById(R.id.edit_username);
        email = v.findViewById(R.id.edit_email);
        phone = v.findViewById(R.id.edit_phone);
        gender = v.findViewById(R.id.edit_gender);
        birthDay = v.findViewById(R.id.edit_birthDate);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//        Log.v("LogID", uid);

        DatabaseReference databaseReference = database.getReference().child("User").child(uid);
//        Log.v("UserKey", String.valueOf(databaseReference));


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    Objects.requireNonNull(username.getEditText()).setText(user.getFullName());
                    Objects.requireNonNull(email.getEditText()).setText(user.getEmail());
                    Objects.requireNonNull(phone.getEditText()).setText(user.getPhone());
                    Objects.requireNonNull(gender.getEditText()).setText(user.getGender());
                    Objects.requireNonNull(birthDay.getEditText()).setText(user.getDateOfBirth());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        MaterialButton saveBtn = v.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(task -> {

            databaseReference.child("fullName").setValue(Objects.requireNonNull(username.getEditText()).getText().toString());
            databaseReference.child("email").setValue(Objects.requireNonNull(email.getEditText()).getText().toString());
            databaseReference.child("phone").setValue(Objects.requireNonNull(phone.getEditText()).getText().toString());
            databaseReference.child("gender").setValue(Objects.requireNonNull(gender.getEditText()).getText().toString());
            databaseReference.child("dateOfBirth").setValue(Objects.requireNonNull(birthDay.getEditText()).getText().toString());

            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_up,R.anim.slide_out_up).remove(this).commit();

        });


        return v;
    }


}

