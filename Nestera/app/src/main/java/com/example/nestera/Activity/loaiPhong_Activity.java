package com.example.nestera.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.nestera.Adapter.RoomAdapter;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.R;
import com.example.nestera.model.PhongTro;

import java.util.ArrayList;
import java.util.List;

public class loaiPhong_Activity extends AppCompatActivity {
    RecyclerView rvRooms;
    ArrayList<PhongTro> roomList;
    RoomAdapter roomAdapter;
    phongTroDao dao;
    
    // Category buttons
    LinearLayout btnGanToi, btnDanhGiaCao, btnGiaRe, btnYeuThich;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loai_phong);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        initViews();
        setupRecyclerView();
        loadSampleData();
        setupClickListeners();
        loadImagesFromDatabase();
    }

    private void initViews() {
        rvRooms = findViewById(R.id.rvRooms);
        dao = new phongTroDao(this);
        
        // Setup back button
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, roomList);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        rvRooms.setAdapter(roomAdapter);
    }

    private void loadSampleData() {
        // Load dữ liệu thật từ database với ảnh
        try {
            List<PhongTro> rooms = dao.getAll();
            roomList.clear();
            roomList.addAll(rooms);
            roomAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            // Nếu có lỗi, tạo dữ liệu mẫu với ảnh thật
            PhongTro room1 = new PhongTro();
            room1.setTenPhong("Phòng trọ cao cấp Quận 1");
            room1.setGia(3500000);
            room1.setImagePath("phong_tro_1_1");
            room1.setTienNghi("Điều hoà, Nóng lạnh, Tủ lạnh, Tủ quần áo");
            room1.setTrangThai(0);
            roomList.add(room1);

            PhongTro room2 = new PhongTro();
            room2.setTenPhong("Nhà trọ cao cấp Quận 1");
            room2.setGia(4200000);
            room2.setImagePath("phong_tro_1_2");
            room2.setTienNghi("Điều hoà, Nóng lạnh, Tủ lạnh, Tủ quần áo");
            room2.setTrangThai(1);
            roomList.add(room2);

            PhongTro room3 = new PhongTro();
            room3.setTenPhong("Phòng trọ sinh viên");
            room3.setGia(2800000);
            room3.setImagePath("phong_tro_1_3");
            room3.setTienNghi("Điều hoà, Tủ lạnh, WiFi");
            room3.setTrangThai(0);
            roomList.add(room3);

            roomAdapter.notifyDataSetChanged();
        }
    }

    private void setupClickListeners() {
        // Có thể thêm các click listener cho category buttons ở đây
    }

    private void loadImagesFromDatabase() {
        // Các ImageView đã có src trong XML nên không cần set lại
        // Chỉ load ảnh cho room image và favorite nếu cần
        ImageView ivRecentRoomImage = findViewById(R.id.ivRecentRoomImage);
        ImageView ivRecentFavorite = findViewById(R.id.ivRecentFavorite);
        
        if (ivRecentRoomImage != null) {
            ivRecentRoomImage.setImageResource(R.drawable.ic_launcher_foreground);
        }
        if (ivRecentFavorite != null) {
            ivRecentFavorite.setImageResource(R.drawable.ic_favorite);
        }
    }

}