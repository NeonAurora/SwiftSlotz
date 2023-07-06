package com.example.swiftslotz;

import com.example.swiftslotz.dependency_injection.DatabaseService;
import com.example.swiftslotz.utilities.Appointment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockDatabaseService implements DatabaseService {
    private final List<Appointment> data = new ArrayList<>();

    @Override
    public String addAppointment(Appointment appointment) throws Exception {
        data.add(appointment);
        return appointment.getKey();
    }

    @Override
    public void updateAppointment(Appointment appointment) throws Exception {
        Optional<Appointment> existingAppointment = data.stream()
                .filter(a -> a.getKey().equals(appointment.getKey()))
                .findFirst();

        if (existingAppointment.isPresent()) {
            // Replace the old appointment with the new one
            data.remove(existingAppointment.get());
            data.add(appointment);
        } else {
            throw new Exception("Failed to update appointment: Appointment key not found");
        }
    }

    // Get the data that has been "written" to the mock database
    public List<Appointment> getData() {
        return data;
    }
}
