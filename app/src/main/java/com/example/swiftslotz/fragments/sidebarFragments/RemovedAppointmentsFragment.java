package com.example.swiftslotz.fragments.sidebarFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.R;
import com.example.swiftslotz.adapters.RemovedAppointmentsAdapter;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.utilities.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RemovedAppointmentsFragment extends Fragment {

    private RecyclerView removedAppointmentsRecyclerView;
    private RemovedAppointmentsAdapter adapter;
    private List<Appointment> removedAppointmentsList;
    private AppointmentManager appointmentManager;
    FloatingActionButton deleteButton;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_removed_appointments, container, false);

        if (getActivity() != null) {
            ((BaseActivity) getActivity()).updateBottomNavigationForFragment("FragmentX");
        }

        mAuth = FirebaseAuth.getInstance();

        removedAppointmentsRecyclerView = view.findViewById(R.id.removedAppointmentsRecyclerView);
        removedAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        deleteButton = view.findViewById(R.id.cloud_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllRemovedAppointments();
            }
        });

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

    private void deleteAllRemovedAppointments() {
        DatabaseReference removedAppointmentsRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(mAuth.getCurrentUser().getUid()).child("RemovedAppointments");
        removedAppointmentsRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "All removed appointments deleted successfully", Toast.LENGTH_SHORT).show();
                // Clear the local list and update the adapter
                removedAppointmentsList.clear();
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Failed to delete removed appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
