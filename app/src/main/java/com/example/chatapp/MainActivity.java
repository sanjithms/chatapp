package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    ImageButton search_btn;
    chat_fragment chatFragment;
    profile_fragment profileFragment;
    NavigationView navigationView;
    private FirebaseAuth auth;


    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        auth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        search_btn=findViewById(R.id.main_search_btn);
        chatFragment=new chat_fragment();
        profileFragment=new profile_fragment();
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
        auth.signOut();
        Intent intent = new Intent(MainActivity.this, login_activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}