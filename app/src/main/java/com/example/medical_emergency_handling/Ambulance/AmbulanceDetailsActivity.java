package com.example.medical_emergency_handling.Ambulance;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.medical_emergency_handling.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class AmbulanceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "AmbulanceDetails";
    private TextView nameTextView, addressTextView, phoneTextView, statusTextView,
            hoursTextView, ratingTextView, totalRatingsTextView;
    private Button callButton;
    private ProgressBar progressBar;
    private GoogleMap mMap;
    private String placeId;
    private double latitude, longitude;
    private String ambulanceName, ambulanceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_details);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ambulance Details");

        // Initialize views
        nameTextView = findViewById(R.id.ambulance_name);
        addressTextView = findViewById(R.id.ambulance_address);
        phoneTextView = findViewById(R.id.ambulance_phone);
        statusTextView = findViewById(R.id.ambulance_status);
        hoursTextView = findViewById(R.id.ambulance_hours);
        ratingTextView = findViewById(R.id.ambulance_rating);
        totalRatingsTextView = findViewById(R.id.ambulance_total_ratings);
        callButton = findViewById(R.id.call_button);
        progressBar = findViewById(R.id.progress_bar);

        // Get the ambulance info from intent
        Intent intent = getIntent();
        placeId = intent.getStringExtra("place_id");
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        ambulanceName = intent.getStringExtra("name");
        ambulanceStatus = intent.getStringExtra("status");

        // Set initial data
        nameTextView.setText(ambulanceName);
        statusTextView.setText("Status: " + ambulanceStatus);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Fetch details
        fetchPlaceDetails();

        // Set up call button (initially disabled until we get the phone number)
        callButton.setEnabled(false);
    }

    private void fetchPlaceDetails() {
        progressBar.setVisibility(View.VISIBLE);

        // Build the Place Details URL
        String detailsUrl = getPlaceDetailsUrl(placeId);

        // Execute the API call
        new FetchPlaceDetailsTask().execute(detailsUrl);
    }

    private String getPlaceDetailsUrl(String placeId) {
        StringBuilder googleURL = new StringBuilder("https://maps.gomaps.pro/maps/api/place/details/json?");
        googleURL.append("place_id=").append(placeId);
        googleURL.append("&fields=name,formatted_address,formatted_phone_number,opening_hours,rating,user_ratings_total,website,international_phone_number");
        googleURL.append("&key=AlzaSyVTHHXZc8D2SjyMz-oAxS7kcRneXppdvVB"); // Replace with your actual API key

        Log.d(TAG, "Place Details URL: " + googleURL.toString());

        return googleURL.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker for the ambulance and move the camera
        LatLng ambulanceLocation = new LatLng(latitude, longitude);

        // Choose marker color based on status
        float markerColor = BitmapDescriptorFactory.HUE_RED;
        if (ambulanceStatus.equals("Available")) {
            markerColor = BitmapDescriptorFactory.HUE_GREEN;
        } else if (ambulanceStatus.equals("On Call")) {
            markerColor = BitmapDescriptorFactory.HUE_YELLOW;
        } else if (ambulanceStatus.equals("En Route")) {
            markerColor = BitmapDescriptorFactory.HUE_BLUE;
        }

        mMap.addMarker(new MarkerOptions()
                .position(ambulanceLocation)
                .title(ambulanceName)
                .snippet("Status: " + ambulanceStatus)
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ambulanceLocation, 15));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchPlaceDetailsTask extends AsyncTask<String, Void, HashMap<String, String>> {
        @Override
        protected HashMap<String, String> doInBackground(String... params) {
            String url = params[0];
            String data = "";
            HashMap<String, String> details = new HashMap<>();

            try {
                DownloadUrl downloadUrl = new DownloadUrl();
                data = downloadUrl.ReadTheURL(url);

                // Parse JSON data
                JSONObject jsonObject = new JSONObject(data);

                if (jsonObject.getString("status").equals("OK")) {
                    JSONObject result = jsonObject.getJSONObject("result");

                    if (result.has("name")) {
                        details.put("name", result.getString("name"));
                    }

                    if (result.has("formatted_address")) {
                        details.put("address", result.getString("formatted_address"));
                    }

                    if (result.has("formatted_phone_number")) {
                        details.put("phone", result.getString("formatted_phone_number"));
                    } else if (result.has("international_phone_number")) {
                        details.put("phone", result.getString("international_phone_number"));
                    }

                    if (result.has("website")) {
                        details.put("website", result.getString("website"));
                    }

                    if (result.has("rating")) {
                        details.put("rating", result.getString("rating"));
                    }

                    if (result.has("user_ratings_total")) {
                        details.put("ratings_total", result.getString("user_ratings_total"));
                    }

                    if (result.has("opening_hours")) {
                        JSONObject openingHours = result.getJSONObject("opening_hours");
                        if (openingHours.has("weekday_text")) {
                            JSONArray weekdayText = openingHours.getJSONArray("weekday_text");
                            StringBuilder hours = new StringBuilder();
                            for (int i = 0; i < weekdayText.length(); i++) {
                                hours.append(weekdayText.getString(i)).append("\n");
                            }
                            details.put("hours", hours.toString());
                        }

                        if (openingHours.has("open_now")) {
                            boolean isOpen = openingHours.getBoolean("open_now");
                            details.put("open_now", isOpen ? "Open Now" : "Closed");
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching place details: " + e.getMessage());
            }

            return details;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> details) {
            progressBar.setVisibility(View.GONE);

            // Update UI with details
            if (details.containsKey("name")) {
                nameTextView.setText(details.get("name"));
            }

            if (details.containsKey("address")) {
                addressTextView.setText(details.get("address"));
                addressTextView.setVisibility(View.VISIBLE);
            }

            if (details.containsKey("phone")) {
                final String phoneNumber = details.get("phone");
                phoneTextView.setText(phoneNumber);
                phoneTextView.setVisibility(View.VISIBLE);

                // Enable the call button
                callButton.setEnabled(true);
                callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(callIntent);
                    }
                });
            }

            if (details.containsKey("hours")) {
                hoursTextView.setText(details.get("hours"));
                hoursTextView.setVisibility(View.VISIBLE);
            }

            if (details.containsKey("rating") && details.containsKey("ratings_total")) {
                String rating = details.get("rating");
                String ratingsTotal = details.get("ratings_total");
                ratingTextView.setText("Rating: " + rating + "/5");
                totalRatingsTextView.setText("(" + ratingsTotal + " reviews)");
                ratingTextView.setVisibility(View.VISIBLE);
                totalRatingsTextView.setVisibility(View.VISIBLE);
            }

            if (details.isEmpty()) {
                Toast.makeText(AmbulanceDetailsActivity.this, "Unable to fetch ambulance details", Toast.LENGTH_SHORT).show();
            }
        }
    }
}