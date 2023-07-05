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
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        Intent intent;
        if(isLoggedIn) {
            intent = new Intent(MainActivity.this, BaseActivity.class);
        } else {
            intent = new Intent(MainActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish(); // so this activity won't be included in the back stack
    }

}
