package com.example.swiftslotz.fragments.bottomBarFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.views.charts.CustomPieChart;
import com.example.swiftslotz.views.charts.Sector;

import java.util.List;

public class ScheduleChartFragment extends Fragment {
    private AppointmentManager appointmentManager;
    private CustomPieChart customPieChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appointmentManager = new AppointmentManager(getContext());  // or provide appointments and adapter if necessary
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_chart, container, false);
        customPieChart = view.findViewById(R.id.custom_pie_chart);  // Assuming you have a CustomPieChart in your layout with this id
        appointmentManager.setCustomPieChart(customPieChart);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        appointmentManager.fetchAppointmentsFromDatabase();
    }

    // Some method that gets called when the data changes
    public void onDataChanged() {
        List<Sector> sectors = appointmentManager.getSectors();
        customPieChart.setSectors(sectors);
    }
}
