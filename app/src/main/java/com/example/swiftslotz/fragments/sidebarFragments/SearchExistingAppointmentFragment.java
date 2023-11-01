package com.example.swiftslotz.fragments.sidebarFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.swiftslotz.R;
import com.example.swiftslotz.fragments.pageFragments.JoinAppointmentFragment;

public class SearchExistingAppointmentFragment extends Fragment {

    public SearchExistingAppointmentFragment() {
        // Required empty public constructor
    }

    public static SearchExistingAppointmentFragment newInstance() {
        return new SearchExistingAppointmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_existing_appointment, container, false);

        // Initialize UI elements
        final EditText appointmentKeyInput = view.findViewById(R.id.appointment_key_input);
        Button findAppointmentButton = view.findViewById(R.id.find_appointment_button);

        // Handle button click
        findAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appointmentKey = appointmentKeyInput.getText().toString();
                // Navigate to JoinAppointmentFragment and pass the appointmentKey
                JoinAppointmentFragment joinAppointmentFragment = JoinAppointmentFragment.newInstance(appointmentKey);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, joinAppointmentFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
