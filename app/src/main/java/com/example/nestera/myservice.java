package com.example.nestera;

import static com.example.nestera.myapplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.nestera.Activity.hoaDon_Activity;


public class myservice extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //nhan du lieu
        String phong = intent.getStringExtra("phong");
        sendNotification(phong);
        return START_NOT_STICKY; // service se khoi dong lai khi co lenh startService()
        // return super.onStartCommand(intent,flags,startID);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Hủy service", Toast.LENGTH_SHORT).show();
    }
    private void sendNotification(String data){
        // su dung intent or bundle de nhan du lieu trong service
        Intent intent = new Intent(this, hoaDon_Activity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        // tao ra 1 thong bao va truyen no vao startForeground services
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID).setContentTitle("Hóa đơn mới phòng "+data)
                .setContentText("Chủ trọ đã thêm 1 hóa đơn phòng "+data)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent).build();
        // id cua startforeground se la 1 so lon hon 0
        startForeground(1,notification);
    }
}
