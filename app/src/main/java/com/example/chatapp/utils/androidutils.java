package com.example.chatapp.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.chatapp.models.usermodel;

public class androidutils {
    public static void showtoast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    public static void passuserdetailsasintent(Intent intent, usermodel model){
        intent.putExtra("username",model.getUsername());
        intent.putExtra("phone",model.getPhonenumber());
        intent.putExtra("userId",model.getUserId());
    }
    public static   usermodel getuserdataforintent(Intent intent){
        usermodel userModel=new usermodel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhonenumber(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;


    }
}
