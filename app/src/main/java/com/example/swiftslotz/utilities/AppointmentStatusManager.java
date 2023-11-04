package com.example.swiftslotz.utilities;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AppointmentStatusManager {

    private static AppointmentStatusManager instance;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable statusChecker;
    private boolean isUserLoggedIn = false;
    private AppointmentManager appointmentManager;
    private final Context context;

    private AppointmentStatusManager(Context context) {
        this.context = context.getApplicationContext(); // Use application context to avoid memory leaks
        statusChecker = new Runnable() {
            @Override
            public void run() {
                if (isUserLoggedIn) {
                    // Your code to check and update appointment statuses
                    if (appointmentManager == null) {
                        initializeAppointmentManager();
                    }
                    appointmentManager.intervalCheck();
                    appointmentManager.fetchAppointmentsFromDatabase();
                    appointmentManager.checkAndUpdateAppointmentStatuses();
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void initializeAppointmentManager() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            appointmentManager = new AppointmentManager(context);
        }
    }

    public static synchronized AppointmentStatusManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppointmentStatusManager(context);
        }
        return instance;
    }

    public void start() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            isUserLoggedIn = true;
            initializeAppointmentManager(); // Initialize here when starting
            statusChecker.run();
        }
    }

    public void stop() {
        isUserLoggedIn = false;
        handler.removeCallbacks(statusChecker);
        appointmentManager = null; // Clear the appointment manager
    }
}
