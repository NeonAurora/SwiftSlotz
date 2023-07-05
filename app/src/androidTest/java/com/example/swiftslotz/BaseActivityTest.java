package com.example.swiftslotz;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.BaseActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@RunWith(AndroidJUnit4.class)
public class BaseActivityTest {


    @Rule
    public ActivityScenarioRule<BaseActivity> activityRule = new ActivityScenarioRule<>(BaseActivity.class);

//    @Test
//    public void clickAddAppointmentStartButton_opensAddAppointmentFragment1() {
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Find the "Appointments" button and click on it
//        onView(withId(R.id.action_page1)).perform(click());
//
//        // Find the "addAppointmentStartButton" button and click on it
//        onView(withId(R.id.addAppointmentStartButton)).perform(click());
//
//        // Check if the addAppointmentButton in the new fragment is displayed
//        onView(withId(R.id.addAppointmentButton)).check(matches(isDisplayed()));
//    }

    @Test
    public void coreView() {


        // Find the "Appointments" button and click on it
        onView(withId(R.id.action_page1)).perform(click());

        // Find the "addAppointmentStartButton" button and click on it
        onView(withId(R.id.addAppointmentStartButton)).perform(click());

        // Check if the calendarView in the new fragment is displayed
        onView(withId(R.id.calendarView)).check(matches(isDisplayed()));

        // Write something in the appointment title EditText field
        onView(withId(R.id.appointmentTitleEditText)).perform(typeText("Test Appointment Title"), closeSoftKeyboard());

        // Write something in the appointment details EditText field
        onView(withId(R.id.appointmentEditText)).perform(typeText("Test Appointment Details"), closeSoftKeyboard());

        // Click the selectTimeButton
        onView(withId(R.id.selectTimeButton)).perform(click());

        // Handle the TimePicker dialog
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.appointmentDurationEditText)).perform(scrollTo(), typeText("60"), closeSoftKeyboard());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.addAppointmentButton)).perform(scrollTo(), click());
        onView(withId(R.id.action_page1)).perform(click());
        onView(withId(R.id.action_page2)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.action_page1)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.appointmentOptions)).perform(click());
        onView(withText("Edit")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.modify_appointment_title)).perform(clearText(), typeText("Edited Title"), closeSoftKeyboard());
        onView(withId(R.id.modify_appointment_details)).perform(clearText(), typeText("Edited Details"), closeSoftKeyboard());
        onView(withId(R.id.modify_appointment_date)).perform(clearText(), typeText("2023-07-06"), closeSoftKeyboard());
        onView(withId(R.id.modify_appointment_time)).perform(clearText(), typeText("09:00"), closeSoftKeyboard());
        onView(withId(R.id.update_appointment_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.action_page2)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.action_page1)).perform(click());
        onView(withId(R.id.action_page1)).perform(click());
        onView(withId(R.id.appointmentOptions)).perform(click());
        onView(withText("Delete")).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.action_page1)).perform(click());
//        onView(withId(R.id.appointmentOptions)).perform(click());
//        onView(withId(R.id.action_edit)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void clickEdit() {
        onView(withId(R.id.action_page1)).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.appointmentOptions)).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Edit")).check(matches(isDisplayed())).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.modify_appointment_title)).perform(clearText(), typeText("Edited Title"), closeSoftKeyboard());
        onView(withId(R.id.modify_appointment_details)).perform(clearText(), typeText("Edited Details"), closeSoftKeyboard());
        onView(withId(R.id.modify_appointment_date)).perform(clearText(), typeText("2023-07-06"), closeSoftKeyboard());
        onView(withId(R.id.modify_appointment_time)).perform(clearText(), typeText("09:00"), closeSoftKeyboard());
        onView(withId(R.id.update_appointment_button)).perform(click());
    }





}

