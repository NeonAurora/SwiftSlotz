package com.example.swiftslotz.activities;

import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swiftslotz.utilities.BaseActivity;
import com.example.swiftslotz.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends BaseActivity {

    public static final String TAG = "LoginActivity";
    public static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private ProgressDialog progressDialog;
    private Button defaultLoginButton1, defaultLoginButton2, defaultLoginButton3;
    private CheckBox rememberMeCheckbox;
    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.googleSignInButton);
        signInButton.setOnClickListener(view -> signIn());

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        defaultLoginButton1 = findViewById(R.id.defaultLoginButton1);
        defaultLoginButton2 = findViewById(R.id.defaultLoginButton2);
        defaultLoginButton3 = findViewById(R.id.defaultLoginButton3);
        signUpTextView = findViewById(R.id.signUpTextView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckBox);
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("rememberMe", false)) {
            startActivity(new Intent(LoginActivity.this, BaseActivity.class));
            finish();
        }

        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userLogin();
                }
            });
        }

        defaultLoginButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailEditText.setText("arnarnsde@gmail.com");
                passwordEditText.setText("asdfgh");
                userLogin();
            }
        });

        defaultLoginButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailEditText.setText("arnarnsd@gmail.com");
                passwordEditText.setText("asdfgh");
                userLogin();
            }
        });

        defaultLoginButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailEditText.setText("eglebone@gmail.com");
                passwordEditText.setText("asdfgh");
                userLogin();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void        signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.show();

        if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//                handleSignInResult(task);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                progressDialog.dismiss();
                // Update UI appropriately
            }
        } else {
            progressDialog.dismiss(); // Ensure dialog is dismissed if this isn't the Google sign-in result.
        }
    }

//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
//            updateUI(account);
//        } catch (ApiException e) {
//            updateUI(null);
//        }
//    }

//    public void updateUI(GoogleSignInAccount account) {
//        if (account != null) {
//            firebaseAuthWithGoogle(account.getIdToken());
//        } else {
//            progressDialog.dismiss();
//            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if(task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("rememberMe", rememberMeCheckbox.isChecked());
                                editor.putString("fragmentToLoad", "AppointmentsFragment");
                                editor.apply();
                                finish();
                                startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify your email before logging in.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void userLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please provide a valid email");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Minimum password length should be 6");
            passwordEditText.requestFocus();
            return;
        }

        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("rememberMe", rememberMeCheckbox.isChecked());
                                editor.putString("fragmentToLoad", "AppointmentsFragment");
                                editor.apply();
                                finish();
                                startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Please verify your email before logging in.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
