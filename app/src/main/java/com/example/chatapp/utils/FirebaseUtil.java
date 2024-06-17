package com.example.chatapp.utils;

import static android.view.View.VISIBLE;

import android.view.View;
import android.widget.TextView;

import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {


    public static String currentuserid() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static Boolean isloggedin() {
        if (currentuserid() != null) {
            return true;
        }
        return false;
    }

    public static DocumentReference currentuserdetaild() {
        return FirebaseFirestore.getInstance().collection("users").document(currentuserid());
    }

    public static CollectionReference alluserCollectionRefference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getchatroomreference(String chatroomID) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomID);
    }

    public static CollectionReference getChatRoomMessageReference(String chatroomid) {
        return getchatroomreference(chatroomid).collection("chats");

    }

    public static String getchatroomId(String userid1, String userid2) {
        if (userid1.hashCode() < userid2.hashCode()) {
            return userid1 + "_" + userid2;
        } else {
            return userid2 + "_" + userid1;
        }

    }

    public static CollectionReference allchatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentuserid())) {
            return alluserCollectionRefference().document(userIds.get(1));
        } else {
            return alluserCollectionRefference().document(userIds.get(0));
        }

    }

    public static String timestamptostring(Timestamp timestamp) {
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentprofilepivstorageref() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(FirebaseUtil.currentuserid());
    }

    public static StorageReference getotherprofilepivstorageref(String otheruser) {
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(otheruser);
    }




    }




