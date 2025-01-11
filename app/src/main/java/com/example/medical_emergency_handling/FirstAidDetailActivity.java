package com.example.medical_emergency_handling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class FirstAidDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aid_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get data from intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String[] steps = getIntent().getStringArrayExtra("steps");
        String[] symptoms = getIntent().getStringArrayExtra("symptoms");
        int imageResource = getIntent().getIntExtra("image", 0);

        // Set title
        setTitle(title);

        // Initialize views
        TextView descriptionText = findViewById(R.id.descriptionText);
        TextView symptomsText = findViewById(R.id.symptomsText);
        TextView stepsText = findViewById(R.id.stepsText);
        ImageView imageView = findViewById(R.id.imageView);
        Button emergencyCallButton = findViewById(R.id.emergencyCallButton);

        // Set data to views
        descriptionText.setText(description);

        // Set symptoms
        StringBuilder symptomsBuilder = new StringBuilder("Common Symptoms:\n\n");
        for (String symptom : symptoms) {
            symptomsBuilder.append(symptom).append("\n");
        }
        symptomsText.setText(symptomsBuilder.toString());

        // Set steps
        StringBuilder stepsBuilder = new StringBuilder("Emergency Steps:\n\n");
        for (String step : steps) {
            stepsBuilder.append(step).append("\n\n");
        }
        stepsText.setText(stepsBuilder.toString());

        // Set image
        if (imageResource != 0) {
            imageView.setImageResource(imageResource);
        }

        emergencyCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:112"));
                startActivity(callIntent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}