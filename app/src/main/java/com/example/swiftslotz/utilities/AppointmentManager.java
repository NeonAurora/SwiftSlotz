package com.example.swiftslotz.utilities;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.swiftslotz.BuildConfig;
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
    private RequestedAppointmentsAdapter requestedAppointmentsAdapter;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference userDb;
    private List<Sector> sectors = new ArrayList<>();
    private CustomPieChart customPieChart;

    public AppointmentManager(Context context, List<Appointment> appointments, AppointmentsAdapter appointmentsAdapter) {
        this.context = context;
        this.appointments = appointments;
        this.appointmentsAdapter = appointmentsAdapter;
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId).child("appointments");
    }

    public AppointmentManager(Context context, List<Appointment> requestedAppointments, RequestedAppointmentsAdapter requestedAppointmentsAdapter) {
        this.context = context;
        this.appointments = requestedAppointments;
        this.requestedAppointmentsAdapter = requestedAppointmentsAdapter;
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId).child("RequestedAppointments");
    }

    public AppointmentManager(Context context) {
        this.context = context;
        this.appointments = new ArrayList<>();
        this.sectors = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId).child("appointments");
    }


    public void setCustomPieChart(CustomPieChart customPieChart) {
        this.customPieChart = customPieChart;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public void fetchAppointmentsFromDatabase() {
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

    public void fetchRequestedAppointmentsFromDatabase() {
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
                    }
                }
                String isFlag;
                if (requestedAppointmentsAdapter != null) {
                    requestedAppointmentsAdapter.notifyDataSetChanged();
                    isFlag="true";
                } else {
                    isFlag = "False";
                }
                Log.e("ISFLAG", isFlag);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void addAppointmentRequest(Appointment appointment, String firebaseKey) {
        DatabaseReference specificUserDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                .getReference("users")
                .child(firebaseKey)
                .child("RequestedAppointments");

        String key = specificUserDb.push().getKey();
        if (key != null) {
            appointment.setRequestingUserFirebaseKey(mAuth.getCurrentUser().getUid());

            specificUserDb.child(key).setValue(appointment)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Appointment added successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to add appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
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

    public void getClientNameFromKey(String firebaseKey, ClientNameCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(firebaseKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String clientName = dataSnapshot.child("username").getValue(String.class);
                    if (clientName != null) {
                        callback.onClientNameReceived(clientName);
                        Log.e("Client Name", clientName);
                    } else {
                        callback.onError("Client name is null");
                    }
                } else {
                    callback.onError("DataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
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
