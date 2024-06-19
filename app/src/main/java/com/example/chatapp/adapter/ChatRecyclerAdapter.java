package com.example.chatapp.adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.models.ChatMessageModel;
import com.example.chatapp.models.ChatroomModel;
import com.example.chatapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {
    Context context;
    private String chatroomID; // Add this member variable

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context, String chatroomID) {
        super(options);
        this.context = context;
        this.chatroomID = chatroomID; // Initialize the member variable
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int i, @NonNull ChatMessageModel model) {
        if (model.getSenderId().equals(FirebaseUtil.currentuserid())) {
            holder.leftchatlayout.setVisibility(View.GONE);
            holder.rightchatlayout.setVisibility(View.VISIBLE);
            holder.rightchattxtview.setText(model.getMessage());
            holder.right_chat_time_txtview.setText(formatTimestamp(model.getTimestamp()));


            if (isMessageWithin24Hours(model.getTimestamp())) {
                // Enable long click listener for messages sent by the user within 24 hours
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDeleteDialog(model.getMessageId(),model.getTimestamp());
                        return true;
                    }
                });
            } else {
                // Disable long click listener if the message is older than 24 hours
                holder.itemView.setOnLongClickListener(null);
            }
        } else {
            holder.rightchatlayout.setVisibility(View.GONE);
            holder.leftchatlayout.setVisibility(View.VISIBLE);
            holder.leftchattxtview.setText(model.getMessage());
            holder.left_chat_time_txtview.setText(formatTimestamp(model.getTimestamp()));

            // Disable long click listener for messages not sent by the user
            holder.itemView.setOnLongClickListener(null);
        }
    }
    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date(timestamp.toDate().getTime()));
    }


    private boolean isMessageWithin24Hours(Timestamp messageTimestamp) {
        long currentTimeMillis = System.currentTimeMillis();
        long messageTimeMillis = messageTimestamp.toDate().getTime();
        return (currentTimeMillis - messageTimeMillis) < 1 * 60 * 60 * 1000; // 24 hours in milliseconds
    }


    private void showDeleteDialog(String messageId, Timestamp timestamp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete this message?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMessage(messageId,timestamp); // Method to delete message
            }
        });
        builder.setNegativeButton("Cancel", null);

        // Check if the context is still valid and the activity is not finishing
        if (context != null && context instanceof AppCompatActivity && !((AppCompatActivity) context).isFinishing()) {
            builder.show();
        }
    }


    private void deleteMessage(String messageId, Timestamp timestamp) {
        FirebaseUtil.getChatRoomMessageReference(chatroomID)
                .document(messageId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkAndUpdateLastMessage(messageId, timestamp);
                        Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndUpdateLastMessage(String messageId, Timestamp timestamp) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chatrooms").document(chatroomID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String lastMessageId = task.getResult().getString("lastmessageID");
                if (messageId.equals(lastMessageId)) {
                    updateLastMessage();

                }
            }
        });
    }

    private void updateLastMessage() {
        Log.d("ChatRecylerAdapter","successfull5");
        FirebaseUtil.getChatRoomMessageReference(chatroomID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        String lastMessageId = snapshot.getId();
                        String lastMessage = snapshot.getString("message");
                        Timestamp lastMessageTimestamp = snapshot.getTimestamp("timestamp");

                        // Update ChatroomModel with the new last message details
                        FirebaseFirestore.getInstance().collection("chatrooms")
                                .document(chatroomID)
                                .update("lastmessageID", lastMessageId,
                                        "lastmessage", lastMessage,
                                        "lastmessagesenttimestap", lastMessageTimestamp);
                    }

                });
    }





    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.chat_recyler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftchatlayout,rightchatlayout;
        TextView leftchattxtview,rightchattxtview,right_chat_time_txtview,left_chat_time_txtview;


        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftchatlayout=itemView.findViewById(R.id.left_chat_layout);
            rightchatlayout=itemView.findViewById(R.id.right_chat_layout);
            rightchattxtview=itemView.findViewById(R.id.right_chat_txtview);
            leftchattxtview=itemView.findViewById(R.id.left_chat_txtview);
            left_chat_time_txtview=itemView.findViewById(R.id.left_chat_time_txtview);
            right_chat_time_txtview=itemView.findViewById(R.id.right_chat_time_txtview);



        }
    }
}
