package com.example.swiftslotz.fragments.sidebarFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.BaseActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendFeedbackFragment extends Fragment {

    private RatingBar ratingBar;
    private EditText featureRequestEditText, generalFeedbackEditText, improvementSuggestionsEditText, contactInfoEditText;
    private Button submitButton;
    Spinner usabilitySpinner, userEngagementSpinner;
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8, checkBox9, checkBox10;
    // Add other UI components as needed

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_feedback, container, false);

        if (getActivity() != null) {
            ((BaseActivity) getActivity()).updateBottomNavigationForFragment("FragmentX");
        }

        // Initialize UI components
        ratingBar = view.findViewById(R.id.ratingBar);
        usabilitySpinner = view.findViewById(R.id.spinner_usability);
        checkBox1 = view.findViewById(R.id.checkbox_feature1);
        checkBox2 = view.findViewById(R.id.checkbox_feature2);
        checkBox3 = view.findViewById(R.id.checkbox_feature3);
        checkBox4 = view.findViewById(R.id.checkbox_feature4);
        featureRequestEditText = view.findViewById(R.id.et_feature_request);
        generalFeedbackEditText = view.findViewById(R.id.et_general_feedback);
        improvementSuggestionsEditText = view.findViewById(R.id.et_improvement_suggestions);
        userEngagementSpinner = view.findViewById(R.id.spinner_user_engagement);
//        contactInfoEditText = view.findViewById(R.id.et_contact_info);
        submitButton = view.findViewById(R.id.btn_submit_feedback);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.usability_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usabilitySpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.user_engagement_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userEngagementSpinner.setAdapter(adapter2);
        // Initialize other components here

        // Set up button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        return view;
    }

    private void submitFeedback() {
        float userRating = ratingBar.getRating();
        String usabilityFeedback = usabilitySpinner.getSelectedItem().toString();
        String featureRequest = featureRequestEditText.getText().toString().trim();
        String generalFeedback = generalFeedbackEditText.getText().toString().trim();
        String improvementSuggestions = improvementSuggestionsEditText.getText().toString().trim();
        String userEngagement = userEngagementSpinner.getSelectedItem().toString();
//        String contactInfo = contactInfoEditText.getText().toString().trim();

        // Collecting which features are found useful
        List<String> usefulFeatures = new ArrayList<>();
        if (checkBox1.isChecked()) usefulFeatures.add(checkBox1.getText().toString());
        if (checkBox2.isChecked()) usefulFeatures.add(checkBox2.getText().toString());
        if (checkBox3.isChecked()) usefulFeatures.add(checkBox3.getText().toString());
        if (checkBox4.isChecked()) usefulFeatures.add(checkBox4.getText().toString());

        // Create a map to store the feedback data
        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("userRating", userRating);
        feedbackData.put("usabilityFeedback", usabilityFeedback);
        feedbackData.put("usefulFeatures", usefulFeatures);
        feedbackData.put("featureRequest", featureRequest);
        feedbackData.put("generalFeedback", generalFeedback);
        feedbackData.put("improvementSuggestions", improvementSuggestions);
        feedbackData.put("userEngagement", userEngagement);
//        if (!contactInfo.isEmpty()) {
//            feedbackData.put("contactInfo", contactInfo);
//        }

        // Send the data to Firebase or your backend
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("FeedbackCollection");
        feedbackRef.push().setValue(feedbackData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to submit feedback", Toast.LENGTH_SHORT).show());
    }


    // Add other methods as needed
}
