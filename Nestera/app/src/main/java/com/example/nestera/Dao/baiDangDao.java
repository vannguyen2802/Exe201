package com.example.nestera.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.BaiDang;

import java.util.ArrayList;
import java.util.List;

public class baiDangDao {
    private final SQLiteDatabase db;

    public baiDangDao(Context context){
        DbHelper helper = new DbHelper(context);
        db = helper.getWritableDatabase();
    }

    public long insert(BaiDang bd){
        ContentValues v = new ContentValues();
        v.put("tieuDe", bd.getTieuDe());
        v.put("diaChi", bd.getDiaChi());
        v.put("giaThang", bd.getGiaThang());
        v.put("dienTich", bd.getDienTich());
        v.put("tienNghi", bd.getTienNghi());
        v.put("trangThai", bd.getTrangThai());
        v.put("hinhAnh", bd.getHinhAnh());
        v.put("chuTroId", bd.getChuTroId());
        if (bd.getMaPhong()!=null) v.put("maPhong", bd.getMaPhong());
        return db.insert("BaiDang", null, v);
    }

    public List<BaiDang> getAll(){
        return getData("SELECT * FROM BaiDang ORDER BY id DESC");
    }

    public List<BaiDang> getByChuTro(String chuTroId){
        return getData("SELECT * FROM BaiDang WHERE chuTroId=? ORDER BY id DESC", chuTroId);
    }

    public BaiDang getById(int id){
        List<BaiDang> list = getData("SELECT * FROM BaiDang WHERE id=?", String.valueOf(id));
        return list.isEmpty()? null : list.get(0);
    }

    public int update(BaiDang bd){
        ContentValues v = new ContentValues();
        v.put("tieuDe", bd.getTieuDe());
        v.put("diaChi", bd.getDiaChi());
        v.put("giaThang", bd.getGiaThang());
        v.put("dienTich", bd.getDienTich());
        v.put("tienNghi", bd.getTienNghi());
        v.put("trangThai", bd.getTrangThai());
        v.put("hinhAnh", bd.getHinhAnh());
        if (bd.getMaPhong()!=null) v.put("maPhong", bd.getMaPhong());
        return db.update("BaiDang", v, "id=?", new String[]{String.valueOf(bd.getId())});
    }

    private List<BaiDang> getData(String sql, String...args){
        List<BaiDang> list = new ArrayList<>();
        Cursor c = db.rawQuery(sql, args);
        while (c.moveToNext()){
            BaiDang b = new BaiDang();
            b.setId(c.getInt(c.getColumnIndex("id")));
            b.setTieuDe(c.getString(c.getColumnIndex("tieuDe")));
            b.setDiaChi(c.getString(c.getColumnIndex("diaChi")));
            b.setGiaThang(c.getInt(c.getColumnIndex("giaThang")));
            b.setDienTich(c.getDouble(c.getColumnIndex("dienTich")));
            b.setTienNghi(c.getString(c.getColumnIndex("tienNghi")));
            b.setTrangThai(c.getString(c.getColumnIndex("trangThai")));
            b.setHinhAnh(c.getString(c.getColumnIndex("hinhAnh")));
            b.setChuTroId(c.getString(c.getColumnIndex("chuTroId")));
            try { b.setMaPhong(c.getInt(c.getColumnIndex("maPhong"))); } catch (Exception ignored) {}
            list.add(b);
        }
        c.close();
        return list;
    }
}


