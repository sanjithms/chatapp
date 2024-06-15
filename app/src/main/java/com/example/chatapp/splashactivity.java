package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp.utils.firebaseutils;

public class splashactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashactivity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseutils.isloggedin()) {
                    startActivity(new Intent(splashactivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(splashactivity.this, login_activity.class));
                }

                finish();
            }
        },1500);


    }
}