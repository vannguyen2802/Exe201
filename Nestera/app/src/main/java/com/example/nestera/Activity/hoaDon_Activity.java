package com.example.nestera.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nestera.Adapter.HoaDon_Adapter;
import com.example.nestera.Adapter.NguoiThueSpinerAdapter;
import com.example.nestera.Adapter.SPPhong_Adapter;
import com.example.nestera.Dao.LoaiPhongDao;
import com.example.nestera.Dao.hoaDonDao;
import com.example.nestera.Dao.hopDongDao;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.MainActivity;
import com.example.nestera.R;
import com.example.nestera.model.HoaDon;
import com.example.nestera.model.LoaiPhong;
import com.example.nestera.model.NguoiThue;
import com.example.nestera.model.PhongTro;
import com.example.nestera.myservice;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class hoaDon_Activity extends AppCompatActivity {
    ListView lstHoaDon;
    ArrayList<HoaDon> list;
    ArrayList<HoaDon> listtemp;
    EditText edtSearch;
    ArrayList<PhongTro> listpt;
    ArrayList<NguoiThue> listnt;
    HoaDon_Adapter hoaDonAdapter;
    HoaDon hoaDon;
    hoaDonDao hdDao;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    TextView txtNgayTao, txtTenTruongPhong, txtSdt, txtTenPhong, txtSoDien, txtGiaDien, txtTongDien, txtSoNguoi, txtGiaNuoc, txtTongNuoc, txtPhiDichVu_hd, txtTienPhong_hd, txtGhiChu_hd, txtTongHd;
    EditText edtNguoithue, edtMaHoaDon, edtSdt, edtPhiDichVu, edtTienphong, edtSoDien, edtDonGiaDien, edtSoNguoi, edtDonGiaNuoc, edtNgayTao, edtGhiChu_hd;
    ImageView btnAdd, imgAnhhd;
    Spinner spPhong;
    NguoiThueSpinerAdapter nguoiThueSpinerAdapter;
    SPPhong_Adapter spPhongAdapter;

    int maPhong, tienPhong, positionNT, positionPT;
    String maNguoiThue, sdt;
    CheckBox chkDaThanhToan;

    Button btnXacNhan, btnHuy;
    Dialog dialog;
    phongTroDao ptDao;
    byte[] hinhAnh;
    nguoiThueDao ntDao;
    final int REQUEST_CODE_FOLDER = 456;
    hopDongDao dao_hd;
    LoaiPhongDao dao_lp;
    int songuoii, maloaii;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoa_don);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hóa Đơn");

        Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lstHoaDon = findViewById(R.id.lstHoaDon);
        hdDao = new hoaDonDao(hoaDon_Activity.this);
        btnAdd = findViewById(R.id.btnadd_toolbar);

        listtemp= (ArrayList<HoaDon>) hdDao.getAll();
        edtSearch=findViewById(R.id.edtSearch);
//        edtSearch.setEnabled(false);

        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar lich=Calendar.getInstance();
                int year=lich.get(Calendar.YEAR);
                int month=lich.get(Calendar.MONTH);
                int day=lich.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datedg=new DatePickerDialog(hoaDon_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        edtSearch.setText(String.format("%d-%02d-%02d",year,month,dayOfMonth));
                    }
                },year,month,day);
                datedg.show();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                list.clear();
                for (HoaDon hd : listtemp){
                    if (sdf.format(hd.getNgayTao()).contains(charSequence.toString())){
                        list.add(hd);
                    }
                }
                hoaDonAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        ntDao = new nguoiThueDao(hoaDon_Activity.this);
        SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");

        dao_hd = new hopDongDao(hoaDon_Activity.this);
        songuoii = dao_hd.getSoNguoiByMaPhongHD(maPhong);
        if (username.equalsIgnoreCase("admin")) {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog(hoaDon_Activity.this, 0);
                }
            });
        } else {
            btnAdd.setVisibility(View.GONE);
        }

        if (username.equalsIgnoreCase("admin")) {
            capNhatLv();
        } else {
            int mp = ntDao.getMaPhongByUser(username);
            list = new ArrayList<HoaDon>();
            list = (ArrayList<HoaDon>) hdDao.getHoaDonByMaPhong(mp);
            hoaDonAdapter = new HoaDon_Adapter(hoaDon_Activity.this, list, this);
            lstHoaDon.setAdapter(hoaDonAdapter);
        }
