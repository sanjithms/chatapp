package com.example.chatapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatapp.models.UserModel;
import com.example.chatapp.utils.AndroidUtil;
import com.example.chatapp.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {
    ImageView profilepic;
    TextView profile_logouttxtview;
    EditText profile_username;
    EditText profile_phonenum;
    Button profile_updatebtn;
    ImageButton prfile_settings;
    ProgressBar progressBar;
    UserModel currentusermodel;
    ActivityResultLauncher<Intent> imagepickerlauncher;
    Uri selectedimageuri;


    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagepickerlauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                   if (result.getResultCode() == Activity.RESULT_OK){
                       Intent data=result.getData();
                       if (data!=null && data.getData()!=null){
                           selectedimageuri=data.getData();
                           AndroidUtil.setProfilePic(getContext(),selectedimageuri,profilepic);
                       }
                   }
                }
                );
    }

    @SuppressLint("MissingInflatedId")
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
        prfile_settings=view.findViewById(R.id.profile_settings);


        getuserdetails();
        prfile_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });

        profile_updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBtnClick();
            }
        });

        profile_logouttxtview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseUtil.logout();
                        Intent intent=new Intent(getContext(),splashactivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });

            }
        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ProfileFragment.this).cropSquare().compress(512).maxResultSize(512,512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagepickerlauncher.launch(intent);
                                return null;
                            }
                        });
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
        if (selectedimageuri!=null){
            FirebaseUtil.getCurrentprofilepivstorageref().putFile(selectedimageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    updatefirestore();
                }
            });
        }
        else {
            updatefirestore();
        }

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
        FirebaseUtil.getCurrentprofilepivstorageref().getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri uri = task.getResult();
                            // Check if the fragment is attached to its context before loading the image
                            if (isAdded() && getContext() != null) {
                                AndroidUtil.setProfilePic(getContext(), uri, profilepic);
                            }
                        }
                        setprogress(false);
                    }
                });

        FirebaseUtil.currentuserdetaild().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setprogress(false);
                if (task.isSuccessful()) {
                    currentusermodel = task.getResult().toObject(UserModel.class);
                    profile_username.setText(currentusermodel.getUsername());
                    profile_phonenum.setText(currentusermodel.getPhonenumber());
                }
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