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

    public int delete(String id){
        return db.delete("BaiDang", "id=?", new String[]{id});
    }

    private List<BaiDang> getData(String sql, String...args){
        List<BaiDang> list = new ArrayList<>();
        Cursor c = db.rawQuery(sql, args);
        while (c.moveToNext()){
            BaiDang b = new BaiDang();
            
            int idIndex = c.getColumnIndex("id");
            if (idIndex >= 0) b.setId(c.getInt(idIndex));
            
            int tieuDeIndex = c.getColumnIndex("tieuDe");
            if (tieuDeIndex >= 0) b.setTieuDe(c.getString(tieuDeIndex));
            
            int diaChiIndex = c.getColumnIndex("diaChi");
            if (diaChiIndex >= 0) b.setDiaChi(c.getString(diaChiIndex));
            
            int giaThangIndex = c.getColumnIndex("giaThang");
            if (giaThangIndex >= 0) b.setGiaThang(c.getInt(giaThangIndex));
            
            int dienTichIndex = c.getColumnIndex("dienTich");
            if (dienTichIndex >= 0) b.setDienTich(c.getDouble(dienTichIndex));
            
            int tienNghiIndex = c.getColumnIndex("tienNghi");
            if (tienNghiIndex >= 0) b.setTienNghi(c.getString(tienNghiIndex));
            
            int trangThaiIndex = c.getColumnIndex("trangThai");
            if (trangThaiIndex >= 0) b.setTrangThai(c.getString(trangThaiIndex));
            
            int hinhAnhIndex = c.getColumnIndex("hinhAnh");
            if (hinhAnhIndex >= 0) b.setHinhAnh(c.getString(hinhAnhIndex));
            
            int chuTroIdIndex = c.getColumnIndex("chuTroId");
            if (chuTroIdIndex >= 0) b.setChuTroId(c.getString(chuTroIdIndex));
            
            try { 
                int maPhongIndex = c.getColumnIndex("maPhong");
                if (maPhongIndex >= 0) b.setMaPhong(c.getInt(maPhongIndex));
            } catch (Exception ignored) {}
            list.add(b);
        }
        c.close();
        return list;
    }
}


