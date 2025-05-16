package com.example.medical_emergency_handling.ChatBot;

public interface ResponseCallBack {

    void onResponse(String response);

    void onError(Throwable throwable);
}
