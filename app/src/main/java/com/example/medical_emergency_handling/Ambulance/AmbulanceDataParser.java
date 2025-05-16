package com.example.medical_emergency_handling.Ambulance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AmbulanceDataParser {
    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String NameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {
            if (!googlePlaceJSON.isNull("name")) {
                NameOfPlace = googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.isNull("vicinity")) {
                vicinity = googlePlaceJSON.getString("vicinity");
            } else if (!googlePlaceJSON.isNull("formatted_address")) {
                // Text Search API may return formatted_address instead of vicinity
                vicinity = googlePlaceJSON.getString("formatted_address");
            }

            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJSON.getString("place_id"); // Using place_id as reference

            googlePlaceMap.put("place_name", NameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray) {
        int counter = jsonArray.length();

        List<HashMap<String, String>> NearbyPlacesList = new ArrayList<>();
        HashMap<String, String> NearbyPlaceMap = null;

        for (int i = 0; i < counter; i++) {
            try {
                NearbyPlaceMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
                NearbyPlacesList.add(NearbyPlaceMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return NearbyPlacesList;
    }

    public List<HashMap<String, String>> parse(String jSONdata) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jSONdata);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }
}
