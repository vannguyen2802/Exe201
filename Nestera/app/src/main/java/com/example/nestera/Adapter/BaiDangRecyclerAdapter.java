package com.example.nestera.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nestera.Activity.BaiDangDetailActivity;
import com.example.nestera.R;
import com.example.nestera.model.BaiDang;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BaiDangRecyclerAdapter extends RecyclerView.Adapter<BaiDangRecyclerAdapter.BaiDangViewHolder> {
    private Context context;
    private List<BaiDang> baiDangList;

    public BaiDangRecyclerAdapter(Context context, List<BaiDang> baiDangList) {
        this.context = context;
        this.baiDangList = baiDangList;
    }

    @NonNull
    @Override
    public BaiDangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new BaiDangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaiDangViewHolder holder, int position) {
        BaiDang baiDang = baiDangList.get(position);
        
        // Tên phòng (Tiêu đề)
        holder.tvRoomName.setText(baiDang.getTieuDe());
        
        // Địa chỉ
        String location = baiDang.getDiaChi();
        if (!TextUtils.isEmpty(location)) {
            holder.tvLocation.setText(location);
        } else {
            holder.tvLocation.setText("Chưa cập nhật địa chỉ");
        }
        
        // Giá phòng
        double priceInMillion = baiDang.getGiaThang() / 1000000.0;
        holder.tvPrice.setText(String.format("%.2ftr/tháng", priceInMillion));
        
        // Rating (mặc định 4.8 - có thể mở rộng sau)
        holder.tvRating.setText("4.8");
        
        // Diện tích
        if (holder.tvArea != null) {
            holder.tvArea.setText(String.format("%.0fm²", baiDang.getDienTich()));
        }
        
        // Tiện nghi
        if (holder.tvAmenities != null && !TextUtils.isEmpty(baiDang.getTienNghi())) {
            holder.tvAmenities.setText(baiDang.getTienNghi());
        }
        
        // Trạng thái phòng
        if (holder.tvStatus != null) {
            String status = baiDang.getTrangThai();
            if (TextUtils.isEmpty(status)) {
                status = "Còn trống";
            }
            holder.tvStatus.setText(status);
            
            // Màu sắc theo trạng thái
            if ("Còn trống".equalsIgnoreCase(status)) {
                holder.tvStatus.setBackgroundResource(R.drawable.status_badge_background);
            } else {
                holder.tvStatus.setBackgroundResource(R.drawable.filter_button_background);
            }
        }
        
        // Load ảnh từ hinhAnh (URI string)
        if (!TextUtils.isEmpty(baiDang.getHinhAnh())) {
            try {
                String[] imageUris = baiDang.getHinhAnh().split(";");
                if (imageUris.length > 0 && !TextUtils.isEmpty(imageUris[0])) {
                    // Load ảnh từ URI
                    holder.ivRoomImage.setImageURI(Uri.parse(imageUris[0]));
                } else {
                    holder.ivRoomImage.setImageResource(R.drawable.phong_tro_1_1);
                }
            } catch (Exception e) {
                holder.ivRoomImage.setImageResource(R.drawable.phong_tro_1_1);
            }
        } else {
            holder.ivRoomImage.setImageResource(R.drawable.phong_tro_1_1);
        }
        
        // Landlord name under area
        try {
            android.widget.TextView tvLandlord = holder.itemView.findViewById(R.id.tvLandlordName);
            if (tvLandlord != null) {
                String landlordId = baiDang.getChuTroId();
                String display = landlordId;
                try {
                    com.example.nestera.Dao.chuTroDao cDao = new com.example.nestera.Dao.chuTroDao(context);
                    com.example.nestera.model.ChuTro ct = cDao.getID(landlordId);
                    if (ct != null && ct.getTenChuTro() != null && !ct.getTenChuTro().isEmpty()) {
                        display = ct.getTenChuTro();
                    }
                } catch (Exception ignored) {}
                if (display == null || display.isEmpty()) display = landlordId != null ? landlordId : "--";
                tvLandlord.setText("Chủ trọ: " + display);
            }
        } catch (Exception ignored) {}

        // Button xem chi tiết
        holder.btnViewDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, BaiDangDetailActivity.class);
            intent.putExtra("postId", baiDang.getId());
            intent.putExtra("tieuDe", baiDang.getTieuDe());
            intent.putExtra("diaChi", baiDang.getDiaChi());
            intent.putExtra("giaThang", baiDang.getGiaThang());
            intent.putExtra("dienTich", baiDang.getDienTich());
            intent.putExtra("tienNghi", baiDang.getTienNghi());
            intent.putExtra("trangThai", baiDang.getTrangThai());
            intent.putExtra("hinhAnh", baiDang.getHinhAnh());
            intent.putExtra("chuTroId", baiDang.getChuTroId());
            if (baiDang.getMaPhong() != null) {
                intent.putExtra("maPhong", baiDang.getMaPhong());
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return baiDangList.size();
    }

    public static class BaiDangViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoomImage, ivFavorite;
        TextView tvRoomName, tvLocation, tvPrice, tvRating, tvArea, tvAmenities, tvStatus, btnViewDetail;

        public BaiDangViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvArea = itemView.findViewById(R.id.tvArea);
            tvAmenities = itemView.findViewById(R.id.tvAmenities);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
        }
    }
}



