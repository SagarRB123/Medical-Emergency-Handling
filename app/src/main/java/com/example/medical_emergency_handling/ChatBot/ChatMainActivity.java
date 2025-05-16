package com.example.medical_emergency_handling.ChatBot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.medical_emergency_handling.R;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChatMainActivity extends AppCompatActivity {
    private TextInputEditText queryEditText;
    private ImageView sendQuery , appIcon;
    FloatingActionButton btnShowDialog;
    private ProgressBar progressBar2;

    private LinearLayout chatResponse;

    private ChatFutures chatModel;

    private Toolbar toolbar_chat;
    Dialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

        toolbar_chat = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("AI Assistant");
        toolbar_chat.setNavigationOnClickListener(v -> onBackPressed());

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.message_dialog);

        if(dialog.getWindow() != null)
        {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        sendQuery = dialog.findViewById(R.id.sendMessage);
        queryEditText = dialog.findViewById(R.id.queryEditText);

        btnShowDialog = findViewById(R.id.showMessage);
        progressBar2 = findViewById(R.id.progressBar2);
        chatResponse = findViewById(R.id.chatResponse);
        appIcon = findViewById(R.id.appIcon);

        chatModel = getChatModel();

        dialog.show();

        btnShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        sendQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                progressBar2.setVisibility(View.VISIBLE);
                appIcon.setVisibility(View.GONE);
                String query = queryEditText.getText().toString();

                queryEditText.setText("");

                chatBody("You" , query , getDrawable(R.drawable.user_icon));

                GeminiResponse.getResponse(chatModel, query, new ResponseCallBack() {
                    @Override
                    public void onResponse(String response) {
                        progressBar2.setVisibility(View.GONE);
                        chatBody("AI", response , getDrawable(R.drawable.ai_icon));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        chatBody("AI", "Please try again." , getDrawable(R.drawable.ai_icon));
                        progressBar2.setVisibility(View.GONE);

                    }
                });


            }
        });
    }
    private ChatFutures getChatModel(){

        GeminiResponse model = new GeminiResponse();
        GenerativeModelFutures modelFutures = model.getModel();

        return modelFutures.startChat();
    }

    @SuppressLint("MissingInflatedId")
    private void chatBody(String userName, String query, Drawable image) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_message,null);

        TextView name = view.findViewById(R.id.nameTextView);
        TextView message = view.findViewById(R.id.agentMessage);
        ImageView logo = view.findViewById(R.id.logo);

        name.setText(userName);
        message.setText(query);
        logo.setImageDrawable(image);

        chatResponse.addView(view);

        ScrollView scrollView = findViewById(R.id.scrollView_chat);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}