package com.example.chatapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.adapter.RecentChatRecylerAdapter;
import com.example.chatapp.models.chatroommodel;
import com.example.chatapp.utils.firebaseutils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class chat_fragment extends Fragment {
    RecyclerView recyclerView;
    RecentChatRecylerAdapter adapter;


        public chat_fragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat_fragment, container, false);
        recyclerView=view.findViewById(R.id.recyle_chat_fragment);
        setuprecylervhatview();
        return view;
    }

    private void setuprecylervhatview() {
        Query query = firebaseutils.allchatroomCollectionReference()
                .whereArrayContains("userIds",firebaseutils.currentuserid())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<chatroommodel> options = new FirestoreRecyclerOptions.Builder<chatroommodel>()
                .setQuery(query, chatroommodel.class).build();

        adapter = new RecentChatRecylerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }


    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}