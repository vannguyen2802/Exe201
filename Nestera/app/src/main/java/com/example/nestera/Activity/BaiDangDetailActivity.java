package com.example.nestera.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nestera.Dao.chuTroDao;
import com.example.nestera.R;
import com.example.nestera.model.ChuTro;

public class BaiDangDetailActivity extends AppCompatActivity {
    private final java.util.ArrayList<android.net.Uri> editImageUris = new java.util.ArrayList<>();
    private android.widget.TextView tvSelectedEdit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidang_detail);
        setTitle("Chi tiết phòng");

        ImageView ivBack = findViewById(R.id.ivBack);
        
        // Xử lý nút back
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadDetailData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload dữ liệu mỗi khi quay lại màn hình
        loadDetailData();
    }

    private void loadDetailData() {
        Intent it = getIntent();
        int postId = it.getIntExtra("postId", -1);
        
        // Load lại dữ liệu từ database để có trạng thái mới nhất
        com.example.nestera.Dao.baiDangDao dao = new com.example.nestera.Dao.baiDangDao(this);
        com.example.nestera.model.BaiDang baiDang = dao.getById(postId);
        
        String tieuDe, diaChi, tienNghi, trangThai, hinhAnh, chuTroId;
        int giaThang;
        double dienTich;
        
        if (baiDang != null) {
            // Sử dụng dữ liệu từ database (mới nhất)
            tieuDe = baiDang.getTieuDe();
            diaChi = baiDang.getDiaChi();
            giaThang = baiDang.getGiaThang();
            dienTich = baiDang.getDienTich();
            tienNghi = baiDang.getTienNghi();
            trangThai = baiDang.getTrangThai();
            hinhAnh = baiDang.getHinhAnh();
            chuTroId = baiDang.getChuTroId();
        } else {
            // Fallback: sử dụng dữ liệu từ intent
            tieuDe = it.getStringExtra("tieuDe");
            diaChi = it.getStringExtra("diaChi");
            giaThang = it.getIntExtra("giaThang", 0);
            dienTich = it.getDoubleExtra("dienTich", 0);
            tienNghi = it.getStringExtra("tienNghi");
            trangThai = it.getStringExtra("trangThai");
            hinhAnh = it.getStringExtra("hinhAnh");
            chuTroId = it.getStringExtra("chuTroId");
        }

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvAmenities = findViewById(R.id.tvAmenities);
        TextView tvArea = findViewById(R.id.tvArea);
        TextView tvStatus = findViewById(R.id.tvStatus);
        LinearLayout imagesContainer = findViewById(R.id.imagesContainer);
        TextView tvMaPhong = findViewById(R.id.tvMaPhong);
        TextView tvOwner = findViewById(R.id.tvOwner);
        TextView tvPhone = findViewById(R.id.tvPhone);
        Button btnCall = findViewById(R.id.btnCall);
        Button btnEdit = findViewById(R.id.btnEditPost);
        Button btnCreateContract = findViewById(R.id.btnCreateContract);

        tvTitle.setText(tieuDe);
        tvLocation.setText(diaChi);
        tvPrice.setText(String.format(java.util.Locale.getDefault(), "%s/tháng", java.text.NumberFormat.getNumberInstance(new java.util.Locale("vi","VN")).format(giaThang)));
        tvAmenities.setText(tienNghi);
        tvArea.setText(String.valueOf(dienTich) + "m²");

        // Hiển thị trạng thái với style khác nhau
        tvStatus.setText(trangThai);
        tvStatus.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16); // Tăng size chữ
        tvStatus.setTypeface(null, android.graphics.Typeface.BOLD); // In đậm
        
        if ("Đã thuê".equalsIgnoreCase(trangThai)) {
            tvStatus.setTextColor(0xFFDC3545); // Màu đỏ
        } else {
            tvStatus.setTextColor(0xFF22C55E); // Màu xanh lá (Còn trống)
        }

        // Hiển thị mã phòng nếu có
        int maPhong = it.getIntExtra("maPhong", -1);
        if (maPhong >= 0) {
            tvMaPhong.setText(String.valueOf(maPhong));
        } else {
            tvMaPhong.setText("-");
        }

        if (hinhAnh != null && !hinhAnh.isEmpty()){
            String[] arr = hinhAnh.split(";");
            for (String u : arr){
                if (u == null || u.trim().isEmpty()) continue;
                ImageView iv = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (getResources().getDisplayMetrics().density*280), (int)(getResources().getDisplayMetrics().density*180));
                lp.rightMargin = (int)(getResources().getDisplayMetrics().density*8);
                iv.setLayoutParams(lp);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                try { iv.setImageURI(Uri.parse(u)); } catch (Exception ignored) {}
                imagesContainer.addView(iv);
            }
        }

        // Role-based UI: nếu là Chủ trọ -> hiển thị nút chỉnh sửa; nếu là User -> hiển thị liên hệ
        String role = getSharedPreferences("user11", MODE_PRIVATE).getString("role", "");
        LinearLayout ownerSection = findViewById(R.id.ownerSection);
        LinearLayout landlordButtonsSection = findViewById(R.id.landlordButtonsSection);
        
        // Kiểm tra trạng thái phòng đã thuê hay chưa
        boolean isDaThue = "Đã thuê".equalsIgnoreCase(trangThai);
        
        // Kiểm tra xem đã có hợp đồng chưa
        int maPhongLocal = getIntent().getIntExtra("maPhong", -1);
        com.example.nestera.Dao.hopDongDao hopDongDao = new com.example.nestera.Dao.hopDongDao(this);
        boolean coHopDong = !hopDongDao.getHopDongByMaPhong(maPhongLocal).isEmpty();
        
        if ("LANDLORD".equalsIgnoreCase(role)) {
            // Nếu là landlord, ẩn phần thông tin chủ trọ và hiển thị section buttons landlord
            ownerSection.setVisibility(View.GONE);
            landlordButtonsSection.setVisibility(View.VISIBLE);
            
            // Thay đổi text button dựa vào trạng thái hợp đồng
            if (coHopDong || isDaThue) {
                btnCreateContract.setText("Xem hợp đồng");
                btnEdit.setEnabled(false);
                btnEdit.setAlpha(0.5f);
            } else {
                btnCreateContract.setText("Tạo hợp đồng");
                btnEdit.setEnabled(true);
                btnEdit.setAlpha(1.0f);
            }
            
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isDaThue && !coHopDong) {
                        openEditDialog(postId, tieuDe, diaChi, giaThang, dienTich, tienNghi, trangThai, hinhAnh, chuTroId);
                    }
                }
            });
            
            btnCreateContract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Nếu đã có hợp đồng hoặc phòng đã thuê -> vào xem hợp đồng trực tiếp
                    if (coHopDong || isDaThue) {
                        Intent i = new Intent(BaiDangDetailActivity.this, com.example.nestera.Activity.hopDong_Activity.class);
                        i.putExtra("maphong", maPhongLocal);
                        i.putExtra("openDetail", true);
                        startActivity(i);
                    } else {
                        // Chưa có hợp đồng -> mở tạo hợp đồng như cũ
                        Intent i = new Intent(BaiDangDetailActivity.this, com.example.nestera.Activity.hopDong_Activity.class);
                        i.putExtra("maphong", maPhongLocal);
                        startActivity(i);
                    }
                }
            });
        } else {
            // Nếu là user, hiển thị thông tin chủ trọ và ẩn section buttons landlord
            landlordButtonsSection.setVisibility(View.GONE);
            ownerSection.setVisibility(View.VISIBLE);
            
            chuTroDao chuTroDao = new chuTroDao(this);
            ChuTro ct = chuTroDao.getID(chuTroId);
            if (ct != null){
                String ownerName = (ct.getTenChuTro()==null||ct.getTenChuTro().trim().isEmpty())? chuTroId : ct.getTenChuTro();
                tvOwner.setText(ownerName);
                tvPhone.setText("📱 " + ct.getSdt());

                btnCall.setEnabled(ct.getSdt()!=null && !ct.getSdt().isEmpty());
                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ct.getSdt()));
                        startActivity(dial);
                    }
                });
            } else {

                tvOwner.setText("Không có thông tin");
                tvPhone.setText("");

                btnCall.setEnabled(false);
            }
        }


    }

    private void openEditDialog(int id, String tieuDe, String diaChi, int gia, double dienTich, String tienNghi, String trangThai, String hinhAnh, String chuTroId){
        View view = getLayoutInflater().inflate(R.layout.dialog_baidang,null);
        android.widget.EditText edtTieuDe = view.findViewById(R.id.edtTieuDe);
        android.widget.EditText edtDiaChi = view.findViewById(R.id.edtDiaChi);
        android.widget.EditText edtGia = view.findViewById(R.id.edtGia);
        android.widget.EditText edtDienTich = view.findViewById(R.id.edtDienTich);
        android.widget.CheckBox c1 = view.findViewById(R.id.chkAmenity1);
        android.widget.CheckBox c2 = view.findViewById(R.id.chkAmenity2);
        android.widget.CheckBox c3 = view.findViewById(R.id.chkAmenity3);
        android.widget.CheckBox c4 = view.findViewById(R.id.chkAmenity4);
        android.widget.CheckBox c5 = view.findViewById(R.id.chkAmenity5);
        android.widget.CheckBox c6 = view.findViewById(R.id.chkAmenity6);
        android.widget.CheckBox c7 = view.findViewById(R.id.chkAmenity7);
        android.widget.Spinner spTrangThai = view.findViewById(R.id.spTrangThai);
        Button btnPick = view.findViewById(R.id.btnPickImages);
        tvSelectedEdit = view.findViewById(R.id.tvSelectedImages);

        edtTieuDe.setText(tieuDe);
        edtDiaChi.setText(diaChi);
        edtGia.setText(String.valueOf(gia));
        edtDienTich.setText(String.valueOf(dienTich));
        // tick tiện nghi theo chuỗi hiện có
        java.util.List<String> a = java.util.Arrays.asList(tienNghi.split(" · "));
        if (a.contains(c1.getText().toString())) c1.setChecked(true);
        if (a.contains(c2.getText().toString())) c2.setChecked(true);
        if (a.contains(c3.getText().toString())) c3.setChecked(true);
        if (a.contains(c4.getText().toString())) c4.setChecked(true);
        if (a.contains(c5.getText().toString())) c5.setChecked(true);
        if (a.contains(c6.getText().toString())) c6.setChecked(true);
        if (a.contains(c7.getText().toString())) c7.setChecked(true);
        // ảnh hiện tại
        if (hinhAnh!=null && !hinhAnh.isEmpty()){
            tvSelectedEdit.setText("Đang có "+hinhAnh.split(";").length+" ảnh");
        }
        editImageUris.clear();
        btnPick.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra(android.content.Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(android.content.Intent.createChooser(intent, "Chọn ảnh"), 1002);
        });

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chỉnh sửa bài đăng")
                .setView(view)
                .setPositiveButton("Lưu", (d,w)->{
                    com.example.nestera.model.BaiDang b = new com.example.nestera.model.BaiDang();
                    b.setId(id);
                    b.setTieuDe(edtTieuDe.getText().toString());
                    b.setDiaChi(edtDiaChi.getText().toString());
                    try { b.setGiaThang(Integer.parseInt(edtGia.getText().toString())); } catch (Exception e){ b.setGiaThang(gia); }
                    try { b.setDienTich(Double.parseDouble(edtDienTich.getText().toString())); } catch (Exception e){ b.setDienTich(dienTich); }
                    java.util.List<String> sel = new java.util.ArrayList<>();
                    if (c1.isChecked()) sel.add(c1.getText().toString());
                    if (c2.isChecked()) sel.add(c2.getText().toString());
                    if (c3.isChecked()) sel.add(c3.getText().toString());
                    if (c4.isChecked()) sel.add(c4.getText().toString());
                    if (c5.isChecked()) sel.add(c5.getText().toString());
                    if (c6.isChecked()) sel.add(c6.getText().toString());
                    if (c7.isChecked()) sel.add(c7.getText().toString());
                    b.setTienNghi(android.text.TextUtils.join(" · ", sel));
                    b.setTrangThai(spTrangThai.getSelectedItem().toString());
                    // nếu có chọn lại ảnh, lưu thay thế; nếu không giữ nguyên
                    if (!editImageUris.isEmpty()){
                        java.util.List<String> uriStrings = new java.util.ArrayList<>();
                        for (android.net.Uri u : editImageUris) uriStrings.add(u.toString());
                        b.setHinhAnh(android.text.TextUtils.join(";", uriStrings));
                    } else {
                        b.setHinhAnh(hinhAnh);
                    }
                    b.setChuTroId(chuTroId);
                    com.example.nestera.Dao.baiDangDao dao = new com.example.nestera.Dao.baiDangDao(this);
                    int rows = dao.update(b);
                    if (rows > 0) {
                        // Reload UI with latest data
                        com.example.nestera.model.BaiDang updated = dao.getById(id);
                        if (updated != null) {
                            TextView tTitle = findViewById(R.id.tvTitle);
                            TextView tLoc = findViewById(R.id.tvLocation);
                            TextView tPrice = findViewById(R.id.tvPrice);
                            TextView tAmenities = findViewById(R.id.tvAmenities);
                            TextView tArea = findViewById(R.id.tvArea);
                            TextView tStatus = findViewById(R.id.tvStatus);
                            tTitle.setText(updated.getTieuDe());
                            tLoc.setText(updated.getDiaChi());
                            tPrice.setText(String.format(java.util.Locale.getDefault(), "%s/tháng", java.text.NumberFormat.getNumberInstance(new java.util.Locale("vi","VN")).format(updated.getGiaThang())));
                            tAmenities.setText(updated.getTienNghi());
                            tArea.setText(String.valueOf(updated.getDienTich()) + "m²");
                            tStatus.setText(updated.getTrangThai());
                            LinearLayout imgs = findViewById(R.id.imagesContainer);
                            imgs.removeAllViews();
                            if (updated.getHinhAnh() != null && !updated.getHinhAnh().isEmpty()) {
                                String[] arr = updated.getHinhAnh().split(";");
                                for (String u : arr) {
                                    if (u == null || u.trim().isEmpty()) continue;
                                    ImageView iv = new ImageView(this);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (getResources().getDisplayMetrics().density*280), (int)(getResources().getDisplayMetrics().density*180));
                                    lp.rightMargin = (int)(getResources().getDisplayMetrics().density*8);
                                    iv.setLayoutParams(lp);
                                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    try { iv.setImageURI(Uri.parse(u)); } catch (Exception ignored2) {}
                                    imgs.addView(iv);
                                }
                            }
                        }
                        android.widget.Toast.makeText(this, "Đã lưu chỉnh sửa", android.widget.Toast.LENGTH_SHORT).show();
                    } else {
                        android.widget.Toast.makeText(this, "Không có thay đổi", android.widget.Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }
}


