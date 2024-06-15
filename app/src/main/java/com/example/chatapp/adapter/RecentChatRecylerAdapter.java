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
import com.example.chatapp.models.chatroommodel;
import com.example.chatapp.models.usermodel;
import com.example.chatapp.utils.androidutils;
import com.example.chatapp.utils.firebaseutils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecylerAdapter extends FirestoreRecyclerAdapter<chatroommodel, RecentChatRecylerAdapter.chatroommodelViewHolder> {
    Context context;


    public RecentChatRecylerAdapter(@NonNull FirestoreRecyclerOptions<chatroommodel> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull chatroommodelViewHolder holder, int i, @NonNull chatroommodel model) {
        firebaseutils.getOtherUserFromChatroom(model.getUserids())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Boolean lastmessagesentByMe=model.getLastmessagesentid().equals(firebaseutils.currentuserid());

                        usermodel otherusermodel=task.getResult().toObject(usermodel.class);
                        holder.usernametxt.setText(otherusermodel.getUsername());
                        if (lastmessagesentByMe)
                            holder.Lastmessagetxt.setText(model.getLastmessage());
                        else
                            holder.Lastmessagetxt.setText("You:" +model.getLastmessage());

                        holder.Lastmessagetime.setText(firebaseutils.timestamptostring(model.getLastmessagesenttimestap()));

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(context, chatacticity.class);
                                androidutils.passuserdetailsasintent(intent,otherusermodel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        });
                    }
                });
    }

    @NonNull
    @Override
    public chatroommodelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recent_chat_recyle_row,parent,false);
        return new chatroommodelViewHolder(view);
    }

    class chatroommodelViewHolder extends RecyclerView.ViewHolder{
        TextView usernametxt;
        TextView Lastmessagetxt;
        TextView Lastmessagetime;
        ImageView prifilepic;

        public chatroommodelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernametxt=itemView.findViewById(R.id.recent_user_name);
            Lastmessagetxt=itemView.findViewById(R.id.recent_user_message);
            Lastmessagetime=itemView.findViewById(R.id.recent_message_time);
            prifilepic=itemView.findViewById(R.id.profile_pic_view);

        }
    }
}
