package com.example.chatapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class firebaseutils {

    public  static  String currentuserid(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static  Boolean isloggedin(){
        if (currentuserid()!=null){
            return true;
        }
        return false;
    }
     public  static DocumentReference currentuserdetaild(){
         return FirebaseFirestore.getInstance().collection("users").document(currentuserid());
     }

     public static CollectionReference alluserCollectionRefference(){
        return FirebaseFirestore.getInstance().collection("users");
     }
     public static DocumentReference getchatroomreference(String chatroomID){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomID);
     }

     public static CollectionReference getChatRoomMessageReference(String chatroomid){
        return getchatroomreference(chatroomid).collection("chats");

     }

     public static String getchatroomId(String userid1,String userid2){
        if (userid1.hashCode()<userid2.hashCode()){
            return userid1+"_"+userid2;
        }
        else {
            return userid2+"_"+userid1;
        }

     }
}
