package com.example.medical_emergency_handling;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MedicalIdCardActivity extends AppCompatActivity {

    private CardView medicalIdCard;
    private TextView nameText, dobText, heightWeightText, bloodGroupText;
    private TextView allergiesText, medicationsText, conditionsText;
    private TextView emergencyContactText, emergencyPhoneText;
    private LinearLayout loadingLayout, noDataLayout;
    private View contentLayout; // Changed from LinearLayout to View

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_id_card);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set up toolbar
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Toolbar toolbar = findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Medical ID Card");

        // Initialize UI elements
        initializeUI();

        // Load medical ID data
        loadMedicalIdData();
    }

    private void initializeUI() {
        medicalIdCard = findViewById(R.id.medical_id_card);
        nameText = findViewById(R.id.name_text);
        dobText = findViewById(R.id.dob_text);
        heightWeightText = findViewById(R.id.height_weight_text);
        bloodGroupText = findViewById(R.id.blood_group_text);
        allergiesText = findViewById(R.id.allergies_text);
        medicationsText = findViewById(R.id.medications_text);
        conditionsText = findViewById(R.id.conditions_text);
        emergencyContactText = findViewById(R.id.emergency_contact_text);
        emergencyPhoneText = findViewById(R.id.emergency_phone_text);

        loadingLayout = findViewById(R.id.loading_layout);
        contentLayout = findViewById(R.id.content_layout);
        noDataLayout = findViewById(R.id.no_data_layout);
    }

    private void loadMedicalIdData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            loadingLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.GONE);

            mDatabase.child("medical_ids").child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            loadingLayout.setVisibility(View.GONE);

                            if (dataSnapshot.exists()) {
                                MedicalIdData medicalIdData = dataSnapshot.getValue(MedicalIdData.class);
                                if (medicalIdData != null) {
                                    displayMedicalIdData(medicalIdData);
                                    contentLayout.setVisibility(View.VISIBLE);
                                } else {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                noDataLayout.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            loadingLayout.setVisibility(View.GONE);
                            noDataLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(MedicalIdCardActivity.this,
                                    "Error loading data: " + databaseError.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void displayMedicalIdData(MedicalIdData data) {
        nameText.setText(data.getName());
        dobText.setText("DOB: " + data.getDob());

        String heightWeight = "";
        if (!data.getHeight().isEmpty()) {
            heightWeight += "Height: " + data.getHeight();
        }
        if (!data.getWeight().isEmpty()) {
            if (!heightWeight.isEmpty()) {
                heightWeight += " | ";
            }
            heightWeight += "Weight: " + data.getWeight();
        }
        heightWeightText.setText(heightWeight);

        bloodGroupText.setText(data.getBloodGroup());

        if (data.getAllergies().isEmpty()) {
            allergiesText.setText("No known allergies");
        } else {
            allergiesText.setText(data.getAllergies());
        }

        if (data.getMedications().isEmpty()) {
            medicationsText.setText("No medications");
        } else {
            medicationsText.setText(data.getMedications());
        }

        if (data.getConditions().isEmpty()) {
            conditionsText.setText("No medical conditions");
        } else {
            conditionsText.setText(data.getConditions());
        }

        emergencyContactText.setText("Contact: " + data.getEmergencyContact());
        emergencyPhoneText.setText("Phone: " + data.getEmergencyPhone());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.medical_id_card_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(MedicalIdCardActivity.this, MedicalIdActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_share) {
            shareCard();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareCard() {
        if (contentLayout.getVisibility() == View.VISIBLE) {
            // Generate bitmap from card
            Bitmap bitmap = getBitmapFromView(medicalIdCard);

            // Share bitmap
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Medical ID Card", null);
            Uri bitmapUri = Uri.parse(bitmapPath);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "My Medical ID Card from Emergency Hub app");

            startActivity(Intent.createChooser(shareIntent, "Share Medical ID Card"));
        } else {
            Toast.makeText(this, "No Medical ID data to share", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();

        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);
        return bitmap;
    }
}