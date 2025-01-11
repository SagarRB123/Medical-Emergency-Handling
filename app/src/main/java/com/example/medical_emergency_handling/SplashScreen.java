package com.example.medical_emergency_handling;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, SignInMethodActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);


        TextView animatedText = findViewById(R.id.animatedText);
        Animation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1500);
        fadeIn.setStartOffset(500);
        animatedText.startAnimation(fadeIn);

    }
}
