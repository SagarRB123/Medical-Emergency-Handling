package com.example.medical_emergency_handling;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MedicalIdActivity extends AppCompatActivity {

    private TextInputLayout nameLayout, dobLayout, heightLayout, weightLayout, bloodGroupLayout, allergiesLayout,
            medicationsLayout, conditionsLayout, emergencyContactLayout, emergencyPhoneLayout;
    private EditText nameEdit, dobEdit, heightEdit, weightEdit, bloodGroupEdit, allergiesEdit,
            medicationsEdit, conditionsEdit, emergencyContactEdit, emergencyPhoneEdit;
    private Button saveButton, viewCardButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_id);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        initializeUI();

        // Set up date picker for DOB field
        setupDatePicker();

        // Load existing data if available
        loadExistingData();

        // Save button click listener
        saveButton.setOnClickListener(v -> saveData());

        // View ID Card button click listener
        viewCardButton.setOnClickListener(v -> {
            Intent intent = new Intent(MedicalIdActivity.this, MedicalIdCardActivity.class);
            startActivity(intent);
        });
    }

    private void initializeUI() {
        // TextInputLayouts
        nameLayout = findViewById(R.id.name_layout);
        dobLayout = findViewById(R.id.dob_layout);
        heightLayout = findViewById(R.id.height_layout);
        weightLayout = findViewById(R.id.weight_layout);
        bloodGroupLayout = findViewById(R.id.blood_group_layout);
        allergiesLayout = findViewById(R.id.allergies_layout);
        medicationsLayout = findViewById(R.id.medications_layout);
        conditionsLayout = findViewById(R.id.conditions_layout);
        emergencyContactLayout = findViewById(R.id.emergency_contact_layout);
        emergencyPhoneLayout = findViewById(R.id.emergency_phone_layout);

        // EditTexts
        nameEdit = findViewById(R.id.name_edit);
        dobEdit = findViewById(R.id.dob_edit);
        heightEdit = findViewById(R.id.height_edit);
        weightEdit = findViewById(R.id.weight_edit);
        bloodGroupEdit = findViewById(R.id.blood_group_edit);
        allergiesEdit = findViewById(R.id.allergies_edit);
        medicationsEdit = findViewById(R.id.medications_edit);
        conditionsEdit = findViewById(R.id.conditions_edit);
        emergencyContactEdit = findViewById(R.id.emergency_contact_edit);
        emergencyPhoneEdit = findViewById(R.id.emergency_phone_edit);

        // Buttons
        saveButton = findViewById(R.id.save_button);
        viewCardButton = findViewById(R.id.view_card_button);
    }

    private void setupDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        dobEdit.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String dateString = sdf.format(new Date(selection));
            dobEdit.setText(dateString);
        });
    }

    private void loadExistingData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("medical_ids").child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                MedicalIdData medicalIdData = dataSnapshot.getValue(MedicalIdData.class);
                                if (medicalIdData != null) {
                                    populateFields(medicalIdData);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MedicalIdActivity.this,
                                    "Error loading data: " + databaseError.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void populateFields(MedicalIdData data) {
        nameEdit.setText(data.getName());
        dobEdit.setText(data.getDob());
        heightEdit.setText(data.getHeight());
        weightEdit.setText(data.getWeight());
        bloodGroupEdit.setText(data.getBloodGroup());
        allergiesEdit.setText(data.getAllergies());
        medicationsEdit.setText(data.getMedications());
        conditionsEdit.setText(data.getConditions());
        emergencyContactEdit.setText(data.getEmergencyContact());
        emergencyPhoneEdit.setText(data.getEmergencyPhone());
    }

    private void saveData() {
        if (!validateFields()) {
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            MedicalIdData medicalIdData = new MedicalIdData(
                    nameEdit.getText().toString(),
                    dobEdit.getText().toString(),
                    heightEdit.getText().toString(),
                    weightEdit.getText().toString(),
                    bloodGroupEdit.getText().toString(),
                    allergiesEdit.getText().toString(),
                    medicationsEdit.getText().toString(),
                    conditionsEdit.getText().toString(),
                    emergencyContactEdit.getText().toString(),
                    emergencyPhoneEdit.getText().toString()
            );

            mDatabase.child("medical_ids").child(userId).setValue(medicalIdData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MedicalIdActivity.this,
                                "Medical ID saved successfully",
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MedicalIdActivity.this,
                                "Failed to save: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (nameEdit.getText().toString().isEmpty()) {
            nameLayout.setError("Name is required");
            isValid = false;
        } else {
            nameLayout.setError(null);
        }

        if (bloodGroupEdit.getText().toString().isEmpty()) {
            bloodGroupLayout.setError("Blood group is required");
            isValid = false;
        } else {
            bloodGroupLayout.setError(null);
        }

        if (emergencyContactEdit.getText().toString().isEmpty()) {
            emergencyContactLayout.setError("Emergency contact is required");
            isValid = false;
        } else {
            emergencyContactLayout.setError(null);
        }

        if (emergencyPhoneEdit.getText().toString().isEmpty()) {
            emergencyPhoneLayout.setError("Emergency phone is required");
            isValid = false;
        } else {
            emergencyPhoneLayout.setError(null);
        }

        return isValid;
    }
}
