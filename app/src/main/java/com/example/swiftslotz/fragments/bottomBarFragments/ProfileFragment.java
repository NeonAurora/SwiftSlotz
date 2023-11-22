package com.example.swiftslotz.fragments.bottomBarFragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.R;
import com.example.swiftslotz.activities.LogoutActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private EditText firstName, lastName, email, phone, occupation, address;
    private TextView username;
    private ImageView profileImage;
    private Button uploadPhotoButton, logoutButton, updateInfoButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userDb;
    private StorageReference storageReference;

    private final long TEXT_WATCHER_DELAY = 500;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
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
        });

        updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
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

        // Check if first name or last name is empty
        if (updatedFirstName.isEmpty() || updatedLastName.isEmpty()) {
            Toast.makeText(getActivity(), "First name and last name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            userDb.child("firstName").setValue(updatedFirstName);
            userDb.child("lastName").setValue(updatedLastName);
            userDb.child("email").setValue(updatedEmail);
            userDb.child("phone").setValue(updatedPhone);
            userDb.child("occupation").setValue(updatedOccupation);
            userDb.child("address").setValue(updatedAddress);

            Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
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
        }, TEXT_WATCHER_DELAY);
    }



}
