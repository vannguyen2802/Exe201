package com.example.nestera.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nestera.Activity.nguoiThue_Activity;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.R;
import com.example.nestera.model.NguoiThue;
import com.example.nestera.model.PhongTro;

import java.util.ArrayList;

public class NguoiThue_Adapter extends ArrayAdapter<NguoiThue> {
    private Context context;
    nguoiThue_Activity nguoiThue_activity;
    private ArrayList<NguoiThue> list;
    phongTroDao ptDao;
    TextView txtHoTen,txtGioiTinh,txtNamSinh,txtSdt,txtThuongTru,txtCCCD,txtPhong;
    ImageView btnDelete;

    public NguoiThue_Adapter(@NonNull Context context, nguoiThue_Activity nguoiThue_activity, ArrayList<NguoiThue> list) {
        super(context, 0,list);
        this.context = context;
        this.nguoiThue_activity = nguoiThue_activity;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.item_nguoithue,null);

        }
        final  NguoiThue nguoiThue=list.get(position);
        if (nguoiThue!= null){
            txtHoTen=v.findViewById(R.id.txtHoTen);
            txtGioiTinh=v.findViewById(R.id.txtGioiTinh);
            txtNamSinh=v.findViewById(R.id.txtNamSinh);
            txtThuongTru=v.findViewById(R.id.txtThuongTru);
            txtSdt=v.findViewById(R.id.txtSdt);
            txtCCCD=v.findViewById(R.id.txtCCCD);
            txtPhong=v.findViewById(R.id.txtPhong);
            btnDelete=v.findViewById(R.id.btnDelete);

            txtHoTen.setText("Họ tên: "+nguoiThue.getTenNguoiThue());

            // Map giới tính: 1 = Nam, 0 = Nữ, 2 = Khác
            if (nguoiThue.getGioiTinh()==1){
                txtGioiTinh.setText("Giới tính: Nam");
            } else if (nguoiThue.getGioiTinh()==0){
                txtGioiTinh.setText("Giới tính: Nữ");
            } else {
                txtGioiTinh.setText("Giới tính: Khác");
            }

            txtNamSinh.setText("Năm sinh: "+String.valueOf(nguoiThue.getNamSinh()));
            txtThuongTru.setText("Thường trú: "+nguoiThue.getThuongTru());
            txtSdt.setText("SĐT: "+nguoiThue.getSdt());
            // Hiển thị phòng an toàn kể cả khi người thuê chưa có phòng hoặc phòng bị xoá
            ptDao = new phongTroDao(context);
            if (nguoiThue.getMaPhong() <= 0) {
                txtPhong.setText("Phòng: Chưa có phòng");
            } else {
                PhongTro phongTro = null;
                try {
                    phongTro = ptDao.getID(String.valueOf(nguoiThue.getMaPhong()));
                } catch (Exception ignore) { }
                if (phongTro != null) {
                    txtPhong.setText("Phòng: "+phongTro.getTenPhong());
                } else {
                    txtPhong.setText("Phòng: #"+nguoiThue.getMaPhong());
                }
            }

            txtCCCD.setText("CCCD: "+nguoiThue.getcCCD());

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nguoiThue_activity.xoa(nguoiThue.getMaNguoithue());
                }
            });

        }
        return v;
    }
}
