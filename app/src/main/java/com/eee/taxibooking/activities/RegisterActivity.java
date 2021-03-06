package com.eee.taxibooking.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eee.taxibooking.R;
import com.eee.taxibooking.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmail;
    private EditText registerPassword;
    private EditText fullName;
    private EditText confirmPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    public static final String MY_PREFS_NAME = "PREFS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button signUpBtn = findViewById(R.id.sign_up_btn);

        signUpBtn.setOnClickListener(v -> createAccount());

        LinearLayout alreadyHaveAnAccount = findViewById(R.id.tv_sign_in);

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

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        User user = new User(name, email, "tag", "tag", "tag");
        if (validate(name, email, password, conPassword)) {

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            databaseReference.child("User").child(currentUserID).setValue(user);
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                    editor.putString("email", email);
                                    editor.apply();

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

        CheckBox termsAndCondition = findViewById(R.id.terms_and_conditions);

        if (!termsAndCondition.isChecked()) {
            termsAndCondition.setError("Agree to Terms & Conditions to continue!");
            termsAndCondition.requestFocus();
            return false;
        }
        return true;
    }
}