package com.example.nestera;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class myapplication extends Application {

    public static final String CHANNEL_ID="FPTPOLYTECHNIC";
    // CHANNEL_ID dc su dung de xac dinh kenh thong bao khi tao hoac cap nhat thong bao
    @Override
    public void onCreate() {
        super.onCreate();
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
