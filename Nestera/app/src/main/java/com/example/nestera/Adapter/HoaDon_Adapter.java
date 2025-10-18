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
        if (v == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            v = inflater.inflate(R.layout.item_hoadon, null);
        }
        final HoaDon hoaDon = list.get(position);

        SharedPreferences preferences = context.getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");

        if (hoaDon != null) {
            // Ánh xạ view
            txtPhong_HoaDon = v.findViewById(R.id.txtPhong_HoaDon);
            txtTenTruongPhong_HoaDon = v.findViewById(R.id.txtTenTruongPhong_HoaDon);
            txtNgayTao_HoaDon = v.findViewById(R.id.txtNgayTao_HoaDon);
            txtGhiChu_HoaDon = v.findViewById(R.id.txtGhiChu_HoaDon);
            txtTongHoaDon = v.findViewById(R.id.txtTongHoaDon);
            txtTrangThai_HoaDon = v.findViewById(R.id.txtTrangThai_HoaDon);
            btnDelete = v.findViewById(R.id.btnDelete);
            imgAnh = v.findViewById(R.id.imgAnh);
            imgXN = v.findViewById(R.id.imgXN);

            // Set dữ liệu
            ptDao = new phongTroDao(context);
            PhongTro phongTro = ptDao.getID(String.valueOf(hoaDon.getMaPhong()));
            txtPhong_HoaDon.setText("Phòng: " + phongTro.getTenPhong());

            ntDao = new nguoiThueDao(context);
            NguoiThue nguoiThue = ntDao.getID(hoaDon.getMaNguoiThue());
            txtTenTruongPhong_HoaDon.setText("Tên trưởng phòng: " + nguoiThue.getTenNguoiThue());
            txtNgayTao_HoaDon.setText("Ngày: " + sdf.format(hoaDon.getNgayTao()));
            txtGhiChu_HoaDon.setText("Ghi chú: " + hoaDon.getGhiChu());

            hoadonDao = new hoaDonDao(context);
            int tong = hoadonDao.getTongTien(hoaDon.getMaHoaDon());
            txtTongHoaDon.setText("Tổng: " + tong + "đ");

            // --- SỬA LỖI LOGIC HIỂN THỊ VÀ CHỨC NĂNG ---

            // Mặc định ẩn các nút chức năng của admin
            btnDelete.setVisibility(View.GONE);
            imgXN.setVisibility(View.GONE);

            if (username.equalsIgnoreCase("admin")) {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(view -> hoaDonActivity.xoa(String.valueOf(hoaDon.getMaHoaDon())));

                // Chỉ hiển thị nút xác nhận khi hóa đơn đang ở trạng thái "Chờ xác nhận"
                if (hoaDon.getTrangThai() == 1) {
                    imgXN.setVisibility(View.VISIBLE);
                    imgXN.setOnClickListener(view -> hoaDonActivity.udTrangThai(hoaDon.getMaHoaDon()));
                }
            }

            // Logic trạng thái
            switch (hoaDon.getTrangThai()) {
                case 0: // Chưa thanh toán
                    txtTrangThai_HoaDon.setText("Thanh toán ngay");
                    txtTrangThai_HoaDon.setTextColor(Color.parseColor("#FF009900")); // Màu xanh lá
                    break;
                case 1: // Chờ xác nhận
                    txtTrangThai_HoaDon.setText("Chờ xác nhận");
                    txtTrangThai_HoaDon.setTextColor(Color.parseColor("#FFFF8800")); // Màu cam
                    break;
                case 2: // Đã thanh toán
                    txtTrangThai_HoaDon.setText("Đã thanh toán");
                    txtTrangThai_HoaDon.setTextColor(Color.GRAY);
                    break;
            }

            // --- SỬA LỖI CRASH KHI ẢNH NULL ---
            anhthanhtoan = hoaDon.getAnhThanhToan();
            if (anhthanhtoan != null && anhthanhtoan.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(anhthanhtoan, 0, anhthanhtoan.length);
                imgAnh.setImageBitmap(bitmap);
                imgAnh.setVisibility(View.VISIBLE);
            } else {
                // Ẩn ImageView đi nếu không có ảnh
                imgAnh.setVisibility(View.GONE);
            }

            // Click để thanh toán
            txtTrangThai_HoaDon.setOnClickListener(view -> {
                if (hoaDon.getTrangThai() == 0) { // Chỉ cho phép thanh toán khi chưa thanh toán
                    Intent intent = new Intent(context, ThanhToan_Activity.class);
                    intent.putExtra("mahoadon", hoaDon.getMaHoaDon());
                    context.startActivity(intent);
                }
            });
        }
        return v;
    }
}
