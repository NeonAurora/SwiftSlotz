package com.example.swiftslotz.fragments.pageFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.R;

public class ModifyAppointmentFragment extends Fragment {

    private EditText appointmentTitleEditText;
    private EditText appointmentDateEditText;
    private EditText appointmentTimeEditText;
    private EditText appointmentDetailsEditText;
    private Button updateAppointmentButton;
    private AppointmentManager appointmentManager;
    private int appointmentId;
    private String appointmentKey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modify_appointment, container, false);

        appointmentTitleEditText = view.findViewById(R.id.modify_appointment_title);
        appointmentDateEditText = view.findViewById(R.id.modify_appointment_date);
        appointmentTimeEditText = view.findViewById(R.id.modify_appointment_time);
        appointmentDetailsEditText = view.findViewById(R.id.modify_appointment_details);
        updateAppointmentButton = view.findViewById(R.id.update_appointment_button);

        appointmentManager = new AppointmentManager(getActivity());

        // Check if arguments are not null
        if(getArguments() != null){
            appointmentKey = getArguments().getString("appointment_key");
            String title = getArguments().getString("appointment_title");
            String date = getArguments().getString("appointment_date");
            String time = getArguments().getString("appointment_time");
            String details = getArguments().getString("appointment_details");

            appointmentTitleEditText.setText(title);
            appointmentDateEditText.setText(date);
            appointmentTimeEditText.setText(time);
            appointmentDetailsEditText.setText(details);
        }

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

                // Return to the previous fragment
                if(getActivity() != null){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }
}
