package com.example.nestera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mancho.this,dangnhap.class));
                finish();
            }
        },5000);
    }
}