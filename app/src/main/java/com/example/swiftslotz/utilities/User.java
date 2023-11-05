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

    public User(String firstName, String lastName, String username, String email, String phone, String occupation, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.occupation = occupation;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setOccupation(String company) {
        this.occupation = company;
    }

    public void setAddress(String address) {
        this.address = address;
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

