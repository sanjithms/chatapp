package com.example.chatapp;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.SearchUserRecyclerAdapter;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class searchuseractivity extends AppCompatActivity {
    ImageButton search_btn;
    ImageButton back_btn;
    RecyclerView recyclerView;
    EditText search_edittxt;
    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchuseractivity);

        search_btn = findViewById(R.id.search_user_btn);
        back_btn = findViewById(R.id.back_btn);
        search_edittxt = findViewById(R.id.search_edittxt);
        recyclerView = findViewById(R.id.search_recycular_view);

        search_edittxt.requestFocus();

        back_btn.setOnClickListener(v -> {
            onBackPressed();
        });

        search_btn.setOnClickListener(v -> {
            performSearch();
        });

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
        Query query = FirebaseUtil.alluserCollectionRefference()
                .whereGreaterThanOrEqualTo("username", searchuser)
                .whereLessThanOrEqualTo("username", searchuser + "\uf8ff");

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}
