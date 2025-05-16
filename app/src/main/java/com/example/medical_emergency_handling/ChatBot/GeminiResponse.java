package com.example.medical_emergency_handling.ChatBot;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class GeminiResponse {
    // Map of predefined Q&A pairs with exact match keys
    private static final Map<String, String> predefinedResponses = new HashMap<>();

    static {
        // Hospital Location Questions
        predefinedResponses.put("how can i find the nearest hospital",
                "You can tap on the **\"Find Hospital\"** button, and the app will display nearby hospitals on the map.");
        predefinedResponses.put("can i search for a specific type of hospital",
                "Yes! Use the search box and enter keywords like **\"eye hospital\"** or **\"heart specialist\"** to find relevant hospitals.");
        predefinedResponses.put("how do i call an ambulance",
                "Tap the **\"Find Ambulance\"** button, and it will show ambulance services nearby. You can also call **112** directly from the app.");
        predefinedResponses.put("how can i get directions to a hospital",
                "Select a hospital from the map or list, and tap the **\"Get Directions\"** button to open Google Maps with navigation.");
        predefinedResponses.put("does this app work without the internet",
                "The app needs an internet connection to fetch hospital locations and maps, but first-aid instructions are available offline.");

        // First Aid Questions
        predefinedResponses.put("where can i find first-aid instructions",
                "Go to the **\"First Aid\"** section to find guides on treating **burns, strokes, heart attacks, choking, and more.**");
        predefinedResponses.put("what should i do if someone is having a heart attack",
                "Call **112** immediately, help them sit down, loosen tight clothing, and **offer aspirin if available**.");
        predefinedResponses.put("how do i stop severe bleeding",
                "Apply firm pressure on the wound with a **clean cloth**, keep the injured area elevated, and call **emergency services**.");
        predefinedResponses.put("can i call emergency services from the app",
                "Yes! Every **first-aid guide** has an **emergency call button** that dials **112** for quick help.");

        // Account Questions
        predefinedResponses.put("do i need to create an account to use the app",
                "No, you can **skip login** and still access hospital search and first-aid guides.");
        predefinedResponses.put("how do i sign up",
                "Tap **\"Sign Up\"**, enter your **name, email, and password**, and create your account.");
        predefinedResponses.put("what if i forget my password",
                "The app currently doesn't have a password reset feature. You may need to **create a new account**.");
        predefinedResponses.put("can i use this app without giving location access",
                "The app works without location access, but **it won't show nearby hospitals.**");

        // Technical Questions
        predefinedResponses.put("why is the app not showing my location",
                "Make sure your **GPS is enabled** and **location permissions** are granted in your phone settings.");
        predefinedResponses.put("what if the hospital list is not loading",
                "Check your **internet connection**, restart the app, and try again.");
        predefinedResponses.put("how accurate is the hospital location data",
                "The app fetches **real-time data from Google Maps**, so accuracy depends on Google's database.");
        predefinedResponses.put("can i report incorrect hospital details",
                "Currently, there is no option to report incorrect data, but you can verify hospital information using Google Maps.");

        // Emergency Questions
        predefinedResponses.put("what should i do if i need urgent medical help",
                "Call **112** immediately and follow the **first-aid steps** provided in the app.");
        predefinedResponses.put("how do i find a doctor for an online consultation",
                "Tap **\"Find Doctor\"**, and it will open a trusted website where you can book a doctor's appointment.");
        predefinedResponses.put("can i order medicine through this app",
                "Yes! Tap **\"Buy Medicine\"** to visit an online pharmacy and place an order.");
    }

    public static void getResponse(ChatFutures chatModel, String query, ResponseCallBack callBack) {
        // Normalize the query for matching (remove punctuation, convert to lowercase)
        String normalizedQuery = query.toLowerCase().replaceAll("[.?!,]", "").trim();

        // Check for exact match with predefined questions
        String exactMatchResponse = predefinedResponses.get(normalizedQuery);
        if (exactMatchResponse != null) {
            callBack.onResponse(exactMatchResponse);
            return;
        }

        // Check if the query contains a predefined question keyword structure
        boolean isCommonMedicalQuestion = checkIfCommonMedicalQuestion(normalizedQuery);

        if (isCommonMedicalQuestion) {
            // Try to match with close predefined questions
            for (Map.Entry<String, String> entry : predefinedResponses.entrySet()) {
                double similarity = calculateSimilarity(normalizedQuery, entry.getKey());
                if (similarity > 0.75) { // Higher threshold for better precision
                    callBack.onResponse(entry.getValue());
                    return;
                }
            }
        }

        // If we reach here, this is a random question - use the Gemini model
        String contextPrompt = "You are a medical emergency assistant chatbot in a healthcare app. " +
                "Provide helpful, concise answers about first aid, hospital locations, emergency services, " +
                "and general health advice. Keep responses short and direct. " +
                "For the question: \"" + query + "\", provide a brief, informative answer.";

        Content.Builder userMessageBuilder = new Content.Builder();
        userMessageBuilder.setRole("user");
        userMessageBuilder.addText(contextPrompt);
        Content userMessage = userMessageBuilder.build();

        Executor executor = Runnable::run;

        ListenableFuture<GenerateContentResponse> response = chatModel.sendMessage(userMessage);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                callBack.onResponse(resultText);
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
                callBack.onError(throwable);
            }
        }, executor);
    }

    // Helper method to check if query looks like a typical medical/app question
    private static boolean checkIfCommonMedicalQuestion(String query) {
        // Common question patterns related to the app's functions
        String[] patterns = {
                "how", "where", "what", "can i", "find", "hospital", "ambulance",
                "emergency", "first aid", "account", "login", "password", "app",
                "location", "medicine", "doctor", "bleeding", "heart attack"
        };

        // If it contains at least 2 of these keywords, it might be a common question
        int matches = 0;
        for (String pattern : patterns) {
            if (query.contains(pattern)) {
                matches++;
                if (matches >= 2) return true;
            }
        }

        return false;
    }

    // Improved similarity calculation using Jaccard similarity
    private static double calculateSimilarity(String query, String predefinedQuestion) {
        // Split into words
        String[] queryWords = query.split("\\s+");
        String[] predefinedWords = predefinedQuestion.split("\\s+");

        // Count matches
        int matches = 0;
        for (String qWord : queryWords) {
            if (qWord.length() <= 2) continue; // Skip short words

            for (String pWord : predefinedWords) {
                if (pWord.length() <= 2) continue;

                if (qWord.equals(pWord)) {
                    matches++;
                    break;
                }
            }
        }

        // Jaccard similarity: intersection size / union size
        int totalUniqueWords = countUniqueWords(queryWords, predefinedWords);

        return (double) matches / totalUniqueWords;
    }

    private static int countUniqueWords(String[] arr1, String[] arr2) {
        Map<String, Boolean> wordMap = new HashMap<>();

        // Filter short words and add to map
        for (String word : arr1) {
            if (word.length() > 2) {
                wordMap.put(word, true);
            }
        }

        for (String word : arr2) {
            if (word.length() > 2) {
                wordMap.put(word, true);
            }
        }

        return Math.max(1, wordMap.size()); // Avoid division by zero
    }

    public GenerativeModelFutures getModel() {
        String apiKey = BuildConfig.apiKey;

        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH);

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.7f;
        configBuilder.topK = 16;
        configBuilder.topP = 0.1f;
        configBuilder.maxOutputTokens = 200; // Slightly increased for more complete answers
        GenerationConfig generationConfig = configBuilder.build();

        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                apiKey,
                generationConfig,
                Collections.singletonList(harassmentSafety)
        );

        return GenerativeModelFutures.from(gm);
    }
}