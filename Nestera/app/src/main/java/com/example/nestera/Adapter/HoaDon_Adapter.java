package com.example.nestera.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.nestera.Activity.ThanhToan_Activity;
import com.example.nestera.Activity.hoaDon_Activity;
import com.example.nestera.Dao.NganHangDao;
import com.example.nestera.Dao.hoaDonDao;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.R;
import com.example.nestera.model.HoaDon;
import com.example.nestera.model.NganHang;
import com.example.nestera.model.NguoiThue;
import com.example.nestera.model.PhongTro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HoaDon_Adapter extends ArrayAdapter<HoaDon> {
    TextView txtPhong_HoaDon,txtTenTruongPhong_HoaDon,txtNgayTao_HoaDon,txtGhiChu_HoaDon,txtTongHoaDon,txtTrangThai_HoaDon;
    ImageView btnDelete, imgAnh,imgAnhQR_tt, imgAnhThanhToan,imgXN;
    private Context context;
    private ArrayList<HoaDon> list;
    hoaDon_Activity hoaDonActivity;
    phongTroDao ptDao;
    Spinner spNganHang;
    NganHangSpinner_Adapter nganHangSpinnerAdapter;
    nguoiThueDao ntDao;
    EditText edtmaHoaDon;
    hoaDonDao hoadonDao;
    byte[] anhthanhtoan;
    byte[] anhqr;
    int maNganHang;
    Button btnChonAnhtt,btnXacNhantt,btnHuytt;
    Dialog dialog;
    ArrayList<NganHang> listnh;
    NganHangDao nhDao;
    final int REQUEST_CODE_FOLDER = 456;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public HoaDon_Adapter(@NonNull Context context, ArrayList<HoaDon> list, hoaDon_Activity hoaDonActivity) {
        super(context, 0,list);
        this.context = context;
        this.list = list;
        this.hoaDonActivity = hoaDonActivity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v=inflater.inflate(R.layout.item_hoadon,null);
        }
        final HoaDon hoaDon = list.get(position);

        SharedPreferences preferences = context.getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        if (hoaDon!=null){
            txtPhong_HoaDon=v.findViewById(R.id.txtPhong_HoaDon);
            txtTenTruongPhong_HoaDon=v.findViewById(R.id.txtTenTruongPhong_HoaDon);
            txtNgayTao_HoaDon=v.findViewById(R.id.txtNgayTao_HoaDon);
            txtGhiChu_HoaDon=v.findViewById(R.id.txtGhiChu_HoaDon);
            txtTongHoaDon=v.findViewById(R.id.txtTongHoaDon);
            txtTrangThai_HoaDon=v.findViewById(R.id.txtTrangThai_HoaDon);
            btnDelete=v.findViewById(R.id.btnDelete);
            imgAnh=v.findViewById(R.id.imgAnh);
            imgXN=v.findViewById(R.id.imgXN);
            txtTrangThai_HoaDon=v.findViewById(R.id.txtTrangThai_HoaDon);

            ptDao = new phongTroDao(context);
            PhongTro phongTro = ptDao.getID(String.valueOf(hoaDon.getMaPhong()));
            txtPhong_HoaDon.setText("Phòng: "+phongTro.getTenPhong());
            ntDao = new nguoiThueDao(context);
            NguoiThue nguoiThue = ntDao.getID(hoaDon.getMaNguoiThue());
            txtTenTruongPhong_HoaDon.setText("Tên trưởng phòng: "+nguoiThue.getTenNguoiThue());
            txtNgayTao_HoaDon.setText("Ngày: "+sdf.format(hoaDon.getNgayTao()));
            txtGhiChu_HoaDon.setText("Ghi chú: "+hoaDon.getGhiChu());


            int tong= 0;
            hoadonDao=new hoaDonDao(context);
            tong=hoadonDao.getTongTienDien(hoaDon.getMaHoaDon())+hoadonDao.getTongTienNuoc(hoaDon.getMaHoaDon())+hoaDon.getPhiDichVu()+hoaDon.getTienPhong();
            txtTongHoaDon.setText("Tổng: "+tong+"đ");
            if (username.equalsIgnoreCase("admin")){
                imgXN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Cảnh báo");
                        builder.setIcon(R.drawable.baseline_warning_24);
                        builder.setMessage("Bạn có chắc chắn xác nhận đã thanh toán");
                        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hoadonDao.updateTrangThaiHoaDon(hoaDon.getMaHoaDon(),2);
                                hoaDon.setTrangThai(2);
                                notifyDataSetChanged();
                                dialogInterface.cancel();
                                Toast.makeText(context, "Đã xác nhận ", Toast.LENGTH_SHORT).show();
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
                });
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hoaDonActivity.xoa(String.valueOf(hoaDon.getMaHoaDon()));
                    }
                });
            }else {
                imgXN.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }




            if (hoaDon.getTrangThai()==0){
                txtTrangThai_HoaDon.setText("Thanh toán ngay");
                txtTrangThai_HoaDon.setTextColor(Color.GREEN);
                imgXN.setVisibility(View.GONE);

            }else if (hoaDon.getTrangThai()==1){
//                txtTrangThai_HoaDon.setText("Chờ xác nhận");
//                txtTrangThai_HoaDon.setTextColor(Color.RED);
                txtTrangThai_HoaDon.setText("Đã thanh toán");
                txtTrangThai_HoaDon.setTextColor(Color.GREEN);
                imgXN.setVisibility(View.GONE);

            }else {
//                txtTrangThai_HoaDon.setText("Đã thanh toán");
//                txtTrangThai_HoaDon.setTextColor(Color.GREEN);
                txtTrangThai_HoaDon.setText("Chờ xác nhận");
                txtTrangThai_HoaDon.setTextColor(Color.RED);
                imgXN.setVisibility(View.GONE);
            }

//            final int[] isButtonClicked = {0};


//            if (isButtonClicked[0] ==1){
//                hoadonDao.updateTrangThaiHoaDon(hoaDon.getMaHoaDon(),2);
//            }


            anhthanhtoan=hoaDon.getAnhThanhToan();
            Bitmap bitmap = BitmapFactory.decodeByteArray(anhthanhtoan,0,anhthanhtoan.length);
            imgAnh.setImageBitmap(bitmap);




            txtTrangThai_HoaDon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (hoaDon.getTrangThai()==0){
                        Intent intent = new Intent(context, ThanhToan_Activity.class);
                        intent.putExtra("mahoadon",hoaDon.getMaHoaDon());
                        context.startActivity(intent);
                    }
                }
            });
        }


        return v;
    }

}
