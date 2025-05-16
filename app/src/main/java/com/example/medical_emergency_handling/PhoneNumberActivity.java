package com.example.medical_emergency_handling;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.hbb20.CountryCodePicker;

public class PhoneNumberActivity extends AppCompatActivity {
    private EditText phoneBox;
    private Button continueBtn;
    private CountryCodePicker countryCodePicker;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        phoneBox = findViewById(R.id.phoneBox);
        continueBtn = findViewById(R.id.ContinueBtn);
        countryCodePicker = findViewById(R.id.countryCode);

        phoneBox.requestFocus();

        countryCodePicker.registerCarrierNumberEditText(phoneBox);
        continueBtn.setOnClickListener(v -> {
            if (!countryCodePicker.isValidFullNumber()) {
                phoneBox.setError("Invalid phone number");
                return;
            }
            Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
            intent.putExtra("phoneNumber", countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}
