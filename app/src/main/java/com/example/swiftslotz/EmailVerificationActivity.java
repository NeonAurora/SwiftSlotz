package com.example.swiftslotz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends BaseActivity {
    private Button verifyEmailButton, resendEmailButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        mAuth = FirebaseAuth.getInstance();

        resendEmailButton = findViewById(R.id.buttonResendEmail);



        resendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                user.sendEmailVerification()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(EmailVerificationActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EmailVerificationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
