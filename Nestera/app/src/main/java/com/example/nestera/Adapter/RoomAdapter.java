package com.example.nestera.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nestera.R;
import com.example.nestera.model.PhongTro;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private Context context;
    private List<PhongTro> roomList;

    public RoomAdapter(Context context, List<PhongTro> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        PhongTro room = roomList.get(position);
        
        // Tên phòng
        holder.tvRoomName.setText(room.getTenPhong());
        
        // Địa chỉ (có thể mở rộng từ model sau)
        holder.tvLocation.setText("Quận 1, TP.HCM • 1.2km");
        
        // Giá phòng
        holder.tvPrice.setText(String.format("%.1ftr/tháng", room.getGia() / 1000000.0));
        
        // Rating (có thể mở rộng từ model sau)
        holder.tvRating.setText("4.8");
        
        // Diện tích (có thể mở rộng từ model sau)
        if (holder.tvArea != null) {
            holder.tvArea.setText("25m²");
        }
        
        // Tiện nghi
        if (holder.tvAmenities != null) {
            // Format tiện nghi với dấu bullet
            String amenities = room.getTienNghi().replace(",", " •");
            holder.tvAmenities.setText(amenities);
        }
        
        // Trạng thái phòng
        if (holder.tvStatus != null) {
            String status = room.getTrangThai() == 0 ? "Còn trống" : "Đã thuê";
            holder.tvStatus.setText(status);
            // Sử dụng background nhất quán với text trắng dễ nhìn
            holder.tvStatus.setBackgroundResource(R.drawable.status_badge_background);
            // Màu text theo trạng thái (vì background đã cố định)
            if (room.getTrangThai() == 0) {
                holder.tvStatus.setTextColor(Color.WHITE); // Còn trống - trắng
            } else {
                holder.tvStatus.setTextColor(Color.YELLOW); // Đã thuê - vàng để phân biệt
            }
        }
        
        // Load ảnh từ imagePath
        String imagePath = room.getImagePath();
        if (!TextUtils.isEmpty(imagePath)) {
            // Load ảnh từ drawable theo tên (hỗ trợ cả .jpg và .xml)
            String imageName = imagePath;
            // Loại bỏ extension nếu có
            if (imageName.contains(".")) {
                imageName = imageName.substring(0, imageName.lastIndexOf("."));
            }
            
            int imageResId = context.getResources().getIdentifier(
                imageName, "drawable", context.getPackageName());
            if (imageResId != 0) {
                holder.ivRoomImage.setImageResource(imageResId);
            } else {
                // Nếu không tìm thấy ảnh theo tên, thử với ảnh mặc định
                holder.ivRoomImage.setImageResource(R.drawable.phong_tro_1_1);
            }
        } else {
            // Sử dụng ảnh mặc định nếu không có imagePath
            holder.ivRoomImage.setImageResource(R.drawable.phong_tro_1_1);
        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoomImage, ivFavorite;
        TextView tvRoomName, tvLocation, tvPrice, tvRating, tvArea, tvAmenities, tvStatus, btnViewDetail;

        public RoomViewHolder(@NonNull View itemView) {
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