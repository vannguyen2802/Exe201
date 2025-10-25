package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.ChuTro;

import java.util.ArrayList;
import java.util.List;

public class chuTroDao {
    private SQLiteDatabase db;
    private Context context;

    public chuTroDao(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        this.context = context;
    }

    public long insert(ChuTro obj) {
        ContentValues values = new ContentValues();
        values.put("maChuTro", obj.getMaChuTro());
        values.put("matKhau", obj.getMatKhau());
        values.put("tenChuTro", obj.getTenChuTro());
        values.put("email", obj.getEmail());
        values.put("sdt", obj.getSdt());
        values.put("cccd", obj.getCccd());
        // approved default 0 when inserting via UI
        values.put("approved", 0);
        values.put("banned", 0);
        return db.insert("ChuTro", null, values);
    }

    public int update(ChuTro obj) {
        ContentValues values = new ContentValues();
        values.put("matKhau", obj.getMatKhau());
        values.put("tenChuTro", obj.getTenChuTro());
        values.put("email", obj.getEmail());
        values.put("sdt", obj.getSdt());
        values.put("cccd", obj.getCccd());
        return db.update("ChuTro", values, "maChuTro=?", new String[]{obj.getMaChuTro()});
    }

    public int delete(String id) {
        return db.delete("ChuTro", "maChuTro=?", new String[]{id});
    }

    public int checkLoginCT(String id, String password) {
        String sql = "SELECT * FROM ChuTro WHERE maChuTro=? AND matKhau=? AND approved=1";
        List<ChuTro> list = getData(sql, id, password);
        if (list.size() == 0) return -1;
        return 1;
    }

    public boolean exists(String id) {
        String sql = "SELECT * FROM ChuTro WHERE maChuTro=?";
        return !getData(sql, id).isEmpty();
    }

    public List<ChuTro> getPending() {
        String sql = "SELECT * FROM ChuTro WHERE approved=0";
        return getData(sql);
    }

    public int approveUser(String id) {
        ContentValues values = new ContentValues();
        values.put("approved", 1);
        return db.update("ChuTro", values, "maChuTro=?", new String[]{id});
    }

    public int approveAllPending() {
        ContentValues values = new ContentValues();
        values.put("approved", 1);
        return db.update("ChuTro", values, "approved=0", null);
    }

    public int rejectUser(String id) {
        ContentValues values = new ContentValues();
        values.put("approved", -1);
        return db.update("ChuTro", values, "maChuTro=?", new String[]{id});
    }

    public Integer getApprovedStatus(String id) {
        Integer status = null;
        Cursor c = db.rawQuery("SELECT approved FROM ChuTro WHERE maChuTro=?", new String[]{id});
        if (c.moveToFirst()) {
            int idx = c.getColumnIndex("approved");
            if (idx >= 0) status = c.getInt(idx);
        }
        c.close();
        return status;
    }

    public ChuTro getID(String id) {
        String sql = "SELECT * FROM ChuTro WHERE maChuTro=?";
        List<ChuTro> list = getData(sql, id);
        if (!list.isEmpty()) return list.get(0);
        return null;
    }

    public List<ChuTro> getAll() {
        String sql = "SELECT * FROM ChuTro";
        return getData(sql);
    }

    @SuppressLint("Range")
    private List<ChuTro> getData(String sql, String... args) {
        List<ChuTro> list = new ArrayList<>();
        Cursor c = db.rawQuery(sql, args);
        while (c.moveToNext()) {
            ChuTro obj = new ChuTro();
            obj.setMaChuTro(c.getString(c.getColumnIndex("maChuTro")));
            obj.setMatKhau(c.getString(c.getColumnIndex("matKhau")));
            obj.setTenChuTro(c.getString(c.getColumnIndex("tenChuTro")));
            obj.setEmail(c.getString(c.getColumnIndex("email")));
            obj.setSdt(c.getString(c.getColumnIndex("sdt")));
            obj.setCccd(c.getString(c.getColumnIndex("cccd")));
            try { obj.setApproved(c.getInt(c.getColumnIndex("approved"))); } catch (Exception ignored) {}
            try { obj.setBanned(c.getInt(c.getColumnIndex("banned"))); } catch (Exception ignored) {}
            list.add(obj);
        }
        c.close();
        return list;
    }

    public Integer getBannedStatus(String id) {
        Integer status = null;
        Cursor c = db.rawQuery("SELECT banned FROM ChuTro WHERE maChuTro=?", new String[]{id});
        if (c.moveToFirst()) {
            int idx = c.getColumnIndex("banned");
            if (idx >= 0) status = c.getInt(idx);
        }
        c.close();
        return status;
    }

    public int banUser(String id){
        ContentValues v=new ContentValues();
        v.put("banned",1);
        return db.update("ChuTro", v, "maChuTro=?", new String[]{id});
    }

    public int unbanUser(String id){
        ContentValues v=new ContentValues();
        v.put("banned",0);
        return db.update("ChuTro", v, "maChuTro=?", new String[]{id});
    }
}


