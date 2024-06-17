package com.example.chatapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Settings extends AppCompatActivity {
    ImageButton settingsback;
    TextView settings_delete;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String verificationId;
    private EditText phoneNumberInput, otpinput;
    CountryCodePicker countryCodePicker;
    Button relogin, reotpbtn;
    ProgressBar progressBar,ProgressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settingsback = findViewById(R.id.settings_back_btn);
        settings_delete = findViewById(R.id.settings_delete);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        settingsback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Handle back button press
            }
        });

        settings_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
    }

    @SuppressLint("MissingInflatedId")
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

        View customLayout = getLayoutInflater().inflate(R.layout.dialog_phone_auth, null);
        builder.setView(customLayout);

        phoneNumberInput = customLayout.findViewById(R.id.relogin_mobilenumber);
        countryCodePicker = customLayout.findViewById(R.id.relogin_countrycode); // Initialize CountryCodePicker here
        countryCodePicker.registerCarrierNumberEditText(phoneNumberInput);
        relogin = customLayout.findViewById(R.id.relogin_sendotpbtn);
        progressBar = customLayout.findViewById(R.id.relogin_progressbar);

        progressBar.setVisibility(View.GONE);

        relogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!countryCodePicker.isValidFullNumber()) {
                    phoneNumberInput.setError("Phone number is not valid");
                    return;
                } else {
                    sendOtp(countryCodePicker.getFullNumberWithPlus());
                }
            }
        });

        if (!isFinishing()) {
            builder.show();
        }
    }

    private void sendOtp(String phoneNumber) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Auto verification or instant verification
                        signInWithPhoneAuthCredential(phoneAuthCredential);

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(Settings.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Settings.this.verificationId = verificationId;
                        promptOtpInput();

                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @SuppressLint("MissingInflatedId")
    private void promptOtpInput() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

        View customLayout = getLayoutInflater().inflate(R.layout.dialog_otp_auth, null);
        builder.setView(customLayout);
        otpinput = customLayout.findViewById(R.id.relogin_otp);
        reotpbtn = customLayout.findViewById(R.id.relogin_next);
        ProgressBar = customLayout.findViewById(R.id.relogin_progressbar);
        ProgressBar.setVisibility(View.GONE);

        reotpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpinput.getText().toString();
                verifyOtp(otp);
            }
        });

        if (!isFinishing()) {
            builder.show();
        }
    }

    private void verifyOtp(String otp) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    deleteAccount();
                } else {
                    Toast.makeText(Settings.this, "Re-authentication failed", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void deleteAccount() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Delete user details from Firestore
            db.collection("users").document(userId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Settings.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Settings.this, splashactivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Settings.this, "Failed to delete account", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    } else {
                        Toast.makeText(Settings.this, "Failed to delete user data", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }


}