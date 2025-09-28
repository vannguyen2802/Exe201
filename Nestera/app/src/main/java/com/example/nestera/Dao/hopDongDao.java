package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.HopDong;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class hopDongDao {
    private SQLiteDatabase db;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public hopDongDao(Context context){
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public  long insert(HopDong hopDong){
        ContentValues values=new ContentValues();
        values.put("maNguoiThue", hopDong.getMaNguoiThue());
        values.put("sdt", hopDong.getSdt());
        values.put("CCCD", hopDong.getCCCD());
        values.put("thuongTru", hopDong.getThuongTru());
        values.put("ngayKy", sdf.format(hopDong.getNgayKy()));
        values.put("thoiHan", hopDong.getThoiHan());
        values.put("maPhong", hopDong.getMaPhong());
        values.put("tienCoc", hopDong.getTienCoc());
        values.put("giaTien", hopDong.getGiaTien());
        values.put("soNguoi", hopDong.getSoNguoi());
        values.put("soXe", hopDong.getSoXe());
        values.put("hinhAnh", hopDong.getHinhAnhhd());
        values.put("ghiChu", hopDong.getGhiChu());


        return db.insert("HopDong",null,values);
    }

    public int update(HopDong hopDong){
        ContentValues values=new ContentValues();
        values.put("maNguoiThue", hopDong.getMaNguoiThue());
        values.put("sdt", hopDong.getSdt());
        values.put("CCCD", hopDong.getCCCD());
        values.put("thuongTru", hopDong.getThuongTru());
        values.put("ngayKy", sdf.format(hopDong.getNgayKy()));
        values.put("thoiHan", hopDong.getThoiHan());
        values.put("tienCoc", hopDong.getTienCoc());
        values.put("giaTien", hopDong.getGiaTien());
        values.put("soNguoi", hopDong.getSoNguoi());
        values.put("soXe", hopDong.getSoXe());
        values.put("ghiChu", hopDong.getGhiChu());
        values.put("hinhAnh", hopDong.getHinhAnhhd());
        values.put("maPhong", hopDong.getMaPhong());
        return db.update("HopDong", values,"maHopDong=?", new String[]{String.valueOf(hopDong.getMaHopDong())});
    }
    public int delete(String id){
        return db.delete("HopDong","maHopDong=?",new String[]{id});
    }

    @SuppressLint("Range")
    private List<HopDong> getDaTa(String sql, String...selectionArgs){
        List<HopDong> list = new ArrayList<>();
        Cursor c=db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()){
            HopDong hopDong = new HopDong();
            hopDong.setMaHopDong(Integer.parseInt(c.getString(c.getColumnIndex("maHopDong"))));
            hopDong.setSdt(c.getString(c.getColumnIndex("sdt")));
            hopDong.setCCCD(c.getString(c.getColumnIndex("CCCD")));
            hopDong.setThuongTru(c.getString(c.getColumnIndex("thuongTru")));
            try {
                hopDong.setNgayKy(sdf.parse(c.getString(c.getColumnIndex("ngayKy"))));
            }catch (ParseException e){
                e.printStackTrace();
            }

            hopDong.setThoiHan(Integer.parseInt(c.getString(c.getColumnIndex("thoiHan"))));

            hopDong.setTienCoc(Integer.parseInt(c.getString(c.getColumnIndex("tienCoc"))));
            hopDong.setGiaTien(Integer.parseInt(c.getString(c.getColumnIndex("giaTien"))));
            hopDong.setSoNguoi(Integer.parseInt(c.getString(c.getColumnIndex("soNguoi"))));
            hopDong.setSoXe(Integer.parseInt(c.getString(c.getColumnIndex("soXe"))));
            hopDong.setGhiChu(c.getString(c.getColumnIndex("ghiChu")));
            hopDong.setHinhAnhhd(c.getBlob(c.getColumnIndex("hinhAnh")));
            hopDong.setMaNguoiThue(c.getString(c.getColumnIndex("maNguoiThue")));
            hopDong.setMaPhong(Integer.parseInt(c.getString(c.getColumnIndex("maPhong"))));


            list.add(hopDong);
        }
        return list;
    }
    //get all
    public List<HopDong> getAll(){
        String sql = "SELECT * FROM HopDong";
        return getDaTa(sql);
    }

    //get id
    public HopDong getID(String id){
        String sql = "SELECT * FROM HopDong WHERE maHopDong=?";
        List<HopDong> list = getDaTa(sql,id);
        return list.get(0);
    }
    @SuppressLint("Range")
    public ArrayList<HopDong> getHopDongByMaPhong(int maPhong) {
        ArrayList<HopDong> danhSachHopDong = new ArrayList<>();

        String sql = "SELECT * FROM HopDong WHERE maPhong=?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(maPhong)});

        while (c.moveToNext()) {
            HopDong hopDong = new HopDong();
            hopDong.setMaHopDong(c.getInt(c.getColumnIndex("maHopDong")));
            hopDong.setSdt(c.getString(c.getColumnIndex("sdt")));
            hopDong.setCCCD(c.getString(c.getColumnIndex("CCCD")));
            hopDong.setThuongTru(c.getString(c.getColumnIndex("thuongTru")));
            try {
                hopDong.setNgayKy(sdf.parse(c.getString(c.getColumnIndex("ngayKy"))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            hopDong.setThoiHan(c.getInt(c.getColumnIndex("thoiHan")));
            hopDong.setTienCoc(c.getInt(c.getColumnIndex("tienCoc")));
            hopDong.setGiaTien(c.getInt(c.getColumnIndex("giaTien")));
            hopDong.setSoNguoi(c.getInt(c.getColumnIndex("soNguoi")));
            hopDong.setSoXe(c.getInt(c.getColumnIndex("soXe")));
            hopDong.setGhiChu(c.getString(c.getColumnIndex("ghiChu")));
            hopDong.setHinhAnhhd(c.getBlob(c.getColumnIndex("hinhAnh")));
            hopDong.setMaNguoiThue(c.getString(c.getColumnIndex("maNguoiThue")));
            hopDong.setMaPhong(c.getInt(c.getColumnIndex("maPhong")));


            danhSachHopDong.add(hopDong);
        }

        c.close();

        return danhSachHopDong;
    }
    public void updateTrangThaiPhong(int maphong, int trangthai) {
        ContentValues values = new ContentValues();
        values.put("trangthai", trangthai);
        db.update("PhongTro", values, "maphong = ?", new String[]{String.valueOf(maphong)});
        db.close();
    }
    @SuppressLint("Range")
    public int getSoNguoiByMaPhongHD(int maPhong) {
        String sql = "SELECT soNguoi FROM HopDong WHERE maPhong=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(maPhong)});
        int soNguoi = 0;
        if (cursor.moveToFirst()) {
            soNguoi = cursor.getInt(cursor.getColumnIndex("soNguoi"));
        }
        cursor.close();
        return soNguoi;
    }
    @SuppressLint("Range")
    public String getMaNguoiThueByMaPhong(int maPhong) {
        String maNguoiThue = "";
        String sql = "SELECT maNguoiThue FROM HopDong WHERE maPhong=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(maPhong)});
        if (cursor.moveToFirst()) {
            maNguoiThue = cursor.getString(cursor.getColumnIndex("maNguoiThue"));
        }
        cursor.close();
        return maNguoiThue;
    }
    @SuppressLint("Range")
    public String getSDTByMaPhong(int maPhong) {
        String sdt = "";
        String sql = "SELECT sdt FROM HopDong WHERE maPhong=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(maPhong)});
        if (cursor.moveToFirst()) {
            sdt = cursor.getString(cursor.getColumnIndex("sdt"));
        }
        cursor.close();
        return sdt;
    }
}
