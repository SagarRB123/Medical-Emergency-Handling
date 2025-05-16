package com.example.medical_emergency_handling;

public class MedicalIdData {
    private String name;
    private String dob;
    private String height;
    private String weight;
    private String bloodGroup;
    private String allergies;
    private String medications;
    private String conditions;
    private String emergencyContact;
    private String emergencyPhone;

    // Default constructor required for Firebase
    public MedicalIdData() {
    }

    public MedicalIdData(String name, String dob, String height, String weight, String bloodGroup,
                         String allergies, String medications, String conditions,
                         String emergencyContact, String emergencyPhone) {
        this.name = name;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
        this.medications = medications;
        this.conditions = conditions;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }
}