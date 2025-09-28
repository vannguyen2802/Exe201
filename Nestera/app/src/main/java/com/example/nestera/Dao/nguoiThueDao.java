package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.NguoiThue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class nguoiThueDao {
    private SQLiteDatabase db;
    private Context context;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public nguoiThueDao(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(NguoiThue obj) {
        ContentValues values = new ContentValues();
        values.put("maNguoiThue", obj.getMaNguoithue());
        values.put("matKhauNT", obj.getMatKhauNT());
        values.put("tenNguoiThue", obj.getTenNguoiThue());
        values.put("thuongTru", obj.getThuongTru());
        values.put("sdt", obj.getSdt());
        values.put("CCCD", obj.getcCCD());
        values.put("namSinh", obj.getNamSinh());
        values.put("gioiTinh", obj.getGioiTinh());
        values.put("maPhong", obj.getMaPhong());
        return db.insert("NguoiThue", null, values);
    }

    public int update(NguoiThue obj) {
        ContentValues values = new ContentValues();
        values.put("maNguoiThue", obj.getMaNguoithue());
        values.put("matKhauNT", obj.getMatKhauNT());
        values.put("tenNguoiThue", obj.getTenNguoiThue());
        values.put("thuongTru", obj.getThuongTru());
        values.put("sdt", obj.getSdt());
        values.put("CCCD", obj.getcCCD());
        values.put("namSinh", obj.getNamSinh());
        values.put("gioiTinh", obj.getGioiTinh());
        values.put("maPhong", obj.getMaPhong());
        return db.update("NguoiThue", values, "maNguoiThue=?", new String[]{obj.getMaNguoithue()});
    }

    public int delete(String id) {
        return db.delete("NguoiThue", "maNguoiThue=?", new String[]{id});
    }

    public List<NguoiThue> getAll() {
        String sql="SELECT * FROM NguoiThue";
        return getData(sql);
    }

    public NguoiThue getID(String id) {
        String sql = "SELECT * FROM NguoiThue WHERE maNguoiThue=?";
        List<NguoiThue> list = getData(sql, id);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            // Trả về null hoặc thực hiện các xử lý khác tùy thuộc vào yêu cầu của bạn
            return null;
        }
    }
    @SuppressLint("Range")
    private List<NguoiThue> getData(String sql, String...selectionArgs){
        List<NguoiThue> list = new ArrayList<>();
        Cursor c=db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()){
            NguoiThue obj = new NguoiThue();
            obj.setMaNguoithue(c.getString(c.getColumnIndex("maNguoiThue")));
            obj.setMatKhauNT(c.getString(c.getColumnIndex("matKhauNT")));
            obj.setTenNguoiThue(c.getString(c.getColumnIndex("tenNguoiThue")));
            obj.setThuongTru(c.getString(c.getColumnIndex("thuongTru")));
            obj.setSdt( c.getString(c.getColumnIndex("sdt")));
            obj.setcCCD(c.getString(c.getColumnIndex("CCCD")));
            obj.setNamSinh(c.getString(c.getColumnIndex("namSinh")));


            obj.setGioiTinh(Integer.parseInt(c.getString(c.getColumnIndex("gioiTinh"))));
            obj.setMaPhong(Integer.parseInt(c.getString(c.getColumnIndex("maPhong"))));
            list.add(obj);
        }
        return list;
    }
    public int CheckLoginNT(String id,String password){
        String sql = "select * from NguoiThue where maNguoiThue=? and matKhauNT=?";
        List<NguoiThue> list=getData(sql,id,password);
        if (list.size()==0){
            return -1;
        }return 1;
    }

    @SuppressLint("Range")
    public ArrayList<NguoiThue> getNguoiThueByUser(String user){
        ArrayList<NguoiThue> danhSachNguoiThue = new ArrayList<>();

        String sql = "SELECT * FROM NguoiThue WHERE maNguoiThue=?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(user)});

        while (c.moveToNext()){
            NguoiThue obj = new NguoiThue();
            obj.setMaNguoithue(c.getString(c.getColumnIndex("maNguoiThue")));
            obj.setMatKhauNT(c.getString(c.getColumnIndex("matKhauNT")));
            obj.setTenNguoiThue(c.getString(c.getColumnIndex("tenNguoiThue")));
            obj.setThuongTru(c.getString(c.getColumnIndex("thuongTru")));
            obj.setSdt( c.getString(c.getColumnIndex("sdt")));
            obj.setcCCD(c.getString(c.getColumnIndex("CCCD")));
            obj.setNamSinh( c.getString(c.getColumnIndex("namSinh")));
            obj.setGioiTinh(Integer.parseInt(c.getString(c.getColumnIndex("gioiTinh"))));
            obj.setMaPhong(Integer.parseInt(c.getString(c.getColumnIndex("maPhong"))));
            danhSachNguoiThue.add(obj);
        }
        c.close();

        return danhSachNguoiThue;
    }
    @SuppressLint("Range")
    public int getMaPhongByUser(String user) {
        String sql = "SELECT maPhong FROM NguoiThue WHERE maNguoiThue=?";
        Cursor cursor = db.rawQuery(sql, new String[]{user});
        int maPhong = -1; // Mã phòng mặc định nếu không tìm thấy

        if (cursor.moveToFirst()) {
            maPhong = cursor.getInt(cursor.getColumnIndex("maPhong"));
        }

        cursor.close();
        return maPhong;
    }
    @SuppressLint("Range")
    public ArrayList<NguoiThue> getNguoiThueByMaPhong(int maPhong) {
        String sql = "SELECT * FROM NguoiThue WHERE maPhong=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(maPhong)});
        ArrayList<NguoiThue> danhSachNguoiThue = new ArrayList<>();

        while (cursor.moveToNext()) {
            NguoiThue obj = new NguoiThue();
            obj.setMaNguoithue(cursor.getString(cursor.getColumnIndex("maNguoiThue")));
            obj.setMatKhauNT(cursor.getString(cursor.getColumnIndex("matKhauNT")));
            obj.setTenNguoiThue(cursor.getString(cursor.getColumnIndex("tenNguoiThue")));
            obj.setThuongTru(cursor.getString(cursor.getColumnIndex("thuongTru")));
            obj.setSdt(cursor.getString(cursor.getColumnIndex("sdt")));
            obj.setcCCD(cursor.getString(cursor.getColumnIndex("CCCD")));
            obj.setNamSinh(cursor.getString(cursor.getColumnIndex("namSinh")));
            obj.setGioiTinh(Integer.parseInt(cursor.getString(cursor.getColumnIndex("gioiTinh"))));
            obj.setMaPhong(Integer.parseInt(cursor.getString(cursor.getColumnIndex("maPhong"))));
            danhSachNguoiThue.add(obj);
        }

        cursor.close();
        return danhSachNguoiThue;
    }

}
