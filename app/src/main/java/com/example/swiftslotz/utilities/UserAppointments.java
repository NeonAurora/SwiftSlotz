package com.example.swiftslotz.utilities;

import java.util.Map;

public class UserAppointments {
    private Map<String, String> appointments;

    public UserAppointments() {
        // Default constructor
    }

    public Map<String, String> getAppointments() {
        return appointments;
    }

    public void setAppointments(Map<String, String> appointments) {
        this.appointments = appointments;
    }
}
