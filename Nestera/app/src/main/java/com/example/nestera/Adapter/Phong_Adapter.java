package com.example.nestera.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
    TextView txtPhong, txtXemHopDong, txtGia, txtTienNghi,txtCoSo_Phong,txtTinhTrang,txtma, tvLocation, tvRating;
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
            tvLocation = v.findViewById(R.id.tvLocation);


            txtPhong.setText(phongTro.getTenPhong());
            txtGia.setText(phongTro.getGia() + " VND/tháng");
            txtTienNghi.setText("Tiện nghi: " + phongTro.getTienNghi());
            
            // Set địa chỉ
            if (phongTro.getDiaChi() != null && !phongTro.getDiaChi().isEmpty()) {
                tvLocation.setText(phongTro.getDiaChi());
            } else {
                tvLocation.setText("Chưa cập nhật địa chỉ");
            }
            

            loaiPhongDao=new LoaiPhongDao(context);
            LoaiPhong loaiPhong=loaiPhongDao.getID(String.valueOf(phongTro.getMaLoai()));
            txtCoSo_Phong.setText(loaiPhong.getTenLoaiPhong());
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
            
            // Load ảnh từ imagePath
            if (ivRoomImage != null) {
                String imagePath = phongTro.getImagePath();
                android.util.Log.d("PhongAdapter", "Loading image for room " + phongTro.getTenPhong() + " - imagePath: " + imagePath);
                if (imagePath != null && !imagePath.isEmpty()) {
                    // Kiểm tra xem có phải URI hay tên file drawable
                    if (imagePath.startsWith("content://") || imagePath.startsWith("file://")) {
                        // Load từ URI bằng Glide
                        try {
                            Glide.with(context)
                                .load(Uri.parse(imagePath))
                                .placeholder(R.drawable.phong_tro_1_1)
                                .error(R.drawable.phong_tro_1_2)
                                .into(ivRoomImage);
                        } catch (Exception e) {
                            android.util.Log.e("PhongAdapter", "Failed to load URI: " + imagePath, e);
                            // Fallback với ảnh phòng trọ đa dạng
                            int[] roomImages = {R.drawable.phong_tro_1_1, R.drawable.phong_tro_1_2, R.drawable.phong_tro_1_3};
                            int imageIndex = position % roomImages.length;
                            ivRoomImage.setImageResource(roomImages[imageIndex]);
                        }
                    } else {
                        // Load từ drawable resources
                        String imageName = imagePath;
                        // Loại bỏ extension nếu có
                        if (imageName.contains(".")) {
                            imageName = imageName.substring(0, imageName.lastIndexOf("."));
                        }
                        
                        int imageResId = context.getResources().getIdentifier(
                            imageName, "drawable", context.getPackageName());
                        if (imageResId != 0) {
                            ivRoomImage.setImageResource(imageResId);
                        } else {
                            // Fallback với ảnh phòng trọ đa dạng
                            int[] roomImages = {R.drawable.phong_tro_1_1, R.drawable.phong_tro_1_2, R.drawable.phong_tro_1_3};
                            int imageIndex = position % roomImages.length;
                            ivRoomImage.setImageResource(roomImages[imageIndex]);
                        }
                    }
                } else {
                    // Hiển thị ảnh phòng trọ đa dạng khi chưa có imagePath
                    int[] roomImages = {R.drawable.phong_tro_1_1, R.drawable.phong_tro_1_2, R.drawable.phong_tro_1_3};
                    int imageIndex = position % roomImages.length;
                    ivRoomImage.setImageResource(roomImages[imageIndex]);
                }
            }
        }

        return v;
    }
}
