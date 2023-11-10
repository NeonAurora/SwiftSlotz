package com.example.swiftslotz.fragments.sidebarFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.fragments.pageFragments.AppointmentDetailsFragment;
import com.example.swiftslotz.fragments.pageFragments.RemovedAppointmentsFragment;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.adapters.PastAppointmentsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PastAppointmentsFragment extends Fragment {

    private RecyclerView pastAppointmentsRecyclerView;
    private PastAppointmentsAdapter adapter;
    private List<Appointment> pastAppointmentsList;
    private AppointmentManager appointmentManager;
    FloatingActionButton fabRemovedAppointments;

    public PastAppointmentsFragment() {
        // Required empty public constructor
    }

    public static PastAppointmentsFragment newInstance(String param1, String param2) {
        PastAppointmentsFragment fragment = new PastAppointmentsFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("ARG_PARAM1");
            String mParam2 = getArguments().getString("ARG_PARAM2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_appointments, container, false);

        pastAppointmentsRecyclerView = view.findViewById(R.id.pastAppointmentsRecyclerView);
        fabRemovedAppointments = view.findViewById(R.id.fab_removed_appointments);
        pastAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pastAppointmentsList = new ArrayList<>();
        appointmentManager = new AppointmentManager(getContext());
        Fragment selectedFragment = null;
        fabRemovedAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new instance of RemovedAppointmentsFragment
                RemovedAppointmentsFragment removedAppointmentsFragment = new RemovedAppointmentsFragment();

                // Use FragmentManager and FragmentTransaction to replace the current fragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the RemovedAppointmentsFragment instance
                fragmentTransaction.replace(R.id.content_frame, removedAppointmentsFragment);

                // Add the transaction to the back stack if you want to navigate back
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        fetchPastAppointments();

        return view;
    }


    private void fetchPastAppointments() {
        appointmentManager.fetchExpiredAppointments(new AppointmentManager.FetchExpiredAppointmentsCallback() {
            @Override
            public void onFetched(List<Appointment> expiredAppointments) {
                pastAppointmentsList = expiredAppointments;
                adapter = new PastAppointmentsAdapter(getContext(), pastAppointmentsList);
                pastAppointmentsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error fetching expired appointments: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnItemClickListener(new PastAppointmentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Appointment appointment) {
                // Create a new instance of AppointmentDetailsFragment
                AppointmentDetailsFragment appointmentDetailsFragment = new AppointmentDetailsFragment();

                // Use FragmentManager and FragmentTransaction to replace the current fragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the AppointmentDetailsFragment instance
                fragmentTransaction.replace(R.id.content_frame, appointmentDetailsFragment);

                // Add the transaction to the back stack if you want to navigate back
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });
    }

}