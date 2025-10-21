package com.example.nestera.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.nestera.Database.DbHelper;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public final class MigrationToFirestore {
    private static final String TAG = "FS_Migration";
    private static final int BATCH_LIMIT = 450; // < 500/commit

    private MigrationToFirestore() {}

    // Gọi hàm này 1 lần khi đăng nhập admin (chạy trên background thread)
    public static void runOnce(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences("fs_migration", Context.MODE_PRIVATE);
        // TODO: Tạm thời comment dòng check này để force migration chạy lại
         if (sp.getBoolean("done_v1", false)) {
             Log.d(TAG, "Migration already done, skip.");
             return;
         }
        
        // Chạy trên background thread để tránh blocking main thread
        new Thread(() -> {
            try {
                Log.d(TAG, "🚀 Starting migration...");
                migrateChuTro(ctx);
                migrateKeToan(ctx);
                migrateLoaiPhong(ctx);
                migratePhongTro(ctx);
                migratePhongTroImages(ctx);
                migrateNguoiThue(ctx);
                migrateHopDong(ctx);
                migrateHoaDon(ctx);
                migrateCTHoaDon(ctx);
                migrateSuCo(ctx);
                migrateNganHang(ctx);
                migrateBaiDang(ctx);
                sp.edit().putBoolean("done_v1", true).apply();
                Log.d(TAG, "✅ Migration completed successfully.");
            } catch (Exception e) {
                Log.e(TAG, "❌ Migration failed", e);
            }
        }).start();
    }

    private static SQLiteDatabase openDb(Context ctx) {
        DbHelper h = new DbHelper(ctx);
        return h.getReadableDatabase();
    }

    // 1. Migrate ChuTro
    private static void migrateChuTro(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM ChuTro", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int maChuTroIdx = c.getColumnIndex("maChuTro");
            int matKhauIdx = c.getColumnIndex("matKhau");
            int tenChuTroIdx = c.getColumnIndex("tenChuTro");
            int emailIdx = c.getColumnIndex("email");
            int sdtIdx = c.getColumnIndex("sdt");
            int cccdIdx = c.getColumnIndex("cccd");
            int approvedIdx = c.getColumnIndex("approved");
            int bannedIdx = c.getColumnIndex("banned");
            
            String maChuTro = maChuTroIdx >= 0 ? c.getString(maChuTroIdx) : "";
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("maChuTro", maChuTro);
            if (matKhauIdx >= 0) doc.put("matKhau", c.getString(matKhauIdx));
            if (tenChuTroIdx >= 0) doc.put("tenChuTro", c.getString(tenChuTroIdx));
            if (emailIdx >= 0) doc.put("email", c.getString(emailIdx));
            if (sdtIdx >= 0) doc.put("sdt", c.getString(sdtIdx));
            if (cccdIdx >= 0) doc.put("cccd", c.getString(cccdIdx));
            if (approvedIdx >= 0) doc.put("approved", c.getInt(approvedIdx));
            if (bannedIdx >= 0) doc.put("banned", c.getInt(bannedIdx));
            doc.put("createdAt", FieldValue.serverTimestamp());
            
            batch.set(fs.collection("chuTro").document(maChuTro), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close(); if (count > 0) Tasks.await(batch.commit()); sql.close();
        Log.d(TAG, "✓ migrateChuTro done (" + count + " records)");
    }

    // 2. Migrate KeToan
    private static void migrateKeToan(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM KeToan", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int maKeToanIdx = c.getColumnIndex("maKeToan");
            int tenKeToanIdx = c.getColumnIndex("tenKeToan");
            int matKhauKTIdx = c.getColumnIndex("matKhauKT");
            
            String maKeToan = maKeToanIdx >= 0 ? c.getString(maKeToanIdx) : "";
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("maKeToan", maKeToan);
            if (tenKeToanIdx >= 0) doc.put("tenKeToan", c.getString(tenKeToanIdx));
            if (matKhauKTIdx >= 0) doc.put("matKhauKT", c.getString(matKhauKTIdx));
            doc.put("createdAt", FieldValue.serverTimestamp());
            
            batch.set(fs.collection("keToan").document(maKeToan), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close(); if (count > 0) Tasks.await(batch.commit()); sql.close();
        Log.d(TAG, "✓ migrateKeToan done (" + count + " records)");
    }

    private static void migrateLoaiPhong(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM LoaiPhong", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int maLoaiIdx = c.getColumnIndex("maLoai");
            int tenLoaiIdx = c.getColumnIndex("tenLoai");
            int phiDichVuIdx = c.getColumnIndex("phiDichVu");
            int giaDienIdx = c.getColumnIndex("giaDien");
            int giaNuocIdx = c.getColumnIndex("giaNuoc");
            
            int maLoai = maLoaiIdx >= 0 ? c.getInt(maLoaiIdx) : 0;

            Map<String, Object> doc = new HashMap<>();
            doc.put("maLoai", maLoai);
            if (tenLoaiIdx >= 0) doc.put("tenLoai", c.getString(tenLoaiIdx));
            if (phiDichVuIdx >= 0) doc.put("phiDichVu", safeLong(c, phiDichVuIdx));
            if (giaDienIdx >= 0) doc.put("giaDien", safeLong(c, giaDienIdx));
            if (giaNuocIdx >= 0) doc.put("giaNuoc", safeLong(c, giaNuocIdx));
            doc.put("updatedAt", FieldValue.serverTimestamp());

            batch.set(fs.collection("loaiPhong").document(String.valueOf(maLoai)), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close();
        if (count > 0) Tasks.await(batch.commit());
        sql.close();
        Log.d(TAG, "✓ migrateLoaiPhong done (" + count + " records)");
    }

    private static void migratePhongTro(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        
        // Lấy danh sách cột thực tế từ bảng PhongTro
        Cursor c = sql.rawQuery("SELECT * FROM PhongTro LIMIT 0", null);
        String[] columnNames = c.getColumnNames();
        c.close();
        
        Log.d(TAG, "PhongTro columns: " + String.join(", ", columnNames));
        
        // Query với các cột cơ bản nhất
        c = sql.rawQuery("SELECT * FROM PhongTro", null);
        
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int maPhongIdx = c.getColumnIndex("maPhong");
            int maLoaiIdx = c.getColumnIndex("maLoai");
            int tenPhongIdx = c.getColumnIndex("tenPhong");
            int giaTienIdx = c.getColumnIndex("giaTien");
            int tienNghiIdx = c.getColumnIndex("tienNghi");
            int trangThaiIdx = c.getColumnIndex("trangThai");
            int imagePathIdx = c.getColumnIndex("imagePath");
            
            int maPhong = maPhongIdx >= 0 ? c.getInt(maPhongIdx) : 0;
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("maPhong", maPhong);
            if (maLoaiIdx >= 0) doc.put("maLoai", c.getInt(maLoaiIdx));
            if (tenPhongIdx >= 0) doc.put("tenPhong", c.getString(tenPhongIdx));
            if (giaTienIdx >= 0) doc.put("giaTien", safeLong(c, giaTienIdx));
            if (tienNghiIdx >= 0) doc.put("tienNghi", c.getString(tienNghiIdx));
            if (trangThaiIdx >= 0) doc.put("trangThai", c.getInt(trangThaiIdx));
            if (imagePathIdx >= 0) doc.put("imagePath", c.getString(imagePathIdx));
            doc.put("mainImageUrl", null);
            doc.put("updatedAt", FieldValue.serverTimestamp());

            batch.set(fs.collection("phongTro").document(String.valueOf(maPhong)), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close();
        if (count > 0) Tasks.await(batch.commit());
        sql.close();
        Log.d(TAG, "✓ migratePhongTro done (" + count + " records)");
    }

    // 5. Migrate PhongTroImages
    private static void migratePhongTroImages(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM PhongTroImages", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int idIdx = c.getColumnIndex("id");
            int maPhongIdx = c.getColumnIndex("maPhong");
            int imagePathIdx = c.getColumnIndex("imagePath");
            int thuTuIdx = c.getColumnIndex("thuTu");
            int isMainIdx = c.getColumnIndex("isMain");
            
            int id = idIdx >= 0 ? c.getInt(idIdx) : 0;
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", id);
            if (maPhongIdx >= 0) doc.put("maPhong", c.getInt(maPhongIdx));
            if (imagePathIdx >= 0) doc.put("imagePath", c.getString(imagePathIdx));
            if (thuTuIdx >= 0) doc.put("thuTu", c.getInt(thuTuIdx));
            if (isMainIdx >= 0) doc.put("isMain", c.getInt(isMainIdx));
            doc.put("imageUrl", null); // URL từ Firebase Storage sẽ cập nhật sau
            doc.put("createdAt", FieldValue.serverTimestamp());
            
            batch.set(fs.collection("phongTroImages").document(String.valueOf(id)), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close(); if (count > 0) Tasks.await(batch.commit()); sql.close();
        Log.d(TAG, "✓ migratePhongTroImages done (" + count + " records)");
    }

    private static void migrateNguoiThue(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM NguoiThue", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int maNguoiThueIdx = c.getColumnIndex("maNguoiThue");
            int matKhauNTIdx = c.getColumnIndex("matKhauNT");
            int tenNguoiThueIdx = c.getColumnIndex("tenNguoiThue");
            int thuongTruIdx = c.getColumnIndex("thuongTru");
            int sdtIdx = c.getColumnIndex("sdt");
            int cccdIdx = c.getColumnIndex("CCCD");
            int namSinhIdx = c.getColumnIndex("namSinh");
            int gioiTinhIdx = c.getColumnIndex("gioiTinh");
            int maPhongIdx = c.getColumnIndex("maPhong");
            int chuTroIdIdx = c.getColumnIndex("chuTroId");
            
            String maNguoiThue = maNguoiThueIdx >= 0 ? c.getString(maNguoiThueIdx) : "";
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("maNguoiThue", maNguoiThue);
            if (matKhauNTIdx >= 0) doc.put("matKhauNT", c.getString(matKhauNTIdx));
            if (tenNguoiThueIdx >= 0) doc.put("tenNguoiThue", c.getString(tenNguoiThueIdx));
            if (thuongTruIdx >= 0) doc.put("thuongTru", c.getString(thuongTruIdx));
            if (sdtIdx >= 0) doc.put("sdt", c.getString(sdtIdx));
            if (cccdIdx >= 0) doc.put("CCCD", c.getString(cccdIdx));
            if (namSinhIdx >= 0) doc.put("namSinh", c.getString(namSinhIdx));
            if (gioiTinhIdx >= 0) doc.put("gioiTinh", c.getInt(gioiTinhIdx));
            if (maPhongIdx >= 0) doc.put("maPhong", c.getInt(maPhongIdx));
            if (chuTroIdIdx >= 0) doc.put("chuTroId", c.getString(chuTroIdIdx));
            doc.put("createdAt", FieldValue.serverTimestamp());
            
            batch.set(fs.collection("nguoiThue").document(maNguoiThue), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close(); if (count > 0) Tasks.await(batch.commit()); sql.close();
        Log.d(TAG, "✓ migrateNguoiThue done (" + count + " records)");
    }

    private static void migrateHopDong(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM HopDong", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int maHopDongIdx = c.getColumnIndex("maHopDong");
            int sdtIdx = c.getColumnIndex("sdt");
            int cccdIdx = c.getColumnIndex("CCCD");
            int thuongTruIdx = c.getColumnIndex("thuongTru");
            int ngayKyIdx = c.getColumnIndex("ngayKy");
            int thoiHanIdx = c.getColumnIndex("thoiHan");
            int tienCocIdx = c.getColumnIndex("tienCoc");
            int giaTienIdx = c.getColumnIndex("giaTien");
            int soNguoiIdx = c.getColumnIndex("soNguoi");
            int soXeIdx = c.getColumnIndex("soXe");
            int ghiChuIdx = c.getColumnIndex("ghiChu");
            int hinhAnhIdx = c.getColumnIndex("hinhAnh");
            int maNguoiThueIdx = c.getColumnIndex("maNguoiThue");
            int maPhongIdx = c.getColumnIndex("maPhong");
            
            int maHopDong = maHopDongIdx >= 0 ? c.getInt(maHopDongIdx) : 0;
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("maHopDong", maHopDong);
            if (sdtIdx >= 0) doc.put("sdt", c.getString(sdtIdx));
            if (cccdIdx >= 0) doc.put("CCCD", c.getInt(cccdIdx));
            if (thuongTruIdx >= 0) doc.put("thuongTru", c.getString(thuongTruIdx));
            if (ngayKyIdx >= 0) doc.put("ngayKy", c.getString(ngayKyIdx));
            if (thoiHanIdx >= 0) doc.put("thoiHan", c.getInt(thoiHanIdx));
            if (tienCocIdx >= 0) doc.put("tienCoc", safeLong(c, tienCocIdx));
            if (giaTienIdx >= 0) doc.put("giaTien", safeLong(c, giaTienIdx));
            if (soNguoiIdx >= 0) doc.put("soNguoi", c.getInt(soNguoiIdx));
            if (soXeIdx >= 0) doc.put("soXe", c.getInt(soXeIdx));
            if (ghiChuIdx >= 0) doc.put("ghiChu", c.getString(ghiChuIdx));
            // Skip BLOB hinhAnh - sẽ upload lên Storage sau
            if (maNguoiThueIdx >= 0) doc.put("maNguoiThue", c.getString(maNguoiThueIdx));
            if (maPhongIdx >= 0) doc.put("maPhong", c.getInt(maPhongIdx));
            doc.put("hinhAnhUrl", null); // URL từ Storage
            doc.put("createdAt", FieldValue.serverTimestamp());
            
            batch.set(fs.collection("hopDong").document(String.valueOf(maHopDong)), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close(); if (count > 0) Tasks.await(batch.commit()); sql.close();
        Log.d(TAG, "✓ migrateHopDong done (" + count + " records)");
    }

    private static void migrateHoaDon(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM HoaDon", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int maHoaDonIdx = c.getColumnIndex("maHoaDon");
            int sdtIdx = c.getColumnIndex("sdt");
            int ngayTaoIdx = c.getColumnIndex("ngayTao");
            int soDienIdx = c.getColumnIndex("soDien");
            int donGiaDienIdx = c.getColumnIndex("donGiaDien");
            int soNguoiIdx = c.getColumnIndex("soNguoi");
            int donGiaNuocIdx = c.getColumnIndex("donGiaNuoc");
            int phiDichVuIdx = c.getColumnIndex("phiDichVu");
            int ghiChuIdx = c.getColumnIndex("ghiChu");
            int tienPhongIdx = c.getColumnIndex("tienPhong");
            int anhThanhToanIdx = c.getColumnIndex("anhThanhToan");
            int trangThaiIdx = c.getColumnIndex("trangThai");
            int maPhongIdx = c.getColumnIndex("maPhong");
            int maNguoiThueIdx = c.getColumnIndex("maNguoiThue");
            
            int maHoaDon = maHoaDonIdx >= 0 ? c.getInt(maHoaDonIdx) : 0;
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("maHoaDon", maHoaDon);
            if (sdtIdx >= 0) doc.put("sdt", c.getString(sdtIdx));
            if (ngayTaoIdx >= 0) doc.put("ngayTao", c.getString(ngayTaoIdx));
            if (soDienIdx >= 0) doc.put("soDien", c.getInt(soDienIdx));
            if (donGiaDienIdx >= 0) doc.put("donGiaDien", safeLong(c, donGiaDienIdx));
            if (soNguoiIdx >= 0) doc.put("soNguoi", c.getInt(soNguoiIdx));
            if (donGiaNuocIdx >= 0) doc.put("donGiaNuoc", safeLong(c, donGiaNuocIdx));
            if (phiDichVuIdx >= 0) doc.put("phiDichVu", safeLong(c, phiDichVuIdx));
            if (ghiChuIdx >= 0) doc.put("ghiChu", c.getString(ghiChuIdx));
            if (tienPhongIdx >= 0) doc.put("tienPhong", safeLong(c, tienPhongIdx));
            // Skip BLOB anhThanhToan - sẽ upload lên Storage sau
            if (trangThaiIdx >= 0) doc.put("trangThai", c.getInt(trangThaiIdx));
            if (maPhongIdx >= 0) doc.put("maPhong", c.getInt(maPhongIdx));
            if (maNguoiThueIdx >= 0) doc.put("maNguoiThue", c.getString(maNguoiThueIdx));
            doc.put("anhThanhToanUrl", null); // URL từ Storage
            doc.put("createdAt", FieldValue.serverTimestamp());
            
            batch.set(fs.collection("hoaDon").document(String.valueOf(maHoaDon)), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close(); if (count > 0) Tasks.await(batch.commit()); sql.close();
        Log.d(TAG, "✓ migrateHoaDon done (" + count + " records)");
    }

    // 10. Migrate CTHoaDon
    private static void migrateCTHoaDon(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM CTHoaDon", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int maCTHoaDonIdx = c.getColumnIndex("maCTHoaDon");
            int maPhongIdx = c.getColumnIndex("maPhong");
            int maNguoiThueIdx = c.getColumnIndex("maNguoiThue");
            int soDienIdx = c.getColumnIndex("soDien");
            int soNuocIdx = c.getColumnIndex("soNuoc");
            int phiDichVuIdx = c.getColumnIndex("phiDichVu");
            int tongTienIdx = c.getColumnIndex("tongTien");
            
            int maCTHoaDon = maCTHoaDonIdx >= 0 ? c.getInt(maCTHoaDonIdx) : 0;
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("maCTHoaDon", maCTHoaDon);
            if (maPhongIdx >= 0) doc.put("maPhong", c.getInt(maPhongIdx));
            if (maNguoiThueIdx >= 0) doc.put("maNguoiThue", c.getString(maNguoiThueIdx));
            if (soDienIdx >= 0) doc.put("soDien", c.getInt(soDienIdx));
            if (soNuocIdx >= 0) doc.put("soNuoc", c.getInt(soNuocIdx));
            if (phiDichVuIdx >= 0) doc.put("phiDichVu", safeLong(c, phiDichVuIdx));
            if (tongTienIdx >= 0) doc.put("tongTien", safeLong(c, tongTienIdx));
            doc.put("createdAt", FieldValue.serverTimestamp());
            
            batch.set(fs.collection("ctHoaDon").document(String.valueOf(maCTHoaDon)), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close(); if (count > 0) Tasks.await(batch.commit()); sql.close();
        Log.d(TAG, "✓ migrateCTHoaDon done (" + count + " records)");
    }

    private static void migrateSuCo(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM SuCo", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int maSuCoIdx = c.getColumnIndex("maSuCo");
            int tenSuCoIdx = c.getColumnIndex("tenSuCo");
            int noiDungIdx = c.getColumnIndex("noiDung");
            int trangThaiIdx = c.getColumnIndex("trangThai");
            int maPhongIdx = c.getColumnIndex("maPhong");
            int maNguoiThueIdx = c.getColumnIndex("maNguoiThue");
            
            int maSuCo = maSuCoIdx >= 0 ? c.getInt(maSuCoIdx) : 0;
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("maSuCo", maSuCo);
            if (tenSuCoIdx >= 0) doc.put("tenSuCo", c.getString(tenSuCoIdx));
            if (noiDungIdx >= 0) doc.put("noiDung", c.getString(noiDungIdx));
            if (trangThaiIdx >= 0) doc.put("trangThai", c.getInt(trangThaiIdx));
            if (maPhongIdx >= 0) doc.put("maPhong", c.getInt(maPhongIdx));
            if (maNguoiThueIdx >= 0) doc.put("maNguoiThue", c.getString(maNguoiThueIdx));
            doc.put("createdAt", FieldValue.serverTimestamp());
            
            batch.set(fs.collection("suCo").document(String.valueOf(maSuCo)), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close(); if (count > 0) Tasks.await(batch.commit()); sql.close();
        Log.d(TAG, "✓ migrateSuCo done (" + count + " records)");
    }

    // 12. Migrate NganHang
    private static void migrateNganHang(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM NganHang", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int idIdx = c.getColumnIndex("Id");
            int tenTKNganHangIdx = c.getColumnIndex("tenTKNganHang");
            int tenNganHangIdx = c.getColumnIndex("tenNganHang");
            int stkIdx = c.getColumnIndex("STK");
            int hinhAnhIdx = c.getColumnIndex("HinhAnh");
            
            int id = idIdx >= 0 ? c.getInt(idIdx) : 0;
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", id);
            if (tenTKNganHangIdx >= 0) doc.put("tenTKNganHang", c.getString(tenTKNganHangIdx));
            if (tenNganHangIdx >= 0) doc.put("tenNganHang", c.getString(tenNganHangIdx));
            if (stkIdx >= 0) doc.put("STK", c.getString(stkIdx));
            // Skip BLOB HinhAnh - sẽ upload lên Storage sau
            doc.put("hinhAnhUrl", null);
            doc.put("createdAt", FieldValue.serverTimestamp());
            
            batch.set(fs.collection("nganHang").document(String.valueOf(id)), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close(); if (count > 0) Tasks.await(batch.commit()); sql.close();
        Log.d(TAG, "✓ migrateNganHang done (" + count + " records)");
    }

    // 13. Migrate BaiDang
    private static void migrateBaiDang(Context ctx) throws Exception {
        SQLiteDatabase sql = openDb(ctx);
        Cursor c = sql.rawQuery("SELECT * FROM BaiDang", null);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        WriteBatch batch = fs.batch();
        int count = 0;
        
        while (c.moveToNext()) {
            int idIdx = c.getColumnIndex("id");
            int tieuDeIdx = c.getColumnIndex("tieuDe");
            int diaChiIdx = c.getColumnIndex("diaChi");
            int giaThangIdx = c.getColumnIndex("giaThang");
            int dienTichIdx = c.getColumnIndex("dienTich");
            int tienNghiIdx = c.getColumnIndex("tienNghi");
            int trangThaiIdx = c.getColumnIndex("trangThai");
            int hinhAnhIdx = c.getColumnIndex("hinhAnh");
            int maPhongIdx = c.getColumnIndex("maPhong");
            int chuTroIdIdx = c.getColumnIndex("chuTroId");
            
            int id = idIdx >= 0 ? c.getInt(idIdx) : 0;
            
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", id);
            if (tieuDeIdx >= 0) doc.put("tieuDe", c.getString(tieuDeIdx));
            if (diaChiIdx >= 0) doc.put("diaChi", c.getString(diaChiIdx));
            if (giaThangIdx >= 0) doc.put("giaThang", safeLong(c, giaThangIdx));
            if (dienTichIdx >= 0) doc.put("dienTich", c.getDouble(dienTichIdx));
            if (tienNghiIdx >= 0) doc.put("tienNghi", c.getString(tienNghiIdx));
            if (trangThaiIdx >= 0) doc.put("trangThai", c.getString(trangThaiIdx));
            if (hinhAnhIdx >= 0) doc.put("hinhAnhPath", c.getString(hinhAnhIdx));
            if (maPhongIdx >= 0) doc.put("maPhong", c.getInt(maPhongIdx));
            if (chuTroIdIdx >= 0) doc.put("chuTroId", c.getString(chuTroIdIdx));
            doc.put("hinhAnhUrl", null); // URL từ Storage
            doc.put("createdAt", FieldValue.serverTimestamp());
            
            batch.set(fs.collection("baiDang").document(String.valueOf(id)), doc);
            if (++count >= BATCH_LIMIT) { Tasks.await(batch.commit()); batch = fs.batch(); count = 0; }
        }
        c.close(); if (count > 0) Tasks.await(batch.commit()); sql.close();
        Log.d(TAG, "✓ migrateBaiDang done (" + count + " records)");
    }

    private static long safeLong(Cursor c, int idx) {
        try { return c.isNull(idx) ? 0L : c.getLong(idx); } catch (Exception e) {
            try { return Long.parseLong(c.getString(idx)); } catch (Exception ignored) { return 0L; }
        }
    }
}