package com.example.chatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    UserModel chatuser;
    EditText chatedittxt;
    TextView chat_txtview;
    ImageButton chat_returnbtn;
    ImageButton chat_sendbtn;
    RecyclerView recyclerView;
    String chatroomID;
    ChatroomModel chatRoommodel;
    ChatRecyclerAdapter adapter;

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
        recyclerView = findViewById(R.id.chat_recylerview);

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

    }



    private void setupchatrecyleview() {
        Query query = FirebaseUtil.getChatRoomMessageReference(chatroomID)
                .orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
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

        chatRoommodel.setLastmessagesenttimestap(Timestamp.now());
        chatRoommodel.setLastmessagesentid(FirebaseUtil.currentuserid());
        chatRoommodel.setLastmessage(message);

        FirebaseUtil.getchatroomreference(chatroomID).set(chatRoommodel);

        ChatMessageModel chatMessageModel=new ChatMessageModel(message, FirebaseUtil.currentuserid(),Timestamp.now());
        FirebaseUtil.getChatRoomMessageReference(chatroomID).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            chatedittxt.setText("");
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
}
