package com.example.nestera.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nestera.R;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

public class AdminDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        setTitle("Admin Dashboard");

        DrawerLayout drawerLayout = findViewById(R.id.admin_drawerlayout);
        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav = findViewById(R.id.admin_nav);
        nav.setItemIconTintList(null);
        // Ẩn các mục không dùng cho Admin - chỉ giữ lại Đăng xuất
        nav.getMenu().findItem(R.id.nav_profileUser).setVisible(false);
        nav.getMenu().findItem(R.id.nav_ChangePass).setVisible(false);
        nav.getMenu().findItem(R.id.nav_NganHang).setVisible(false);
        nav.getMenu().findItem(R.id.nav_Home).setVisible(false);
        nav.getMenu().findItem(R.id.nav_AdminDashboard).setVisible(false);
        nav.getMenu().findItem(R.id.nav_ManageAccounts).setVisible(false);

        nav.setNavigationItemSelectedListener(new com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(android.view.MenuItem item) {

                if (item.getItemId() == R.id.nav_Loguot) {
                    // Xóa session và đăng xuất
                    android.content.SharedPreferences prefs = getSharedPreferences("user11", MODE_PRIVATE);
                    android.content.SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();
                    // Quay về trang Catalog
                    startActivity(new android.content.Intent(AdminDashboardActivity.this, CatalogActivity.class));
                    finish();

                }
                drawerLayout.close();
                return true;
            }
        });

        Button btnManageAccounts = findViewById(R.id.btnManageAccounts);
        Button btnStats = findViewById(R.id.btnStats);

        btnManageAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new android.content.Intent(AdminDashboardActivity.this, ManageAccountsActivity.class));

            }
        });
        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new android.content.Intent(AdminDashboardActivity.this, SystemStatsActivity.class));
            }
        });
    }
}


