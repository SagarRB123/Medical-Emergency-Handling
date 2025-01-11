package com.example.medical_emergency_handling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SignInMethodActivity extends AppCompatActivity {
    private CardView phoneSignInCard;
    private CardView emailSignInCard;

    TextView signUpPage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_method);

        phoneSignInCard = findViewById(R.id.phoneSignInCard);
        emailSignInCard = findViewById(R.id.emailSignInCard);
        signUpPage = findViewById(R.id.signUpPage);

        phoneSignInCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhoneSignIn();
            }
        });

        emailSignInCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEmailSignIn();
            }
        });

        signUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPageSignUp();
            }
        });
    }

    private void startPageSignUp(){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    private void startPhoneSignIn() {
        Intent intent = new Intent(this, PhoneNumberActivity.class);
        startActivity(intent);
    }

    private void startEmailSignIn() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}
