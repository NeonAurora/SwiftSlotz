package com.example.swiftslotz;

import com.example.swiftslotz.dependency_injection.DatabaseService;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.utilities.AppointmentsAdapter;
import com.example.swiftslotz.views.charts.CustomPieChart;
import com.example.swiftslotz.views.charts.Sector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppointmentUnitTest {
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

    @Test
    public void updateAppointmentTest() {
        String appointmentKey = "TestKey";
        String title = "TestTitle";
        String date = "TestDate";
        String time = "TestTime";
        String details = "TestDetails";
        int durationInMinutes = 30;

        Appointment testAppointment = new Appointment(title, date, time, details, durationInMinutes);
        testAppointment.setKey(appointmentKey); // I'm assuming there's a method to set the key

        try {
            doAnswer(invocation -> null).when(mockDatabaseService).updateAppointment(testAppointment);
            System.out.println(appointmentManager.getDatabaseService());  // Add a getter in AppointmentManager if you don't have one
            appointmentManager.updateAppointment(testAppointment);
            verify(mockDatabaseService).updateAppointment(testAppointment);
        } catch (Exception e) {
            e.printStackTrace();
            fail("No exception should be thrown");
        }
    }

    @Test
    public void getterSetter() {
        String title = "Important Meeting";
        String date = "2023-07-05";
        String time = "10:30 AM";
        String details = "Meeting with the client";
        int duration = 60;

        Appointment appointment = new Appointment(title, date, time, details, duration);

        assertEquals("Important Meeting", appointment.getTitle());
        assertEquals("2023-07-05", appointment.getDate());
        assertEquals("10:30 AM", appointment.getTime());
        assertEquals("Meeting with the client", appointment.getDetails());
        assertEquals(60, appointment.getDuration());

        String newTitle = "Very Important Meeting";
        appointment.setTitle(newTitle);
        assertEquals("Very Important Meeting", appointment.getTitle());

        String newDate = "2023-07-06";
        appointment.setDate(newDate);
        assertEquals("2023-07-06", appointment.getDate());

        String newTime = "11:00 AM";
        appointment.setTime(newTime);
        assertEquals("11:00 AM", appointment.getTime());

        String newDetails = "Updated details for the meeting";
        appointment.setDetails(newDetails);
        assertEquals("Updated details for the meeting", appointment.getDetails());

        int newDuration = 120;
        appointment.setDuration(newDuration);
        assertEquals(120, appointment.getDuration());
    }
    @Test
    public void testSectorDivision() {
        // Create some sectors, some with AM times, some with PM.
        // For 24-hour format, "00:00" to "11:59" is AM, and "12:00" to "23:59" is PM.
        Sector sector1 = new Sector(0, 30, Color.BLACK, "Sector1", "09:00"); // AM
        Sector sector2 = new Sector(30, 60, Color.BLACK, "Sector2", "13:00"); // PM
        Sector sector3 = new Sector(60, 90, Color.BLACK, "Sector3", "15:00"); // PM

        List<Sector> sectors = Arrays.asList(sector1, sector2, sector3);

        CustomPieChart realCustomPieChart = new CustomPieChart(context); // Provide a mocked or a real context

        // We create a spy of our real object
        CustomPieChart customPieChart = spy(realCustomPieChart);

        // Here we tell Mockito to do nothing when invalidate method is called on our spied object
        doNothing().when(customPieChart).invalidate();

        customPieChart.setSectors(sectors);

        // Check if sectors are correctly divided
        List<Sector> sectorsAM = customPieChart.getSectorsAM();
        List<Sector> sectorsPM = customPieChart.getSectorsPM();

        assertTrue(sectorsAM.contains(sector1));
        assertFalse(sectorsAM.contains(sector2));
        assertFalse(sectorsAM.contains(sector3));

        assertFalse(sectorsPM.contains(sector1));
        assertTrue(sectorsPM.contains(sector2));
        assertTrue(sectorsPM.contains(sector3));
    }


}

