package com.example.nestera;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.nestera.Activity.hoaDon_Activity;
import com.example.nestera.Activity.hopDong_Activity;
import com.example.nestera.Activity.loaiPhong_Activity;
import com.example.nestera.Activity.nguoiThue_Activity;
import com.example.nestera.Activity.phong_Activity;
import com.example.nestera.Activity.suCo_Activity;
import com.example.nestera.Activity.thongKe_Activity;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Fragment.frg_NganHang;
import com.example.nestera.Fragment.frg_doimatkhau;
import com.example.nestera.Fragment.frg_thongtintaikhoan;
import com.example.nestera.model.NguoiThue;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView nav;
    View mHeader;
    AdView mAdView;
    Button btnLoaiPhong, btnPhong, btnNguoiThue, btnThongKe, btnHoaDon, btnSuCo,btnHopDong;
    nguoiThueDao dao;
    TextView txtUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);




        drawerLayout = findViewById(R.id.drawerlayout);
        toolbar = findViewById(R.id.toolbar);
        nav = findViewById(R.id.nav);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mHeader = nav.getHeaderView(0);
        txtUser = mHeader.findViewById(R.id.txtUser);
        btnLoaiPhong = findViewById(R.id.btnLoaiPhong);
        btnHoaDon = findViewById(R.id.btnHoaDon);
        btnPhong = findViewById(R.id.btnPhong);
        btnNguoiThue = findViewById(R.id.btnNguoiThue);
        btnThongKe = findViewById(R.id.btnThongKe);
        btnSuCo = findViewById(R.id.btnSuCo);
        btnHopDong = findViewById(R.id.btnHopDong);
        Button btnBaiDang = new Button(this);
        btnBaiDang.setId(View.generateViewId());
        btnBaiDang.setText("Bài đăng");
        btnBaiDang.setBackgroundResource(R.drawable.khungedt);
        btnBaiDang.setTextColor(getResources().getColor(R.color.white));
        btnBaiDang.setPadding(20,20,20,20);
        btnLoaiPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentClass(loaiPhong_Activity.class);

            }
        });
        btnHoaDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentClass(hoaDon_Activity.class);
            }
        });
        btnPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentClass(phong_Activity.class);
            }
        });
        btnNguoiThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("MainActivity", "Opening nguoiThue_Activity");
                try {
                    IntentClass(nguoiThue_Activity.class);
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "Error opening nguoiThue_Activity", e);
                    Toast.makeText(MainActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        btnThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentClass(thongKe_Activity.class);
            }
        });
        btnSuCo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentClass(suCo_Activity.class);
            }
        });
        btnHopDong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentClass(hopDong_Activity.class);
            }
        });
        btnBaiDang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentClass(com.example.nestera.Activity.BaiDangActivity.class);
            }
        });

        nav.setItemIconTintList(null);
        drawerLayout.closeDrawers();
        SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        String role = preferences.getString("role", "");
        Bundle bundle = new Bundle();
        bundle.putString("key", username);
        frg_thongtintaikhoan frgThongtintaikhoan=new frg_thongtintaikhoan();
        frgThongtintaikhoan.setArguments(bundle);


        String user = username;
        if ("LANDLORD".equalsIgnoreCase(role) || user.equalsIgnoreCase("landlord")) {
            // Chủ trọ: ẩn mục dành cho người thuê
            nav.getMenu().findItem(R.id.nav_ChangePass).setVisible(false);
            nav.getMenu().findItem(R.id.nav_profileUser).setVisible(false);
            // Ẩn "Kiểu Phòng" theo yêu cầu
            btnLoaiPhong.setVisibility(View.GONE);
            // Hiển thị nút "Quản lý hợp đồng" và dùng icon Kiểu Phòng
            btnHopDong.setVisibility(View.VISIBLE);
            btnHopDong.setText("Quản lý hợp đồng");
            btnHopDong.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_coso, 0, 0, 0);
            btnHopDong.setCompoundDrawablePadding(16);
            btnHopDong.setGravity(android.view.Gravity.CENTER);
            btnHopDong.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            // Lấy tên chủ trọ để hiển thị Welcome + tên
            try {
                com.example.nestera.Dao.chuTroDao ctDao = new com.example.nestera.Dao.chuTroDao(this);
                com.example.nestera.model.ChuTro ct = ctDao.getID(user);
                if (ct != null && ct.getTenChuTro() != null && !ct.getTenChuTro().isEmpty()) {
                    txtUser.setText("Welcome " + ct.getTenChuTro());
                } else {
                    txtUser.setText("Welcome " + user);
                }
            } catch (Exception e) {
                txtUser.setText("Welcome " + user);
            }
            // thêm nút Bài đăng cạnh các nút landlord
            ((android.widget.LinearLayout)((android.widget.FrameLayout)findViewById(R.id.frmnav)).getChildAt(0)).addView(btnBaiDang);
        } else if ("ADMIN".equalsIgnoreCase(role) || user.equalsIgnoreCase("admin")) {
            // Admin: có thể hiển thị menu dành cho Admin sau
            btnHopDong.setVisibility(View.GONE);
            txtUser.setText("Welcome Admin");
        } else {
            // Người thuê
            btnLoaiPhong.setVisibility(View.GONE);
            btnNguoiThue.setVisibility(View.GONE);
            btnThongKe.setVisibility(View.GONE);
            btnPhong.setVisibility(View.GONE);
            dao = new nguoiThueDao(this);
            NguoiThue nt = dao.getID(user);
            String u = nt != null && nt.getTenNguoiThue()!=null ? nt.getTenNguoiThue() : user;
            txtUser.setText("Welcome "+u);
            nav.getMenu().findItem(R.id.nav_NganHang).setVisible(false);
            // người dùng cũng có nút Bài đăng
            ((android.widget.LinearLayout)((android.widget.FrameLayout)findViewById(R.id.frmnav)).getChildAt(0)).addView(btnBaiDang);
        }
        // Show/hide admin items depending on role
        if (!"ADMIN".equalsIgnoreCase(role)) {
            nav.getMenu().findItem(R.id.nav_AdminDashboard).setVisible(false);
            nav.getMenu().findItem(R.id.nav_ManageAccounts).setVisible(false);
        }

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.nav_profileUser){
                    setTitle("Thông tin tài khoản");

                    replaceFrg(frgThongtintaikhoan);
                }else if (item.getItemId()==R.id.nav_Home){
                    IntentClass(MainActivity.class);
                } else if (item.getItemId()==R.id.nav_Loguot) {
                    // Xóa thông tin đăng nhập
                    SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    // Quay về trang Catalog
                    startActivity(new Intent(getApplicationContext(), com.example.nestera.Activity.CatalogActivity.class));
                    finish();
                }else if(item.getItemId()==R.id.nav_ChangePass){
                    setTitle("Đổi mật khẩu");
                    frg_doimatkhau frg_dmk=new frg_doimatkhau();
                    replaceFrg(frg_dmk);
                }else if (item.getItemId()==R.id.nav_NganHang){
                    setTitle("Ngân Hàng Thanh Toán");
                    frg_NganHang frg_nh=new frg_NganHang();
                    replaceFrg(frg_nh);
                } else if (item.getItemId() == R.id.nav_AdminDashboard) {
                    IntentClass(com.example.nestera.Activity.AdminDashboardActivity.class);
                } else if (item.getItemId() == R.id.nav_ManageAccounts) {
                    IntentClass(com.example.nestera.Activity.ManageAccountsActivity.class);
                }

                drawerLayout.close();
                return true;
            }

        });


    }

    public void replaceFrg(Fragment frg) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frmnav, frg).commit();
    }

    public void IntentClass(Class target) {
        Intent intent = new Intent(MainActivity.this, target);
        startActivity(intent);
    }
}