package com.example.swiftslotz.dependency_injection;

import com.example.swiftslotz.utilities.Appointment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseService implements DatabaseService {

    private DatabaseReference userDb;

    public FirebaseDatabaseService(DatabaseReference userDb) {
        this.userDb = userDb;
    }

    @Override
    public String addAppointment(Appointment appointment) throws Exception {
        String key = userDb.push().getKey();
        if (key != null) {
            userDb.child(key).setValue(appointment);
        } else {
            throw new Exception("Failed to push appointment to Firebase");
        }
        return key;
    }

    @Override
    public void updateAppointment(Appointment appointment) throws Exception { // Add this method
        if (appointment.getKey() != null) {
            userDb.child(appointment.getKey()).setValue(appointment);
        } else {
            throw new Exception("Failed to update appointment: Appointment key not found");
        }
    }
}
