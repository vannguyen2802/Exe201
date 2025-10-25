package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.KeToan;

import java.util.ArrayList;
import java.util.List;

public class keToanDao {
    public SQLiteDatabase db;
    private Context context;
    public keToanDao(Context context){
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(KeToan kt){
        ContentValues values=new ContentValues();
        values.put("maKeToan", kt.getMaKeToan());
        values.put("tenKeToan",kt.getTenKeToan());
        values.put("matKhauKT",kt.getMatkhauKT());
        return db.insert("KeToan",null,values);
    }

    public int update(KeToan kt){
        ContentValues values=new ContentValues();
        values.put("maKeToan", kt.getMaKeToan());
        values.put("tenKeToan",kt.getTenKeToan());
        values.put("matKhauKT",kt.getMatkhauKT());
        return db.update("KeToan",values,"maKeToan",new String[]{String.valueOf(kt.getMaKeToan())});
    }

    public int delete(String id){
        return db.delete("KeToan","maKeToan",new String[]{id});
    }

    @SuppressLint("Range")
    private List<KeToan> getDaTa(String sql, String...selectionArgs){
        List<KeToan> list = new ArrayList<>();
        Cursor c=db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()){
            KeToan keToan = new KeToan();
            keToan.setMaKeToan(c.getString(c.getColumnIndex("maKeToan")));
            keToan.setTenKeToan(c.getString(c.getColumnIndex("tenKeToan")));
            keToan.setMatkhauKT(c.getString(c.getColumnIndex("matKhauKT")));

            list.add(keToan);
        }
        return list;
    }
    //get all
    public List<KeToan> getAll(){
        String sql = "SELECT * FROM KeToan";
        return getDaTa(sql);
    }

    //get id
    public KeToan getID(String id){
        String sql = "SELECT *FROM KeToan WHERE maKeToan=?";
        List<KeToan> list = getDaTa(sql,id);
        return list.get(0);
    }

    //checkLogin
    public int checkLoginKT(String id, String passwordkt){
        String sql = "SELECT * FROM KeToan WHERE maKeToan=? AND matKhauKT=?";
        List<KeToan> list = getDaTa(sql,id,passwordkt);
        if (list.size() == 0)
            return -1;
        return 1;
    }
}