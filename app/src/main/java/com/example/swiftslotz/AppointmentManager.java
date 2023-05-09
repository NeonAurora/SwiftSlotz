package com.example.swiftslotz;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AppointmentManager {
    private DatabaseReference db;
    private List<Appointment> appointments;
    private AppointmentsAdapter appointmentsAdapter;
    private Context context;

    public AppointmentManager(Context context, List<Appointment> appointments, AppointmentsAdapter appointmentsAdapter) {
        this.context = context;
        this.appointments = appointments;
        this.appointmentsAdapter = appointmentsAdapter;
        db = FirebaseDatabase.getInstance("https://swiftslotz-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("appointments");
    }

    public AppointmentManager(Context context) {
        this.context = context;
        db = FirebaseDatabase.getInstance("https://swiftslotz-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("appointments");
    }

    public void fetchDataFromDatabase() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointments.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    appointments.add(appointment);
                }
                appointmentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addAppointment(Appointment appointment) {
        String key = db.push().getKey();
        if (key != null) {
            db.child(key).setValue(appointment);
        }
    }

    public void updateAppointment(Appointment appointment) {
        db.child(String.valueOf(appointment.getId())).setValue(appointment)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Appointment updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void deleteAppointment(Appointment appointment) {
        db.child(String.valueOf(appointment.getId())).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Appointment deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
