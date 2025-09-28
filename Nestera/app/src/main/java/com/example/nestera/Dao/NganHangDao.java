package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;
import com.example.nestera.model.NganHang;

import java.util.ArrayList;
import java.util.List;

public class NganHangDao {
    private SQLiteDatabase db;
    protected byte[] xx;

    public NganHangDao(Context context){
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }
//    public void insert(String tenTK, String tenNH, String stk, byte[] anh){
//        String sql = "insert into NganHang values(null, ?, ?, ?, ?)";
//        SQLiteStatement statement = db.compileStatement(sql);
//        statement.clearBindings();
//
//        statement.bindString(1,tenTK);
//        statement.bindString(2,tenNH);
//        statement.bindString(3,stk);
//        statement.bindBlob(3, anh);
//
//        statement.executeInsert();
//    }

    public  long insert(NganHang obj){
        ContentValues values=new ContentValues();
        values.put("tenTKNganHang", obj.getTenTKNganHang());
        values.put("tenNganHang", obj.getTenNganHang());
        values.put("STK", obj.getSTK());
        values.put("HinhAnh", obj.getHinhAnh());
        return db.insert("NganHang",null,values);
    }
    public int update(NganHang obj){
        ContentValues values=new ContentValues();
        values.put("tenTKNganHang", obj.getTenTKNganHang());
        values.put("tenNganHang", obj.getTenNganHang());
        values.put("STK", obj.getSTK());
        values.put("HinhAnh", obj.getHinhAnh());
        return db.update("NganHang",values,"Id=?",new String[]{String.valueOf(obj.getId())});
    }
    public int delete(String id){
        return db.delete("NganHang","Id=?",new String[]{id});
    }

    @SuppressLint("Range")
    private List<NganHang> getDaTa(String sql, String...selectionArgs){
        List<NganHang> list = new ArrayList<>();
        Cursor c=db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()){
            NganHang obj = new NganHang();
            obj.setId(Integer.parseInt(c.getString(c.getColumnIndex("Id"))));
            obj.setTenTKNganHang(c.getString(c.getColumnIndex("tenTKNganHang")));
            obj.setTenNganHang(c.getString(c.getColumnIndex("tenNganHang")));
            obj.setSTK(c.getString(c.getColumnIndex("STK")));
            obj.setHinhAnh(c.getBlob(c.getColumnIndex("HinhAnh")));
//            if (c.getBlob((4)) != null) {
//                byte[] x = c.getBlob(4);
//                xx = x;
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 2; // Scale down the image if it's too large
//                options.inMutable = true;
//                obj.setHinhAnh(x);
//            }

            list.add(obj);
        }
        return list;
    }
    //get all
    public List<NganHang> getAll(){
        String sql = "SELECT * FROM NganHang";
        return getDaTa(sql);
    }

    //get id
    public NganHang getID(String id){
        String sql = "SELECT *FROM NganHang WHERE Id=?";
        List<NganHang> list = getDaTa(sql,id);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
