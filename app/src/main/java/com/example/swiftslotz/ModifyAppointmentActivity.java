package com.example.swiftslotz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ModifyAppointmentActivity extends AppCompatActivity {

    private EditText appointmentTitleEditText;
    private EditText appointmentDateEditText;
    private EditText appointmentTimeEditText;
    private EditText appointmentDetailsEditText;
    private Button updateAppointmentButton;
    private AppointmentManager appointmentManager;
    private int appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_appointment);

        appointmentTitleEditText = findViewById(R.id.modify_appointment_title);
        appointmentDateEditText = findViewById(R.id.modify_appointment_date);
        appointmentTimeEditText = findViewById(R.id.modify_appointment_time);
        appointmentDetailsEditText = findViewById(R.id.modify_appointment_details);
        updateAppointmentButton = findViewById(R.id.update_appointment_button);

        appointmentManager = new AppointmentManager(this);

        Intent intent = getIntent();
        appointmentId = intent.getIntExtra("appointment_id", -1);
        String title = intent.getStringExtra("appointment_title");
        String date = intent.getStringExtra("appointment_date");
        String time = intent.getStringExtra("appointment_time");
        String details = intent.getStringExtra("appointment_details");

        appointmentTitleEditText.setText(title);
        appointmentDateEditText.setText(date);
        appointmentTimeEditText.setText(time);
        appointmentDetailsEditText.setText(details);

        updateAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = appointmentTitleEditText.getText().toString();
                String newDate = appointmentDateEditText.getText().toString();
                String newTime = appointmentTimeEditText.getText().toString();
                String newDetails = appointmentDetailsEditText.getText().toString();
                Appointment updatedAppointment = new Appointment(appointmentId, newTitle, newDate, newTime, details);
                appointmentManager.updateAppointment(updatedAppointment);
                finish();
            }
        });
    }
}
