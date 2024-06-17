package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chatapp.utils.AndroidUtil;
import com.example.chatapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    ImageButton search_btn;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    NavigationView navigationView;
    TextView headername;
    ImageView imageView;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private ListenerRegistration onlineStatusListener;

    Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Set up Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Get header view from NavigationView
        View headerView = navigationView.getHeaderView(0);
        headername = headerView.findViewById(R.id.header_username);
        imageView = headerView.findViewById(R.id.header_profile_pic_view);

        // Set up BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        search_btn = findViewById(R.id.main_search_btn);
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        search_btn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, searchuseractivity.class)));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.chat_menu) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment).commit();
                setMenuNavigationItem(R.id.nav_chat);
                return true;
            } else if (item.getItemId() == R.id.chat_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
                setMenuNavigationItem(R.id.nav_profile);
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.chat_menu);
        setHeaderDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Update user status to online
        if (userRef != null) {
            setUserOnlineStatus(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Update user status to offline
        if (userRef != null) {
            setUserOnlineStatus(false);
            userRef.update("lastOnline", Timestamp.now());
        }
    }

    private void setUserOnlineStatus(boolean isOnline) {
        if (userRef != null) {
            userRef.update("isOnline", isOnline);
        }
    }

    private void setHeaderDetails() {
        if (userRef != null) {
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    headername.setText(username);
                    FirebaseUtil.getCurrentprofilepivstorageref().getDownloadUrl()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Uri uri = task.getResult();
                                    AndroidUtil.setProfilePic(MainActivity.this, uri, imageView);
                                }
                            });
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemid = menuItem.getItemId();
        if (itemid == R.id.nav_chat) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment).commit();
            setBottomNavigationItem(R.id.chat_menu);
        } else if (itemid == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
            setBottomNavigationItem(R.id.chat_profile);
        } else if (itemid == R.id.nav_search) {
            startActivity(new Intent(MainActivity.this, searchuseractivity.class));
            return true;
        } else if (itemid == R.id.nav_logout) {
            signOut();
            return true;
        } else if (itemid == R.id.nav_share) {
            Toast.makeText(this, "Shared Successfully", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemid == R.id.nav_about) {
            Toast.makeText(this, "my details", Toast.LENGTH_SHORT).show();
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setBottomNavigationItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }

    private void setMenuNavigationItem(int itemId) {
        navigationView.setCheckedItem(itemId);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void signOut() {
        FirebaseUtil.logout();
        Intent intent = new Intent(MainActivity.this, splashactivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}