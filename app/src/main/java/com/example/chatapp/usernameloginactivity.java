package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.models.UserModel;
import com.example.chatapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class usernameloginactivity extends AppCompatActivity {
    Button login_letschatbtn;
    ProgressBar progressBar;
    EditText login_username;
    String phonenunber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usernameloginactivity);
        login_letschatbtn=findViewById(R.id.login_letschat);
        login_username=findViewById(R.id.login_username);
        progressBar=findViewById(R.id.login_progressbar);
        phonenunber=getIntent().getExtras().getString("phone");
        getusername();
        login_letschatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setusername();
            }
        });


    }

    private void setusername() {

        String username=login_username.getText().toString();
        if (username.isEmpty() || username.length()<3){
            login_username.setError("user name should be atleast 3 char");
            return;
        }
        setprogress(true);

        if (userModel!=null){
            userModel.setUsername(username);
        }
        else {
            userModel=new UserModel(phonenunber,username, Timestamp.now(), FirebaseUtil.currentuserid());
        }
        FirebaseUtil.currentuserdetaild().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setprogress(false);
                if (task.isSuccessful()){
                    Intent intent=new Intent(usernameloginactivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    private void getusername() {
        setprogress(true);
        FirebaseUtil.currentuserdetaild().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setprogress(false);
                if (task.isSuccessful()){
                    userModel =task.getResult().toObject(UserModel.class);
                    if (userModel!=null){
                        login_username.setText(userModel.getUsername());
                    }
                }
            }
        });
    }

    void setprogress(boolean inprogress){
        if (inprogress){
            progressBar.setVisibility(View.VISIBLE);
            login_letschatbtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            login_letschatbtn.setVisibility(View.VISIBLE);
        }
    }
}