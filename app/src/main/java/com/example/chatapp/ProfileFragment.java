package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatapp.models.UserModel;
import com.example.chatapp.utils.AndroidUtil;
import com.example.chatapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;


public class ProfileFragment extends Fragment {
    ImageView profilepic;
    TextView profile_logouttxtview;
    EditText profile_username;
    EditText profile_phonenum;
    Button profile_updatebtn;
    ProgressBar progressBar;
    UserModel currentusermodel;


    public ProfileFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        profilepic=view.findViewById(R.id.profile_pic_view);
        profile_logouttxtview=view.findViewById(R.id.profile_logout);
        profile_username=view.findViewById(R.id.profile_username);
        profile_phonenum=view.findViewById(R.id.profile_phonenumber);
        profile_updatebtn=view.findViewById(R.id.profile_updatebtn);
        progressBar=view.findViewById(R.id.profile_progressbar);

        getuserdetails();
        profile_updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBtnClick();
            }
        });

        profile_logouttxtview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.logout();
                Intent intent=new Intent(getContext(),splashactivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        return view;

    }

    private void updateBtnClick() {
        String username=profile_username.getText().toString();
        if (username.isEmpty() || username.length()<3){
            profile_username.setError("user name should be atleast 3 char");
            return;
        }
        currentusermodel.setUsername(username);
        setprogress(true);
        updatefirestore();
    }

    void  updatefirestore(){
        FirebaseUtil.currentuserdetaild().set(currentusermodel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setprogress(false);
                       if (task.isSuccessful()){
                           AndroidUtil.showtoast(getContext(),"Updated Successfully");
                       }
                       else {
                           AndroidUtil.showtoast(getContext(),"Update failed");
                       }
                    }
                });
    }

    private void getuserdetails() {
        setprogress(true);
        FirebaseUtil.currentuserdetaild().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setprogress(false);
                currentusermodel=task.getResult().toObject(UserModel.class);
                profile_username.setText(currentusermodel.getUsername());
                profile_phonenum.setText(currentusermodel.getPhonenumber());

            }
        });
    }
    void setprogress(boolean inprogress){
        if (inprogress){
            progressBar.setVisibility(View.VISIBLE);
            profile_updatebtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            profile_updatebtn.setVisibility(View.VISIBLE);
        }
    }
}