package com.example.nestera.Dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nestera.Database.DbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDao {

    private SQLiteDatabase db;
    private Context context;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ThongKeDao(Context context) {
        this.context = context;
        DbHelper dbHelper = new DbHelper(context);
        db=dbHelper.getWritableDatabase();
    }

    @SuppressLint("Range")
    public int getDoanhThu(String tuNgay, String denNgay){
        String sqlDoanhThu = "select sum((soDien * donGiaDien)+(soNguoi * donGiaNuoc)+phiDichVu+tienPhong) as doanhThu from HoaDon where ngayTao between ? and ? and trangThai = 2";
        List<Integer> list = new ArrayList<Integer>();
        Cursor c= db.rawQuery(sqlDoanhThu, new String[]{tuNgay,denNgay});

        while (c.moveToNext()){
            try {
                list.add(Integer.parseInt(c.getString(c.getColumnIndex("doanhThu"))));

            }catch (Exception e){
                list.add(0);
            }
        }
        return list.get(0);
    }
}
