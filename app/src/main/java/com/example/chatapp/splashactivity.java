package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.utils.FirebaseUtil;

public class splashactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashactivity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseUtil.isloggedin()) {
                    startActivity(new Intent(splashactivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(splashactivity.this, login_activity.class));
                }

                finish();
            }
        },1000);


    }
}