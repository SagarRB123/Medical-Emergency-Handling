package com.example.medical_emergency_handling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.medical_emergency_handling.Hospital.GoogleMapsActivity;

public class Home extends AppCompatActivity {

    CardView FindHospital, FindAmbulance, FindDoctor, BuyMedicine, PrimaryAid, MyOrders, Helpline, logout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FindHospital = findViewById(R.id.FindHospital);
        FindAmbulance = findViewById(R.id.FindAmbulance);
        FindDoctor = findViewById(R.id.FindDoctor);
        BuyMedicine = findViewById(R.id.BuyMedicine);
        PrimaryAid = findViewById(R.id.PrimaryAid);
        MyOrders = findViewById(R.id.MyOrders);
        Helpline = findViewById(R.id.Helpline);
        logout = findViewById(R.id.Logout);

        FindHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, GoogleMapsActivity.class));
            }
        });

        FindAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMapsWithAmbulanceSearch();
            }
        });

        FindDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, WebViewActivity.class);
                intent.putExtra("website_url", "https://www.practo.com/doctors/");
                intent.putExtra("toolbar_title", "Find Doctor");
                startActivity(intent);
            }
        });

        BuyMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, WebViewActivity.class);
                intent.putExtra("website_url", "https://healthplus.flipkart.com/");
                intent.putExtra("toolbar_title", "Buy Medicine");
                startActivity(intent);
            }
        });

        PrimaryAid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, FirstAidActivity.class);
                startActivity(intent);
            }
        });

        MyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, WebViewActivity.class);
                intent.putExtra("website_url", "https://healthplus.flipkart.com/index.php/customers/dashboard#/myorders/");
                intent.putExtra("toolbar_title", "My Orders");
                startActivity(intent);
            }
        });

        Helpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelplineConfirmationDialog(Home.this);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "Logged out successfully!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Home.this, SignInMethodActivity.class));
            }
        });

    }

    private void openGoogleMapsWithAmbulanceSearch() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=ambulance");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void showHelplineConfirmationDialog(Home context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Emergency Helpline")
                .setMessage("Are you sure you want to call the emergency helpline (112)?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                    startActivity(callIntent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}