package com.example.swiftslotz.utilities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.swiftslotz.R;
import com.example.swiftslotz.fragments.bottomBarFragments.ProfileFragment;
import com.example.swiftslotz.fragments.bottomBarFragments.ScheduleChartFragment;
import com.example.swiftslotz.fragments.bottomBarFragments.SearchFragment;
import com.example.swiftslotz.fragments.pageFragments.AppointmentsFragment;
import com.example.swiftslotz.fragments.sidebarFragments.Item1Fragment;
import com.example.swiftslotz.fragments.sidebarFragments.Item2Fragment;
import com.example.swiftslotz.fragments.sidebarFragments.Item3Fragment;
import com.example.swiftslotz.fragments.sidebarFragments.Item4Fragment;
import com.example.swiftslotz.fragments.sidebarFragments.Item5Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Set up the drawer toggle, which will display the hamburger icon and handle opening and closing the drawer.
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Show the hamburger icon.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the navigation view.
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                // The Fragment that will replace the current one.
                Fragment selectedFragment = null;

                switch (id) {
                    case R.id.drawer_item1:
                        // Replace with your actual fragments
                        selectedFragment = new Item1Fragment();
                        break;
                    case R.id.drawer_item2:
                        selectedFragment = new Item2Fragment();
                        break;
                    case R.id.drawer_item3:
                        selectedFragment = new Item3Fragment();
                        break;
                    case R.id.drawer_item4:
                        selectedFragment = new Item4Fragment();
                        break;
                    case R.id.drawer_item5:
                        selectedFragment = new Item5Fragment();
                        break;
                }

                if (selectedFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, selectedFragment);
                    transaction.commit();
                }

                // Close the drawer.
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

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
                        selectedFragment = new AppointmentsFragment();
                        break;
                    case R.id.action_page2:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.action_page3:
                        selectedFragment = new ScheduleChartFragment();
                        break;
//                    case R.id.action_page4:
//                        selectedFragment = new ProfileFragment();
//                        break;
                    //handle more items as needed
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
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }
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
        if (id == R.id.user_profile) {
            Fragment selectedFragment = new ProfileFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, selectedFragment);
            transaction.commit();
//            Intent intent = new Intent(this, LogoutActivity.class);
//            startActivity(intent);
//            return true;
        }

        // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event.
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
