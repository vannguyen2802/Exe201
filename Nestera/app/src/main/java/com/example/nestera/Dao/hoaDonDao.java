package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.HoaDon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class hoaDonDao {
    private SQLiteDatabase db   ;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public hoaDonDao(Context context){
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public  long insert(HoaDon hd){
        ContentValues values=new ContentValues();
        values.put("sdt", hd.getSoDien());
        values.put("ngayTao", sdf.format(hd.getNgayTao()));
        values.put("soDien", hd.getSoDien());
        values.put("donGiaDien", hd.getDonGiaDien());
        values.put("soNguoi", hd.getSoNguoi());
        values.put("donGiaNuoc", hd.getDonGiaNuoc());
        values.put("phiDichVu", hd.getPhiDichVu());
        values.put("ghiChu", hd.getGhiChu());
        values.put("tienPhong", hd.getTienPhong());
        values.put("anhThanhToan", hd.getAnhThanhToan());
        values.put("trangThai", hd.getTrangThai());
        values.put("maPhong", hd.getMaPhong());
        values.put("maNguoiThue", hd.getMaNguoiThue());
        return db.insert("HoaDon",null,values);
    }

    public int update(HoaDon hd){
        ContentValues values=new ContentValues();
//        values.put("sdt", hd.getSoDien());
//        values.put("ngayTao", sdf.format(hd.getNgayTao()));
//        values.put("soDien", hd.getSoDien());
//        values.put("donGiaDien", hd.getDonGiaDien());
//        values.put("soNguoi", hd.getSoNguoi());
//        values.put("donGiaNuoc", hd.getDonGiaNuoc());
//        values.put("phiDichVu", hd.getPhiDichVu());
//        values.put("ghiChu", hd.getGhiChu());
//        values.put("tienPhong", hd.getTienPhong());
        values.put("trangThai", hd.getTrangThai());
//        values.put("maPhong", hd.getMaPhong());
//        values.put("maNguoiThue", hd.getMaNguoiThue());
        return db.update("HoaDon", values,"maHoaDon=?", new String[]{String.valueOf(hd.getMaHoaDon())});
    }
    public int updateanh(HoaDon hd){
        ContentValues values=new ContentValues();
        values.put("anhThanhToan", hd.getAnhThanhToan());
        return db.update("HoaDon", values,"maHoaDon=?", new String[]{String.valueOf(hd.getMaHoaDon())});
    }

    public int delete(String id){
        return db.delete("HoaDon","maHoaDon=?",new String[]{id});
    }

    @SuppressLint("Range")
    private List<HoaDon> getDaTa(String sql, String...selectionArgs){
        List<HoaDon> list = new ArrayList<>();
        Cursor c=db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()){

            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(Integer.parseInt(c.getString(c.getColumnIndex("maHoaDon"))));
            hd.setSdt(c.getString(c.getColumnIndex("maHoaDon")));
            try {
                hd.setNgayTao(sdf.parse(c.getString(c.getColumnIndex("ngayTao"))));
            }catch (ParseException e){
                e.printStackTrace();
            }
            hd.setSoDien(Integer.parseInt(c.getString(c.getColumnIndex("soDien"))));
            hd.setDonGiaDien(Integer.parseInt(c.getString(c.getColumnIndex("donGiaDien"))));
            hd.setSoNguoi(Integer.parseInt(c.getString(c.getColumnIndex("soNguoi"))));
            hd.setDonGiaNuoc(Integer.parseInt(c.getString(c.getColumnIndex("donGiaNuoc"))));
            hd.setPhiDichVu(Integer.parseInt(c.getString(c.getColumnIndex("phiDichVu"))));
            hd.setGhiChu(c.getString(c.getColumnIndex("ghiChu")));
            hd.setTienPhong(Integer.parseInt(c.getString(c.getColumnIndex("tienPhong"))));
            hd.setAnhThanhToan(c.getBlob(c.getColumnIndex("anhThanhToan")));
            hd.setTrangThai(Integer.parseInt(c.getString(c.getColumnIndex("trangThai"))));
            hd.setMaPhong(Integer.parseInt(c.getString(c.getColumnIndex("maPhong"))));
            hd.setMaNguoiThue(c.getString(c.getColumnIndex("maNguoiThue")));

            list.add(hd);
        }
        return list;
    }
    public List<HoaDon> getHoaDonByMaPhong(int maPhong) {
        String sql = "SELECT * FROM HoaDon WHERE maPhong = ?";
        return getDaTa(sql, String.valueOf(maPhong));
    }
    //get all
    public List<HoaDon> getAll(){
        String sql = "SELECT * FROM HoaDon";
        return getDaTa(sql);
    }

    //get id
    public HoaDon getID(String id){
        String sql = "SELECT *FROM HoaDon WHERE maHoaDon=?";
        List<HoaDon> list = getDaTa(sql,id);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
    @SuppressLint("Range")
    public int getTongTienDien(int maHoaDon){
        int tongTienDien=0;
        String sqlTongDien = "select soDien * donGiaDien as TongTienDien from HoaDon where maHoaDon=?";
        Cursor cursor = db.rawQuery(sqlTongDien, new String[]{String.valueOf(maHoaDon)});

        if (cursor.moveToFirst()) {
            tongTienDien = cursor.getInt(cursor.getColumnIndex("TongTienDien"));
        }

        cursor.close();

        return tongTienDien;

    }

    @SuppressLint("Range")
    public int getTongTienNuoc(int maHoaDon){
        int tongTienNuoc=0;
        String sqlTongDien = "select soNguoi * donGiaNuoc as TongTienNuoc from HoaDon where maHoaDon=?";
        Cursor cursor = db.rawQuery(sqlTongDien, new String[]{String.valueOf(maHoaDon)});

        if (cursor.moveToFirst()) {
            tongTienNuoc = cursor.getInt(cursor.getColumnIndex("TongTienNuoc"));
        }

        cursor.close();

        return tongTienNuoc;

    }

    public void updateTrangThaiHoaDon(int mahoadon, int trangthai) {
        ContentValues values = new ContentValues();
        values.put("trangThai", trangthai);
        db.update("HoaDon", values, "maHoaDon = ?", new String[]{String.valueOf(mahoadon)});
        db.close();
    }





}
