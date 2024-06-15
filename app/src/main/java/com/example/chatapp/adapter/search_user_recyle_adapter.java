package com.example.chatapp.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.chatacticity;
import com.example.chatapp.models.usermodel;
import com.example.chatapp.utils.androidutils;
import com.example.chatapp.utils.firebaseutils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class search_user_recyle_adapter extends FirestoreRecyclerAdapter<usermodel, search_user_recyle_adapter.UserModelViewHolder> {
    Context context;

    public search_user_recyle_adapter(@NonNull FirestoreRecyclerOptions<usermodel> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int i, @NonNull usermodel model) {
        holder.usernametxt.setText(model.getUsername());
        holder.phonetxt.setText(model.getPhonenumber());
        if (model.getUserId().equals(firebaseutils.currentuserid())){
            holder.usernametxt.setText(model.getUsername()+"(ME)");

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, chatacticity.class);
                androidutils.passuserdetailsasintent(intent,model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
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
        ImageView prifilepic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernametxt=itemView.findViewById(R.id.user_profile_name);
            phonetxt=itemView.findViewById(R.id.user_phone_number);
            prifilepic=itemView.findViewById(R.id.profile_pic_view);

        }
    }
}
