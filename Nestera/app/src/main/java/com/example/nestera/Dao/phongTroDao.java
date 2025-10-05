package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.PhongTro;
import com.example.nestera.model.PhongTroImage;
import java.util.ArrayList;
import java.util.List;

public class phongTroDao {
    private SQLiteDatabase db;
    private Context context;
    private PhongTroImageDao imageDao;

    public phongTroDao(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        this.context = context;
        this.imageDao = new PhongTroImageDao(context);
    }

    public long insert(PhongTro obj) {
        ContentValues values = new ContentValues();
        values.put("maLoai", obj.getMaLoai());
        values.put("tenPhong", obj.getTenPhong());
        values.put("giaTien", obj.getGia());
        values.put("tienNghi", obj.getTienNghi());
        values.put("trangThai", obj.getTrangThai());
        values.put("imagePath", obj.getImagePath()); // Thêm lại imagePath
        values.put("diaChi", obj.getDiaChi());
        values.put("timNguoiOGhep", obj.getTimNguoiOGhep());
        values.put("soNguoiHienTai", obj.getSoNguoiHienTai());
        
        long result = db.insert("PhongTro", null, values);
        android.util.Log.d("PhongTroDao", "Insert - Result: " + result + ", Room: " + obj.getTenPhong());
        return result;
    }

    public int update(PhongTro obj) {
        ContentValues values = new ContentValues();
        values.put("maLoai", obj.getMaLoai());
        values.put("tenPhong", obj.getTenPhong());
        values.put("giaTien", obj.getGia());
        values.put("tienNghi", obj.getTienNghi());
        values.put("trangThai", obj.getTrangThai());
        values.put("imagePath", obj.getImagePath()); // Thêm lại imagePath
        values.put("diaChi", obj.getDiaChi());
        values.put("timNguoiOGhep", obj.getTimNguoiOGhep());
        values.put("soNguoiHienTai", obj.getSoNguoiHienTai());
        
        return db.update("PhongTro", values, "maPhong=?", new String[]{String.valueOf(obj.getMaPhong())});
    }

    public int delete(String id) {
        return db.delete("PhongTro", "maPhong=?", new String[]{id});
    }

    public List<PhongTro> getAll() {
        String sql="SELECT * FROM PhongTro";
        return getData(sql);
    }

