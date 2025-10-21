package com.example.nestera.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.nestera.Adapter.BaiDangRecyclerAdapter;
import com.example.nestera.Dao.baiDangDao;
import com.example.nestera.MainActivity;
import com.example.nestera.R;
import com.example.nestera.dangnhap;
import com.example.nestera.model.BaiDang;

import java.util.ArrayList;
import java.util.List;

public class CatalogActivity extends AppCompatActivity {
    RecyclerView rvRooms;
    ArrayList<BaiDang> baiDangList;
    ArrayList<BaiDang> allBaiDangList; // Lưu toàn bộ danh sách để tìm kiếm
    BaiDangRecyclerAdapter baiDangAdapter;
    baiDangDao dao;
    EditText edtSearch;
    
    // Category buttons
    LinearLayout btnGanToi, btnDanhGiaCao, btnGiaRe, btnYeuThich;
    Button btnLogin;
    ImageView ivBack, ivNotification;
    
    // Biến theo dõi trạng thái sắp xếp giá: true = thấp đến cao, false = cao đến thấp
    private boolean isPriceLowToHigh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loai_phong);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        initViews();
        setupRecyclerView();
        loadAllBaiDang();
        setupClickListeners();
    }

    private void initViews() {
        rvRooms = findViewById(R.id.rvRooms);
        dao = new baiDangDao(this);
        btnLogin = findViewById(R.id.btnLogin);
        ivBack = findViewById(R.id.ivBack);
        ivNotification = findViewById(R.id.ivNotification);
        edtSearch = findViewById(R.id.edtSearch);
        
        // Ẩn nút back vì đây là trang chủ
        if (ivBack != null) {
            ivBack.setVisibility(View.GONE);
        }
        
        // Kiểm tra trạng thái đăng nhập và cập nhật button
        updateLoginButton();
        
        // Setup search functionality
        setupSearch();
    }
    
    private void updateLoginButton() {
        SharedPreferences prefs = getSharedPreferences("user11", MODE_PRIVATE);
        String username = prefs.getString("username11", "");
        
        android.util.Log.d("CatalogDebug", "Username: '" + username + "', isEmpty: " + username.isEmpty());
        
        if (btnLogin != null) {
            // Luôn hiển thị "Đăng nhập" theo yêu cầu
            btnLogin.setText("Đăng nhập");
            android.util.Log.d("CatalogDebug", "Button set to: Đăng nhập");
        }
    }
    
    private void setupSearch() {
        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterBaiDang(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
    
    private void filterBaiDang(String query) {
        baiDangList.clear();
        
        if (query.isEmpty()) {
            // Nếu không có query, hiển thị tất cả
            baiDangList.addAll(allBaiDangList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            
            for (BaiDang baiDang : allBaiDangList) {
                // Tìm kiếm theo địa chỉ
                boolean matchAddress = baiDang.getDiaChi() != null && 
                                      baiDang.getDiaChi().toLowerCase().contains(lowerQuery);
                
                // Tìm kiếm theo mã phòng (ID)
                boolean matchId = String.valueOf(baiDang.getId()).contains(lowerQuery);
                
                // Tìm kiếm theo tiêu đề
                boolean matchTitle = baiDang.getTieuDe() != null && 
                                    baiDang.getTieuDe().toLowerCase().contains(lowerQuery);
                
                // Tìm kiếm theo mã phòng trọ (maPhong)
                boolean matchMaPhong = baiDang.getMaPhong() != null && 
                                      String.valueOf(baiDang.getMaPhong()).contains(lowerQuery);
                
                if (matchAddress || matchId || matchTitle || matchMaPhong) {
                    baiDangList.add(baiDang);
                }
            }
        }
        
        baiDangAdapter.notifyDataSetChanged();
    }

    private void setupRecyclerView() {
        baiDangList = new ArrayList<>();
        allBaiDangList = new ArrayList<>(); // Initialize all list
        baiDangAdapter = new BaiDangRecyclerAdapter(this, baiDangList);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));
        rvRooms.setAdapter(baiDangAdapter);
    }

    private void loadAllBaiDang() {
        // Load tất cả bài đăng từ tất cả chủ trọ
        try {
            List<BaiDang> posts = dao.getAll();
            allBaiDangList.clear();
            allBaiDangList.addAll(posts);
            
            baiDangList.clear();
            baiDangList.addAll(posts);
            baiDangAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        // Button login click - check if logged in
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                // Luôn mở màn đăng nhập
                startActivity(new Intent(CatalogActivity.this, dangnhap.class));
            });
        }

        // Notification click
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> {
                Toast.makeText(this, "Thông báo", Toast.LENGTH_SHORT).show();
            });
        }

        // Category buttons
        LinearLayout categoryNearby = (LinearLayout) findViewById(R.id.ivCategoryNearby).getParent();
        LinearLayout categoryRating = (LinearLayout) findViewById(R.id.ivCategoryRating).getParent();
        LinearLayout categoryPrice = (LinearLayout) findViewById(R.id.ivCategoryPrice).getParent();
        LinearLayout categoryFavorite = (LinearLayout) findViewById(R.id.ivCategoryFavorite).getParent();

        if (categoryNearby != null) {
            categoryNearby.setOnClickListener(v -> {
                Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
            });
        }

        if (categoryRating != null) {
            categoryRating.setOnClickListener(v -> {
                Toast.makeText(this, "Sắp xếp theo đánh giá", Toast.LENGTH_SHORT).show();
            });
        }

        if (categoryPrice != null) {
            categoryPrice.setOnClickListener(v -> {
                if (isPriceLowToHigh) {
                    // Sắp xếp từ thấp đến cao (áp dụng cho cả allBaiDangList và baiDangList)
                    allBaiDangList.sort((p1, p2) -> Integer.compare(p1.getGiaThang(), p2.getGiaThang()));
                    baiDangList.sort((p1, p2) -> Integer.compare(p1.getGiaThang(), p2.getGiaThang()));
                    Toast.makeText(this, "Giá: Thấp → Cao", Toast.LENGTH_SHORT).show();
                } else {
                    // Sắp xếp từ cao đến thấp (áp dụng cho cả allBaiDangList và baiDangList)
                    allBaiDangList.sort((p1, p2) -> Integer.compare(p2.getGiaThang(), p1.getGiaThang()));
                    baiDangList.sort((p1, p2) -> Integer.compare(p2.getGiaThang(), p1.getGiaThang()));
                    Toast.makeText(this, "Giá: Cao → Thấp", Toast.LENGTH_SHORT).show();
                }
                // Toggle trạng thái cho lần click tiếp theo
                isPriceLowToHigh = !isPriceLowToHigh;
                baiDangAdapter.notifyDataSetChanged();
            });
        }

        if (categoryFavorite != null) {
            categoryFavorite.setOnClickListener(v -> {
                Toast.makeText(this, "Chức năng yêu thích đang phát triển", Toast.LENGTH_SHORT).show();
            });
        }

        // Bottom navigation
        ImageView ivNavHome = findViewById(R.id.ivNavHome);
//        ImageView ivNavSearch = findViewById(R.id.ivNavSearch);

        if (ivNavHome != null) {
            ivNavHome.setOnClickListener(v -> {
                // Already on home
            });
        }

//        if (ivNavSearch != null) {
//            ivNavSearch.setOnClickListener(v -> {
//                Toast.makeText(this, "Chức năng tìm kiếm", Toast.LENGTH_SHORT).show();
//            });
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload bài đăng when returning to this activity
        loadAllBaiDang();
        // Cập nhật text button login
        updateLoginButton();

    }
}



