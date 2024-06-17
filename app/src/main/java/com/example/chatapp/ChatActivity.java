package com.example.chatapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.ChatRecyclerAdapter;
import com.example.chatapp.models.ChatMessageModel;
import com.example.chatapp.models.ChatroomModel;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.utils.AndroidUtil;
import com.example.chatapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    UserModel chatuser;
    EditText chatedittxt;
    TextView chat_txtview;
    TextView chat_onlinestatus; // NEW
    ImageButton chat_returnbtn;
    ImageButton chat_sendbtn;
    RecyclerView recyclerView;
    String chatroomID;
    ChatMessageModel chatMessageModel;
    ChatroomModel chatRoommodel;
    ChatRecyclerAdapter adapter;
    ImageView profilepic;
    ListenerRegistration userStatusListener;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
// NEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatacticity);

        chatuser = AndroidUtil.getuserdataforintent(getIntent());
        chatroomID = FirebaseUtil.getchatroomId(FirebaseUtil.currentuserid(), chatuser.getUserId());

        // Initialize views
        chatedittxt = findViewById(R.id.chat_edittxt);
        chat_txtview = findViewById(R.id.chat_txtview);
        chat_returnbtn = findViewById(R.id.chat_back_btn);
        chat_sendbtn = findViewById(R.id.send_message_btn);
        chat_onlinestatus = findViewById(R.id.chat_onlinestatus); // NEW
        recyclerView = findViewById(R.id.chat_recylerview);
        profilepic=findViewById(R.id.profile_pic_view);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Firebase User
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = FirebaseUtil.currentuserid();
            userRef = db.collection("users").document(uid);
            setUserOnlineStatus(true);  // Set user as online when the app starts
        }
        FirebaseUtil.getotherprofilepivstorageref(chatuser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> t) {
                        if (t.isSuccessful()){
                            Uri uri=t.getResult();
                            AndroidUtil.setProfilePic(ChatActivity.this,uri,profilepic);
                        }
                    }
                });

        // Retrieve the user model from the intent

        chat_returnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        chat_txtview.setText(chatuser.getUsername());

        chat_sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=chatedittxt.getText().toString().trim();
                if (message.isEmpty()){
                    return;
                }
                sendmessagetouser(message);
            }
        });

        getorcreateChatroom();
        setupchatrecyleview();
        setonlinestatus();

    }


    private void setonlinestatus() {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(chatuser.getUserId());
        userStatusListener = userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Boolean isOnline = snapshot.getBoolean("isOnline");
                    Timestamp lastOnline = snapshot.getTimestamp("lastOnline");

                    if (isOnline != null && isOnline) {
                        chat_onlinestatus.setText("Online");
                        chat_onlinestatus.setVisibility(View.VISIBLE);
                    } else if (lastOnline != null) {
                        String lastOnlineText = "Last online: " + new SimpleDateFormat("HH:mm").format(lastOnline.toDate());
                        chat_onlinestatus.setText(lastOnlineText);
                        chat_onlinestatus.setVisibility(View.VISIBLE);
                    } else {
                        chat_onlinestatus.setText("Offline");
                        chat_onlinestatus.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void setupchatrecyleview() {
        Query query = FirebaseUtil.getChatRoomMessageReference(chatroomID)
                .orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, ChatActivity.this,chatroomID);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });

    }

    private void sendmessagetouser(String message) {
        String messageId = FirebaseUtil.getChatRoomMessageReference(chatroomID).document().getId();



        // Update chat room model with the latest message details
        chatRoommodel.setLastmessagesenttimestap(Timestamp.now());
        chatRoommodel.setLastmessagesentid(FirebaseUtil.currentuserid());
        chatRoommodel.setLastmessage(message);
        chatRoommodel.setLastmessageID(messageId);



        // Update the chat room document in Firestore
        FirebaseUtil.getchatroomreference(chatroomID).set(chatRoommodel);

        // Generate a unique ID for the messag

        // Create a new ChatMessageModel instance with unique message ID
         chatMessageModel = new ChatMessageModel(messageId, message, FirebaseUtil.currentuserid(), Timestamp.now());

        // Add the new message to the chat room's message collection in Firestore
        FirebaseUtil.getChatRoomMessageReference(chatroomID).document(messageId).set(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Clear the message input field after sending
                            chatedittxt.setText("");
                        } else {
                            // Handle error if message sending fails
                            Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void getorcreateChatroom() {
        FirebaseUtil.getchatroomreference(chatroomID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatRoommodel = task.getResult().toObject(ChatroomModel.class);
                if (chatRoommodel == null) {
                    chatRoommodel = new ChatroomModel(
                            chatroomID,
                            Arrays.asList(FirebaseUtil.currentuserid(), chatuser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getchatroomreference(chatroomID).set(chatRoommodel);
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        setUserOnlineStatus(true);

    }

    private void setUserOnlineStatus(boolean isOnline) {
        if (isOnline) {
            userRef.update("isOnline", isOnline);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userStatusListener != null) {
            userStatusListener.remove();
        }
    }
}