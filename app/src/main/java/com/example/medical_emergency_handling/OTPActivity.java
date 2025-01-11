package com.example.medical_emergency_handling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukeshsolanki.OtpView;
import com.mukeshsolanki.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private TextView phoneLabel, resendOtp;
    private OtpView otpView;
    private FirebaseAuth auth;
    private String verificationId;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        phoneLabel = findViewById(R.id.phoneLabel);
        resendOtp = findViewById(R.id.ResendOTPText);
        otpView = findViewById(R.id.otp_view);

        otpView.requestFocus();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);
        dialog.show();

        auth = FirebaseAuth.getInstance();

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        phoneLabel.setText("Verify " + phoneNumber);

        sendOtp(phoneNumber); // Initial OTP sending logic

        // Resend OTP logic
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("Resending OTP...");
                dialog.show();
                sendOtp(phoneNumber);
            }
        });

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                if (verificationId == null) {
                    Log.e("OTPActivity", "Verification ID is null. Cannot verify OTP.");
                    Toast.makeText(OTPActivity.this, "Verification ID is not available. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OTPActivity.this, "Logged in successfully!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(OTPActivity.this, Home.class));
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "OTP verification failed.";
                            Log.e("OTPActivity", "Error: " + error);
                            Toast.makeText(OTPActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void sendOtp(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Auto verification logic here
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e("OTPActivity", "Verification failed: " + e.getMessage());
                        Toast.makeText(OTPActivity.this, "Verification failed. Try again.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        verificationId = verifyId;
                        dialog.dismiss();
                        Log.d("OTPActivity", "Verification ID received: " + verifyId);
                        Toast.makeText(OTPActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
