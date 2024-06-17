package com.example.chatapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chatapp.models.UserModel;

public class AndroidUtil {
    public static void showtoast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    public static void passuserdetailsasintent(Intent intent, UserModel model){
        intent.putExtra("username",model.getUsername());
        intent.putExtra("phone",model.getPhonenumber());
        intent.putExtra("userId",model.getUserId());
    }
    public static UserModel getuserdataforintent(Intent intent){
        UserModel userModel=new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhonenumber(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }
    public static void setProfilePic(Context context, Uri imageuri, ImageView imageView){
        Glide.with(context).load(imageuri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
