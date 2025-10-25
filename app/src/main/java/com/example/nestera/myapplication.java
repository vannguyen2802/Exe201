package com.example.nestera;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.nestera.Dao.chuTroDao;
import com.example.nestera.model.ChuTro;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;

public class myapplication extends Application {

    public static final String CHANNEL_ID="FPTPOLYTECHNIC";
    // CHANNEL_ID dc su dung de xac dinh kenh thong bao khi tao hoac cap nhat thong bao
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        
        // Anonymous Authentication để có quyền truy cập Firestore
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously()
                    .addOnSuccessListener(authResult -> {
                        Log.d("Firebase", "✅ Anonymous sign-in SUCCESS");
                        Log.d("Firebase", "User ID: " + authResult.getUser().getUid());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "❌ Anonymous sign-in FAILED", e);
                    });
        } else {
            Log.d("Firebase", "✅ User already signed in: " + auth.getCurrentUser().getUid());
        }
        
        // Configure Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                        .build();
        db.setFirestoreSettings(settings);

        phuongthuc();
    }
    
    // tao phuong thuc kenh thong bao
    public void phuongthuc(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O);
        // dang ky notificationChannel
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"FPT POLYTECHNIC",
                NotificationManager.IMPORTANCE_DEFAULT);
        // dang ky channel voi he thong
        NotificationManager manager = getSystemService(NotificationManager.class);
        if(manager!=null){
            manager.createNotificationChannel(channel);
        }

    }
}
