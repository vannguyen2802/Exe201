package com.example.nestera.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nestera.Dao.baiDangDao;
import com.example.nestera.R;
import com.example.nestera.model.BaiDang;

import java.util.ArrayList;
import java.util.List;

public class BaiDangActivity extends AppCompatActivity {
    ListView lv;
    Button fabAdd;
    android.widget.LinearLayout llTabs;
    android.widget.Button btnTabChuTroCuaBan, btnTabChuTroKhac;
    private final java.util.ArrayList<android.net.Uri> selectedImageUris = new java.util.ArrayList<>();
    private android.widget.TextView tvSelectedGlobal;
    private android.widget.EditText edtSearchPost;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidang);
        setTitle("Bài đăng cho thuê");

        lv = findViewById(R.id.lvBaiDang);
        fabAdd = findViewById(R.id.btnAddPost);
        android.widget.ImageView ivBack = findViewById(R.id.ivBack);
        llTabs = findViewById(R.id.llTabs);
        btnTabChuTroCuaBan = findViewById(R.id.btnTabChuTroCuaBan);
        btnTabChuTroKhac = findViewById(R.id.btnTabChuTroKhac);
        edtSearchPost = findViewById(R.id.edtSearchPost);

        // Xử lý nút back
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String role = getSharedPreferences("user11", MODE_PRIVATE).getString("role", "");
        if (!"LANDLORD".equalsIgnoreCase(role)) {
            fabAdd.setVisibility(View.GONE);
        }
        // Show filter tabs for USER role
        if ("USER".equalsIgnoreCase(role)) {
            llTabs.setVisibility(View.VISIBLE);
        } else {
            llTabs.setVisibility(View.GONE);
        }

        loadData();

        // Search posts by title or address
        if (edtSearchPost != null) {
            edtSearchPost.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterPosts(s.toString());
                }
                @Override public void afterTextChanged(android.text.Editable s) {}
            });
        }


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateDialog();
            }
        });


        // Tab click listeners for USER
        btnTabChuTroCuaBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataForTab(true);
                // toggle styles -> mark "của bạn" active
                btnTabChuTroCuaBan.setBackgroundResource(R.drawable.bg_btn_primary);
                btnTabChuTroCuaBan.setTextColor(android.graphics.Color.WHITE);
                btnTabChuTroKhac.setBackgroundResource(R.drawable.bg_btn_primary_outline);
                btnTabChuTroKhac.setTextColor(android.graphics.Color.parseColor("#4A90E2"));
            }
        });
        btnTabChuTroKhac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataForTab(false);
                // toggle styles
                btnTabChuTroKhac.setBackgroundResource(R.drawable.bg_btn_primary);
                btnTabChuTroKhac.setTextColor(android.graphics.Color.WHITE);
                btnTabChuTroCuaBan.setBackgroundResource(R.drawable.bg_btn_primary_outline);
                btnTabChuTroCuaBan.setTextColor(android.graphics.Color.parseColor("#4A90E2"));
            }
        });
        // set initial tab style and data (default: "của bạn")
        if ("USER".equalsIgnoreCase(role)) {
            btnTabChuTroCuaBan.post(new Runnable() {
                @Override public void run() { btnTabChuTroCuaBan.performClick(); }
            });
        }

    }

    private void loadData(){
        baiDangDao dao = new baiDangDao(this);
        String role = getSharedPreferences("user11", MODE_PRIVATE).getString("role", "");
        List<BaiDang> list;
        if ("USER".equalsIgnoreCase(role)) {
            // Người thuê: xem tất cả bài đăng
            list = dao.getAll();
        } else {
            // Landlord/Admin: xem bài đăng của mình (giữ hành vi cũ cho landlord)
            String currentUser = getSharedPreferences("user11", MODE_PRIVATE).getString("username11", "");
            list = dao.getByChuTro(currentUser);
        }

        com.example.nestera.Adapter.BaiDangAdapter adapter = new com.example.nestera.Adapter.BaiDangAdapter(this, list);
        lv.setAdapter(adapter);
    }

    private void filterPosts(String query) {
        if (query == null) query = "";
        query = query.trim().toLowerCase();
        com.example.nestera.Dao.baiDangDao dao = new com.example.nestera.Dao.baiDangDao(this);
        java.util.List<com.example.nestera.model.BaiDang> source;
        String role = getSharedPreferences("user11", MODE_PRIVATE).getString("role", "");
        if ("USER".equalsIgnoreCase(role)) {
            source = dao.getAll();
        } else {
            String currentUser = getSharedPreferences("user11", MODE_PRIVATE).getString("username11", "");
            source = dao.getByChuTro(currentUser);
        }
        java.util.ArrayList<com.example.nestera.model.BaiDang> filtered = new java.util.ArrayList<>();
        for (com.example.nestera.model.BaiDang b : source) {
            String title = b.getTieuDe() == null ? "" : b.getTieuDe().toLowerCase();
            String addr = b.getDiaChi() == null ? "" : b.getDiaChi().toLowerCase();
            if (title.contains(query) || addr.contains(query)) filtered.add(b);
        }
        lv.setAdapter(new com.example.nestera.Adapter.BaiDangAdapter(this, filtered));
    }

    // Load theo tab khi role là USER
    private void loadDataForTab(boolean isYourLandlord) {
        String role = getSharedPreferences("user11", MODE_PRIVATE).getString("role", "");
        if (!"USER".equalsIgnoreCase(role)) {
            loadData();
            return;
        }
        baiDangDao dao = new baiDangDao(this);
        String landlordId = getTenantLandlordId();
        List<BaiDang> listAll = dao.getAll();
        java.util.ArrayList<BaiDang> filtered = new java.util.ArrayList<>();
        if (landlordId == null || landlordId.isEmpty()) {
            // Nếu chưa xác định được chủ trọ của người thuê, không hiển thị gì ở tab "của bạn"
            if (!isYourLandlord) filtered.addAll(listAll);
        } else {
            for (BaiDang b : listAll) {
                boolean isYours = landlordId.equalsIgnoreCase(b.getChuTroId());
                if (isYourLandlord && isYours) filtered.add(b);
                if (!isYourLandlord && !isYours) filtered.add(b);
            }
        }
        com.example.nestera.Adapter.BaiDangAdapter adapter = new com.example.nestera.Adapter.BaiDangAdapter(this, filtered);
        lv.setAdapter(adapter);
    }

    // Lấy mã chủ trọ (chuTroId) mà người thuê hiện tại thuộc về
    private String getTenantLandlordId() {
        String role = getSharedPreferences("user11", MODE_PRIVATE).getString("role", "");
        if (!"USER".equalsIgnoreCase(role)) return null;
        String username = getSharedPreferences("user11", MODE_PRIVATE).getString("username11", "");
        try {
            com.example.nestera.Dao.nguoiThueDao ntDao = new com.example.nestera.Dao.nguoiThueDao(this);
            com.example.nestera.model.NguoiThue nt = ntDao.getID(username);
            if (nt != null) return nt.getChuTroId();
        } catch (Exception ignored) {}
        return null;
    }


    private void openCreateDialog(){
        View view = getLayoutInflater().inflate(R.layout.dialog_baidang,null);
        EditText edtTieuDe = view.findViewById(R.id.edtTieuDe);
        EditText edtDiaChi = view.findViewById(R.id.edtDiaChi);
        EditText edtGia = view.findViewById(R.id.edtGia);
        EditText edtDienTich = view.findViewById(R.id.edtDienTich);
        CheckBox c1 = view.findViewById(R.id.chkAmenity1);
        CheckBox c2 = view.findViewById(R.id.chkAmenity2);
        CheckBox c3 = view.findViewById(R.id.chkAmenity3);
        CheckBox c4 = view.findViewById(R.id.chkAmenity4);
        CheckBox c5 = view.findViewById(R.id.chkAmenity5);
        CheckBox c6 = view.findViewById(R.id.chkAmenity6);
        CheckBox c7 = view.findViewById(R.id.chkAmenity7);
        Spinner spTrangThai = view.findViewById(R.id.spTrangThai);
        Button btnPick = view.findViewById(R.id.btnPickImages);
        tvSelectedGlobal = view.findViewById(R.id.tvSelectedImages);
        selectedImageUris.clear();

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(android.content.Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(android.content.Intent.createChooser(intent, "Chọn ảnh"), 1001);
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("Tạo bài đăng")
                .setView(view)
                .setPositiveButton("Lưu", (d, which) -> {
                    BaiDang b = new BaiDang();
                    b.setTieuDe(edtTieuDe.getText().toString());
                    b.setDiaChi(edtDiaChi.getText().toString());
                    try { b.setGiaThang(Integer.parseInt(edtGia.getText().toString())); } catch (Exception ignored) { b.setGiaThang(0); }
                    try { b.setDienTich(Double.parseDouble(edtDienTich.getText().toString())); } catch (Exception ignored) { b.setDienTich(0); }
                    List<String> a = new ArrayList<>();
                    if (c1.isChecked()) a.add(c1.getText().toString());
                    if (c2.isChecked()) a.add(c2.getText().toString());
                    if (c3.isChecked()) a.add(c3.getText().toString());
                    if (c4.isChecked()) a.add(c4.getText().toString());
                    if (c5.isChecked()) a.add(c5.getText().toString());
                    if (c6.isChecked()) a.add(c6.getText().toString());
                    if (c7.isChecked()) a.add(c7.getText().toString());
                    b.setTienNghi(android.text.TextUtils.join(" · ", a));
                    b.setTrangThai(spTrangThai.getSelectedItem().toString());
                    // Lưu danh sách URI ảnh thành chuỗi
                    java.util.List<String> uriStrings = new java.util.ArrayList<>();
                    for (android.net.Uri u : selectedImageUris) { uriStrings.add(u.toString()); }
                    b.setHinhAnh(android.text.TextUtils.join(";", uriStrings));
                    // Lấy chuTroId từ người đăng nhập hiện tại
                    android.content.SharedPreferences pref = getSharedPreferences("user11", MODE_PRIVATE);
                    String currentUser = pref.getString("username11", "");
                    b.setChuTroId(currentUser);
                    long postId = new baiDangDao(this).insert(b);
                    // Đồng bộ sang danh sách Phòng Trọ để hiển thị ở mục Phòng
                    try {
                        com.example.nestera.model.PhongTro p = new com.example.nestera.model.PhongTro();
                        p.setTenPhong(b.getTieuDe());
                        p.setGia(b.getGiaThang());
                        p.setTienNghi(b.getTienNghi());
                        p.setMaLoai(1); // mặc định Full option; có thể thêm chọn loại sau
                        p.setTrangThai("Đã thuê".equalsIgnoreCase(b.getTrangThai()) ? 1 : 0);
                        p.setDiaChi(b.getDiaChi());
                        // nếu có ảnh, set ảnh đầu tiên vào imagePath để list Phòng hiển thị
                        if (b.getHinhAnh() != null && !b.getHinhAnh().isEmpty()) {
                            String first = b.getHinhAnh().split(";")[0];
                            p.setImagePath(first); // có thể là URI content:
                        }
                        long idPhong = new com.example.nestera.Dao.phongTroDao(this).insert(p);
                        if (idPhong > 0) {
                            b.setMaPhong((int) idPhong);
                            if (postId > 0) b.setId((int) postId);
                            new baiDangDao(this).update(b); // cập nhật maPhong vào bài đăng vừa tạo
                        }
                    } catch (Exception ignored) {}
                    loadData();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            selectedImageUris.clear();
            if (data != null) {
                if (data.getClipData() != null) {
                    android.content.ClipData clip = data.getClipData();
                    for (int i = 0; i < clip.getItemCount(); i++) {
                        android.net.Uri uri = clip.getItemAt(i).getUri();
                        // persist read permission for future access
                        try {
                            final int takeFlags = data.getFlags() & (android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION | android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            getContentResolver().takePersistableUriPermission(uri, takeFlags);
                        } catch (Exception ignored) {}
                        selectedImageUris.add(uri);
                    }
                } else if (data.getData() != null) {
                    android.net.Uri uri = data.getData();
                    try {
                        final int takeFlags = data.getFlags() & (android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION | android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    } catch (Exception ignored) {}
                    selectedImageUris.add(uri);
                }
            }
            if (tvSelectedGlobal != null) {
                tvSelectedGlobal.setText("Đã chọn " + selectedImageUris.size() + " ảnh");
            }
        }
    }
}


