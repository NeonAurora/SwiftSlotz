package com.example.swiftslotz.fragments.sidebarFragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.swiftslotz.R;
import com.example.swiftslotz.fragments.bottomBarFragments.AppointmentsFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class EditPasswordFragment extends Fragment {

    private EditText oldPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private Button changePasswordButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loader;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_password, container, false);

        oldPasswordEditText = view.findViewById(R.id.et_old_password);
        newPasswordEditText = view.findViewById(R.id.et_new_password);
        confirmNewPasswordEditText = view.findViewById(R.id.et_confirm_new_password);
        changePasswordButton = view.findViewById(R.id.btn_change_password);

        loader=new ProgressDialog(getActivity());
        loader.setMessage("Login in progress");
        loader.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        return view;
    }

    private void changePassword() {
        loader.show();
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();

        // Validations
        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(getActivity(), "New passwords do not match", Toast.LENGTH_SHORT).show();
            confirmNewPasswordEditText.setError("New passwords do not match");
        }else if (TextUtils.isEmpty(oldPassword)) {
            oldPasswordEditText.setError("Old Password Required");
        } else if (TextUtils.isEmpty(newPassword)) {
            newPasswordEditText.setError("New Password Required");
        }else if((TextUtils.isEmpty(confirmNewPassword))) {
            confirmNewPasswordEditText.setError("Please Confirm New Password");
        }else{
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null && user.getEmail() != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

                // Reauthenticate user
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update password
                        user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();


                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.content_frame, new AppointmentsFragment());
                                transaction.commit();

                            } else {

                                Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Reauthentication failed. Check your old password.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
            }
        }
            loader.dismiss();




    }

}
