package com.example.nestera.Activity;

import android.os.Bundle;
import android.util.Log;
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
import com.example.nestera.Firebase.BaiDangHybridDao;
import com.example.nestera.Firebase.ImageUploader;
import com.example.nestera.R;
import com.example.nestera.model.BaiDang;

import java.util.ArrayList;
import java.util.List;

public class BaiDangActivity extends AppCompatActivity {
    ListView lv;
    Button fabAdd;
    BaiDangHybridDao hybridDao; // Sử dụng Hybrid DAO cho sync Firestore
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

        // Khởi tạo Hybrid DAO để sync với Firestore
        hybridDao = new BaiDangHybridDao(this);

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

        // Enable real-time sync từ Firestore
        hybridDao.enableRealtimeSync();

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
        // Sử dụng Hybrid DAO - tự động sync với Firestore
        String role = getSharedPreferences("user11", MODE_PRIVATE).getString("role", "");
        List<BaiDang> list;
        if ("USER".equalsIgnoreCase(role)) {
            // Người thuê: xem tất cả bài đăng
            list = hybridDao.getAll();
        } else {
            // Landlord/Admin: xem bài đăng của mình
            String currentUser = getSharedPreferences("user11", MODE_PRIVATE).getString("username11", "");
            list = hybridDao.getByChuTro(currentUser);
        }

        com.example.nestera.Adapter.BaiDangAdapter adapter = new com.example.nestera.Adapter.BaiDangAdapter(this, list);
        lv.setAdapter(adapter);
    }

    private void filterPosts(String query) {
        if (query == null) query = "";
        query = query.trim().toLowerCase();
        
        java.util.List<com.example.nestera.model.BaiDang> source;
        String role = getSharedPreferences("user11", MODE_PRIVATE).getString("role", "");
        if ("USER".equalsIgnoreCase(role)) {
            source = hybridDao.getAll();
        } else {
            String currentUser = getSharedPreferences("user11", MODE_PRIVATE).getString("username11", "");
            source = hybridDao.getByChuTro(currentUser);
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
        String landlordId = getTenantLandlordId();
        List<BaiDang> listAll = hybridDao.getAll();
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
                    
                    // Lấy chuTroId từ người đăng nhập hiện tại
                    android.content.SharedPreferences pref = getSharedPreferences("user11", MODE_PRIVATE);
                    String currentUser = pref.getString("username11", "");
                    b.setChuTroId(currentUser);
                    
                    // Upload ảnh lên Firebase Storage trước khi lưu
                    if (!selectedImageUris.isEmpty()) {
                        android.app.ProgressDialog progress = new android.app.ProgressDialog(this);
                        progress.setMessage("Đang upload ảnh...");
                        progress.setCancelable(false);
                        progress.show();
                        
                        ImageUploader uploader = new ImageUploader(this);
                        uploader.uploadMultipleImages(selectedImageUris, "baiDang", new ImageUploader.MultiUploadCallback() {
                            @Override
                            public void onAllSuccess(List<String> downloadUrls) {
                                progress.dismiss();
                                // Debug: Log số lượng URL trả về
                                Log.d("BaiDangActivity", "Upload success: " + downloadUrls.size() + " images");
                                for (int i = 0; i < downloadUrls.size(); i++) {
                                    Log.d("BaiDangActivity", "URL " + i + ": " + downloadUrls.get(i));
                                }
                                // Lưu URLs từ Firebase Storage
                                String urlsJoined = android.text.TextUtils.join(";", downloadUrls);
                                Log.d("BaiDangActivity", "Joined URLs: " + urlsJoined);
                                b.setHinhAnh(urlsJoined);
                                saveBaiDangToDatabase(b);
                            }

                            @Override
                            public void onError(Exception e) {
                                progress.dismiss();
                                android.widget.Toast.makeText(BaiDangActivity.this, 
                                    "Lỗi upload ảnh: " + e.getMessage() + ". Bài đăng không được lưu!", 
                                    android.widget.Toast.LENGTH_LONG).show();
                                // KHÔNG lưu gì cả khi có lỗi upload
                                Log.e("BaiDangActivity", "Upload failed, data NOT saved", e);
                            }
                        });
                    } else {
                        // Không có ảnh, lưu trực tiếp
                        saveBaiDangToDatabase(b);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Helper method để lưu bài đăng vào database
    private void saveBaiDangToDatabase(BaiDang b) {
        // Insert vào SQLite + Firestore (chỉ 1 lần)
        long postId = hybridDao.insert(b);
        
        if (postId > 0) {
            android.widget.Toast.makeText(this, "Đã tạo bài đăng và sync lên Cloud!", android.widget.Toast.LENGTH_SHORT).show();
            loadData();
            selectedImageUris.clear(); // Clear ảnh đã chọn để tránh duplicate
        } else {
            android.widget.Toast.makeText(this, "Lỗi tạo bài đăng!", android.widget.Toast.LENGTH_SHORT).show();
        }
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


