package com.example.swiftslotz.utilities;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable {

    private String firstName, lastName, username, email, phone, occupation, address, facebook, instagram, linkedin;
    private UserAppointments appointments;
    private String profileImageUrl;
    private Set<String> activeDays;
    private String activeHoursStart;
    private String activeHoursEnd;

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

    public User(String firstName, String lastName, String username, String email, String phone, String occupation, String address, UserAppointments appointments, Set<String> activeDays, String activeHoursStart, String activeHoursEnd) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.occupation = occupation;
        this.address = address;
        this.appointments = appointments;
        this.activeDays = activeDays;
        this.activeHoursStart = activeHoursStart;
        this.activeHoursEnd = activeHoursEnd;
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }
    public Set<String> getActiveDays() { return activeDays; }
    public void setActiveDays(Set<String> activeDays) { this.activeDays = activeDays; }

    public String getActiveHoursStart() { return activeHoursStart; }
    public void setActiveHoursStart(String activeHoursStart) { this.activeHoursStart = activeHoursStart; }

    public String getActiveHoursEnd() { return activeHoursEnd; }
    public void setActiveHoursEnd(String activeHoursEnd) { this.activeHoursEnd = activeHoursEnd; }
}

