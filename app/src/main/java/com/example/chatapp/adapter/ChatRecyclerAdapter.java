package com.example.chatapp.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.models.ChatMessageModel;
import com.example.chatapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {
    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int i, @NonNull ChatMessageModel model) {
        if (model.getSenderId().equals(FirebaseUtil.currentuserid())){
            holder.leftchatlayout.setVisibility(View.GONE);
            holder.rightchatlayout.setVisibility(View.VISIBLE);
            holder.rightchattxtview.setText(model.getMessage());
        }
        else {
            holder.rightchatlayout.setVisibility(View.GONE);
            holder.leftchatlayout.setVisibility(View.VISIBLE);
            holder.leftchattxtview.setText(model.getMessage());
        }
        
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.chat_recyler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftchatlayout,rightchatlayout;
        TextView leftchattxtview,rightchattxtview;


        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftchatlayout=itemView.findViewById(R.id.left_chat_layout);
            rightchatlayout=itemView.findViewById(R.id.right_chat_layout);
            rightchattxtview=itemView.findViewById(R.id.right_chat_txtview);
            leftchattxtview=itemView.findViewById(R.id.left_chat_txtview);


        }
    }
}
