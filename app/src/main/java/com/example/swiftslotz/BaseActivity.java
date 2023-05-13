package com.example.swiftslotz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String fragmentToLoad = preferences.getString("fragmentToLoad", "");

        if (fragmentToLoad.equals("AppointmentsFragment")) {
            // Load AppointmentsFragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new AppointmentsFragment());
            transaction.commit();

            // Clear fragmentToLoad from SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("fragmentToLoad");
            editor.apply();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.action_page1:
                        selectedFragment = new Page1Fragment();
                        break;
                    case R.id.action_page2:
                        selectedFragment = new Page2Fragment();
                        break;
                    // Handle more items as needed
                }

                if (selectedFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, selectedFragment);
                    transaction.commit();
                }

                return true;
            }
        });

        // Hide the title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle action bar item clicks here.
        if (id == R.id.action_logout) {
            // Handle logout
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
