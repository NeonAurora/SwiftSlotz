package com.example.swiftslotz.fragments.sidebarFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.adapters.AppointmentsAdapter;
import com.example.swiftslotz.fragments.pageFragments.ModifyAppointmentFragment;
import com.example.swiftslotz.fragments.pageFragments.RequestedAppointmentsFragment;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.utilities.AppointmentStatusManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AllAppointmentsFragment extends Fragment implements AppointmentsAdapter.OnAppointmentInteractionListener, AppointmentManager.OnAppointmentsFetchedListener, AppointmentManager.AppointmentUpdateListener {

    private List<Appointment> appointments;
    private AppointmentsAdapter appointmentsAdapter;
    private RecyclerView allAppointmentsRecyclerView;
    private AppointmentManager appointmentManager;
    private TextView badgeTextView, tvCountdownTimer;
    private CountDownTimer countDownTimer;
    private Handler progressUpdateHandler = new Handler(Looper.getMainLooper());
    private Runnable progressUpdateRunnable;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            appointmentManager = new AppointmentManager(getContext(), appointments, appointmentsAdapter);
            appointmentManager.setOnAppointmentsFetchedListener(this);
            appointmentManager.setAppointmentUpdateListener(this);
//            appointmentManager.checkListener();

            AppointmentStatusManager appointmentStatusManager = AppointmentStatusManager.getInstance(getContext());
            appointmentStatusManager.setAppointmentUpdateListener(this);
        } else {
            Log.e("AppointmentsFragment", "Context is null");
        }




        progressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateAppointmentProgress(appointments);
                progressUpdateHandler.postDelayed(this, 2000); // Schedule the next execution
            }
        };
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        mAuth = FirebaseAuth.getInstance();
        setupViews(view);
        setupRecyclerView(view);
        setupAppointmentFetching();
        return view;
    }

    private void setupViews(View view) {
        badgeTextView = view.findViewById(R.id.badge_text_view);
        tvCountdownTimer = view.findViewById(R.id.tvCountdownTimer);
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.silverballbold);
        tvCountdownTimer.setTypeface(typeface);
    }

    private void setupRecyclerView(View view) {
        allAppointmentsRecyclerView = view.findViewById(R.id.allAppointmentsRecyclerView);
        appointments = new ArrayList<>();
        allAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        appointmentsAdapter = new AppointmentsAdapter(appointments, this, appointmentManager, getContext(), allAppointmentsRecyclerView);
        allAppointmentsRecyclerView.setAdapter(appointmentsAdapter);
    }



    private void setupAppointmentFetching() {
        appointmentManager.fetchCountOfRequestedAppointments(new AppointmentManager.RequestedAppointmentCountCallback() {
            @Override
            public void onFetched(int count) {
                updateBadge(count);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error fetching count of requested appointments: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBadge(int count) {
        if (badgeTextView != null) {
            badgeTextView.setText(String.valueOf(count));
            badgeTextView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (appointments != null) {
//            appointments.clear();
//        }
        appointmentManager.fetchAppointmentsFromDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        boolean refreshNeeded = prefs.getBoolean("refreshNeeded", false);
        if (refreshNeeded) {
            appointmentManager.fetchAppointmentsFromDatabase();
            prefs.edit().remove("refreshNeeded").apply();
        }

        progressUpdateHandler.post(progressUpdateRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        progressUpdateHandler.removeCallbacks(progressUpdateRunnable);
    }

    // Other overridden methods and custom methods (onEditAppointment, onDeleteAppointment, etc.)

    @Override
    public void onEditAppointment(Appointment appointment) {
        startModifyAppointmentFragment(appointment.getId(), appointment.getTitle(), appointment.getDate(), appointment.getTime(), appointment.getDetails(), appointment.getKey());
    }

    @Override
    public void onLeaveAppointment(Appointment appointment, int position) {
        appointmentManager.leaveAppointment(appointment, position);
    }

    public void onDeleteAppointment(String appointmentKey) {
        appointmentManager.deleteAppointment(appointmentKey);
    }

    @Override
    public void onAppointmentsFetched(List<Appointment> fetchedAppointments) {
        appointments.clear();
        appointments.addAll(fetchedAppointments);
        sortAndDisplayAppointments(fetchedAppointments);
        appointmentsAdapter.notifyDataSetChanged();
    }

    private void updateAppointmentProgress(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            long timeToStart = calculateTimeToStart(appointment);
            appointment.setTimeToStart(timeToStart);

            if (timeToStart <= 0) {
                // Once the appointment starts, hide the linear progress bar and show the circular progress bar
                appointment.setLinearProgressVisible(false);
                long elapsedTimeSinceStart = Math.abs(timeToStart); // Convert to positive value
                int duration = appointment.getDuration(); // Total duration in minutes
                long totalDurationInSeconds = duration * 60; //convert to seconds
                if (timeToStart == 0) {
                    playSound(R.raw.ding_dong);
                }

                if ((totalDurationInSeconds - elapsedTimeSinceStart) == 0) {
                    playSound(R.raw.interface_hint);
                }
                int circularProgressPercentage = (int) ((elapsedTimeSinceStart * 100) / totalDurationInSeconds);
                appointment.setCircularProgressPercentage(circularProgressPercentage);
                appointment.setCircularProgressVisible(true);
            } else {
                // Before the appointment starts, only show the linear progress bar
//                if (timeToStart == 0) {
//                    playSound(R.raw.ding_dong);
//                }
                long totalDuration = getTotalDuration(appointment);
                int linearProgressPercentage = calculateProgressPercentage(timeToStart, totalDuration);
                appointment.setLinearProgressPercentage(linearProgressPercentage);
                appointment.setLinearProgressVisible(true);
                appointment.setCircularProgressVisible(false);
            }
        }
        appointmentsAdapter.notifyDataSetChanged();
    }

    private void playSound(int soundResourceId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), soundResourceId);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

    private void sortAndDisplayAppointments(List<Appointment> fetchedAppointments) {
        // Calculate time to start for each appointment and sort
        List<Appointment> sortedAppointments = new ArrayList<>(fetchedAppointments);
        for (Appointment appointment : sortedAppointments) {
            appointment.setTimeToStart(calculateTimeToStart(appointment));
        }
        Collections.sort(sortedAppointments, (a1, a2) -> Long.compare(a1.getTimeToStart(), a2.getTimeToStart()));

        // Update the adapter
        appointments.clear();
        appointments.addAll(sortedAppointments);
        appointmentsAdapter.notifyDataSetChanged();

        if (!sortedAppointments.isEmpty()) {
            startCountdownForClosestAppointment(sortedAppointments.get(0));
        }
        updateAppointmentProgress(sortedAppointments);
    }

    private void startCountdownForClosestAppointment(Appointment closestAppointment) {
        long timeToStart = closestAppointment.getTimeToStart();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeToStart * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountdownTimer.setText(formatMillis(millisUntilFinished));
            }

            public void onFinish() {
                tvCountdownTimer.setText("Appointment Running!");
            }
        }.start();
    }

    private String formatMillis(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        return String.format(Locale.getDefault(), "%02d :%02d :%02d", hours, minutes, seconds);
    }

    private int calculateProgressPercentage(long timeToStart, long totalDuration) {
        long timeElapsed = totalDuration - timeToStart;
        return (int) ((timeElapsed * 100) / totalDuration);
    }

    // Other methods for calculating timeToStart, formatting millis, etc.
    private long getTotalDuration(Appointment appointment) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date appointmentDateTime = sdf.parse(appointment.getDate() + " " + appointment.getTime());
            long creationTimestamp = appointment.getCreationTimestamp(); // This should be in milliseconds

            if (appointmentDateTime != null) {
                // The total duration is the difference between the appointment start time and its creation time
                long totalDurationInMillis = appointmentDateTime.getTime() - creationTimestamp;
                return Math.max(totalDurationInMillis / 1000, 0); // Convert to seconds and ensure it's not negative
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if parsing fails or if the appointment date is in the past
    }


    private long calculateTimeToStart(Appointment appointment) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date appointmentDate = sdf.parse(appointment.getDate() + " " + appointment.getTime());
            if (appointmentDate != null) {
                long diffInMillis = appointmentDate.getTime() - System.currentTimeMillis();
                return diffInMillis / 1000; // Convert milliseconds to seconds
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Long.MAX_VALUE; // Return a large value if parsing fails
    }

    @Override
    public void onAppointmentsUpdated() {
//        refreshAppointmentsList();
    }
    @Override
    public void onAppointmentExpired(Appointment appointment) {
        removeExpiredAppointment(appointment.getKey());
    }

    private void removeExpiredAppointment(String appointmentKey) {
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getKey().equals(appointmentKey)) {
//                appointments.remove(i);
//                appointmentsAdapter.notifyItemRemoved(i);
                appointmentsAdapter.removeItemWithAnimation(i);
                return;
            }
        }
    }

    public void refreshAppointmentsList() {
        if (appointmentManager != null) {
            appointmentManager.fetchAppointmentsFromDatabase();
            appointmentsAdapter.notifyDataSetChanged();

            Log.e("AppointmentsFragment", "Appointments list refreshed");
        } else {
            Log.e("AppointmentsFragment", "AppointmentManager is null");
        }
    }


    public void startModifyAppointmentFragment(int appointmentId, String title, String date, String time, String details, String key) {
        ModifyAppointmentFragment modifyAppointmentFragment = new ModifyAppointmentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("appointment_id", appointmentId);
        bundle.putString("appointment_title", title);
        bundle.putString("appointment_date", date);
        bundle.putString("appointment_time", time);
        bundle.putString("appointment_details", details);
        bundle.putString("appointment_key", key);
        modifyAppointmentFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, modifyAppointmentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


