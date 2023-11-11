package com.example.swiftslotz.utilities;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.adapters.AppointmentsAdapter;
import com.example.swiftslotz.adapters.RequestedAppointmentsAdapter;
import com.example.swiftslotz.views.charts.CustomPieChart;
import com.example.swiftslotz.views.charts.Sector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentManager {
    private List<Appointment> appointments;
    private AppointmentsAdapter appointmentsAdapter, adapter;
    private RequestedAppointmentsAdapter requestedAppointmentsAdapter;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference userDb,rootRef, globalAppointmentDb;
    private List<Sector> sectors = new ArrayList<>();
    private CustomPieChart customPieChart;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public AppointmentManager(Context context, List<Appointment> appointments, AppointmentsAdapter appointmentsAdapter) {
        this.context = context;
        this.appointments = appointments;
        this.appointmentsAdapter = appointmentsAdapter;
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId).child("appointments");
        rootRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId);
        globalAppointmentDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("AppointmentCollection");
        updateFCMToken();


    }

    public AppointmentManager(Context context, List<Appointment> requestedAppointments, RequestedAppointmentsAdapter requestedAppointmentsAdapter) {
        this.context = context;
        this.appointments = requestedAppointments;
        this.requestedAppointmentsAdapter = requestedAppointmentsAdapter;
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId).child("RequestedAppointments");
        rootRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId);
        globalAppointmentDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("AppointmentCollection");
        updateFCMToken();


    }

    public AppointmentManager(Context context) {
        this.context = context;
        this.appointments = new ArrayList<>();
        this.sectors = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId).child("appointments");
        rootRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId);
        globalAppointmentDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("AppointmentCollection");
        updateFCMToken();

    }


    public void setCustomPieChart(CustomPieChart customPieChart) {
        this.customPieChart = customPieChart;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public void fetchAppointmentsFromDatabase() {
        // Fetch appointment keys for the current user
        rootRef.child("appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> appointmentKeys = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (key != null) {
                        appointmentKeys.add(key);
                    }
                }
                // Clear existing appointments
                appointments.clear();
                sectors.clear();

                // Fetch actual appointment data using the keys
                for (String key : appointmentKeys) {
                    globalAppointmentDb.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Appointment appointment = snapshot.getValue(Appointment.class);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String today = sdf.format(new Date());
                            if (appointment != null && appointment.getDate().equals(today)) {
                                appointment.setKey(snapshot.getKey());
                                appointments.add(appointment);
                                checkAndUpdateAppointmentStatuses();

                                // Convert the appointment to a sector and add it to the list
                                Sector sector = AppointmentManager.this.appointmentToSector(appointment);
                                sectors.add(sector);
                            }
                            if (appointmentsAdapter != null) {
                                appointmentsAdapter.notifyDataSetChanged();
                            }
                            if (customPieChart != null) {
                                customPieChart.setSectors(sectors);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            // Handle failure, you might want to log this or show a message to the user
                            return;
                        }
                        // Get the token
                        String token = task.getResult();
                        // Save this token to your Firebase database against the user's ID
                        rootRef.child("device_token").setValue(token);
                    }
                });
    }


    public void fetchRequestedAppointmentsFromDatabase() {
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointments.clear();
                sectors.clear();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today = sdf.format(new Date());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointment.setKey(snapshot.getKey());
                        appointments.add(appointment);
                    }
                }
                String isFlag;
                if (requestedAppointmentsAdapter != null) {
                    requestedAppointmentsAdapter.notifyDataSetChanged();
                    isFlag="true";
                } else {
                    isFlag = "False";
                }
                Log.e("IsFLAG", isFlag);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void addAppointmentRequest(Appointment appointment, String firebaseKey) {
        DatabaseReference specificUserDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                .getReference("users")
                .child(firebaseKey)
                .child("RequestedAppointments");

        String key = specificUserDb.push().getKey();
        if (key != null) {
            appointment.setRequestingUserFirebaseKey(mAuth.getCurrentUser().getUid());

            specificUserDb.child(key).setValue(appointment)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Appointment added successfully", Toast.LENGTH_SHORT).show();

                        // Fetch the FCM token of the user to whom you're sending the appointment request
                        DatabaseReference userRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                                .getReference("users")
                                .child(firebaseKey)
                                .child("device_token");

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String userFcmToken = dataSnapshot.getValue(String.class);
                                if (userFcmToken != null) {
                                    // Fetch the username of the user making the appointment request
                                    getUserNameFromFirebaseKey(mAuth.getCurrentUser().getUid(), new UserNameCallback() {
                                        @Override
                                        public void onUserNameReceived(String userName) {
                                            // Send FCM notification with the username
                                            NotificationManager.sendFCMNotification(userFcmToken, userName);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle error here
                            }
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to add appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }



    public void updateAppointment(Appointment appointment) {
        if (appointment.getKey() != null) {
            userDb.child(appointment.getKey()).setValue(appointment)
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Appointment updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to update appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(context, "Failed to update appointment: Appointment key not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAppointment(Appointment appointment) {
        if (appointment.getKey() != null) {
            // Step 1: Fetch the appointment details from the global collection
            globalAppointmentDb.child(appointment.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Appointment globalAppointment = snapshot.getValue(Appointment.class);
                    if (globalAppointment != null) {
                        // Step 2: Save the fetched appointment details under the user's PastAppointments node
                        DatabaseReference pastAppointmentsRef = rootRef.child("RemovedAppointments");
                        pastAppointmentsRef.child(appointment.getKey()).setValue(globalAppointment)
                                .addOnSuccessListener(aVoid -> {
                                    // Step 3: Remove the appointment from the user's appointments node
                                    userDb.child(appointment.getKey()).removeValue()
                                            .addOnSuccessListener(aVoid2 -> {
                                                // Step 4: Remove the user's ID from the involvedUsers list in the global appointment collection
                                                List<String> involvedUsers = globalAppointment.getInvolvedUsers();
                                                if (involvedUsers != null) {
                                                    involvedUsers.remove(mAuth.getCurrentUser().getUid());
                                                    globalAppointmentDb.child(appointment.getKey()).child("involvedUsers").setValue(involvedUsers)
                                                            .addOnSuccessListener(aVoid3 -> Toast.makeText(context, "Appointment moved to history and user removed from involved users successfully", Toast.LENGTH_SHORT).show())
                                                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to remove user from involved users: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                                    appointmentsAdapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Failed to move appointment to history: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(context, "Failed to fetch appointment details from global collection", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to fetch appointment details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    public void getClientNameFromKey(String firebaseKey, ClientNameCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(firebaseKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String clientName = dataSnapshot.child("username").getValue(String.class);
                    if (clientName != null) {
                        callback.onClientNameReceived(clientName);
                        Log.e("Client Name", clientName);
                    } else {
                        callback.onError("Client name is null");
                    }
                } else {
                    callback.onError("DataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }


    private Sector appointmentToSector(Appointment appointment) {
        // Parse the appointment time into hours and minutes.
        String[] timeParts = appointment.getTime().split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);

        // Calculate the start angle and sweep angle in degrees.
        float startAngle = (hours * 60 + minutes) / 2f;
        float sweepAngle = appointment.getDuration() / 2f;

        startAngle -= 90;

        // Use a default color for now. You can change this to use different colors for different appointments.
        int color = Color.RED;

        String title = appointment.getTitle();
        String time = appointment.getTime();
        // Create and return the new Sector object.
        return new Sector(startAngle, sweepAngle, color, title, time);
    }

    // Method to approve an appointment
    public void approveAppointment(Appointment appointment, String appointmentKey) {
        DatabaseReference requestedAppointmentsRef = rootRef.child("RequestedAppointments").child(appointmentKey);
        DatabaseReference globalAppointmentRef = globalAppointmentDb.push(); // Create a new entry in globalAppointmentDb

        // Generate a unique key for the global appointment
        String globalAppointmentKey = globalAppointmentRef.getKey();

        if (globalAppointmentKey != null) {
            // Add involved users to the appointment object
            List<String> involvedUsers = new ArrayList<>();
            involvedUsers.add(mAuth.getCurrentUser().getUid());  // Boss
            involvedUsers.add(appointment.getRequestingUserFirebaseKey());  // Underling
            appointment.setInvolvedUsers(involvedUsers);

            // Step 1: Write to globalAppointmentDb
            globalAppointmentRef.setValue(appointment).addOnSuccessListener(aVoid -> {
                // Step 2: Update the boss's appointments node with the global unique key
                userDb.child(globalAppointmentKey).setValue(true);

                // Step 3: Update the underling's appointments node with the global unique key
                DatabaseReference underlingDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                        .getReference("users")
                        .child(appointment.getRequestingUserFirebaseKey())
                        .child("appointments");
                underlingDb.child(globalAppointmentKey).setValue(true);

                // Step 4: Delete from RequestedAppointments
                requestedAppointmentsRef.removeValue();
                Toast.makeText(context, "Appointment approved successfully", Toast.LENGTH_SHORT).show();

                // Step 5: Send a notification to the requesting user
                sendApprovalNotification(appointment.getRequestingUserFirebaseKey(), mAuth.getCurrentUser().getUid());

            }).addOnFailureListener(e -> {
                Log.e("ApproveAppointmentError", "Error writing to globalAppointmentDb: " + e.getMessage());
            });
        } else {
            Log.e("ApproveAppointmentError", "Global appointment key is null");
        }
    }

    private void sendApprovalNotification(String requestingUserFirebaseKey, String approverFirebaseKey) {
        DatabaseReference requestingUserRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                .getReference("users")
                .child(requestingUserFirebaseKey)
                .child("device_token");

        requestingUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String requestingUserFcmToken = dataSnapshot.getValue(String.class);
                if (requestingUserFcmToken != null) {
                    // Fetch the username of the user who approved the appointment
                    getUserNameFromFirebaseKey(approverFirebaseKey, new UserNameCallback() {
                        @Override
                        public void onUserNameReceived(String approverUserName) {
                            // Send FCM notification with the approver's username
                            NotificationManager.sendApprovalFCMNotification(requestingUserFcmToken, approverUserName, "Your appointment request has been approved.");
                        }

                        public void onError(String error) {
                            Log.e("NotificationError", "Error fetching username for notification: " + error);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationError", "Error fetching FCM token: " + databaseError.getMessage());
            }
        });
    }




    // Method to reject an appointment
    public void rejectAppointment(String appointmentKey) {
        DatabaseReference requestedAppointmentsRef = rootRef.child("RequestedAppointments").child(appointmentKey);

        // Fetch the appointment details first to get the requesting user's Firebase key
        requestedAppointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Appointment appointment = dataSnapshot.getValue(Appointment.class);
                if (appointment != null) {
                    String requestingUserFirebaseKey = appointment.getRequestingUserFirebaseKey();

                    // Now remove the appointment from RequestedAppointments
                    requestedAppointmentsRef.removeValue().addOnSuccessListener(aVoid -> {
                        // Send a notification to the requesting user about the rejection
                        sendRejectionNotification(requestingUserFirebaseKey, mAuth.getCurrentUser().getUid());
                    }).addOnFailureListener(e -> {
                        // Handle failure
                        Log.e("RejectAppointmentError", "Error rejecting appointment: " + e.getMessage());
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RejectAppointmentError", "Error fetching appointment for rejection: " + databaseError.getMessage());
            }
        });
    }

    private void sendRejectionNotification(String requestingUserFirebaseKey, String rejectorFirebaseKey) {
        DatabaseReference requestingUserRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                .getReference("users")
                .child(requestingUserFirebaseKey)
                .child("device_token");

        requestingUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String requestingUserFcmToken = dataSnapshot.getValue(String.class);
                if (requestingUserFcmToken != null) {
                    // Fetch the username of the user who rejected the appointment
                    getUserNameFromFirebaseKey(rejectorFirebaseKey, new UserNameCallback() {
                        @Override
                        public void onUserNameReceived(String rejectorUserName) {
                            // Send FCM notification with the rejector's username
                            NotificationManager.sendRejectionFCMNotification(requestingUserFcmToken, rejectorUserName, "Your appointment request has been rejected.");
                        }
                        public void onError(String error) {
                            Log.e("NotificationError", "Error fetching username for notification: " + error);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationError", "Error fetching FCM token: " + databaseError.getMessage());
            }
        });
    }


    public void fetchSingleAppointmentFromDatabase(String appointmentKey, SingleAppointmentCallback callback) {
        globalAppointmentDb.child(appointmentKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Appointment appointment = snapshot.getValue(Appointment.class);
                if (appointment != null) {
                    appointment.setKey(snapshot.getKey());
                    callback.onSingleAppointmentReceived(appointment);
                } else {
                    callback.onError("Appointment not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public interface SingleAppointmentCallback {
        void onSingleAppointmentReceived(Appointment appointment);
        void onError(String error);
    }

    public void joinAppointment(Appointment appointment, String appointmentKey) {
        DatabaseReference globalAppointmentRef = globalAppointmentDb.child(appointmentKey); // Reference to the existing appointment

        // Step 1: Add the current user to the involved users list in the appointment object
        List<String> involvedUsers = appointment.getInvolvedUsers();
        involvedUsers.add(mAuth.getCurrentUser().getUid());  // Current User
        appointment.setInvolvedUsers(involvedUsers);

        // Step 2: Update the globalAppointmentDb
        globalAppointmentRef.setValue(appointment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Step 3: Update the current user's appointments node with the global unique key
                userDb.child(appointmentKey).setValue(true);
                Toast.makeText(context, "Joined appointment successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("JoinAppointmentError", "Error writing to globalAppointmentDb: " + e.getMessage());
            }
        });
    }

    public interface UserNameCallback {
        void onUserNameReceived(String userName);
    }

    public void getUserNameFromFirebaseKey(String firebaseKey, UserNameCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(firebaseKey);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("username").getValue(String.class);
                    if (userName != null) {
                        callback.onUserNameReceived(userName);
                    } else {
                        Log.e("NUll Username Found", userName);
                    }
                } else {
                    Log.e("NO user found", "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here
            }
        });
    }

    public interface FetchRemovedAppointmentsCallback {
        void onFetched(List<Appointment> pastAppointments);
        void onError(String error);
    }

    public void getRemovedAppointments(FetchRemovedAppointmentsCallback callback) {
        DatabaseReference pastAppointmentsRef = rootRef.child("RemovedAppointments");
        pastAppointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Appointment> pastAppointments = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointment.setKey(snapshot.getKey());
                        pastAppointments.add(appointment);
                    }
                }
                callback.onFetched(pastAppointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public void intervalCheck() {
        Log.d("intervalCheck", "Interval Check Called");
    }

    public void checkAndUpdateAppointmentStatuses() {
        String currentDateTime = SDF.format(new Date());
        Log.d("Check Update Function", "Function called");

        for (Appointment appointment : appointments) {
            Log.d("Appointments", "For loop running");
            if (isAppointmentExpired(appointment.getDate(), appointment.getTime(), currentDateTime)) {
                Log.d("IF cycle", "IF block executed");
                appointment.setIsExpired(true);
                Log.d("IsExpired", "Set to True");
                updateAppointmentStatus(appointment);
            }
        }
    }

    // Helper method to determine if an appointment is expired
    private boolean isAppointmentExpired(String appointmentDate, String appointmentTime, String currentDateTime) {
        String appointmentDateTime = appointmentDate + " " + appointmentTime;
        try {
            Date appointmentDateObj = SDF.parse(appointmentDateTime);
            Date currentDateObj = SDF.parse(currentDateTime);
            return currentDateObj.after(appointmentDateObj);
        } catch (ParseException e) {
            Log.e("AppointmentManager", "Error parsing dates", e);
            return false;
        }
    }

    // Method to update the appointment status in the database
    private void updateAppointmentStatus(Appointment appointment) {
        if (appointment.getKey() != null) {
            // Reference to the current appointment in AppointmentCollection
            DatabaseReference appointmentRef = globalAppointmentDb.child(appointment.getKey());

            // Set the appointment as expired
            appointmentRef.child("isExpired").setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("AppointmentManager", "Appointment status updated successfully");

                        // Move the appointment to ExpiredAppointmentsCollection
                        DatabaseReference expiredRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                                .getReference("ExpiredAppointmentsCollection")
                                .child(appointment.getKey());
                        expiredRef.setValue(appointment)
                                .addOnSuccessListener(aVoid1 -> {
                                    Log.d("AppointmentManager", "Appointment moved to ExpiredAppointmentsCollection successfully");

                                    // Remove the appointment from AppointmentCollection
                                    appointmentRef.removeValue()
                                            .addOnSuccessListener(aVoid2 -> {
                                                Log.d("AppointmentManager", "Appointment removed from AppointmentCollection successfully");

                                                // Notify involved users and update their lists
                                                updateUsersAppointmentLists(appointment);
                                            })
                                            .addOnFailureListener(e -> Log.e("AppointmentManager", "Failed to remove appointment from AppointmentCollection", e));
                                })
                                .addOnFailureListener(e -> Log.e("AppointmentManager", "Failed to move appointment to ExpiredAppointmentsCollection", e));
                    })
                    .addOnFailureListener(e -> Log.e("AppointmentManager", "Failed to update appointment status", e));
        }
    }

    private void updateUsersAppointmentLists(Appointment appointment) {
        // Assuming you have a list of userIds involved in the appointment
        List<String> userIds = appointment.getInvolvedUsers();
        for (String userId : userIds) {
            // Reference to the user's appointments
            DatabaseReference userAppointmentsRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                    .getReference("users")
                    .child(userId)
                    .child("appointments")
                    .child(appointment.getKey());

            // Remove the appointment from the user's list
            userAppointmentsRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("AppointmentManager", "Appointment removed from user's list successfully");

                        // Add only the appointment key with a value of true to the user's ExpiredAppointments list
                        DatabaseReference userExpiredAppointmentsRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                                .getReference("users")
                                .child(userId)
                                .child("ExpiredAppointments")
                                .child(appointment.getKey());
                        userExpiredAppointmentsRef.setValue(true) // Set the value to true instead of setting the whole appointment object
                                .addOnSuccessListener(aVoid1 -> Log.d("AppointmentManager", "Appointment key added to user's ExpiredAppointments list successfully"))
                                .addOnFailureListener(e -> Log.e("AppointmentManager", "Failed to add appointment key to user's ExpiredAppointments list", e));
                    })
                    .addOnFailureListener(e -> Log.e("AppointmentManager", "Failed to remove appointment from user's list", e));
        }
    }

    public interface FetchExpiredAppointmentsCallback {
        void onFetched(List<Appointment> expiredAppointments);
        void onError(String error);
    }

    // Method to fetch expired appointments
    public void fetchExpiredAppointments(FetchExpiredAppointmentsCallback callback) {
        String userId = mAuth.getCurrentUser().getUid(); // Ensure this is the correct way to get the user ID
        DatabaseReference userExpiredAppointmentsRef = rootRef.child("ExpiredAppointments");

        userExpiredAppointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> expiredAppointmentKeys = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (key != null && Boolean.TRUE.equals(snapshot.getValue(Boolean.class))) {
                        expiredAppointmentKeys.add(key);
                    }
                }

                // Fetch actual expired appointment data using the keys
                DatabaseReference expiredAppointmentsCollectionRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                        .getReference("ExpiredAppointmentsCollection");
                List<Appointment> expiredAppointments = new ArrayList<>();

                for (String key : expiredAppointmentKeys) {
                    expiredAppointmentsCollectionRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Appointment expiredAppointment = snapshot.getValue(Appointment.class);
                            if (expiredAppointment != null) {
                                expiredAppointment.setKey(snapshot.getKey());
                                expiredAppointments.add(expiredAppointment);
                            }
                            // If all keys have been processed, invoke the callback
                            if (expiredAppointments.size() == expiredAppointmentKeys.size()) {
                                callback.onFetched(expiredAppointments);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callback.onError(error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public void uploadImageToFirebaseStorage(Uri imageUri, String appointmentKey, ImageUploadCallback callback) {

        if (appointmentKey == null || appointmentKey.isEmpty()) {
            callback.onError("Appointment key is null");
            return;
        }
        StorageReference storageRef = FirebaseStorage.getInstance(BuildConfig.FIREBASE_STORAGE_URL).getReference();
        StorageReference imageRef = storageRef.child("appointments" + appointmentKey + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    // Get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Got the download URL
                        updateAppointmentWithImageURl(appointmentKey, downloadUri.toString(), callback);
                    }).addOnFailureListener(e -> {
                        // Handle any errors
                        callback.onError("Failed to get image URL: " + e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    callback.onError("Upload failed: " + e.getMessage());
                });

    }

    private void updateAppointmentWithImageURl(String appointmentKey, String imageUrl, ImageUploadCallback callback) {
        DatabaseReference expiredAppointmentsCollectionRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL)
                .getReference("ExpiredAppointmentsCollection").child(appointmentKey);
        expiredAppointmentsCollectionRef.child("imageUrl").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AppointmentManager", "Appointment updated with image URL successfully");
                    callback.onSuccess("Appointment updated with image URL successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("AppointmentManager", "Failed to update appointment with image URL", e);
                    callback.onError("Failed to update appointment with image URL");
                });
    }

    public interface ImageUploadCallback{
        void onSuccess(String message);
        void onError(String error);
    }

}
