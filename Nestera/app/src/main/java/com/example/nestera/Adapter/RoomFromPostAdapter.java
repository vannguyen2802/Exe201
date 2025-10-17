package com.example.nestera.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nestera.R;
import com.example.nestera.model.BaiDang;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RoomFromPostAdapter extends ArrayAdapter<BaiDang> {
    private final LayoutInflater inflater;

    public RoomFromPostAdapter(@NonNull Context context, @NonNull List<BaiDang> data) {
        super(context, 0, data);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.item_phong, parent, false);
        }

        BaiDang b = getItem(position);
        if (b != null) {
            ImageView iv = v.findViewById(R.id.ivRoomImage);
            if (b.getHinhAnh()!=null && !b.getHinhAnh().isEmpty()) {
                String first = b.getHinhAnh().split(";")[0];
                try { iv.setImageURI(Uri.parse(first)); } catch (Exception ignored) {}
            }
            TextView txtPhong = v.findViewById(R.id.txtPhong);
            TextView tvLocation = v.findViewById(R.id.tvLocation);
            TextView txtGia = v.findViewById(R.id.txtGia);
            TextView txtTienNghi = v.findViewById(R.id.txtTienNghi);
            TextView tvArea = v.findViewById(R.id.tvArea);
            TextView tvStatus = v.findViewById(R.id.tvStatus);
            TextView btnDetail = v.findViewById(R.id.txtXemHopDong);


            txtPhong.setText("Phòng: " + b.getTieuDe());

            tvLocation.setText(b.getDiaChi());
            txtGia.setText(NumberFormat.getNumberInstance(new Locale("vi","VN")).format(b.getGiaThang()));
            txtTienNghi.setText("Tiện nghi: " + b.getTienNghi());
            tvArea.setText(String.valueOf(b.getDienTich())+"m²");
            tvStatus.setText(b.getTrangThai());

            if (btnDetail != null) {
                btnDetail.setText("Xem chi tiết");
                btnDetail.setOnClickListener(view -> {
                    android.content.Intent it = new android.content.Intent(getContext(), com.example.nestera.Activity.BaiDangDetailActivity.class);
                    it.putExtra("postId", b.getId());
                    it.putExtra("tieuDe", b.getTieuDe());
                    it.putExtra("diaChi", b.getDiaChi());
                    it.putExtra("giaThang", b.getGiaThang());
                    it.putExtra("dienTich", b.getDienTich());
                    it.putExtra("tienNghi", b.getTienNghi());
                    it.putExtra("trangThai", b.getTrangThai());
                    it.putExtra("hinhAnh", b.getHinhAnh());
                    it.putExtra("chuTroId", b.getChuTroId());
                    if (b.getMaPhong()!=null) it.putExtra("maPhong", b.getMaPhong());
                    getContext().startActivity(it);
                });
            }
        }

        return v;
    }
}


