package com.example.swiftslotz.utilities;

import android.app.Application;
import com.google.firebase.auth.FirebaseAuth;

public class MyApp extends Application {

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        setupAuthStateListener();
    }

    private void setupAuthStateListener() {
        authStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                // User is signed in
                AppointmentStatusManager.getInstance(this).start();
            } else {
                // User is signed out
                AppointmentStatusManager.getInstance(this).stop();
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == Application.TRIM_MEMORY_UI_HIDDEN) {
            // App is going into the background
            AppointmentStatusManager.getInstance(this).stop();
        }
    }

    @Override
    public void onTerminate() {
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
        super.onTerminate();
    }
}
