package com.example.nestera.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.Fragment.frg_thongtintaikhoan;
import com.example.nestera.R;
import com.example.nestera.model.NguoiThue;
import com.example.nestera.model.PhongTro;

import java.util.ArrayList;

public class ThongTinTaiKhoan_Adapter extends ArrayAdapter<NguoiThue> {
    private Context context;
    frg_thongtintaikhoan thongtintaikhoan;
    private ArrayList<NguoiThue> list;
    phongTroDao ptDao;
    TextView txtHoTen;
    EditText edtGioiTinh,edtNamSinh,edtSDT,edtThuongTru,edtPhong,edtCCCD;

    public ThongTinTaiKhoan_Adapter(@NonNull Context context, frg_thongtintaikhoan thongtintaikhoan, ArrayList<NguoiThue> list) {
        super(context, 0,list);
        this.context = context;
        this.thongtintaikhoan = thongtintaikhoan;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=inflater.inflate(R.layout.item_thongtintaikhoan,null);

        }
        final  NguoiThue nguoiThue=list.get(position);
        if (nguoiThue!= null){
            txtHoTen=v.findViewById(R.id.txtHoTen);
            edtGioiTinh=v.findViewById(R.id.edtGioiTinh);
            edtNamSinh=v.findViewById(R.id.edtNamSinh);
            edtSDT=v.findViewById(R.id.edtSDT);
            edtThuongTru=v.findViewById(R.id.edtThuongTru);
            edtPhong=v.findViewById(R.id.edtPhong);
            edtCCCD=v.findViewById(R.id.edtCCCD);

            txtHoTen.setText("Họ tên: "+nguoiThue.getTenNguoiThue());
            if (nguoiThue.getGioiTinh()==0){
                edtGioiTinh.setText("Nam");
            }else if (nguoiThue.getGioiTinh()==1){
                edtGioiTinh.setText("Nữ");
            }else {
                edtGioiTinh.setText("Khác");
            }

            edtNamSinh.setText(String.valueOf(nguoiThue.getNamSinh()));
            edtThuongTru.setText(nguoiThue.getThuongTru());
            edtSDT.setText(nguoiThue.getSdt());
            ptDao = new phongTroDao(context);
            PhongTro phongTro = ptDao.getID(String.valueOf(nguoiThue.getMaPhong()));
            edtPhong.setText(phongTro.getTenPhong());

            edtCCCD.setText(String.valueOf(nguoiThue.getcCCD()));
        }
        return v;
    }
}
