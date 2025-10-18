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
    private SQLiteDatabase db;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public hoaDonDao(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(HoaDon hd) {
        ContentValues values = new ContentValues();
        // SỬA LỖI: Dùng đúng getter cho sdt
        values.put("sdt", hd.getSdt());
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
        return db.insert("HoaDon", null, values);
    }

    // Phương thức để cập nhật cả ảnh và trạng thái (dùng trong ThanhToan_Activity)
    public int updateAnhVaTrangThai(HoaDon hd) {
        ContentValues values = new ContentValues();
        values.put("anhThanhToan", hd.getAnhThanhToan());
        values.put("trangThai", hd.getTrangThai());
        return db.update("HoaDon", values, "maHoaDon=?", new String[]{String.valueOf(hd.getMaHoaDon())});
    }

    public int delete(String id) {
        return db.delete("HoaDon", "maHoaDon=?", new String[]{id});
    }

    @SuppressLint("Range")
    private List<HoaDon> getDaTa(String sql, String... selectionArgs) {
        List<HoaDon> list = new ArrayList<>();
        Cursor c = db.rawQuery(sql, selectionArgs);
        while (c.moveToNext()) {
            HoaDon hd = new HoaDon();
            hd.setMaHoaDon(c.getInt(c.getColumnIndex("maHoaDon")));
            // SỬA LỖI: Lấy đúng cột "sdt"
            hd.setSdt(c.getString(c.getColumnIndex("sdt")));
            try {
                hd.setNgayTao(sdf.parse(c.getString(c.getColumnIndex("ngayTao"))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            hd.setSoDien(c.getInt(c.getColumnIndex("soDien")));
            hd.setDonGiaDien(c.getInt(c.getColumnIndex("donGiaDien")));
            hd.setSoNguoi(c.getInt(c.getColumnIndex("soNguoi")));
            hd.setDonGiaNuoc(c.getInt(c.getColumnIndex("donGiaNuoc")));
            hd.setPhiDichVu(c.getInt(c.getColumnIndex("phiDichVu")));
            hd.setGhiChu(c.getString(c.getColumnIndex("ghiChu")));
            hd.setTienPhong(c.getInt(c.getColumnIndex("tienPhong")));
            hd.setAnhThanhToan(c.getBlob(c.getColumnIndex("anhThanhToan")));
            hd.setTrangThai(c.getInt(c.getColumnIndex("trangThai")));
            hd.setMaPhong(c.getInt(c.getColumnIndex("maPhong")));
            hd.setMaNguoiThue(c.getString(c.getColumnIndex("maNguoiThue")));
            list.add(hd);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return list;
    }

    public List<HoaDon> getHoaDonByMaPhong(int maPhong) {
        String sql = "SELECT * FROM HoaDon WHERE maPhong = ?";
        return getDaTa(sql, String.valueOf(maPhong));
    }

    public List<HoaDon> getAll() {
        String sql = "SELECT * FROM HoaDon";
        return getDaTa(sql);
    }

    // BỔ SUNG: Lấy hóa đơn theo ID
    public HoaDon getHoaDonById(int id) {
        String sql = "SELECT * FROM HoaDon WHERE maHoaDon=?";
        List<HoaDon> list = getDaTa(sql, String.valueOf(id));
        return list.isEmpty() ? null : list.get(0);
    }

    // BỔ SUNG: Hàm tính tổng tiền cho MoMo
    public int getTongTien(int maHoaDon) {
        HoaDon hoaDon = getHoaDonById(maHoaDon);
        if (hoaDon != null) {
            int tongTienDien = hoaDon.getSoDien() * hoaDon.getDonGiaDien();
            int tongTienNuoc = hoaDon.getSoNguoi() * hoaDon.getDonGiaNuoc();
            return tongTienDien + tongTienNuoc + hoaDon.getPhiDichVu() + hoaDon.getTienPhong();
        }
        return 0;
    }

    // Phương thức chỉ cập nhật trạng thái
    public int updateTrangThaiHoaDon(int mahoadon, int trangthai) {
        ContentValues values = new ContentValues();
        values.put("trangThai", trangthai);
        // SỬA LỖI: Không gọi db.close() và trả về kết quả update
        return db.update("HoaDon", values, "maHoaDon = ?", new String[]{String.valueOf(mahoadon)});
    }

    @SuppressLint("Range")
    public int getTongTienDien(int maHoaDon){
        int tongTienDien=0;
        String sqlTongDien = "SELECT soDien * donGiaDien as TongTienDien FROM HoaDon WHERE maHoaDon=?";
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
        String sqlTongNuoc = "SELECT soNguoi * donGiaNuoc as TongTienNuoc FROM HoaDon WHERE maHoaDon=?";
        Cursor cursor = db.rawQuery(sqlTongNuoc, new String[]{String.valueOf(maHoaDon)});
        if (cursor.moveToFirst()) {
            tongTienNuoc = cursor.getInt(cursor.getColumnIndex("TongTienNuoc"));
        }
        cursor.close();
        return tongTienNuoc;
    }
}