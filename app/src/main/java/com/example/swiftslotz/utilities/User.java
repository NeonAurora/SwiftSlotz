package com.example.swiftslotz.utilities;

import java.io.Serializable;

public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private String occupation;
    private String address;
    private UserAppointments appointments;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String lastName, String username, String email, String phone, String company, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.occupation = company;
        this.address = address;
    }

    public User(String firstName, String lastName, String username, String email, String phone, String occupation, String address, UserAppointments appointments) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.occupation = occupation;
        this.address = address;
        this.appointments = appointments;  // Include this line to initialize the appointments field
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getAddress() {
        return address;
    }

    public UserAppointments getAppointments() {
        return appointments;
    }

    public void setAppointments(UserAppointments appointments) {
        this.appointments = appointments;
    }
}

