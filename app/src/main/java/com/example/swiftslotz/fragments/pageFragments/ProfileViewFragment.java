package com.example.swiftslotz.fragments.pageFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileViewFragment extends Fragment {

    private String userKey, facebook, instagram, linkedin;
    private DatabaseReference userDb;
    private TextView usernameTextView, firstNameTextView, lastNameTextView, emailTextView, phoneTextView, occupationTextView, addressTextView, fromActiveHour, toActiveHour;
    private ImageView profileImageView;
    Button seekAppointmentButton;
    ImageButton facebookViewButton, instagramViewButton, linkedinViewButton;
    List<String> activeDays;

    private RadioButton sunday, monday, tuesday, wednesday, thursday, friday, saturday;

    public ProfileViewFragment() {
        // Required empty public constructor
    }

    public static ProfileViewFragment newInstance(String userKey) {
        ProfileViewFragment fragment = new ProfileViewFragment();
        Bundle args = new Bundle();
        args.putString("userKey", userKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userKey = getArguments().getString("userKey");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);
        initializeViews(view);
        fetchUserDetails();


        seekAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddAppointmentFragment();
            }
        });

        return view;
    }

    private void initializeViews(View view) {
        usernameTextView = view.findViewById(R.id.user_username_profile_view);
        firstNameTextView = view.findViewById(R.id.profileViewEditFirstName);
        lastNameTextView = view.findViewById(R.id.profileViewEditLastName);
        emailTextView = view.findViewById(R.id.profileViewEditEmail);
        phoneTextView = view.findViewById(R.id.profileViewEditPhoneNumber);
        occupationTextView = view.findViewById(R.id.profileViewEditCompany);
        addressTextView = view.findViewById(R.id.profileViewEditAddress);
        profileImageView = view.findViewById(R.id.profileViewImage);
        seekAppointmentButton = view.findViewById(R.id.seekAppointmentButton);
        facebookViewButton = view.findViewById(R.id.facebookViewButton);
        instagramViewButton = view.findViewById(R.id.instagramViewButton);
        linkedinViewButton = view.findViewById(R.id.linkedinViewButton);

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

    private String convertTo12HourFormat(String time24h) {
        try {
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = sdf24.parse(time24h);
            return sdf12.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Handle this appropriately
        }
    }

    public void buttonOnClick(ImageButton button, String url) {
        button.setOnClickListener(v -> {
            if(!url.isEmpty()) {
                openWebPage(url);
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

    private void fetchUserDetails() {
        userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userKey);
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && isAdded()) {
                        displayUserData(user);
                    }
                } else {
                    Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToAddAppointmentFragment() {
        AddAppointmentFragment addAppointmentFragment = new AddAppointmentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("firebaseKey", userKey); // Pass the user key to AddAppointmentFragment
        addAppointmentFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, addAppointmentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void displayUserData(User user) {
        usernameTextView.setText(user.getUsername());
        firstNameTextView.setText(user.getFirstName());
        lastNameTextView.setText(user.getLastName());
        emailTextView.setText(user.getEmail());
        phoneTextView.setText(user.getPhone());
        occupationTextView.setText(user.getOccupation());
        addressTextView.setText(user.getAddress());

        facebook = user.getFacebook();
        instagram = user.getInstagram();
        linkedin = user.getLinkedin();
        Log.d("ProfileViewFragment", "displayUserData: " + facebook + instagram + linkedin);

        buttonOnClick(facebookViewButton, facebook);
        buttonOnClick(instagramViewButton, instagram);
        buttonOnClick(linkedinViewButton, linkedin);

        fromActiveHour.setText(convertTo12HourFormat(user.getActiveHoursStart()));
        toActiveHour.setText(convertTo12HourFormat(user.getActiveHoursEnd()));

        activeDays = user.getActiveDays();
        if(activeDays != null) {
            for (String day : activeDays) {
                switch (day) {
                    case "Sunday":
                        sunday.setVisibility(View.VISIBLE);
                        sunday.setChecked(true);
                        break;
                    case "Monday":
                        monday.setVisibility(View.VISIBLE);
                        monday.setChecked(true);
                        break;
                    case "Tuesday":
                        tuesday.setVisibility(View.VISIBLE);
                        tuesday.setChecked(true);
                        break;
                    case "Wednesday":
                        wednesday.setVisibility(View.VISIBLE);
                        wednesday.setChecked(true);
                        break;
                    case "Thursday":
                        thursday.setVisibility(View.VISIBLE);
                        thursday.setChecked(true);
                        break;
                    case "Friday":
                        friday.setVisibility(View.VISIBLE);
                        friday.setChecked(true);
                        break;
                    case "Saturday":
                        saturday.setVisibility(View.VISIBLE);
                        saturday.setChecked(true);
                        break;
                }
            }
        }

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(this).load(user.getProfileImageUrl()).into(profileImageView);
        }
    }
}
