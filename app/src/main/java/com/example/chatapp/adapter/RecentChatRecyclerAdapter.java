package com.example.chatapp.adapter;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.ChatActivity;
import com.example.chatapp.models.ChatroomModel;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.utils.AndroidUtil;
import com.example.chatapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserids())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        if (otherUserModel != null) {
                            DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(otherUserModel.getUserId());
                            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        return;
                                    }

                                    if (snapshot != null && snapshot.exists()) {
                                        Boolean isOnline = snapshot.getBoolean("isOnline");
                                        Timestamp lastOnline = snapshot.getTimestamp("lastOnline");

                                        if (isOnline != null && isOnline) {
                                            holder.chat_recyleonlinestatus.setText("Online");
                                            holder.chat_recyleonlinestatus.setVisibility(View.VISIBLE);
                                        } else if (lastOnline != null) {
                                            holder.chat_recyleonlinestatus.setVisibility(View.GONE);
                                        } else {
                                            holder.chat_recyleonlinestatus.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            });

                            boolean lastMessageSentByMe = model.getLastmessagesentid().equals(FirebaseUtil.currentuserid());

                            FirebaseUtil.getotherprofilepivstorageref(otherUserModel.getUserId()).getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> t) {
                                            if (t.isSuccessful()) {
                                                Uri uri = t.getResult();
                                                AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                                            }
                                        }
                                    });

                            if (otherUserModel.getUserId().equals(FirebaseUtil.currentuserid())) {
                                holder.usernameText.setText(otherUserModel.getUsername() + " (YOU)");
                            } else {
                                holder.usernameText.setText(otherUserModel.getUsername());
                            }

                            if (lastMessageSentByMe) {
                                holder.lastMessageText.setText("You: " + model.getLastmessage());
                            } else {
                                holder.lastMessageText.setText(model.getLastmessage());
                            }

                            holder.lastMessageTime.setText(FirebaseUtil.timestamptostring(model.getLastmessagesenttimestap()));

                            holder.itemView.setOnClickListener(v -> {
                                // Navigate to chat activity
                                Intent intent = new Intent(context, ChatActivity.class);
                                AndroidUtil.passuserdetailsasintent(intent, otherUserModel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });
                        }
                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;
        ListenerRegistration userStatusListener;
        TextView chat_recyleonlinestatus;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_view);
            chat_recyleonlinestatus=itemView.findViewById(R.id.chat_Recyleonlinestatus);
        }
    }
}