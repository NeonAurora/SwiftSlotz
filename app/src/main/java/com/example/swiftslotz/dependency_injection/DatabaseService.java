package com.example.swiftslotz.dependency_injection;

import com.example.swiftslotz.utilities.Appointment;

public interface DatabaseService {
    String addAppointment(Appointment appointment) throws Exception;
}
