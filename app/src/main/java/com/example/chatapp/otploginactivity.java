package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class otploginactivity extends AppCompatActivity {
    Long timeout=30L;
    String phonenumber;
    Button login_next;
    TextView login_rsendotp;
    EditText login_otp;
    ProgressBar progressBar;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    String verification;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otploginactivity);
        phonenumber=getIntent().getExtras().getString("name");
        login_next=findViewById(R.id.login_next);
        login_otp=findViewById(R.id.login_otp);
        login_rsendotp=findViewById(R.id.login_resendotp);
        progressBar=findViewById(R.id.login_progressbar);
        sendotp(phonenumber,false);

        login_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredotp=login_otp.getText().toString();
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verification,enteredotp);
                signin(credential);
                setprogress(true);
            }
        });
        login_rsendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendotp(phonenumber,true);
            }
        });


    }
    void  sendotp(String phoneno,boolean isResend){
        startresendtimer();
        setprogress(true);
        PhoneAuthOptions.Builder builder=
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phonenumber)
                        .setTimeout(timeout, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signin(phoneAuthCredential);
                                setprogress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showtoast(getApplicationContext(),"OTP Verification failed");
                                setprogress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verification=s;
                                resendingToken=forceResendingToken;
                                AndroidUtil.showtoast(getApplicationContext(),"OTP Sent succesfull");
                                setprogress(false);
                            }
                        });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }
        else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void startresendtimer() {
        login_rsendotp.setEnabled(false);
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeout--;
                login_rsendotp.setText("Resend OTP in "+timeout+"seconds");
                if (timeout<=0){
                    timeout=30L;
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            login_rsendotp.setEnabled(true);
                        }
                    });
                }
            }
        },0,1000);
    }

    private void signin(PhoneAuthCredential phoneAuthCredential) {
        setprogress(true);
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setprogress(false);
                if (task.isSuccessful()){
                    Intent intent=new Intent(otploginactivity.this,usernameloginactivity.class);
                    intent.putExtra("phone",phonenumber);
                    startActivity(intent);
                }
                else {
                    AndroidUtil.showtoast(getApplicationContext(),"invalid OTP");
                }
            }
        });
    }

    void setprogress(boolean inprogress){
        if (inprogress){
            progressBar.setVisibility(View.VISIBLE);
            login_next.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            login_next.setVisibility(View.VISIBLE);
        }
    }
}