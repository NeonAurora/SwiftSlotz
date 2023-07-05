package com.example.swiftslotz.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.swiftslotz.utilities.BaseActivity;
import com.example.swiftslotz.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends BaseActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        mAuth.signOut();
        Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
