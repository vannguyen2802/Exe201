package com.example.nestera.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nestera.Activity.hopDong_Activity;
import com.example.nestera.Dao.hopDongDao;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.R;
import com.example.nestera.model.HopDong;
import com.example.nestera.model.NguoiThue;
import com.example.nestera.model.PhongTro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HopDong_Adapter extends ArrayAdapter<HopDong> {
    EditText edtma_hd, edtTenkh_hd, edtSdt_hd, edtCCCD_hd, edtDiaChi_hd, edtNgayki_hd, edtSothang_hd, edtSoPhong_hd, edtTienCoc_hd, edtTienPhong_hd, edtSonguoi_hd, edtSoxe_hd, edtGhiChu_hd;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Context context;
    private ArrayList<HopDong> list;
    hopDong_Activity hopDong_activity;
    Button btnKetThuc, btnCapNhap;
    ImageView imgAnhHopDong;
    hopDongDao dao;
    phongTroDao ptDao;
    nguoiThueDao ntDao;
    private Phong_Adapter phongAdapter;
    byte[] hinhAnh;

    public HopDong_Adapter(@NonNull Context context, ArrayList<HopDong> list, hopDong_Activity hopDong_activity) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
        this.hopDong_activity = hopDong_activity;
        dao = new hopDongDao(context);
    }

    public void setPhongAdapter(Phong_Adapter adapter) {
        this.phongAdapter = adapter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            v = inflater.inflate(R.layout.item_xemhopdong, null);
        }
        final HopDong hd = list.get(position);
        SharedPreferences preferences = context.getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        if (hd != null) {
            edtma_hd = v.findViewById(R.id.edtmaPhong_hd);
            edtTenkh_hd = v.findViewById(R.id.edtTenkh_hd);
            edtSdt_hd = v.findViewById(R.id.edtSdt_hd);
            edtCCCD_hd = v.findViewById(R.id.edtCCCD_hd);
            edtDiaChi_hd = v.findViewById(R.id.edtDiaChi_hd);
            edtNgayki_hd = v.findViewById(R.id.edtNgayki_hd);
            edtSothang_hd = v.findViewById(R.id.edtSothang_hd);
            edtSoPhong_hd = v.findViewById(R.id.edtSoPhong_hd);
            edtTienCoc_hd = v.findViewById(R.id.edtTienCoc_hd);
            edtTienPhong_hd = v.findViewById(R.id.edtTienPhong_hd);
            edtSonguoi_hd = v.findViewById(R.id.edtSonguoi_hd);
            edtSoxe_hd = v.findViewById(R.id.edtSoxe_hd);
            edtGhiChu_hd = v.findViewById(R.id.edtGhiChu_hd);
            btnCapNhap = v.findViewById(R.id.btnCapNhat_hd);
            btnKetThuc = v.findViewById(R.id.btnKetThuc_hd);
            imgAnhHopDong = v.findViewById(R.id.imgAnhHopDong);
            edtma_hd.setEnabled(false);
            edtTenkh_hd.setEnabled(false);
            edtSdt_hd.setEnabled(false);
            edtCCCD_hd.setEnabled(false);
            edtDiaChi_hd.setEnabled(false);
            edtSoPhong_hd.setEnabled(false);
            edtTienPhong_hd.setEnabled(false);
            edtNgayki_hd.setEnabled(false);
            edtTienCoc_hd.setEnabled(false);

            edtma_hd.setText(hd.getMaHopDong() + "");


            edtSdt_hd.setText(hd.getSdt());
            edtCCCD_hd.setText(hd.getCCCD() + "");
            edtDiaChi_hd.setText(hd.getThuongTru());
            edtNgayki_hd.setText(sdf.format(hd.getNgayKy()));
            edtSothang_hd.setText(hd.getThoiHan() + "");

            ptDao = new phongTroDao(context);
            PhongTro phongTro = ptDao.getID(String.valueOf(hd.getMaPhong()));
            edtSoPhong_hd.setText(phongTro.getTenPhong());
            edtTienCoc_hd.setText(hd.getTienCoc() + "");
            edtTienPhong_hd.setText(hd.getGiaTien() + "");
            edtSonguoi_hd.setText(hd.getSoNguoi() + "");
            edtSoxe_hd.setText(hd.getSoXe() + "");
            edtGhiChu_hd.setText(hd.getGhiChu());
            hinhAnh = hd.getHinhAnhhd();
            Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh, 0, hinhAnh.length);
            imgAnhHopDong.setImageBitmap(bitmap);


            ntDao = new nguoiThueDao(context);
            NguoiThue nguoiThue = ntDao.getID(hd.getMaNguoiThue());
            edtTenkh_hd.setText(String.valueOf(nguoiThue.getTenNguoiThue()));
            if(username.equalsIgnoreCase("admin")) {
            btnCapNhap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int soNguoi = Integer.parseInt(edtSonguoi_hd.getText().toString());
                    int soXe = Integer.parseInt(edtSoxe_hd.getText().toString());
                    hd.setSoNguoi(soNguoi);
                    hd.setSoXe(soXe);
                    dao.update(hd);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Cập nhập hợp đồng thàng công", Toast.LENGTH_SHORT).show();
                }
            });
            btnKetThuc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hopDong_activity.xoa(String.valueOf(hd.getMaHopDong()));
                }
            });
        }else {
            btnCapNhap.setVisibility(View.GONE);
            btnKetThuc.setVisibility(View.GONE);
            edtSonguoi_hd.setEnabled(false);
            edtSothang_hd.setEnabled(false);
            edtSoxe_hd.setEnabled(false);
            edtGhiChu_hd.setEnabled(false);
        }
        }
        return v;
    }
}
