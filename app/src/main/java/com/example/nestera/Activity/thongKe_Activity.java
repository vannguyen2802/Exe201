package com.example.nestera.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.nestera.Adapter.viewpage_adapter;
import com.example.nestera.Dao.hoaDonDao;
import com.example.nestera.R;
import com.example.nestera.model.HoaDon;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class thongKe_Activity extends AppCompatActivity {

//    Button btnTuNgay, btnDenNgay, btnDoanhThu;
//    EditText edtTuNgay, edtDenNgay;
//    TextView txtDoanhThu;
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    int mYear, mMonth, mDay;
    ArrayList<HoaDon> list;
hoaDonDao hdDao;
    TabLayout tabLayout;
    viewpage_adapter adapter;
    ViewPager2 viewPager2;
    ImageView btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thống kê");

        Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnAdd = findViewById(R.id.btnadd_toolbar);
        btnAdd.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tabLayout=findViewById(R.id.tablayout);
        viewPager2=findViewById(R.id.viewpage2);
        adapter=new viewpage_adapter(this);
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: tab.setText("Biểu đồ");break;
                    case 1: tab.setText("Khoảng thời gian");break;
                }
            }
        }).attach();







    }

}