package com.example.medical_emergency_handling.Hospital;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.medical_emergency_handling.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GoogleMapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        HospitalAdapter.OnHospitalClickListener{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private double latitude, longitude;
    private int ProximityRadius = 10000;
    private RecyclerView hospitalsRecyclerView;
    private HospitalAdapter hospitalAdapter;
    private List<HospitalInfo> hospitalsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        // Initialize RecyclerView and Adapter
        hospitalsRecyclerView = findViewById(R.id.hospitals_recycler_view);
        hospitalsList = new ArrayList<>();
        hospitalAdapter = new HospitalAdapter(hospitalsList, this);
        hospitalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        hospitalsRecyclerView.setAdapter(hospitalAdapter);

        // Check for Location Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        // Initialize Search Box for Filtering
        EditText searchBox = findViewById(R.id.search_box);
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            String userKeyword = searchBox.getText().toString().trim();
            if (!userKeyword.isEmpty()) {
                String url = getUrl(latitude, longitude, "hospital", userKeyword);

                // Pass the URL to GetNearbyPlaces for execution
                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = url;

                GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces(hospitalsList, hospitalAdapter);
                getNearbyPlaces.execute(transferData);

                Toast.makeText(this, "Searching for " + userKeyword + " hospitals...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a keyword to search!", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    public void onHospitalClick(HospitalInfo hospital) {
        // When a hospital is clicked in the list, center the map on it
        LatLng hospitalLocation = new LatLng(hospital.getLatitude(), hospital.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(hospitalLocation);
        markerOptions.title(hospital.getName());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.addMarker(markerOptions);
        Marker marker = mMap.addMarker(markerOptions);

         // Show the info window programmatically
        if (marker != null) {
            marker.showInfoWindow();
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation, 15));

    }
    private String getUrl(double latitude, double longitude, String nearbyPlace, String userKeyword) {
        StringBuilder googleURL = new StringBuilder("https://maps.gomaps.pro/maps/api/place/nearbysearch/json?");
        googleURL.append("location=").append(latitude).append(",").append(longitude);
        googleURL.append("&radius=").append(ProximityRadius);
        googleURL.append("&type=").append(nearbyPlace);
        if (userKeyword != null && !userKeyword.isEmpty()) {
            googleURL.append("&keyword=").append(userKeyword); // Add user-provided keyword
        }
        googleURL.append("&sensor=true");
        googleURL.append("&key=").append("AlzaSyVTHHXZc8D2SjyMz-oAxS7kcRneXppdvVB");   //  Place your API_KEY here

        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());
        return googleURL.toString();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

            // Delay the execution of the next block of code
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String userKeyword = ""; // Example keyword
                    String url = getUrl(latitude, longitude, "hospital", userKeyword);
                    Object[] transferData = new Object[2];
                    transferData[0] = mMap;
                    transferData[1] = url;

                    GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces(hospitalsList,hospitalAdapter);
                    getNearbyPlaces.execute(transferData);
                    Toast.makeText(GoogleMapsActivity.this, "Searching for Nearby Hospitals...", Toast.LENGTH_SHORT).show();
                    Toast.makeText(GoogleMapsActivity.this, "Showing Nearby Hospitals...", Toast.LENGTH_SHORT).show();
                }
            }, 3000); // Delay for 3 seconds (3000 milliseconds)
        }
    }

    public boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        }
        else
        {
            return true;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
        }
    }




    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lastLocation = location;

        if (currentUserLocationMarker != null)
        {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(13));

        if (googleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


