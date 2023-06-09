package com.example.swiftslotz;

import android.content.Intent;
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
