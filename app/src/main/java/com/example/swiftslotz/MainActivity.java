package com.example.swiftslotz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.swiftslotz.activities.LoginActivity;
import com.example.swiftslotz.utilities.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            startActivity(new Intent(MainActivity.this, BaseActivity.class));
            finish();
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}

