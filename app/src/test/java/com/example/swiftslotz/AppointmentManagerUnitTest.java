package com.example.swiftslotz;

import com.example.swiftslotz.dependency_injection.DatabaseService;
import com.example.swiftslotz.MockDatabaseService;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.utilities.AppointmentsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static org.mockito.Mockito.*;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManagerUnitTest {
    @Mock
    private Context context;
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private DatabaseService mockDatabaseService; // Here is the change

    private AppointmentManager appointmentManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        List<Appointment> appointments = new ArrayList<>();
        AppointmentsAdapter appointmentsAdapter = null;
        when(mockAuth.getCurrentUser()).thenReturn(mock(FirebaseUser.class));// You may replace this with a mock if needed.
        appointmentManager = new AppointmentManager(context, appointments, appointmentsAdapter, mockAuth, mockDatabaseService);
    }

    @Test
    public void addAppointmentTest() {
        String appointmentKey = "TestKey";
        String title = "TestTitle";
        String date = "TestDate";
        String time = "TestTime";
        String details = "TestDetails";
        int durationInMinutes = 30;

        Appointment testAppointment = new Appointment(title, date, time, details, durationInMinutes);

        try {
            when(mockDatabaseService.addAppointment(testAppointment)).thenReturn(appointmentKey); // This should work now
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String returnedKey = null;
        try {
            returnedKey = appointmentManager.addAppointment(testAppointment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(appointmentKey, returnedKey);
    }
}