//        lstHoaDon.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                hoaDon=list.get(i);
//                openDialog(hoaDon_Activity.this,1);
//                return false;
//            }
//        });

        lstHoaDon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hoaDon = list.get(i);
                openDialogCTHD(hoaDon_Activity.this);
            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(hoaDon_Activity.this, MainActivity.class));
            }
        });
    }

    public void xoa(String Id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cảnh báo");
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setMessage("Bạn có chắc chắn muốn xoá");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hdDao.delete(Id);
                capNhatLv();
                dialogInterface.cancel();
                Toast.makeText(hoaDon_Activity.this, "Xóa thành công ", Toast.LENGTH_SHORT).show();
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

    public void udTrangThai(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cảnh báo");
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setMessage("Bạn có chắc chắn muốn xác nhận thanh toán không");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hdDao.updateTrangThaiHoaDon(id, 2);
                capNhatLv();
                dialogInterface.cancel();
                Toast.makeText(hoaDon_Activity.this, "Xóa thành công ", Toast.LENGTH_SHORT).show();
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


    public void capNhatLv() {
        list = (ArrayList<HoaDon>) hdDao.getAll();
        hoaDonAdapter = new HoaDon_Adapter(hoaDon_Activity.this, list, this);
        lstHoaDon.setAdapter(hoaDonAdapter);
    }

    public void capNhatLvbyUser() {

    }


    public void openDialogCTHD(final Context context) {
        dialog = new Dialog(hoaDon_Activity.this);
        dialog.setContentView(R.layout.item_cthoadon);


        txtNgayTao = dialog.findViewById(R.id.txtNgayTao);
        txtTenTruongPhong = dialog.findViewById(R.id.txtTenTruongPhong);
        txtSdt = dialog.findViewById(R.id.txtSdt);
        txtTenPhong = dialog.findViewById(R.id.txtTenPhong);
        txtSoDien = dialog.findViewById(R.id.txtSoDien);
        txtGiaDien = dialog.findViewById(R.id.txtGiaDien);
        txtTongDien = dialog.findViewById(R.id.txtTongDien);
        txtSoNguoi = dialog.findViewById(R.id.txtSoNguoi);
        txtGiaNuoc = dialog.findViewById(R.id.txtGiaNuoc);
        txtTongNuoc = dialog.findViewById(R.id.txtTongNuoc);
        txtPhiDichVu_hd = dialog.findViewById(R.id.txtPhiDichVu_hd);
        txtTienPhong_hd = dialog.findViewById(R.id.txtTienPhong_hd);
        txtGhiChu_hd = dialog.findViewById(R.id.txtGhiChu_hd);
        txtTongHd = dialog.findViewById(R.id.txtTongHd);

        txtNgayTao.setText("" + sdf.format(hoaDon.getNgayTao()));
        ptDao = new phongTroDao(context);
        PhongTro phongTro = ptDao.getID(String.valueOf(hoaDon.getMaPhong()));
        txtTenPhong.setText("Phòng: " + phongTro.getTenPhong());
        ntDao = new nguoiThueDao(context);
        NguoiThue nguoiThue = ntDao.getID(hoaDon.getMaNguoiThue());
        txtTenTruongPhong.setText("Trưởng phòng: " + nguoiThue.getTenNguoiThue());
        txtSdt.setText("Điện thoại: " + nguoiThue.getSdt());
//        String sdt=hoaDon.getSdt();
        txtSoDien.setText("Số điện: " + hoaDon.getSoDien());
        txtGiaDien.setText("Giá điện: " + hoaDon.getDonGiaDien() + "đ/số");
        txtTongDien.setText("Tổng điện: " + hdDao.getTongTienDien(hoaDon.getMaHoaDon()) + "đ");
        txtSoNguoi.setText("Số người: " + hoaDon.getSoNguoi());
        txtGiaNuoc.setText("Giá nước: " + hoaDon.getDonGiaNuoc() + "đ/người");
        txtTongNuoc.setText("Tổng nước: " + hdDao.getTongTienNuoc(hoaDon.getMaHoaDon()) + "đ");
        txtPhiDichVu_hd.setText("Phí dịch vụ: " + hoaDon.getPhiDichVu() + "đ");
        txtTienPhong_hd.setText("Tiền phòng: " + hoaDon.getTienPhong() + "đ");
        txtGhiChu_hd.setText("Ghi chú: " + hoaDon.getGhiChu());

        int tong = 0;
        tong = hdDao.getTongTienDien(hoaDon.getMaHoaDon()) + hdDao.getTongTienNuoc(hoaDon.getMaHoaDon()) + hoaDon.getPhiDichVu() + hoaDon.getTienPhong();
        txtTongHd.setText("Tổng tiền: " + tong + "đ");

        dialog.show();


    }

    public void openDialog(final Context context, final int type) {
        dialog = new Dialog(hoaDon_Activity.this);
        dialog.setContentView(R.layout.dialog_hoa_don);
        edtMaHoaDon = dialog.findViewById(R.id.edtMaHoaDon);
        edtSdt = dialog.findViewById(R.id.edtSdt);
        edtPhiDichVu = dialog.findViewById(R.id.edtPhiDichVu);
        edtTienphong = dialog.findViewById(R.id.edtTienphong);
        edtSoDien = dialog.findViewById(R.id.edtSoDien);
        edtSoNguoi = dialog.findViewById(R.id.edtSoNguoi);
        edtDonGiaDien = dialog.findViewById(R.id.edtDonGiaDien);
        edtDonGiaNuoc = dialog.findViewById(R.id.edtDonGiaNuoc);
        edtNgayTao = dialog.findViewById(R.id.edtNgayTao);
        edtGhiChu_hd = dialog.findViewById(R.id.edtGhiChu_hd);
        chkDaThanhToan = dialog.findViewById(R.id.chkDaThanhToan);
        btnXacNhan = dialog.findViewById(R.id.btnXacNhan);
        btnHuy = dialog.findViewById(R.id.btnHuy);
        spPhong = dialog.findViewById(R.id.spnPhong);
        edtNguoithue = dialog.findViewById(R.id.edtNguoiThue);
        imgAnhhd = dialog.findViewById(R.id.imgAnhQR);
        chkDaThanhToan.setVisibility(View.GONE);
        edtNgayTao.setEnabled(false);
        edtSoNguoi.setEnabled(false);
        edtDonGiaDien.setEnabled(false);
        edtDonGiaNuoc.setEnabled(false);
        edtPhiDichVu.setEnabled(false);
        edtTienphong.setEnabled(false);
        edtNguoithue.setEnabled(false);
        edtSdt.setEnabled(false);
//        edtSoNguoi.setText(hdDao.getSoNguoiByMaPhong(maPhong));

        edtNgayTao.setText("" + sdf.format(new Date()));
        edtMaHoaDon.setVisibility(View.GONE);


        ntDao = new nguoiThueDao(hoaDon_Activity.this);
        listnt = new ArrayList<NguoiThue>();
        listnt = (ArrayList<NguoiThue>) ntDao.getAll();
        nguoiThueSpinerAdapter = new NguoiThueSpinerAdapter(hoaDon_Activity.this, listnt);


        ptDao = new phongTroDao(hoaDon_Activity.this);
        listpt = new ArrayList<PhongTro>();
        listpt = (ArrayList<PhongTro>) ptDao.getPhongByTrangThai(1);
        if (listpt.isEmpty()) {
            Toast.makeText(hoaDon_Activity.this, "Chưa có hợp đồng cho Phòng. Vui lòng tạo hợp đồng trước.", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            return;
        }
        spPhongAdapter = new SPPhong_Adapter(hoaDon_Activity.this, listpt);
        spPhong.setAdapter(spPhongAdapter);
        spPhong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                maPhong = listpt.get(i).getMaPhong();
                tienPhong = listpt.get(i).getGia();
                edtTienphong.setText("" + tienPhong);
                dao_hd = new hopDongDao(hoaDon_Activity.this);
                songuoii = dao_hd.getSoNguoiByMaPhongHD(maPhong);
                edtSoNguoi.setText("" + songuoii);
                sdt = dao_hd.getSDTByMaPhong(maPhong);
                edtSdt.setText(sdt);
                maNguoiThue = dao_hd.getMaNguoiThueByMaPhong(maPhong);
                NguoiThue nt = ntDao.getID(maNguoiThue);
                String ten = nt.getTenNguoiThue();
                edtNguoithue.setText(ten);

                ptDao = new phongTroDao(hoaDon_Activity.this);
                dao_lp = new LoaiPhongDao(hoaDon_Activity.this);
                maloaii = ptDao.getMaLoaiTheoMaPhong(maPhong);

                LoaiPhong lp = dao_lp.getID(String.valueOf(maloaii));
                int gd = lp.getGiaDien();
                int gn = lp.getGiaNuoc();
                int pdv = lp.getPhiDichVu();
                edtDonGiaDien.setText("" + gd);
                edtDonGiaNuoc.setText("" + gn);
                edtPhiDichVu.setText("" + pdv);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (type != 0) {
            edtMaHoaDon.setText(String.valueOf(hoaDon.getMaHoaDon()));
            for (int i = 0; i < listnt.size(); i++) {
                if (hoaDon.getMaNguoiThue() == (listnt.get(i).getMaNguoithue())) {
                    positionNT = i;
                }
            }
            edtSdt.setText(hoaDon.getSdt());
            edtPhiDichVu.setText(String.valueOf(hoaDon.getPhiDichVu()));
            edtTienphong.setText(String.valueOf(hoaDon.getTienPhong()));
            edtSoDien.setText(String.valueOf(hoaDon.getSoDien()));
            edtDonGiaDien.setText(String.valueOf(hoaDon.getDonGiaDien()));
            edtSoNguoi.setText(String.valueOf(hoaDon.getSoNguoi()));
            edtDonGiaNuoc.setText(String.valueOf(hoaDon.getDonGiaNuoc()));
            edtNgayTao.setText("" + sdf.format(new Date()));
            edtGhiChu_hd.setText(hoaDon.getGhiChu());

        }
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtSoDien.getText().toString())) {
                    Toast.makeText(context, "Bạn phải nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int sod = Integer.parseInt(edtSoDien.getText().toString());
                    if (sod <= 0) {
                        Toast.makeText(context, "Số điện phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Số điện phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }
                hoaDon = new HoaDon();
                hoaDon.setMaNguoiThue(maNguoiThue);
                hoaDon.setSdt(sdt);
                hoaDon.setMaPhong(maPhong);
                hoaDon.setPhiDichVu(Integer.parseInt(edtPhiDichVu.getText().toString()));
                hoaDon.setTienPhong(tienPhong);
                hoaDon.setSoDien(Integer.parseInt(edtSoDien.getText().toString()));
                hoaDon.setDonGiaDien(Integer.parseInt(edtDonGiaDien.getText().toString()));
                hoaDon.setSoNguoi(Integer.parseInt(edtSoNguoi.getText().toString()));
                hoaDon.setDonGiaNuoc(Integer.parseInt(edtDonGiaNuoc.getText().toString()));
                hoaDon.setNgayTao(new Date());
                hoaDon.setGhiChu(edtGhiChu_hd.getText().toString());
                hoaDon.setTrangThai(0);


                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgAnhhd.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                hinhAnh = byteArrayOutputStream.toByteArray();
                hoaDon.setAnhThanhToan(hinhAnh);


                if (type == 0) {
                    if (hdDao.insert(hoaDon) > 0) {
                        Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        hoaDon.setTrangThai(0);
                        startSV();
                    } else {
                        Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (chkDaThanhToan.isChecked()) {
                        hoaDon.setTrangThai(2);
                    } else {
                        hoaDon.setTrangThai(1);
                    }
                    hoaDon.setMaHoaDon(Integer.parseInt(edtMaHoaDon.getText().toString()));
                    if (hdDao.update(hoaDon) > 0) {
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                capNhatLv();
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    public void startSV(){
        Intent intent = new Intent(hoaDon_Activity.this, myservice.class);
        ptDao = new phongTroDao(hoaDon_Activity.this);
        PhongTro phongTro = ptDao.getID(String.valueOf(hoaDon.getMaPhong()));
        intent.putExtra("phong",phongTro.getTenPhong());
        startService(intent);
    }


}