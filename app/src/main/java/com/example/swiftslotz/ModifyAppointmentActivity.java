package com.example.swiftslotz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ModifyAppointmentActivity extends BaseActivity {

    private EditText appointmentTitleEditText;
    private EditText appointmentDateEditText;
    private EditText appointmentTimeEditText;
    private EditText appointmentDetailsEditText;
    private Button updateAppointmentButton;
    private AppointmentManager appointmentManager;
    private int appointmentId;

    private String  appointmentKey;

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
        appointmentKey = intent.getStringExtra("appointment_key");
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
                Appointment updatedAppointment = new Appointment( newTitle, newDate, newTime, newDetails);
                updatedAppointment.setKey(appointmentKey); // Add this line
                appointmentManager.updateAppointment(updatedAppointment);
                finish();
            }
        });
    }
}
