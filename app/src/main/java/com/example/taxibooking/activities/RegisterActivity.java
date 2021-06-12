package com.example.taxibooking.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taxiBooking.R;
import com.example.taxibooking.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText registerEmail;
    EditText registerPassword;
    EditText fullName;
    EditText confirmPassword;
    CheckBox termsAndCondition;
    Button signUpBtn;
    LinearLayout alreadyHaveAnAccount;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signUpBtn = findViewById(R.id.sign_up_btn);

        signUpBtn.setOnClickListener(v -> createAccount());

        alreadyHaveAnAccount = findViewById(R.id.tv_sign_in);

        alreadyHaveAnAccount.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            finish();
        });
    }

    @SuppressLint("ResourceType")
    private void createAccount() {
        fullName = findViewById(R.id.fullname_register);
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        confirmPassword = findViewById(R.id.register_confirm_password);

        String name = fullName.getText().toString();
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();
        String conPassword = confirmPassword.getText().toString();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("User");
        mAuth = FirebaseAuth.getInstance();


        User user = new User(name,email,password,null,null,null);
        String keyID = databaseReference.push().getKey();
        databaseReference.child(keyID).setValue(user);

        if (validate(name, email, password, conPassword)) {

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(this, "Verify your email to continue!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            });

                        } else {
                            Toast.makeText(this, "Couldn't create account!", Toast.LENGTH_SHORT).show();

                        }
                    }
            );
        }


    }

    private boolean validate(String name, String email, String password, String conPassword) {
        if (name.isEmpty()) {
            fullName.setError("Enter your Name");
            fullName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            registerEmail.setError("john.smith@example.com");
            registerEmail.requestFocus();
            return false;
        }


        if (password.isEmpty()) {
            registerPassword.setError("Enter Password");
            registerPassword.requestFocus();
            return false;
        }

        if (conPassword.isEmpty()) {
            confirmPassword.setError("Enter Password");
            confirmPassword.requestFocus();
            return false;
        }

        if (!conPassword.equals(password)) {
            confirmPassword.setError("Those passwords didn't match. Try again.");
            confirmPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            registerPassword.setError("Password must be at least 6 characters long!");
            registerPassword.requestFocus();
            return false;
        }

        termsAndCondition = findViewById(R.id.terms_and_conditions);

        if (!termsAndCondition.isChecked()) {
            termsAndCondition.setError("Agree to Terms & Conditions to continue!");
            termsAndCondition.requestFocus();
            return false;
        }
        return true;
    }

}