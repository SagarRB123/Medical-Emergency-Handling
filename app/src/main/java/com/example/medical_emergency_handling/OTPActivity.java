package com.example.medical_emergency_handling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private PhoneAuthProvider.ForceResendingToken resendToken;
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

        sendOtp(phoneNumber);

        resendOtp.setOnClickListener(v -> {
            dialog.setMessage("Resending OTP...");
            dialog.show();
            resendOtp(phoneNumber);
        });

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                if (verificationId == null) {
                    Toast.makeText(OTPActivity.this, "Verification ID not available. Try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithCredential(credential);
            }
        });
    }

    private void sendOtp(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendOtp(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setForceResendingToken(resendToken)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    String code = credential.getSmsCode();
                    if (code != null) {
                        otpView.setText(code);
                        signInWithCredential(credential);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(OTPActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("OTPActivity", "Verification failed", e);
                    dialog.dismiss();
                }

                @Override
                public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    verificationId = verifyId;
                    resendToken = token;
                    dialog.dismiss();
                    Toast.makeText(OTPActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                }
            };

    private void signInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(OTPActivity.this, Home.class));
                Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Invalid OTP. Try again.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
