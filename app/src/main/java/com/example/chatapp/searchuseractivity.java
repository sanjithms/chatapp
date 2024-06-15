package com.example.chatapp;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.search_user_recyle_adapter;
import com.example.chatapp.models.usermodel;
import com.example.chatapp.utils.firebaseutils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class searchuseractivity extends AppCompatActivity {
    private ImageButton search_btn;
    private ImageButton back_btn;
    private RecyclerView recyclerView;
    private EditText search_edittxt;
    private search_user_recyle_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchuseractivity);

        search_btn = findViewById(R.id.search_user_btn);
        back_btn = findViewById(R.id.back_btn);
        search_edittxt = findViewById(R.id.search_edittxt);
        recyclerView = findViewById(R.id.search_recycular_view);

        search_edittxt.requestFocus();

        back_btn.setOnClickListener(v -> onBackPressed());

        search_btn.setOnClickListener(v -> performSearch());

        search_edittxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (event == null || !event.isShiftPressed()) {
                        performSearch();
                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });

        setupRecyclerView(""); // Initialize with empty search
    }

    private void performSearch() {
        String searchuser = search_edittxt.getText().toString().trim();
        if (searchuser.isEmpty()) {
            search_edittxt.setError("Enter the Username");
            return;
        }
        setupRecyclerView(searchuser);
    }

    private void setupRecyclerView(String searchuser) {
        Query query = firebaseutils.alluserCollectionRefference()
                .whereGreaterThanOrEqualTo("username", searchuser)
                .whereLessThanOrEqualTo("username", searchuser + "\uf8ff");

        FirestoreRecyclerOptions<usermodel> options = new FirestoreRecyclerOptions.Builder<usermodel>()
                .setQuery(query, usermodel.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new search_user_recyle_adapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}
