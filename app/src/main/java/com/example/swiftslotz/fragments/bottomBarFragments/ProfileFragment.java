package com.example.swiftslotz.fragments.bottomBarFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.R;
import com.example.swiftslotz.activities.LogoutActivity;
import com.example.swiftslotz.utilities.User;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private EditText firstName, lastName, email, phone, occupation, address;
    private TextView username;
    private ImageView profileImage;
    private Button uploadPhotoButton, logoutButton, updateInfoButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userDb;
    private StorageReference storageReference;

    private final long TEXT_WATCHER_DELAY = 500;

    private ImageButton facebookButton, linkedinButton, instagramButton;

    private ImageButton editFacebookButton, cancelFacebookButton;
    private EditText facebookLink;
    private ImageButton editLinkedinButton, cancelLinkedinButton;
    private EditText linkedinLink;
    private ImageButton editInstagramButton, cancelInstagramButton;
    private EditText instagramLink;
    private Button fromActiveHour, toActiveHour;
    private CheckBox sunday, monday, tuesday, wednesday, thursday, friday, saturday;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        initializeViews(view);
        buttonOnClick(facebookButton, facebookLink);
        buttonOnClick(linkedinButton, linkedinLink);
        buttonOnClick(instagramButton, instagramLink);

        setupSocialMediaEditCancel(editFacebookButton, cancelFacebookButton, facebookLink);
        setupSocialMediaEditCancel(editLinkedinButton, cancelLinkedinButton, linkedinLink);
        setupSocialMediaEditCancel(editInstagramButton, cancelInstagramButton, instagramLink);


        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId);
            fetchUserDetails();
        } else {
            Toast.makeText(getActivity(), "User not signed in", Toast.LENGTH_SHORT).show();
        }

        uploadPhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 123);
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LogoutActivity.class);
            startActivity(intent);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.remove(this).commit();

        });

        updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });

        fromActiveHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ProfileFragment", "onClick: fromActiveHour");
                showTimePicker(fromActiveHour);
            }
        });

        toActiveHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ProfileFragment", "onClick: toActiveHour");
                showTimePicker(toActiveHour);
            }
        });

        setupTextWatchers();
        return view;
    }

    private void enableUpdateButton() {
        updateInfoButton.setEnabled(true);
        int tomatoColor = ContextCompat.getColor(getContext(), R.color.tomato);
        updateInfoButton.setBackgroundColor(tomatoColor); // Or any other color indicating it's enabled
    }


    private void fetchUserDetails() {
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && isAdded()) {
                    // Set user details from the database to the TextViews
                    firstName.setText(dataSnapshot.child("firstName").getValue(String.class));
                    lastName.setText(dataSnapshot.child("lastName").getValue(String.class));
                    username.setText(dataSnapshot.child("username").getValue(String.class));
                    email.setText(dataSnapshot.child("email").getValue(String.class));
                    phone.setText(dataSnapshot.child("phone").getValue(String.class));
                    occupation.setText(dataSnapshot.child("occupation").getValue(String.class));
                    address.setText(dataSnapshot.child("address").getValue(String.class));
                    facebookLink.setText(dataSnapshot.child("facebook").getValue(String.class));
                    linkedinLink.setText(dataSnapshot.child("linkedin").getValue(String.class));
                    instagramLink.setText(dataSnapshot.child("instagram").getValue(String.class));

                    String activeHoursStart = dataSnapshot.child("activeHoursStart").getValue(String.class);
                    String activeHoursEnd = dataSnapshot.child("activeHoursEnd").getValue(String.class);
                    if (activeHoursStart != null) { fromActiveHour.setText(activeHoursStart); }
                    if (activeHoursEnd != null) { toActiveHour.setText(activeHoursEnd); }

                    List<String> activeDays = dataSnapshot.child("activeDays").getValue(new GenericTypeIndicator<List<String>>() {});
                    if(activeDays != null) {
                        for (String day : activeDays) {
                            switch (day) {
                                case "Sunday":
                                    sunday.setChecked(true);
                                    break;
                                case "Monday":
                                    monday.setChecked(true);
                                    break;
                                case "Tuesday":
                                    tuesday.setChecked(true);
                                    break;
                                case "Wednesday":
                                    wednesday.setChecked(true);
                                    break;
                                case "Thursday":
                                    thursday.setChecked(true);
                                    break;
                                case "Friday":
                                    friday.setChecked(true);
                                    break;
                                case "Saturday":
                                    saturday.setChecked(true);
                                    break;
                            }
                        }
                    }

                     //Load profile image if it exists in the database
                    if (dataSnapshot.hasChild("profileImageUrl")) {
                        String imageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                        if (getActivity() != null) {
                            Glide.with(getActivity()).load(imageUrl).into(profileImage);
                        }
                    }
                } else if(isAdded()) {
                    Toast.makeText(getActivity(), "Failed to load user details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load user details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void buttonOnClick(ImageButton button, EditText linkEditText) {
        button.setOnClickListener(v -> {
            String link = linkEditText.getText().toString().trim();
            if(!link.isEmpty()) {
                openWebPage(link);
            } else {
                Toast.makeText(getActivity(), "Link is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void openWebPage(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(webpage);
        startActivity(intent);
    }




    private void setupSocialMediaEditCancel(ImageButton editButton, ImageButton cancelButton, EditText linkEditText) {
        editButton.setOnClickListener(v -> {
            linkEditText.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        });

        cancelButton.setOnClickListener(v -> {
            linkEditText.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            StorageReference fileRef = storageReference.child("profileImages/" + userId + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                userDb.child("profileImageUrl").setValue(imageUrl)
                        .addOnSuccessListener(aVoid -> {
                            Glide.with(ProfileFragment.this).load(imageUrl).into(profileImage);
                            Toast.makeText(getActivity(), "Profile image updated", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update image URL", Toast.LENGTH_SHORT).show());
            })).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateUserInfo() {
        String updatedFirstName = firstName.getText().toString().trim();
        String updatedLastName = lastName.getText().toString().trim();
        String updatedEmail = email.getText().toString().trim();
        String updatedPhone = phone.getText().toString().trim();
        String updatedOccupation = occupation.getText().toString().trim();
        String updatedAddress = address.getText().toString().trim();
        String updatedFacebook = facebookLink.getText().toString().trim();
        String updatedLinkedin = linkedinLink.getText().toString().trim();
        String updatedInstagram = instagramLink.getText().toString().trim();
        String updatedActiveHoursStart = fromActiveHour.getText().toString().trim();
        String updatedActiveHoursEnd = toActiveHour.getText().toString().trim();
        Set<String> updatedActiveDays = new HashSet<>();
        if (sunday.isChecked()) { updatedActiveDays.add("Sunday"); }
        if (monday.isChecked()) { updatedActiveDays.add("Monday"); }
        if (tuesday.isChecked()) { updatedActiveDays.add("Tuesday"); }
        if (wednesday.isChecked()) { updatedActiveDays.add("Wednesday"); }
        if (thursday.isChecked()) { updatedActiveDays.add("Thursday"); }
        if (friday.isChecked()) { updatedActiveDays.add("Friday"); }
        if (saturday.isChecked()) { updatedActiveDays.add("Saturday"); }
        List<String> updatedActiveDaysList = new ArrayList<>(updatedActiveDays);


        // Check if first name or last name is empty
        if (updatedFirstName.isEmpty() || updatedLastName.isEmpty()) {
            Toast.makeText(getActivity(), "First name and last name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date startTime = sdf.parse(updatedActiveHoursStart);
            Date endTime = sdf.parse(updatedActiveHoursEnd);

            // Check if start time is before end time
            if (startTime != null && endTime != null && !startTime.before(endTime)) {
                Toast.makeText(getActivity(), "Start time must be before end time", Toast.LENGTH_SHORT).show();
                return; // Early return if the time is not valid
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Invalid time format", Toast.LENGTH_SHORT).show();
            return; // Early return if the time format is invalid
        }

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            userDb.child("firstName").setValue(updatedFirstName);
            userDb.child("lastName").setValue(updatedLastName);
            userDb.child("email").setValue(updatedEmail);
            userDb.child("phone").setValue(updatedPhone);
            userDb.child("occupation").setValue(updatedOccupation);
            userDb.child("address").setValue(updatedAddress);
            userDb.child("facebook").setValue(updatedFacebook);
            userDb.child("linkedin").setValue(updatedLinkedin);
            userDb.child("instagram").setValue(updatedInstagram);
            userDb.child("activeDays").setValue(updatedActiveDaysList).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update active days", Toast.LENGTH_SHORT).show());
            userDb.child("activeHoursStart").setValue(updatedActiveHoursStart);
            userDb.child("activeHoursEnd").setValue(updatedActiveHoursEnd);

            Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    public void initializeViews(View view) {
        firstName = view.findViewById(R.id.profileEditFirstName);
        lastName = view.findViewById(R.id.profileEditLastName);
        username = view.findViewById(R.id.user_username);
        email = view.findViewById(R.id.profileEditEmail);
        phone = view.findViewById(R.id.profileEditPhoneNumber);
        occupation = view.findViewById(R.id.profileEditCompany);
        address = view.findViewById(R.id.profileEditAddress);
        profileImage = view.findViewById(R.id.profileImage);
        uploadPhotoButton = view.findViewById(R.id.uploadPhotoButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        updateInfoButton = view.findViewById(R.id.updateInfoButton);
        updateInfoButton.setEnabled(false);
        updateInfoButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.fadedButton));

        facebookButton = view.findViewById(R.id.facebookButton);
        linkedinButton = view.findViewById(R.id.linkedinButton);
        instagramButton = view.findViewById(R.id.instagramButton);

        editFacebookButton = view.findViewById(R.id.editFacebookButton);
        cancelFacebookButton = view.findViewById(R.id.editCancelFacebookButton);
        facebookLink = view.findViewById(R.id.facebookLink);
        editLinkedinButton = view.findViewById(R.id.editLinkedinButton);
        cancelLinkedinButton = view.findViewById(R.id.editCancelLinkedinButton);
        linkedinLink = view.findViewById(R.id.linkedinLink);
        editInstagramButton = view.findViewById(R.id.editInstagramButton);
        cancelInstagramButton = view.findViewById(R.id.editCancelInstagramButton);
        instagramLink = view.findViewById(R.id.instagramLink);

        fromActiveHour = view.findViewById(R.id.fromAH);
        toActiveHour = view.findViewById(R.id.toAH);

        sunday = view.findViewById(R.id.sun);
        monday = view.findViewById(R.id.mon);
        tuesday = view.findViewById(R.id.tue);
        wednesday = view.findViewById(R.id.wed);
        thursday = view.findViewById(R.id.thu);
        friday = view.findViewById(R.id.fri);
        saturday = view.findViewById(R.id.sat);
    }

    private void setupTextWatchers() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    enableUpdateButton();
                }

                @Override
                public void afterTextChanged(Editable editable) { }
            };

            firstName.addTextChangedListener(textWatcher);
            lastName.addTextChangedListener(textWatcher);
            email.addTextChangedListener(textWatcher);
            phone.addTextChangedListener(textWatcher);
            occupation.addTextChangedListener(textWatcher);
            address.addTextChangedListener(textWatcher);
            facebookLink.addTextChangedListener(textWatcher);
            linkedinLink.addTextChangedListener(textWatcher);
            instagramLink.addTextChangedListener(textWatcher);

            CompoundButton.OnCheckedChangeListener checkboxListener = (buttonView, isChecked) -> enableUpdateButton();
            sunday.setOnCheckedChangeListener(checkboxListener);
            monday.setOnCheckedChangeListener(checkboxListener);
            tuesday.setOnCheckedChangeListener(checkboxListener);
            wednesday.setOnCheckedChangeListener(checkboxListener);
            thursday.setOnCheckedChangeListener(checkboxListener);
            friday.setOnCheckedChangeListener(checkboxListener);
            saturday.setOnCheckedChangeListener(checkboxListener);

        }, TEXT_WATCHER_DELAY);
    }

    private void showTimePicker(final Button timeButton) {
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build();

        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = materialTimePicker.getHour();
                int minute = materialTimePicker.getMinute();
                String amPm = hour < 12 ? "AM" : "PM";
                if (hour > 12) hour -= 12;
                else if (hour == 0) hour = 12;
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPm);
                timeButton.setText(formattedTime);
                enableUpdateButton(); // Call this to enable the update button
            }
        });

        materialTimePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }

}
