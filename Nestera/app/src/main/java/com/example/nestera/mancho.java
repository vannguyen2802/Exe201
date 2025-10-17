package com.example.nestera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;

public class mancho extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mancho);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        LottieAnimationView animationView = findViewById(R.id.anm);
        animationView.setSpeed(0.5f);
        animationView.playAnimation();
        // Determine next screen based on saved login session
        SharedPreferences prefs = getSharedPreferences("user11", MODE_PRIVATE);
        final String username = prefs.getString("username11", "");
        final String role = prefs.getString("role", "");

        final Class<?> nextActivity;
        if (username != null && !username.isEmpty()) {
            // Logged in previously -> go straight to appropriate dashboard
            if ("ADMIN".equalsIgnoreCase(role)) {
                nextActivity = com.example.nestera.Activity.AdminDashboardActivity.class;
            } else {
                nextActivity = com.example.nestera.MainActivity.class;
            }
        } else {
            // Not logged in -> show catalog/home
            nextActivity = com.example.nestera.Activity.CatalogActivity.class;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mancho.this, nextActivity));
                finish();
            }
        },5000);
    }
}