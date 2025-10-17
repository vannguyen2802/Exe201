package com.example.nestera.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nestera.Activity.hopDong_Activity;
import com.example.nestera.Dao.hopDongDao;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.R;
import com.example.nestera.model.HopDong;
import com.example.nestera.model.NguoiThue;
import com.example.nestera.model.PhongTro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HopDong_Adapter extends ArrayAdapter<HopDong> {
    EditText edtma_hd, edtTenkh_hd, edtSdt_hd, edtCCCD_hd, edtDiaChi_hd, edtNgayki_hd, edtSothang_hd, edtSoPhong_hd, edtTienCoc_hd, edtTienPhong_hd, edtSonguoi_hd, edtSoxe_hd, edtGhiChu_hd;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Context context;
    private ArrayList<HopDong> list;
    hopDong_Activity hopDong_activity;
    Button btnKetThuc, btnCapNhap;
    ImageView imgAnhHopDong;
    hopDongDao dao;
    phongTroDao ptDao;
    nguoiThueDao ntDao;
    private Phong_Adapter phongAdapter;
    byte[] hinhAnh;

    public HopDong_Adapter(@NonNull Context context, ArrayList<HopDong> list, hopDong_Activity hopDong_activity) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
        this.hopDong_activity = hopDong_activity;
        dao = new hopDongDao(context);
    }

    public void setPhongAdapter(Phong_Adapter adapter) {
        this.phongAdapter = adapter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            v = inflater.inflate(R.layout.item_xemhopdong, null);
        }
        final HopDong hd = list.get(position);
        SharedPreferences preferences = context.getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        if (hd != null) {
            edtma_hd = v.findViewById(R.id.edtmaPhong_hd);
            edtTenkh_hd = v.findViewById(R.id.edtTenkh_hd);
            edtSdt_hd = v.findViewById(R.id.edtSdt_hd);
            edtCCCD_hd = v.findViewById(R.id.edtCCCD_hd);
            edtDiaChi_hd = v.findViewById(R.id.edtDiaChi_hd);
            edtNgayki_hd = v.findViewById(R.id.edtNgayki_hd);
            edtSothang_hd = v.findViewById(R.id.edtSothang_hd);
            edtSoPhong_hd = v.findViewById(R.id.edtSoPhong_hd);
            edtTienCoc_hd = v.findViewById(R.id.edtTienCoc_hd);
            edtTienPhong_hd = v.findViewById(R.id.edtTienPhong_hd);
            edtSonguoi_hd = v.findViewById(R.id.edtSonguoi_hd);
            edtSoxe_hd = v.findViewById(R.id.edtSoxe_hd);
            edtGhiChu_hd = v.findViewById(R.id.edtGhiChu_hd);
            btnCapNhap = v.findViewById(R.id.btnCapNhat_hd);
            btnKetThuc = v.findViewById(R.id.btnKetThuc_hd);
            imgAnhHopDong = v.findViewById(R.id.imgAnhHopDong);
            edtma_hd.setEnabled(false);
            edtTenkh_hd.setEnabled(false);
            edtSdt_hd.setEnabled(false);
            edtCCCD_hd.setEnabled(false);
            edtDiaChi_hd.setEnabled(false);
            edtSoPhong_hd.setEnabled(false);
            edtTienPhong_hd.setEnabled(false);
            edtNgayki_hd.setEnabled(false);
            edtTienCoc_hd.setEnabled(false);
            // Khóa chỉnh sửa tất cả các trường khi hợp đồng đã tạo (chỉ xem)
            edtSothang_hd.setEnabled(false); edtSothang_hd.setFocusable(false); edtSothang_hd.setFocusableInTouchMode(false); edtSothang_hd.setCursorVisible(false);
            edtSonguoi_hd.setEnabled(false); edtSonguoi_hd.setFocusable(false); edtSonguoi_hd.setFocusableInTouchMode(false); edtSonguoi_hd.setCursorVisible(false);
            edtSoxe_hd.setEnabled(false); edtSoxe_hd.setFocusable(false); edtSoxe_hd.setFocusableInTouchMode(false); edtSoxe_hd.setCursorVisible(false);
            edtGhiChu_hd.setEnabled(false); edtGhiChu_hd.setFocusable(false); edtGhiChu_hd.setFocusableInTouchMode(false); edtGhiChu_hd.setCursorVisible(false);

            edtma_hd.setText(hd.getMaHopDong() + "");


            edtSdt_hd.setText(hd.getSdt());
            edtCCCD_hd.setText(hd.getCCCD() + "");
            edtDiaChi_hd.setText(hd.getThuongTru());
            edtNgayki_hd.setText(sdf.format(hd.getNgayKy()));
            edtSothang_hd.setText(hd.getThoiHan() + "");

            ptDao = new phongTroDao(context);
            String tenPhong = null;
            boolean isEnded = false; // Đã Kết Thúc khi phòng về trạng thái trống
            try {
                PhongTro phongTro = ptDao.getID(String.valueOf(hd.getMaPhong()));
                if (phongTro != null) {
                    tenPhong = phongTro.getTenPhong();
                    try { isEnded = (phongTro.getTrangThai() == 0); } catch (Exception ignored2) {}
                }
            } catch (Exception ignored) {}
            if (tenPhong == null || tenPhong.isEmpty()) {
                tenPhong = hd.getTenPhong();
            }
            if (tenPhong == null) tenPhong = String.valueOf(hd.getMaPhong());
            edtSoPhong_hd.setText(tenPhong);
            edtTienCoc_hd.setText(hd.getTienCoc() + "");
            edtTienPhong_hd.setText(hd.getGiaTien() + "");
            edtSonguoi_hd.setText(hd.getSoNguoi() + "");
            edtSoxe_hd.setText(hd.getSoXe() + "");
            edtGhiChu_hd.setText(hd.getGhiChu());
            hinhAnh = hd.getHinhAnhhd();
            Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh, 0, hinhAnh.length);
            imgAnhHopDong.setImageBitmap(bitmap);


            ntDao = new nguoiThueDao(context);
            NguoiThue nguoiThue = ntDao.getID(hd.getMaNguoiThue());
            edtTenkh_hd.setText(String.valueOf(nguoiThue.getTenNguoiThue()));
            if(username.equalsIgnoreCase("admin") || username.equalsIgnoreCase("landlord") || context.getSharedPreferences("user11", MODE_PRIVATE).getString("role", "").equalsIgnoreCase("LANDLORD")) {
            // Nếu hợp đồng đã kết thúc (phòng trống) thì chỉ cho phép Gia hạn thêm; ẩn Kết thúc
            if (isEnded) {
                btnKetThuc.setVisibility(View.GONE);
            } else {
                btnKetThuc.setVisibility(View.VISIBLE);
            }
            btnCapNhap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Gia hạn thêm: mở dialog nhập số tháng mới (các trường hiển thị readonly)
                    android.widget.LinearLayout container = new android.widget.LinearLayout(context);
                    container.setOrientation(android.widget.LinearLayout.VERTICAL);
                    int pad = (int) (context.getResources().getDisplayMetrics().density*16);
                    container.setPadding(pad, pad, pad, pad);
                    final android.widget.EditText input = new android.widget.EditText(context);
                    input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                    input.setHint(String.valueOf(hd.getThoiHan()));
                    input.setHintTextColor(0xFF999999);
                    input.setTextColor(0xFF333333);
                    container.addView(input);
                    new androidx.appcompat.app.AlertDialog.Builder(context)
                            .setTitle("Gia hạn – số tháng mới")
                            .setView(container)
                            .setPositiveButton("Tiếp tục", new android.content.DialogInterface.OnClickListener() {
                                @Override public void onClick(android.content.DialogInterface dialog, int which) {
                                    try {
                                        int thoiHanMoi = Integer.parseInt(input.getText().toString());
                                        if (thoiHanMoi <= 0) { Toast.makeText(context, "Số tháng phải > 0", Toast.LENGTH_SHORT).show(); return; }
                                        // Lưu tham số để Activity xử lý cập nhật ngày ký và ảnh sau khi chọn ảnh
                                        if (hopDong_activity instanceof com.example.nestera.Activity.hopDong_Activity) {
                                            com.example.nestera.Activity.hopDong_Activity act = (com.example.nestera.Activity.hopDong_Activity) hopDong_activity;
                                            act.pendingRenewHopDongId = hd.getMaHopDong();
                                            act.pendingRenewThoiHan = thoiHanMoi;
                                            act.pendingRenewNewMaPhong = null;
                                            act.pendingRenewOldMaPhong = null;
                                            act.pendingRenewImageBytes = null;
                                            act.startPickRenewImage();
                                        }
                                    } catch (Exception ex) {
                                        Toast.makeText(context, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            });
            btnKetThuc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hopDong_activity.xoa(String.valueOf(hd.getMaHopDong()));
                }
            });
        }else {
            btnCapNhap.setVisibility(View.GONE);
            btnKetThuc.setVisibility(View.GONE);
            edtSonguoi_hd.setEnabled(false);
            edtSothang_hd.setEnabled(false);
            edtSoxe_hd.setEnabled(false);
            edtGhiChu_hd.setEnabled(false);
        }
        }
        return v;
    }
}
