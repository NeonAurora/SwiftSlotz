package com.example.swiftslotz.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
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

    private GoogleSignInClient client;
    private ActivityResultLauncher<Intent> mGetContent;




    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView, forgotPasswordTextView;
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
        forgotPasswordTextView = findViewById(R.id.tvForgotPassword);

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

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
// google sign in setup

        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK && result != null){
                Intent intent = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    progressDialog.show();
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    mAuth = FirebaseAuth.getInstance();
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(this, entering -> {
                                if (entering.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, mAuth.getUid(),
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("rememberMe", rememberMeCheckbox.isChecked());
                                    editor.putString("fragmentToLoad", "AppointmentsFragment");
                                    editor.apply();
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                                    Log.d(TAG , "successfull");
                                }
                                else {
                                    if (entering.getException() != null) {

                                        String errorMessage = entering.getException().getMessage();
                                        Toast.makeText(LoginActivity.this, "Authentication failed: " + errorMessage,
                                                Toast.LENGTH_LONG).show();
                                        Log.e(TAG, "signInWithCredential:failure", entering.getException());

                                    } else {
                                        Toast.makeText(LoginActivity.this, "Authentication failed",
                                                Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "signInWithCredential:failure - no specific exception");
                                    }
                                }
                            });
                }
                catch (ApiException e) {
                    Log.w(TAG ,e );
                    Log.d(TAG , "try again");
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("606717311294-95dosncopnu17k5sosoil3reef2ivha7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this,gso);

        SignInButton signInButton = findViewById(R.id.googleSignInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = client.getSignInIntent();
                mGetContent.launch(intent);
            }
        });










//        default login button section
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

    private void resetPassword() {
        EditText inputEmail = new EditText(this);
        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputEmail.setHint("Enter your email");

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Enter your email to receive reset link")
                .setView(inputEmail)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = inputEmail.getText().toString().trim();
                        sendPasswordResetEmail(email);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    private void sendPasswordResetEmail(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(LoginActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Unable to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
