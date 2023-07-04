package com.example.swiftslotz;

import com.example.swiftslotz.dependency_injection.DatabaseService;
import com.example.swiftslotz.utilities.Appointment;

import java.util.ArrayList;
import java.util.List;

public class MockDatabaseService implements DatabaseService {
    private final List<Appointment> data = new ArrayList<>();

    @Override
    public String addAppointment(Appointment appointment) throws Exception {
        data.add(appointment);
        return appointment.getKey();
    }

    // Get the data that has been "written" to the mock database
    public List<Appointment> getData() {
        return data;
    }
}
