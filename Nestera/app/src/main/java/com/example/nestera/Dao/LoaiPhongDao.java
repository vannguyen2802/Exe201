package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.LoaiPhong;

import java.util.ArrayList;
import java.util.List;

public class    LoaiPhongDao {
    private SQLiteDatabase db;

    public LoaiPhongDao(Context context){
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }
    public  long insert(LoaiPhong obj){
        ContentValues values=new ContentValues();
        values.put("tenLoai", obj.getTenLoaiPhong());
        values.put("phiDichVu", obj.getPhiDichVu());
        values.put("giaDien", obj.getGiaDien());
        values.put("giaNuoc", obj.getGiaNuoc());
        return db.insert("LoaiPhong",null,values);
    }
    public int update(LoaiPhong obj){
        ContentValues values=new ContentValues();
        values.put("tenLoai", obj.getTenLoaiPhong());
        values.put("phiDichVu", obj.getPhiDichVu());
        values.put("giaDien", obj.getGiaDien());
        values.put("giaNuoc", obj.getGiaNuoc());
        return db.update("LoaiPhong",values,"maLoai=?",new String[]{String.valueOf(obj.getMaLoaiPhong())});
    }
    public int delete(String id){
        return db.delete("LoaiPhong","maLoai=?",new String[]{id});
    }

    @SuppressLint("Range")
    private List<LoaiPhong> getDaTa(String sql, String...selectionArgs){
        List<LoaiPhong> list = new ArrayList<>();
        Cursor c=db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()){
            LoaiPhong obj = new LoaiPhong();
            obj.setMaLoaiPhong(Integer.parseInt(c.getString(c.getColumnIndex("maLoai"))));
            obj.setTenLoaiPhong(c.getString(c.getColumnIndex("tenLoai")));
            obj.setPhiDichVu(Integer.parseInt(c.getString(c.getColumnIndex("phiDichVu"))));
            obj.setGiaDien(Integer.parseInt(c.getString(c.getColumnIndex("giaDien"))));
            obj.setGiaNuoc(Integer.parseInt(c.getString(c.getColumnIndex("giaNuoc"))));
            list.add(obj);
        }
        return list;
    }
    //get all
    public List<LoaiPhong> getAll(){
        String sql = "SELECT * FROM LoaiPhong";
        return getDaTa(sql);
    }

    //get id
    public LoaiPhong getID(String id){
        String sql = "SELECT *FROM LoaiPhong WHERE maLoai=?";
        List<LoaiPhong> list = getDaTa(sql,id);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            // Trả về null hoặc thực hiện các xử lý khác tùy vào yêu cầu của bạn
            return null;
        }
    }

}
