package com.example.medical_emergency_handling.Hospital;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HospitalDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "HospitalDetails";

    private TextView tvName, tvAddress, tvPhoneNumber, tvOpeningHours, tvRating, tvTotalRatings, tvWebsite;
    private ProgressBar progressBar;
    private ImageView ivBackButton;
    private GoogleMap mMap;
    private double latitude = 0, longitude = 0;
    private String hospitalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_details);

        // Initialize UI components
        setupUI();

        // Set up toolbar with back button
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Retrieve place_id from intent
        String placeId = getIntent().getStringExtra("place_id");
        hospitalName = getIntent().getStringExtra("hospital_name");

        // Get coordinates if available
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        // Set title
        if (hospitalName != null && !hospitalName.isEmpty()) {
            setTitle(hospitalName);
        } else {
            setTitle("Hospital Details");
        }

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (placeId != null && !placeId.isEmpty()) {
            fetchHospitalDetails(placeId);
        } else {
            Toast.makeText(this, "Error: Place ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupUI() {
        tvName = findViewById(R.id.tv_hospital_name);
        tvAddress = findViewById(R.id.tv_hospital_address);
        tvPhoneNumber = findViewById(R.id.tv_hospital_phone);
        tvOpeningHours = findViewById(R.id.tv_hospital_hours);
        tvRating = findViewById(R.id.tv_hospital_rating);
        tvTotalRatings = findViewById(R.id.tv_hospital_total_ratings);
        tvWebsite = findViewById(R.id.tv_hospital_website);
        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // If we have coordinates, add a marker and move the camera
        if (latitude != 0 && longitude != 0) {
            LatLng hospitalLocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(hospitalLocation)
                    .title(hospitalName != null ? hospitalName : "Hospital"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation, 15));
        }
    }

    private void fetchHospitalDetails(String placeId) {
        progressBar.setVisibility(View.VISIBLE);

        // Generate a unique session token for this request
        String sessionToken = UUID.randomUUID().toString();

        // Fields to request
        String fields = "name,formatted_address,formatted_phone_number,opening_hours,rating,user_ratings_total,website,place_id,vicinity,geometry";

        // Construct URL
        String url = getPlaceDetailsUrl(placeId, fields, sessionToken);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                DownloadUrl downloadUrl = new DownloadUrl();
                String jsonData = downloadUrl.ReadTheURL(url);

                // Parse on main thread to update UI
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    parseAndDisplayPlaceDetails(jsonData);
                });

            } catch (IOException e) {
                Log.e(TAG, "Error fetching place details: " + e.getMessage());
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HospitalDetailsActivity.this,
                            "Failed to load hospital details", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private String getPlaceDetailsUrl(String placeId, String fields, String sessionToken) {
        StringBuilder url = new StringBuilder("https://maps.gomaps.pro/maps/api/place/details/json?");
        url.append("place_id=").append(placeId);
        url.append("&fields=").append(fields);
        url.append("&sessiontoken=").append(sessionToken);
        url.append("&language=en");
        url.append("&key=").append("AlzaSyVTHHXZc8D2SjyMz-oAxS7kcRneXppdvVB"); // Replace with your actual API key

        Log.d(TAG, "Place Details URL: " + url.toString());
        return url.toString();
    }

    private void parseAndDisplayPlaceDetails(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Check if the request was successful
            if (!jsonObject.getString("status").equals("OK")) {
                Toast.makeText(this, "Error: " + jsonObject.optString("status", "Unknown error"),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject result = jsonObject.getJSONObject("result");

            // Extract data
            String name = result.optString("name", "N/A");
            String address = result.optString("formatted_address", "N/A");
            String phoneNumber = result.optString("formatted_phone_number", "N/A");
            String website = result.optString("website", "N/A");

            double rating = result.optDouble("rating", 0.0);
            int totalRatings = result.optInt("user_ratings_total", 0);

            // Set values to UI
            tvName.setText(name);
            tvAddress.setText(address);
            tvPhoneNumber.setText(phoneNumber);
            tvWebsite.setText(website);

            if (rating > 0) {
                tvRating.setText(String.format("%.1f", rating));
            } else {
                tvRating.setText("N/A");
            }

            tvTotalRatings.setText(String.format("(%d reviews)", totalRatings));

            // Parse opening hours if available
            if (result.has("opening_hours") && !result.isNull("opening_hours")) {
                JSONObject openingHours = result.getJSONObject("opening_hours");
                if (openingHours.has("weekday_text")) {
                    JSONArray weekdayText = openingHours.getJSONArray("weekday_text");
                    StringBuilder hoursBuilder = new StringBuilder();

                    for (int i = 0; i < weekdayText.length(); i++) {
                        hoursBuilder.append(weekdayText.getString(i));
                        if (i < weekdayText.length() - 1) {
                            hoursBuilder.append("\n");
                        }
                    }

                    tvOpeningHours.setText(hoursBuilder.toString());
                } else {
                    tvOpeningHours.setText("Hours information not available");
                }
            } else {
                tvOpeningHours.setText("Hours information not available");
            }

            // Get coordinates for the map
            if (result.has("geometry") && !result.isNull("geometry")) {
                JSONObject geometry = result.getJSONObject("geometry");
                if (geometry.has("location")) {
                    JSONObject location = geometry.getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");

                    // Update the map
                    updateMapLocation(lat, lng, name);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            Toast.makeText(this, "Error parsing hospital details", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMapLocation(double lat, double lng, String name) {
        if (mMap != null) {
            LatLng hospitalLocation = new LatLng(lat, lng);

            // Clear previous markers
            mMap.clear();

            // Add new marker
            mMap.addMarker(new MarkerOptions()
                    .position(hospitalLocation)
                    .title(name));

            // Move camera to the location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation, 15));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}