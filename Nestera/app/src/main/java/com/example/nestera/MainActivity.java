package com.example.nestera;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                IntentClass(nguoiThue_Activity.class);
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

        nav.setItemIconTintList(null);
        drawerLayout.closeDrawers();
        SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        Bundle bundle = new Bundle();
        bundle.putString("key", username);
        frg_thongtintaikhoan frgThongtintaikhoan=new frg_thongtintaikhoan();
        frgThongtintaikhoan.setArguments(bundle);


        String user = username;
        if(user.equalsIgnoreCase("admin")){
            nav.getMenu().findItem(R.id.nav_ChangePass).setVisible(false);
            nav.getMenu().findItem(R.id.nav_profileUser).setVisible(false);
            btnHopDong.setVisibility(View.GONE);
        }else {
            btnLoaiPhong.setVisibility(View.GONE);
            btnNguoiThue.setVisibility(View.GONE);
            btnThongKe.setVisibility(View.GONE);
            btnPhong.setVisibility(View.GONE);
            dao = new nguoiThueDao(this);
            NguoiThue nt = dao.getID(user);
            String u = nt.getTenNguoiThue();
            txtUser.setText("Welcome "+u);
            nav.getMenu().findItem(R.id.nav_NganHang).setVisible(false);
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
                    startActivity(new Intent(getApplicationContext(), dangnhap.class));
                    finish();
                }else if(item.getItemId()==R.id.nav_ChangePass){
                    setTitle("Đổi mật khẩu");
                    frg_doimatkhau frg_dmk=new frg_doimatkhau();
                    replaceFrg(frg_dmk);
                }else if (item.getItemId()==R.id.nav_NganHang){
                    setTitle("Ngân Hàng Thanh Toán");
                    frg_NganHang frg_nh=new frg_NganHang();
                    replaceFrg(frg_nh);
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