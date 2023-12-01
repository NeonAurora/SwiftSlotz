package com.example.swiftslotz.fragments.sidebarFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.adapters.RemovedAppointmentsAdapter;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.utilities.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class RemovedAppointmentsFragment extends Fragment {

    private RecyclerView removedAppointmentsRecyclerView;
    private RemovedAppointmentsAdapter adapter;
    private List<Appointment> removedAppointmentsList;
    private AppointmentManager appointmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_removed_appointments, container, false);

        if (getActivity() != null) {
            ((BaseActivity) getActivity()).updateBottomNavigationForFragment("FragmentX");
        }

        removedAppointmentsRecyclerView = view.findViewById(R.id.removedAppointmentsRecyclerView);
        removedAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        removedAppointmentsList = new ArrayList<>();
        appointmentManager = new AppointmentManager(getContext());

        fetchRemovedAppointments();

        return view;
    }

    private void fetchRemovedAppointments() {
        // This method should be implemented in the AppointmentManager to fetch removed appointments
        appointmentManager.getRemovedAppointments(new AppointmentManager.FetchRemovedAppointmentsCallback() {
            @Override
            public void onFetched(List<Appointment> removedAppointments) {
                removedAppointmentsList = removedAppointments;
                adapter = new RemovedAppointmentsAdapter(getContext(), removedAppointmentsList);
                removedAppointmentsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error fetching removed appointments: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
