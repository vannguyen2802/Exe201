package com.example.nestera.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nestera.Adapter.HopDong_Adapter;
import com.example.nestera.Adapter.NguoiThueSpinerAdapter;
import com.example.nestera.Dao.hopDongDao;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.MainActivity;
import com.example.nestera.R;
import com.example.nestera.model.HopDong;
import com.example.nestera.model.NguoiThue;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class hopDong_Activity extends AppCompatActivity {
    hopDongDao dao;
    ArrayList<HopDong> list;
    ArrayList<NguoiThue> list_nt;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    ListView lsthopDong;
    HopDong_Adapter hopDongAdapter;
    HopDong item;
    ArrayList<HopDong> list_hdm = new ArrayList<>();
    ArrayList<HopDong> list_hdnt = new ArrayList<>();
    ImageView btnAdd,imgAnhhd;
    Button btnChonAnh;
    EditText edtma_hd, edtTenkh_hd, edtSdt_hd, edtCCCD_hd, edtDiaChi_hd, edtNgayki_hd, edtSothang_hd, edtSoPhong_hd, edtTienCoc_hd, edtTienPhong_hd, edtSonguoi_hd, edtSoxe_hd, edtGhiChu_hd;
    Spinner spinner;
    int position,maphong,gia,mp;
    String mant,dc,sdt,cccd;
    nguoiThueDao dao_nt;
    NguoiThue nt;
    NguoiThueSpinerAdapter spinerAdapter;
    phongTroDao dao_pt;
    Button btnTaoHD,btnHuy;
    byte[] hinhAnh;
    final int REQUEST_CODE_FOLDER = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hop_dong);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Xem hợp đồng");

        Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnAdd = findViewById(R.id.btnadd_toolbar);
        btnAdd.setVisibility(View.GONE);
        lsthopDong = findViewById(R.id.lsthopDong);
        dao = new hopDongDao(hopDong_Activity.this);
        list = (ArrayList<HopDong>) dao.getAll();
        hopDongAdapter = new HopDong_Adapter(hopDong_Activity.this, list, this);
        lsthopDong.setAdapter(hopDongAdapter);
        SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(hopDong_Activity.this, phong_Activity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(hopDong_Activity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        maphong = getIntent().getIntExtra("maphong", -1);
        list_hdm = dao.getHopDongByMaPhong(maphong);


        dao_nt=new nguoiThueDao(hopDong_Activity.this);
        mp = dao_nt.getMaPhongByUser(username);
        list_hdnt = dao.getHopDongByMaPhong(mp);
        if(username.equalsIgnoreCase("admin")) {
        if (list_hdm.isEmpty()) {
            list.clear();
            openDialog();
        } else {
            hopDongAdapter = new HopDong_Adapter(hopDong_Activity.this, list_hdm, this);
            lsthopDong.setAdapter(hopDongAdapter);
        }}else {
            if (list_hdnt.isEmpty()) {
                list.clear();
                AlertDialog.Builder builder = new AlertDialog.Builder(hopDong_Activity.this);
                builder.setTitle("Cảnh báo");
                builder.setIcon(R.drawable.baseline_warning_24);
                builder.setMessage("Bạn chưa có hợp đồng");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                builder.show();
            } else if (!list_hdnt.isEmpty()) {
                hopDongAdapter = new HopDong_Adapter(hopDong_Activity.this, list_hdnt, this);
                lsthopDong.setAdapter(hopDongAdapter);

            }
        }


    }
    public void xoa(String Id){
        AlertDialog.Builder builder = new AlertDialog.Builder(hopDong_Activity.this);
        builder.setTitle("Cảnh báo");
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setMessage("Bạn có chắc chắn muốn kết thúc hợp đồng");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dao.delete(Id);
                 maphong = getIntent().getIntExtra("maphong", -1);
                dao.updateTrangThaiPhong(maphong, 0);
                dialogInterface.cancel();
                Toast.makeText(hopDong_Activity.this, "Kết thúc hợp đồng thành công ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(hopDong_Activity.this, phong_Activity.class);
                hopDong_Activity.this.startActivity(intent);
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        builder.show();
    }
    public void openDialog() {
        Dialog dialog = new Dialog(hopDong_Activity.this);
        dialog.setContentView(R.layout.item_taohopdong);
        edtTenkh_hd = dialog.findViewById(R.id.edtTenkh_hd);
        edtSdt_hd = dialog.findViewById(R.id.edtSdt_hd);
        edtCCCD_hd = dialog.findViewById(R.id.edtCCCD_hd);
        edtDiaChi_hd =dialog.findViewById(R.id.edtDiaChi_hd);
        edtNgayki_hd = dialog.findViewById(R.id.edtNgayki_hd);
        edtSothang_hd = dialog.findViewById(R.id.edtSothang_hd);
        edtSoPhong_hd = dialog.findViewById(R.id.edtSoPhong_hd);
        edtTienCoc_hd = dialog.findViewById(R.id.edtTienCoc_hd);
        edtTienPhong_hd = dialog.findViewById(R.id.edtTienPhong_hd);
        edtSonguoi_hd = dialog.findViewById(R.id.edtSonguoi_hd);
        edtSoxe_hd = dialog.findViewById(R.id.edtSoxe_hd);
        edtGhiChu_hd = dialog.findViewById(R.id.edtGhiChu_hd);
        btnTaoHD = dialog.findViewById(R.id.btnTao);
        btnHuy = dialog.findViewById(R.id.btnHuy);
        imgAnhhd=dialog.findViewById(R.id.imgAnhhd);
        btnChonAnh=dialog.findViewById(R.id.btnChonAnh);
        spinner = dialog.findViewById(R.id.spnNguoiThue);
        dao_pt = new phongTroDao(hopDong_Activity.this);
        list_nt = new ArrayList<NguoiThue>();
        dao_nt = new nguoiThueDao(hopDong_Activity.this);
        maphong = getIntent().getIntExtra("maphong", -1);
        list_nt =dao_nt.getNguoiThueByMaPhong(maphong);
        if (list_nt.isEmpty()) {
            Toast.makeText(hopDong_Activity.this, "Không có người thuê phòng. Thêm người thuê trước khi tạo hợp đồng.", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            Intent intent = new Intent(hopDong_Activity.this, phong_Activity.class);
            hopDong_Activity.this.startActivity(intent);
            return;
        }
        spinerAdapter = new NguoiThueSpinerAdapter(hopDong_Activity.this, list_nt);
        spinner.setAdapter(spinerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mant = list_nt.get(i).getMaNguoithue();
                cccd = list_nt.get(i).getcCCD();
                sdt = list_nt.get(i).getSdt();
                dc = list_nt.get(i).getThuongTru();
                edtCCCD_hd.setText(cccd+"");
                edtSdt_hd.setText(sdt);
                edtDiaChi_hd.setText(dc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_FOLDER);
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_FOLDER);
            }
        });


        maphong = getIntent().getIntExtra("maphong", -1);
        gia = dao_pt.getGiaPhongTheoMaPhong(maphong);
        String tenphong=dao_pt.getTenPhongTheoMaPhong(maphong);
        edtTienPhong_hd.setText(gia + "");
        edtSoPhong_hd.setText(tenphong);
        edtNgayki_hd.setText(sdf.format(new Date()));
        edtSoPhong_hd.setEnabled(false);
        edtTienPhong_hd.setEnabled(false);
        edtNgayki_hd.setEnabled(false);
        edtCCCD_hd.setEnabled(false);
        edtSdt_hd.setEnabled(false);
        edtDiaChi_hd.setEnabled(false);
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(hopDong_Activity.this, phong_Activity.class);
                startActivity(intent);
            }
        });

        btnTaoHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edtSothang_hd.getText().toString())||TextUtils.isEmpty(edtTienCoc_hd.getText().toString())||TextUtils.isEmpty(edtSonguoi_hd.getText().toString())||TextUtils.isEmpty(edtSoxe_hd.getText().toString())){
                    Toast.makeText(hopDong_Activity.this, "Bạn phải nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int st = Integer.parseInt(edtSothang_hd.getText().toString());
                    if (st <= 0) {
                        Toast.makeText(hopDong_Activity.this, "Số tháng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(hopDong_Activity.this, "Số tháng phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int tc = Integer.parseInt(edtTienCoc_hd.getText().toString());
                    if (tc <= 0) {
                        Toast.makeText(hopDong_Activity.this, "Tiền cọc phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    if(tc>gia){
//                        Toast.makeText(hopDong_Activity.this, "Tiền cọc phải nhỏ hơnn tiền phòng", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                } catch (Exception e) {
                    Toast.makeText(hopDong_Activity.this, "Tiền cọc phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int sn = Integer.parseInt(edtSonguoi_hd.getText().toString());
                    if (sn <= 0) {
                        Toast.makeText(hopDong_Activity.this, "Số người phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(hopDong_Activity.this, "Số người phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int sx = Integer.parseInt(edtSoxe_hd.getText().toString());
                    if (sx <= 0) {
                        Toast.makeText(hopDong_Activity.this, "Số xe phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(hopDong_Activity.this, "Số xe phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgAnhhd.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                hinhAnh = byteArrayOutputStream.toByteArray();



                int soThang = Integer.parseInt(edtSothang_hd.getText().toString());
                int tienCoc = Integer.parseInt(edtTienCoc_hd.getText().toString());
                int soNguoi = Integer.parseInt(edtSonguoi_hd.getText().toString());
                int soXe = Integer.parseInt(edtSoxe_hd.getText().toString());

                item = new HopDong();
                item.setMaNguoiThue(mant);
                item.setMaPhong(maphong);
                item.setTenPhong(tenphong);
                item.setGhiChu(edtGhiChu_hd.getText().toString());
                item.setNgayKy(new Date());
                item.setSdt(sdt);
                item.setCCCD(cccd);
                item.setGiaTien(gia);
                item.setThuongTru(dc);
                item.setThoiHan(soThang);
                item.setTienCoc(tienCoc);
                item.setSoNguoi(soNguoi);
                item.setSoXe(soXe);
                item.setHinhAnhhd(hinhAnh);
                item.setGhiChu(edtGhiChu_hd.getText().toString());
                if (dao.insert(item) > 0) {

                    Toast.makeText(hopDong_Activity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(hopDong_Activity.this, phong_Activity.class);
                    hopDong_Activity.this.startActivity(intent);
                    dao.updateTrangThaiPhong(maphong, 1);
                } else {
                    Toast.makeText(hopDong_Activity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
//                capNhapLv();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    void capNhapLv(){
        list= (ArrayList<HopDong>) dao.getAll();
        hopDongAdapter=new HopDong_Adapter(hopDong_Activity.this,list, hopDong_Activity.this);
        lsthopDong.setAdapter(hopDongAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAnhhd.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}