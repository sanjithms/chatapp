package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chatapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    ImageButton search_btn;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    NavigationView navigationView;
    TextView headername;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private DocumentReference userRef;


    Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headername=findViewById(R.id.header_username);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = FirebaseUtil.currentuserid();
            userRef = db.collection("users").document(uid);
        }



        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        bottomNavigationView = findViewById(R.id.bottom_navigation);
        search_btn=findViewById(R.id.main_search_btn);
        chatFragment=new ChatFragment();
        profileFragment=new ProfileFragment();
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,searchuseractivity.class));
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.chat_menu) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment).commit();
                    setMenuNavigationItem(R.id.nav_chat);
                    return true;
                } else if (menuItem.getItemId() == R.id.chat_profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
                    setMenuNavigationItem(R.id.nav_profile);
                    return true;
                }
                return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.chat_menu);
        setHeaderDetails();




    }
    private void setHeaderDetails() {
        if (userRef != null) {
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        // Update TextView in navigation header
                        View headerView = navigationView.getHeaderView(0);
                        TextView headerUsername = headerView.findViewById(R.id.header_username);
                        headerUsername.setText(username);
                    }
                }
            });
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemid=menuItem.getItemId();
        if (itemid == R.id.nav_chat) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,chatFragment).commit();
            setBottomNavigationItem(R.id.chat_menu);
        } else if (itemid == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,profileFragment).commit();
            setBottomNavigationItem(R.id.chat_profile);

        } else if (itemid == R.id.nav_search) {
            startActivity(new Intent(MainActivity.this,searchuseractivity.class));
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
        Intent intent=new Intent(MainActivity.this,splashactivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void setheaderdetails() {
    }
}