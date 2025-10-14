package com.example.nestera.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nestera.Activity.phong_Activity;
import com.example.nestera.Dao.LoaiPhongDao;
import com.example.nestera.R;
import com.example.nestera.model.LoaiPhong;
import com.example.nestera.model.PhongTro;

import java.util.ArrayList;

public class Phong_Adapter extends ArrayAdapter<PhongTro> {
    private Context context;
    phong_Activity phong_activity;
    private ArrayList<PhongTro> list;
    LoaiPhongDao loaiPhongDao;
    TextView txtPhong, txtXemHopDong, txtGia, txtTienNghi,txtCoSo_Phong,txtTinhTrang,txtma;
    ImageView btnDelete, ivRoomImage;

    public Phong_Adapter(@NonNull Context context, phong_Activity phong_activity, ArrayList<PhongTro> list) {
        super(context, 0,list);
        this.context = context;
        this.phong_activity = phong_activity;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_phong, null);
        }

        final PhongTro phongTro = list.get(position);

        if (phongTro != null) {
            // Bind views
            txtPhong = v.findViewById(R.id.txtPhong);
            btnDelete = v.findViewById(R.id.btnDeletePhong);
            ivRoomImage = v.findViewById(R.id.ivRoomImage);
            txtGia = v.findViewById(R.id.txtGia);
            txtTienNghi = v.findViewById(R.id.txtTienNghi);
            txtCoSo_Phong = v.findViewById(R.id.txtLoaiPhong_Phong);
            txtTinhTrang = v.findViewById(R.id.tvStatus);
            txtXemHopDong = v.findViewById(R.id.txtXemHopDong);
            TextView tvLocation = v.findViewById(R.id.tvLocation);


            txtPhong.setText("Phòng: " + phongTro.getTenPhong());
            txtGia.setText("Giá: " + phongTro.getGia());
            txtTienNghi.setText("Tiện nghi: " + phongTro.getTienNghi());
            if (tvLocation != null) {
                String dc = phongTro.getDiaChi();
                if (dc == null || dc.isEmpty()) dc = "";
                tvLocation.setText(dc);
            }

            loaiPhongDao=new LoaiPhongDao(context);
            LoaiPhong loaiPhong=loaiPhongDao.getID(String.valueOf(phongTro.getMaLoai()));
            txtCoSo_Phong.setText("Loại phòng: " + loaiPhong.getTenLoaiPhong());
                txtXemHopDong.setPaintFlags(txtXemHopDong.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            if (phongTro.getTrangThai() == 1) {
                txtXemHopDong.setText("Xem hợp đồng");
                txtXemHopDong.setTextColor(Color.GREEN);
                txtTinhTrang.setText("Đã cho thuê");
                txtTinhTrang.setTextColor(Color.GREEN);
//                txtXemHopDong.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//phong_activity.xemHD(position);
//                    }
//                });
            } else {
                txtXemHopDong.setText("Tạo hợp đồng");
                txtXemHopDong.setTextColor(Color.RED);
                txtTinhTrang.setText("Đang trống");
                txtTinhTrang.setTextColor(Color.RED);
//                txtXemHopDong.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(context, TaoHopDong_Activity.class);
//                        context.startActivity(intent);
//                    }
//                });
            }
            txtXemHopDong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    phong_activity.xemHD(position);
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    phong_activity.xoa(String.valueOf(phongTro.getMaPhong()));
                }
            });
            
            // Load ảnh: ưu tiên ảnh URI từ bài đăng (nếu đồng bộ), nếu không thì dùng imagePath drawable
            if (ivRoomImage != null) {
                String imagePath = phongTro.getImagePath();
                if (imagePath != null && imagePath.startsWith("content:")) {
                    try { ivRoomImage.setImageURI(android.net.Uri.parse(imagePath)); } catch (Exception e) { ivRoomImage.setImageResource(R.drawable.phong_tro_1_1); }
                } else if (imagePath != null && !imagePath.isEmpty()) {
                    String imageName = imagePath;
                    if (imageName.contains(".")) {
                        imageName = imageName.substring(0, imageName.lastIndexOf("."));
                    }
                    int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
                    if (imageResId != 0) ivRoomImage.setImageResource(imageResId); else ivRoomImage.setImageResource(R.drawable.phong_tro_1_1);
                } else {
                    ivRoomImage.setImageResource(R.drawable.phong_tro_1_1);
                }
            }
        }

        return v;
    }
}
