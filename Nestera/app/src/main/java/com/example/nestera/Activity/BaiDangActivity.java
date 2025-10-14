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
    private final java.util.ArrayList<android.net.Uri> selectedImageUris = new java.util.ArrayList<>();
    private android.widget.TextView tvSelectedGlobal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidang);
        setTitle("Bài đăng cho thuê");

        lv = findViewById(R.id.lvBaiDang);
        fabAdd = findViewById(R.id.btnAddPost);

        String role = getSharedPreferences("user11", MODE_PRIVATE).getString("role", "");
        if (!"LANDLORD".equalsIgnoreCase(role)) {
            fabAdd.setVisibility(View.GONE);
        }

        loadData();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateDialog();
            }
        });
    }

    private void loadData(){
        baiDangDao dao = new baiDangDao(this);
        List<BaiDang> list = dao.getAll();
        com.example.nestera.Adapter.BaiDangAdapter adapter = new com.example.nestera.Adapter.BaiDangAdapter(this, list);
        lv.setAdapter(adapter);
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


