package com.example.swiftslotz.utilities;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.dependency_injection.DatabaseService;
import com.example.swiftslotz.views.charts.CustomPieChart;
import com.example.swiftslotz.views.charts.Sector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentManager {
    private List<Appointment> appointments;
    private AppointmentsAdapter appointmentsAdapter;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference userDb;
    private List<Sector> sectors = new ArrayList<>();
    private CustomPieChart customPieChart;
    private DatabaseService databaseService;

    public AppointmentManager(Context context, List<Appointment> appointments, AppointmentsAdapter appointmentsAdapter) {
        this.context = context;
        this.appointments = appointments;
        this.appointmentsAdapter = appointmentsAdapter;
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId).child("appointments");
    }

    public AppointmentManager(Context context) {
        this.context = context;
        this.appointments = new ArrayList<>();
        this.sectors = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId).child("appointments");
    }

    public AppointmentManager(Context context, DatabaseReference userDb) {
        this.context = context;
        this.appointments = new ArrayList<>();
        this.sectors = new ArrayList<>();
        this.userDb = userDb;
        mAuth = FirebaseAuth.getInstance();
    }

    public AppointmentManager(Context context, List<Appointment> appointments, AppointmentsAdapter appointmentsAdapter, FirebaseAuth auth, DatabaseService databaseService) {
        this.mAuth = auth;
        this.context = context;
        this.appointments = appointments;
        this.appointmentsAdapter = appointmentsAdapter;
        this.databaseService = databaseService;  // Initialize the databaseService here.
    }


    public void setCustomPieChart(CustomPieChart customPieChart) {
        this.customPieChart = customPieChart;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public void fetchDataFromDatabase() {
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointments.clear();
                sectors.clear();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today = sdf.format(new Date());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null && appointment.getDate().equals(today)) {
                        appointment.setKey(snapshot.getKey());
                        appointments.add(appointment);

                        // Convert the appointment to a sector and add it to the list
                        Sector sector = AppointmentManager.this.appointmentToSector(appointment);
                        sectors.add(sector);
                    }
                }
                if (appointmentsAdapter != null) {
                    appointmentsAdapter.notifyDataSetChanged();
                }
                if (customPieChart != null) {
                    customPieChart.setSectors(sectors);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public String addAppointment(Appointment appointment) throws Exception {
        return databaseService.addAppointment(appointment);
    }



    public void updateAppointment(Appointment appointment) {
        if (appointment.getKey() != null) {
            userDb.child(appointment.getKey()).setValue(appointment)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Appointment updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to update appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(context, "Failed to update appointment: Appointment key not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAppointment(Appointment appointment) {
        if (appointment.getKey() != null) {
            userDb.child(appointment.getKey()).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Appointment deleted successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private Sector appointmentToSector(Appointment appointment) {
        // Parse the appointment time into hours and minutes.
        String[] timeParts = appointment.getTime().split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);

        // Calculate the start angle and sweep angle in degrees.
        float startAngle = (hours * 60 + minutes) / 2f;
        float sweepAngle = appointment.getDuration() / 2f;

        startAngle -= 90;

        // Use a default color for now. You can change this to use different colors for different appointments.
        int color = Color.RED;

        String title = appointment.getTitle();
        String time = appointment.getTime();
        // Create and return the new Sector object.
        return new Sector(startAngle, sweepAngle, color, title, time);
    }

}
