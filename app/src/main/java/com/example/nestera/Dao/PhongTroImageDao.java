package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.PhongTroImage;

import java.util.ArrayList;
import java.util.List;

public class PhongTroImageDao {
    private SQLiteDatabase db;

    public PhongTroImageDao(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(PhongTroImage obj) {
        ContentValues values = new ContentValues();
        values.put("maPhong", obj.getMaPhong());
        values.put("imagePath", obj.getImagePath());
        values.put("thuTu", obj.getThuTu());
        values.put("isMain", obj.getIsMain());
        
        return db.insert("PhongTroImages", null, values);
    }

    public int update(PhongTroImage obj) {
        ContentValues values = new ContentValues();
        values.put("maPhong", obj.getMaPhong());
        values.put("imagePath", obj.getImagePath());
        values.put("thuTu", obj.getThuTu());
        values.put("isMain", obj.getIsMain());
        
        return db.update("PhongTroImages", values, "id = ?", new String[]{String.valueOf(obj.getId())});
    }

    public int delete(int id) {
        return db.delete("PhongTroImages", "id = ?", new String[]{String.valueOf(id)});
    }

    public int deleteByMaPhong(int maPhong) {
        return db.delete("PhongTroImages", "maPhong = ?", new String[]{String.valueOf(maPhong)});
    }

    // Lấy tất cả ảnh của một phòng, sắp xếp theo thứ tự
    public List<PhongTroImage> getImagesByMaPhong(int maPhong) {
        String sql = "SELECT * FROM PhongTroImages WHERE maPhong = ? ORDER BY thuTu ASC";
        return getData(sql, String.valueOf(maPhong));
    }

    // Lấy ảnh chính của một phòng
    public PhongTroImage getMainImageByMaPhong(int maPhong) {
        String sql = "SELECT * FROM PhongTroImages WHERE maPhong = ? AND isMain = 1 LIMIT 1";
        List<PhongTroImage> list = getData(sql, String.valueOf(maPhong));
        return list.isEmpty() ? null : list.get(0);
    }

    // Lấy ảnh đầu tiên của phòng (nếu không có ảnh chính)
    public PhongTroImage getFirstImageByMaPhong(int maPhong) {
        String sql = "SELECT * FROM PhongTroImages WHERE maPhong = ? ORDER BY thuTu ASC LIMIT 1";
        List<PhongTroImage> list = getData(sql, String.valueOf(maPhong));
        return list.isEmpty() ? null : list.get(0);
    }

    public List<PhongTroImage> getAll() {
        String sql = "SELECT * FROM PhongTroImages ORDER BY maPhong, thuTu";
        return getData(sql);
    }

    @SuppressLint("Range")
    private List<PhongTroImage> getData(String sql, String... selectionArgs) {
        List<PhongTroImage> list = new ArrayList<>();
        Cursor c = db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()) {
            PhongTroImage obj = new PhongTroImage();
            obj.setId(c.getInt(c.getColumnIndex("id")));
            obj.setMaPhong(c.getInt(c.getColumnIndex("maPhong")));
            obj.setImagePath(c.getString(c.getColumnIndex("imagePath")));
            obj.setThuTu(c.getInt(c.getColumnIndex("thuTu")));
            obj.setIsMain(c.getInt(c.getColumnIndex("isMain")));
            list.add(obj);
        }
        c.close();
        return list;
    }

    // Đặt một ảnh thành ảnh chính và tắt các ảnh chính khác trong cùng phòng
    public void setMainImage(int imageId, int maPhong) {
        db.beginTransaction();
        try {
            // Tắt tất cả ảnh chính của phòng này
            ContentValues values = new ContentValues();
            values.put("isMain", 0);
            db.update("PhongTroImages", values, "maPhong = ?", new String[]{String.valueOf(maPhong)});

            // Đặt ảnh được chọn thành ảnh chính
            values.put("isMain", 1);
            db.update("PhongTroImages", values, "id = ?", new String[]{String.valueOf(imageId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}