package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hbb20.CountryCodePicker;

public class login_activity extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    Button login_sendotpbtn;
    EditText login_mobile;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        countryCodePicker=findViewById(R.id.login_countrycode);
        login_mobile=findViewById(R.id.login_mobilenumber);
        login_sendotpbtn=findViewById(R.id.login_sendotpbtn);
        progressBar=findViewById(R.id.login_progressbar);

        progressBar.setVisibility(View.GONE);
        countryCodePicker.registerCarrierNumberEditText(login_mobile);



        login_sendotpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!countryCodePicker.isValidFullNumber()){
                    login_mobile.setError("Phone number is not valid");
                    return;
                }
                Intent intent=new Intent(login_activity.this,otploginactivity.class);
                intent.putExtra("name",countryCodePicker.getFullNumberWithPlus());
                startActivity(intent);
            }
        });



    }
}