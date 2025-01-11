package com.example.medical_emergency_handling.Hospital;

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

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
    private String googleplaceData;
    private GoogleMap mMap;
    private String url;
    private List<HospitalInfo> hospitalsList;
    private HospitalAdapter hospitalAdapter;
    private static final String TAG = "GetNearbyPlaces";

    public GetNearbyPlaces(List<HospitalInfo> hospitalsList, HospitalAdapter hospitalAdapter) {
        this.hospitalsList = hospitalsList;
        this.hospitalAdapter = hospitalAdapter;
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
        DataParser dataParser = new DataParser();
        nearByPlacesList = dataParser.parse(result);

        if (nearByPlacesList == null) {
            Log.e(TAG, "Failed to parse places data");
            return;
        }

        DisplayNearbyPlaces(nearByPlacesList);
    }

    private void DisplayNearbyPlaces(List<HashMap<String, String>> nearByPlacesList) {
        // Clear existing data
        hospitalsList.clear();
        mMap.clear();

        LatLng firstHospitalLocation = null;

        for (int i = 0; i < nearByPlacesList.size(); i++) {
            HashMap<String, String> googleNearbyPlace = nearByPlacesList.get(i);

            // Extract place details
            String placeName = googleNearbyPlace.get("place_name");
            String vicinity = googleNearbyPlace.get("vicinity");
            String reference = googleNearbyPlace.get("reference");

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

            // Store first hospital location for camera focus
            if (i == 0) {
                firstHospitalLocation = new LatLng(lat, lng);
            }

            // Create marker
            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(placeName)
                    .snippet(vicinity)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            // Add marker to map
            mMap.addMarker(markerOptions);

            // Create hospital info object
            HospitalInfo hospitalInfo = new HospitalInfo(
                    placeName,
                    vicinity,
                    lat,
                    lng,
                    reference
            );

            // Add to list
            hospitalsList.add(hospitalInfo);
        }

        // Update the adapter
        if (hospitalAdapter != null) {
            hospitalAdapter.notifyDataSetChanged();
        }

        // Move camera to first hospital if found
        if (firstHospitalLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(firstHospitalLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }




}