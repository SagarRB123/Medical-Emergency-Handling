package com.example.medical_emergency_handling;
public class FirstAidItem {
    private String title;
    private int iconResource;
    private String description;
    private String[] steps;
    private String[] symptoms;
    private int imageResource;
    private String videoResource;

    public FirstAidItem(String title, int iconResource, String description,
                        String[] steps, String[] symptoms, int imageResource,
                        String videoResource) {
        this.title = title;
        this.iconResource = iconResource;
        this.description = description;
        this.steps = steps;
        this.symptoms = symptoms;
        this.imageResource = imageResource;
        this.videoResource = videoResource;
    }

    // Getters
    public String getTitle() { return title; }
    public int getIconResource() { return iconResource; }
    public String getDescription() { return description; }
    public String[] getSteps() { return steps; }
    public String[] getSymptoms() { return symptoms; }
    public int getImageResource() { return imageResource; }
    public String getVideoResource() { return videoResource; }
}