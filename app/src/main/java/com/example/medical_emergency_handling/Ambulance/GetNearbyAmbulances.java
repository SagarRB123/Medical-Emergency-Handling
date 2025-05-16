package com.example.medical_emergency_handling.Ambulance;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GetNearbyAmbulances extends AsyncTask<Object, String, String> {
    private String googleplaceData;
    private GoogleMap mMap;
    private String url;
    private List<AmbulanceInfo> ambulancesList;
    private AmbulanceAdapter ambulanceAdapter;
    private static final String TAG = "GetNearbyAmbulances";

    public GetNearbyAmbulances(List<AmbulanceInfo> ambulancesList, AmbulanceAdapter ambulanceAdapter) {
        this.ambulancesList = ambulancesList;
        this.ambulanceAdapter = ambulanceAdapter;
    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleplaceData = downloadUrl.ReadTheURL(url);
        } catch (IOException e) {
            Log.e(TAG, "Error downloading URL: " + e.getMessage());
            return null;
        }

        return googleplaceData;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            Log.e(TAG, "Failed to get places data");
            return;
        }

        List<HashMap<String, String>> nearByPlacesList = null;
        AmbulanceDataParser dataParser = new AmbulanceDataParser();
        nearByPlacesList = dataParser.parse(result);

        if (nearByPlacesList == null) {
            Log.e(TAG, "Failed to parse places data");
            return;
        }

        DisplayNearbyAmbulances(nearByPlacesList);
    }

    private void DisplayNearbyAmbulances(List<HashMap<String, String>> nearByPlacesList) {
        // Clear existing data
        ambulancesList.clear();
        mMap.clear();

        LatLng firstAmbulanceLocation = null;
        String[] statuses = {"Available", "On Call", "En Route", "Returning"};
        Random random = new Random();

        for (int i = 0; i < nearByPlacesList.size(); i++) {
            HashMap<String, String> googleNearbyPlace = nearByPlacesList.get(i);

            // Extract place details
            String placeName = googleNearbyPlace.get("place_name");
            String vicinity = googleNearbyPlace.get("vicinity");
            String reference = googleNearbyPlace.get("reference");

            // Randomly assign a status for demonstration
            String status = statuses[random.nextInt(statuses.length)];

            // Parse coordinates
            double lat = 0;
            double lng = 0;
            try {
                lat = Double.parseDouble(googleNearbyPlace.get("lat"));
                lng = Double.parseDouble(googleNearbyPlace.get("lng"));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing coordinates for place: " + placeName);
                continue;
            }

            // Store first ambulance location for camera focus
            if (i == 0) {
                firstAmbulanceLocation = new LatLng(lat, lng);
            }

            // Create marker with color based on status
            LatLng latLng = new LatLng(lat, lng);
            float markerColor = BitmapDescriptorFactory.HUE_RED;

            // Assign different colors based on status
            if (status.equals("Available")) {
                markerColor = BitmapDescriptorFactory.HUE_GREEN;
            } else if (status.equals("On Call")) {
                markerColor = BitmapDescriptorFactory.HUE_YELLOW;
            } else if (status.equals("En Route")) {
                markerColor = BitmapDescriptorFactory.HUE_BLUE;
            }

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(placeName)
                    .snippet("Status: " + status)
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor));

            // Add marker to map
            mMap.addMarker(markerOptions);

            // Create ambulance info object
            AmbulanceInfo ambulanceInfo = new AmbulanceInfo(
                    placeName,
                    vicinity,
                    lat,
                    lng,
                    reference,
                    status
            );

            // Add to list
            ambulancesList.add(ambulanceInfo);
        }

        // Update the adapter
        if (ambulanceAdapter != null) {
            ambulanceAdapter.notifyDataSetChanged();
        }

        // Move camera to first ambulance if found
        if (firstAmbulanceLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(firstAmbulanceLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
    }
}
