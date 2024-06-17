package com.example.chatapp.adapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.ChatActivity;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.utils.AndroidUtil;
import com.example.chatapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {
    Context context;


    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int i, @NonNull UserModel model) {
        holder.usernametxt.setText(model.getUsername());
        holder.phonetxt.setText(model.getPhonenumber());
        if (model.getUserId().equals(FirebaseUtil.currentuserid())){
            holder.usernametxt.setText(model.getUsername()+"(ME)");

        }
        FirebaseUtil.getotherprofilepivstorageref(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> t) {
                        if (t.isSuccessful()){
                            Uri uri=t.getResult();
                            AndroidUtil.setProfilePic(context,uri,holder.profilepic);
                        }
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                AndroidUtil.passuserdetailsasintent(intent,model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.search_useer_recyle_row,parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernametxt;
        TextView phonetxt;
        ImageView profilepic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernametxt=itemView.findViewById(R.id.user_profile_name);
            phonetxt=itemView.findViewById(R.id.user_phone_number);
            profilepic=itemView.findViewById(R.id.profile_pic_view);

        }
    }
}
