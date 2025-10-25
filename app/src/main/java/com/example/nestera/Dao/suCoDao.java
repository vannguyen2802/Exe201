package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;

import com.example.nestera.model.suCo;

import java.util.ArrayList;
import java.util.List;

public class suCoDao {
    private SQLiteDatabase db;
    private Context context;

    public suCoDao(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(suCo obj) {
        ContentValues values = new ContentValues();
        values.put("tenSuCo", obj.getTenSuCo());
        values.put("noiDung", obj.getNoiDung());
        values.put("trangThai", obj.getTrangThai());
        values.put("maPhong", obj.getMaPhong());
        values.put("maNguoiThue", obj.getMaNguoiThue());
        return db.insert("SuCo", null, values);
    }

    public int update(suCo obj) {
        ContentValues values = new ContentValues();
        values.put("tenSuCo", obj.getTenSuCo());
        values.put("noiDung", obj.getNoiDung());
        values.put("trangThai", obj.getTrangThai());
        values.put("maPhong", obj.getMaPhong());
        values.put("maNguoiThue", obj.getMaNguoiThue());
        return db.update("SuCo", values, "maSuCo=?", new String[]{String.valueOf(obj.getMaSuCo())});
    }

    public int delete(String id) {
        return db.delete("SuCo", "maSuCo=?", new String[]{id});
    }

    public List<suCo> getAll() {
        String sql="SELECT * FROM SuCo";
        return getData(sql);
    }

    public suCo getID(String id) {
        String sql = "SELECT * FROM SuCo WHERE maSuCo=?";
        List<suCo> list = getData(sql, id);
        return list.get(0);
    }
    @SuppressLint("Range")
    private List<suCo> getData(String sql, String...selectionArgs){
        List<suCo> list = new ArrayList<>();
        Cursor c=db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()){
            suCo obj = new suCo();
            obj.setMaSuCo(Integer.parseInt(c.getString(c.getColumnIndex("maSuCo"))));
            obj.setTenSuCo(c.getString(c.getColumnIndex("tenSuCo")));
            obj.setNoiDung(c.getString(c.getColumnIndex("noiDung")));
            obj.setTrangThai(Integer.parseInt(c.getString(c.getColumnIndex("trangThai"))));
            obj.setMaPhong(Integer.parseInt(c.getString(c.getColumnIndex("maPhong"))));
            obj.setMaNguoiThue(Integer.parseInt(c.getString(c.getColumnIndex("maNguoiThue"))));
            list.add(obj);
        }
        return list;
    }
    public void updateTrangThaiHoaDon(int maSuCo, int trangthai) {
        ContentValues values = new ContentValues();
        values.put("trangThai", trangthai);
        db.update("SuCo", values, "maSuCo = ?", new String[]{String.valueOf(maSuCo)});
        db.close();
    }
    public List<suCo> getSuCoByMaPhong(int maPhong) {
        String sql = "SELECT * FROM SuCo WHERE maPhong = ?";
        return getData(sql, String.valueOf(maPhong));
    }
}
