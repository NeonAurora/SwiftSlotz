package com.example.swiftslotz.fragments.pageFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.fragments.sidebarFragments.SearchExistingAppointmentFragment;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.adapters.InvolvedUsersAdapter;

import java.util.ArrayList;

public class JoinAppointmentFragment extends Fragment {

    private String appointmentKey;
    private Appointment appointment;
    private RecyclerView involvedUsersRecyclerView;
    private InvolvedUsersAdapter involvedUsersAdapter;

    public static JoinAppointmentFragment newInstance(String appointmentKey) {
        JoinAppointmentFragment fragment = new JoinAppointmentFragment();
        Bundle args = new Bundle();
        args.putString("appointmentKey", appointmentKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appointmentKey = getArguments().getString("appointmentKey");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_appointment, container, false);

        // Initialize UI elements
        TextView titleView = view.findViewById(R.id.appointment_title);
        TextView dateView = view.findViewById(R.id.appointment_date);
        TextView timeView = view.findViewById(R.id.appointment_time);
        TextView detailsView = view.findViewById(R.id.appointment_details);
        TextView durationView = view.findViewById(R.id.appointment_duration);
        TextView hostUserView = view.findViewById(R.id.hosted_by_user_textview);
        involvedUsersRecyclerView = view.findViewById(R.id.involved_users_recyclerview);
        involvedUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        involvedUsersAdapter = new InvolvedUsersAdapter(getContext(), new ArrayList<>());
        involvedUsersRecyclerView.setAdapter(involvedUsersAdapter);

        Button joinButton = view.findViewById(R.id.join_appointment_button);
        Button goBackButton = view.findViewById(R.id.go_back_button);

        // Initialize AppointmentManager
        AppointmentManager appointmentManager = new AppointmentManager(getContext());

        // Fetch the appointment details
        appointmentManager.fetchSingleAppointmentFromDatabase(appointmentKey, new AppointmentManager.SingleAppointmentCallback() {
            @Override
            public void onSingleAppointmentReceived(Appointment fetchedAppointment) {
                titleView.setText(fetchedAppointment.getTitle());
                dateView.setText(fetchedAppointment.getDate());
                timeView.setText(fetchedAppointment.getTime());
                detailsView.setText(fetchedAppointment.getDetails());
                durationView.setText(String.valueOf(fetchedAppointment.getDuration())+" minutes");

                // Fetch and set the requesting user's name
                appointmentManager.getUserNameFromFirebaseKey(fetchedAppointment.getHostUserFirebaseKey(), userName -> {
                    hostUserView.setText(userName);
                });

                // Update the RecyclerView
                involvedUsersAdapter.updateInvolvedUsers(fetchedAppointment.getInvolvedUsers());

                appointment = fetchedAppointment;
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error fetching appointment: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        joinButton.setOnClickListener(v -> {
            appointmentManager.joinAppointment(appointment, appointmentKey);
        });

        goBackButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new SearchExistingAppointmentFragment());
            fragmentTransaction.commit();
        });

        return view;
    }
}

