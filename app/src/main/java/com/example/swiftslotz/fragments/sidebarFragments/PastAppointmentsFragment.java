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
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.utilities.PastAppointmentsAdapter;

import java.util.ArrayList;
import java.util.List;

public class PastAppointmentsFragment extends Fragment {

    private RecyclerView pastAppointmentsRecyclerView;
    private PastAppointmentsAdapter adapter;
    private List<Appointment> pastAppointmentsList;
    private AppointmentManager appointmentManager;

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
        pastAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pastAppointmentsList = new ArrayList<>();
        appointmentManager = new AppointmentManager(getContext());

        fetchPastAppointments();

        return view;
    }

    private void fetchPastAppointments() {
        appointmentManager.getPastAppointments(new AppointmentManager.FetchPastAppointmentsCallback() {
            @Override
            public void onFetched(List<Appointment> pastAppointments) {
                pastAppointmentsList = pastAppointments;
                adapter = new PastAppointmentsAdapter(getContext(), pastAppointmentsList);
                pastAppointmentsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error fetching past appointments: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
