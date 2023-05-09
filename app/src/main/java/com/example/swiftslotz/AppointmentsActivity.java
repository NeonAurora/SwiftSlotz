package com.example.swiftslotz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity implements AppointmentsAdapter.OnAppointmentInteractionListener {

    private List<Appointment> appointments;
    private AppointmentsAdapter appointmentsAdapter;
    private RecyclerView appointmentsRecyclerView;
    private AppointmentManager appointmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        appointments = new ArrayList<>();
        appointmentsAdapter = new AppointmentsAdapter(appointments, this);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentsRecyclerView.setAdapter(appointmentsAdapter);

        appointmentManager = new AppointmentManager(this, appointments, appointmentsAdapter);

        FloatingActionButton appointmentButton = findViewById(R.id.addAppointmentButton);
        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppointmentsActivity.this, AddAppointmentActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        appointmentManager.fetchDataFromDatabase();
    }

    @Override
    public void onEditAppointment(Appointment appointment) {
        startModifyAppointmentActivity(appointment.getId(), appointment.getTitle(), appointment.getDate(), appointment.getTime());
    }

    @Override
    public void onDeleteAppointment(Appointment appointment) {
        //appointmentManager.deleteAppointment(appointment);
    }

    public void startModifyAppointmentActivity(int appointmentId, String title, String date, String time) {
        Intent intent = new Intent(this, ModifyAppointmentActivity.class);
        intent.putExtra("appointment_id", appointmentId);
        intent.putExtra("appointment_title", title);
        intent.putExtra("appointment_date", date);
        intent.putExtra("appointment_time", time);
        startActivity(intent);
    }
}
