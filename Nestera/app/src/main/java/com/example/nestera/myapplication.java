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
        
        // Auto-create default admin account (runs once)
        createDefaultAdminIfNeeded();
        
        // Configure Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                        .build();
        db.setFirestoreSettings(settings);

        phuongthuc();
    }
    
    /**
     * Tự động tạo tài khoản admin mặc định nếu chưa tồn tại
     * Chỉ chạy 1 lần khi app khởi động lần đầu
     */
    private void createDefaultAdminIfNeeded() {
        SharedPreferences prefs = getSharedPreferences("app_setup", MODE_PRIVATE);
        boolean adminCreated = prefs.getBoolean("admin_created", false);
        
        if (!adminCreated) {
            try {
                chuTroDao dao = new chuTroDao(this);
                
                // Kiểm tra admin đã tồn tại chưa
                ChuTro existing = dao.getID("admin");
                
                if (existing == null) {
                    // Tạo tài khoản admin mặc định
                    ChuTro admin = new ChuTro();
                    admin.setMaChuTro("admin");
                    admin.setTenChuTro("Administrator");
                    admin.setMatKhau("NesteraAdmin22102005@"); // Nên đổi sau khi login
                    admin.setEmail("admin@nestera.com");
                    admin.setSdt("0123456789");
                    admin.setCccd("0000000000");
                    admin.setApproved(1); // Đã được duyệt
                    admin.setBanned(0);   // Không bị khóa
                    
                    long result = dao.insert(admin);
                    
                    if (result > 0) {
                        Log.i("AdminSetup", "✅ Default admin account created successfully");
                        Log.i("AdminSetup", "Username: admin | Password: NesteraAdmin22102005@");
                        
                        // Đánh dấu đã tạo admin
                        prefs.edit().putBoolean("admin_created", true).apply();
                    } else {
                        Log.e("AdminSetup", "❌ Failed to create admin account");
                    }
                } else {
                    Log.i("AdminSetup", "Admin account already exists");
                    prefs.edit().putBoolean("admin_created", true).apply();
                }
            } catch (Exception e) {
                Log.e("AdminSetup", "Error creating admin account", e);
            }
        }
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
