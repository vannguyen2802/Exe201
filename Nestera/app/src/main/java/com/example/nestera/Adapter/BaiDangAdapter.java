package com.example.nestera.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nestera.R;
import com.example.nestera.model.BaiDang;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BaiDangAdapter extends ArrayAdapter<BaiDang> {
    private final LayoutInflater inflater;

    public BaiDangAdapter(@NonNull Context context, @NonNull List<BaiDang> data) {
        super(context, 0, data);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_baidang, parent, false);
            h = new ViewHolder();
            h.img = convertView.findViewById(R.id.imgRoom);
            h.badge = convertView.findViewById(R.id.tvBadge);
            h.title = convertView.findViewById(R.id.tvTitle);
            h.location = convertView.findViewById(R.id.tvLocation);
            h.price = convertView.findViewById(R.id.tvPrice);
            h.amenities = convertView.findViewById(R.id.tvAmenities);
            h.area = convertView.findViewById(R.id.tvArea);
            h.btnDetail = convertView.findViewById(R.id.btnDetail);

            h.landlordName = convertView.findViewById(R.id.tvLandlordName);

            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        BaiDang b = getItem(position);
        if (b != null) {
            // image
            if (b.getHinhAnh() != null && !b.getHinhAnh().isEmpty()) {
                try {
                    String[] arr = b.getHinhAnh().split(";");
                    if (arr.length > 0 && arr[0] != null && !arr[0].trim().isEmpty()) {
                        h.img.setImageURI(Uri.parse(arr[0]));
                    } else {
                        h.img.setImageResource(R.drawable.phong_tro_1_1);
                    }
                } catch (Exception e) {
                    h.img.setImageResource(R.drawable.phong_tro_1_1);
                }
            } else {
                h.img.setImageResource(R.drawable.phong_tro_1_1);
            }
            // badge
            h.badge.setText(b.getTrangThai() == null || b.getTrangThai().isEmpty() ? "Còn trống" : b.getTrangThai());
            h.title.setText(b.getTieuDe());
            h.location.setText(b.getDiaChi());
            String price = NumberFormat.getNumberInstance(new Locale("vi","VN")).format(b.getGiaThang()) + "/tháng";
            h.price.setText(price);
            h.amenities.setText(b.getTienNghi());
            h.area.setText(String.valueOf(b.getDienTich()) + "m²");
            // Hiển thị tên chủ trọ
            String landlordId = b.getChuTroId();
            String display = landlordId;
            try {
                com.example.nestera.Dao.chuTroDao cDao = new com.example.nestera.Dao.chuTroDao(getContext());
                com.example.nestera.model.ChuTro ct = cDao.getID(landlordId);
                if (ct != null && ct.getTenChuTro() != null && !ct.getTenChuTro().isEmpty()) {
                    display = ct.getTenChuTro();
                }
            } catch (Exception ignored) {}
            if (display == null || display.isEmpty()) display = landlordId != null ? landlordId : "--";
            h.landlordName.setText("Chủ trọ: " + display);


            h.btnDetail.setOnClickListener(v -> {
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

        return convertView;
    }

    static class ViewHolder {

        ImageView img; TextView badge; TextView title; TextView location; TextView price; TextView amenities; TextView area; Button btnDetail; TextView landlordName;

    }
}


