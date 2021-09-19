package com.example.shoppie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class homeactivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeactivity);
        mAuth = FirebaseAuth.getInstance();



    }


    public void profile(View view) {
        startActivity(new Intent(getApplicationContext(),profileactivity.class));
    }

    public void shop(View view) {
        startActivity(new Intent(getApplicationContext(),ImagesActivity.class));
    }

    public void logouthere(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),loginactivity.class));
        finish();
    }

    public void addItem(View view) {
        startActivity(new Intent(getApplicationContext(),shopactivity.class));
    }
}