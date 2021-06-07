package com.example.taxibooking.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taxiBooking.R;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText email_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Button resetBtn = findViewById(R.id.reset_psw_btn);
        resetBtn.setOnClickListener(v -> sendResetPasswordLink());


        LinearLayout signUpLinearLayout = findViewById(R.id.sign_up);
        signUpLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

    }

    void sendResetPasswordLink() {
        email_link = findViewById(R.id.edit_email_chp);
        String email = email_link.getText().toString();
        if (validate(email)) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Reset password link send successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LogInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }).addOnFailureListener(task -> Toast.makeText(this, "Failed to send reset password email link: " + task.getMessage(), Toast.LENGTH_SHORT).show());
        }

    }

    private boolean validate(String email) {
        if (email.isEmpty()) {
            email_link.setError("Please enter your email to continue!");
            email_link.requestFocus();
            return false;
        }
        return true;
    }

}