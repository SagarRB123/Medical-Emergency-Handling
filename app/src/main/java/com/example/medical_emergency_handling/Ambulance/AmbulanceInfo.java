package com.example.medical_emergency_handling.Ambulance;

public class AmbulanceInfo {
    private String name;
    private String vicinity;
    private double latitude;
    private double longitude;
    private String reference;
    private String status; // Available, Busy, etc.

    public AmbulanceInfo(String name, String vicinity, double latitude, double longitude, String reference, String status) {
        this.name = name;
        this.vicinity = vicinity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reference = reference;
        this.status = status;
    }

    // Getters
    public String getName() { return name; }
    public String getVicinity() { return vicinity; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getReference() { return reference; }
    public String getStatus() { return status; }
}