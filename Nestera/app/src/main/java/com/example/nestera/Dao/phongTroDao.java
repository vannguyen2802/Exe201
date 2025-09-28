package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.PhongTro;

import java.util.ArrayList;
import java.util.List;

public class phongTroDao {
    private SQLiteDatabase db;
    private Context context;

    public phongTroDao(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(PhongTro obj) {
        ContentValues values = new ContentValues();
        values.put("maLoai", obj.getMaLoai());
        values.put("tenPhong", obj.getTenPhong());
        values.put("giaTien", obj.getGia());
        values.put("tienNghi", obj.getTienNghi());
        values.put("trangThai", obj.getTrangThai());
        return db.insert("PhongTro", null, values);
    }

    public int update(PhongTro obj) {
        ContentValues values = new ContentValues();
        values.put("maLoai", obj.getMaLoai());
        values.put("tenPhong", obj.getTenPhong());
        values.put("giaTien", obj.getGia());
        values.put("tienNghi", obj.getTienNghi());
        values.put("trangThai", obj.getTrangThai());
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

}