    public PhongTro getID(String id) {
        String sql = "SELECT * FROM PhongTro WHERE maPhong=?";
        List<PhongTro> list = getData(sql, id);
        return list.get(0);
    }
    @SuppressLint("Range")
    private List<PhongTro> getData(String sql, String...selectionArgs){
        List<PhongTro> list = new ArrayList<>();
        Cursor c=db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()){
            PhongTro obj = new PhongTro();
            obj.setMaPhong(Integer.parseInt(c.getString(c.getColumnIndex("maPhong"))));
            obj.setMaLoai(Integer.parseInt(c.getString(c.getColumnIndex("maLoai"))));
            obj.setTenPhong(c.getString(c.getColumnIndex("tenPhong")));
            obj.setGia(Integer.parseInt(c.getString(c.getColumnIndex("giaTien"))));
            obj.setTienNghi(c.getString(c.getColumnIndex("tienNghi")));
            obj.setTrangThai(Integer.parseInt(c.getString(c.getColumnIndex("trangThai"))));
            
            // Đọc các field mới (với kiểm tra tồn tại)
            try {
                obj.setDiaChi(c.getString(c.getColumnIndex("diaChi")));
                obj.setTimNguoiOGhep(c.getInt(c.getColumnIndex("timNguoiOGhep")));
                obj.setSoNguoiHienTai(c.getInt(c.getColumnIndex("soNguoiHienTai")));
            } catch (Exception e) {
                // Nếu column chưa tồn tại (database cũ), set giá trị mặc định
                obj.setDiaChi("Chưa cập nhật");
                obj.setTimNguoiOGhep(0);
                obj.setSoNguoiHienTai(0);
            }
            
            // Load ảnh chính từ bảng PhongTroImages
            try {
                if (imageDao != null) {
                    PhongTroImage mainImage = imageDao.getMainImageByMaPhong(obj.getMaPhong());
                    if (mainImage != null) {
                        obj.setImagePath(mainImage.getImagePath());
                    } else {
                        // Nếu không có ảnh chính, lấy ảnh đầu tiên
                        PhongTroImage firstImage = imageDao.getFirstImageByMaPhong(obj.getMaPhong());
                        if (firstImage != null) {
                            obj.setImagePath(firstImage.getImagePath());
                        } else {
                            obj.setImagePath(""); // Set empty string nếu không có ảnh
                        }
                    }
                } else {
                    obj.setImagePath(""); // Set empty string nếu imageDao null
                }
            } catch (Exception e) {
                // Nếu lỗi load ảnh thì set empty string
                obj.setImagePath("");
                android.util.Log.e("PhongTroDao", "Error loading image for room " + obj.getMaPhong(), e);
            }
            
            list.add(obj);
        }
        return list;
    }
    @SuppressLint("Range")
    public int getGiaPhongTheoMaPhong(int maPhong) {
        String sql = "SELECT giaTien FROM PhongTro WHERE maPhong = ?";
        int giaTien = 0;
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(maPhong)});
        if (cursor.moveToFirst()) {
            giaTien = cursor.getInt(cursor.getColumnIndex("giaTien"));
        }
        cursor.close();
        return giaTien;
    }
    @SuppressLint("Range")
    public String getTenPhongTheoMaPhong(int maPhong) {
        String sql = "SELECT tenPhong FROM PhongTro WHERE maPhong = ?";
        String tenPhong = null;
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(maPhong)});
        if (cursor.moveToFirst()) {
            tenPhong = cursor.getString(cursor.getColumnIndex("tenPhong"));
        }

        cursor.close();
        return tenPhong;
    }
    @SuppressLint("Range")
    public int getMaLoaiTheoMaPhong(int maPhong) {
        String sql = "SELECT maLoai FROM PhongTro WHERE maPhong = ?";
        int maLoai = 0;
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(maPhong)});
        if (cursor.moveToFirst()) {
            maLoai = cursor.getInt(cursor.getColumnIndex("maLoai"));
        }
        cursor.close();
        return maLoai;
    }
    public List<PhongTro> getPhongByTrangThai(int trangThai) {
        String sql = "SELECT * FROM PhongTro WHERE trangThai = ?";
        return getData(sql, String.valueOf(trangThai));
    }

    // Lấy tất cả ảnh của một phòng
    public List<PhongTroImage> getImagesByMaPhong(int maPhong) {
        return imageDao.getImagesByMaPhong(maPhong);
    }

    // Thêm ảnh mới cho phòng
    public long addImageToPhong(int maPhong, String imagePath, int thuTu, boolean isMain) {
        PhongTroImage image = new PhongTroImage(maPhong, imagePath, thuTu, isMain ? 1 : 0);
        return imageDao.insert(image);
    }

    // Xóa ảnh của phòng
    public int deleteImageFromPhong(int imageId) {
        return imageDao.delete(imageId);
    }

    // Đặt ảnh chính cho phòng
    public void setMainImageForPhong(int imageId, int maPhong) {
        imageDao.setMainImage(imageId, maPhong);
    }

    // Lưu nhiều ảnh cho phòng sau khi insert thành công
    public void saveImagesForPhong(int maPhong, List<Uri> imageUris) {
        if (imageUris != null && !imageUris.isEmpty()) {
            for (int i = 0; i < imageUris.size(); i++) {
                String imagePath = imageUris.get(i).toString();
                boolean isMain = (i == 0); // Ảnh đầu tiên là ảnh chính
                addImageToPhong(maPhong, imagePath, i + 1, isMain);
            }
            
            // Cập nhật imagePath chính cho bảng PhongTro
            if (!imageUris.isEmpty()) {
                String mainImagePath = imageUris.get(0).toString();
                ContentValues values = new ContentValues();
                values.put("imagePath", mainImagePath);
                db.update("PhongTro", values, "maPhong=?", new String[]{String.valueOf(maPhong)});
            }
        }
    }

}
