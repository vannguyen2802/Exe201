package com.example.nestera.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.example.nestera.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

public class DbHelper extends SQLiteOpenHelper {
    static final String dbName="Nestera";

    static final int dbVersion=13; // Add chuTroId to NguoiThue

    Context context;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public DbHelper(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Tạo bảng ChuTro (Chủ trọ)
        String createTableChuTro = "create table ChuTro(" +
                "maChuTro TEXT PRIMARY KEY," +
                "matKhau TEXT NOT NULL," +
                "tenChuTro TEXT," +
                "email TEXT," +
                "sdt TEXT," +
                "cccd TEXT," +
                "approved INTEGER DEFAULT 0," +
                "banned INTEGER DEFAULT 0)";
        sqLiteDatabase.execSQL(createTableChuTro);

        // Thêm dữ liệu mẫu cho Chủ trọ
        sqLiteDatabase.execSQL("insert into ChuTro(maChuTro, matKhau, tenChuTro, email, sdt, cccd, approved, banned) values" +
                "('chutro1','12345','Chủ trọ 1','ct1@example.com','0900000001','012345678901',1,0)," +

                "('chutro2','12345','Chủ trọ 2','ct3@example.com','0900000003','012345678903',1,0)");

        //Tạo bảng KeToan
        String createTableKeToan="create table KeToan(" +
                "maKeToan TEXT PRIMARY KEY," +
                "tenKeToan TEXT NOT NULL," +
                "matKhauKT TEXT NOT NULL)";
        sqLiteDatabase.execSQL(createTableKeToan);

        //Tạo bảng CoSo
        String createTableLoaiPhong="create table LoaiPhong(" +
                "maLoai INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tenLoai TEXT NOT NULL," +
                "phiDichVu INTEGER NOT NULL," +
                "giaDien INTEGER NOT NULL," +
                "giaNuoc INTEGER NOT NULL)";
        sqLiteDatabase.execSQL(createTableLoaiPhong);
        //Thêm dữ liệu bảng CoSo
        sqLiteDatabase.execSQL("insert into LoaiPhong(tenLoai,phiDichVu,giaDien,giaNuoc) values" +
                "('Full option',100000,3500,100000)," +
                "('Đồ cơ bản',100000,3500,100000)");

        //Tạo bảng PhongTro
        String createTablePhongTro = "create table PhongTro(" +
                "maPhong INTEGER PRIMARY KEY AUTOINCREMENT," +
                "maLoai INTEGER REFERENCES LoaiPhong(maLoai)," +
                "tenPhong TEXT NOT NULL," +
                "giaTien INTEGER NOT NULL," +
                "tienNghi TEXT NOT NULL," +
                "trangThai INTEGER NOT NULL," +
                "imagePath TEXT)"; // Lưu đường dẫn ảnh thay vì BLOB
        sqLiteDatabase.execSQL(createTablePhongTro);

        //Thêm dữ liệu bảng PhongTro (với ảnh thật)
//        sqLiteDatabase.execSQL("insert into PhongTro(maLoai,tenPhong,giaTien,tienNghi,trangThai,imagePath) values" +
//                "(1,'P102',3500000,'Điều hoà, Nóng lạnh, Tủ lạnh, Tủ quần áo',0,'phong_tro_1_1')," +
//                "(2,'P102',3200000,'Điều hoà, Nóng lạnh, Tủ lạnh, Tủ quần áo',0,'phong_tro_1_3')," +
//                "(1,'P202',3800000,'Điều hoà, Nóng lạnh, Tủ lạnh, Tủ quần áo, Máy giặt',0,'phong_tro_1_2')");

        //Tạo bảng PhongTroImages để lưu nhiều ảnh cho mỗi phòng
        String createTablePhongTroImages = "create table PhongTroImages(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "maPhong INTEGER REFERENCES PhongTro(maPhong)," +
                "imagePath TEXT NOT NULL," +
                "thuTu INTEGER DEFAULT 0," + // Thứ tự hiển thị ảnh
                "isMain INTEGER DEFAULT 0)"; // Ảnh chính (0: không, 1: có)
        sqLiteDatabase.execSQL(createTablePhongTroImages);

        //Thêm dữ liệu mẫu cho ảnh phòng trọ (sử dụng ảnh thật)
        sqLiteDatabase.execSQL("insert into PhongTroImages(maPhong,imagePath,thuTu,isMain) values" +
                "(1,'phong_tro_1_1',1,1)," + // Ảnh chính phòng 1
                "(1,'phong_tro_1_2',2,0)," + // Ảnh phụ phòng 1 - ảnh 1
                "(1,'phong_tro_1_3',3,0)," + // Ảnh phụ phòng 1 - ảnh 2
                "(2,'phong_tro_1_3',1,1)," + // Ảnh chính phòng 2
                "(2,'phong_tro_1_1',2,0)," + // Ảnh phụ phòng 2 - ảnh 1
                "(2,'phong_tro_1_2',3,0)," + // Ảnh phụ phòng 2 - ảnh 2
                "(3,'phong_tro_1_2',1,1)," + // Ảnh chính phòng 3
                "(3,'phong_tro_1_3',2,0)," + // Ảnh phụ phòng 3 - ảnh 1
                "(3,'phong_tro_1_1',3,0)"); // Ảnh phụ phòng 3 - ảnh 2

        //Tạo bảng HoaDon
        String createTableHoaDon="create table HoaDon("+
                "maHoaDon INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sdt TEXT ," +
                "ngayTao DATE ," +
                "soDien INTEGER," +
                "donGiaDien INTEGER," +
                "soNguoi INTEGER," +
                "donGiaNuoc INTEGER," +
                "phiDichVu INTEGER," +
                "ghiChu TEXT ," +
                "tienPhong INTEGER," +
                "anhThanhToan BLOB," +
                "trangThai INTEGER ," +
                "maPhong INTEGER REFERENCES PhongTro(maPhong)," +
                "maNguoiThue TEXT REFERENCES NguoiThue(maNguoiThue))";
        sqLiteDatabase.execSQL(createTableHoaDon);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_camera);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageData = stream.toByteArray();

        sqLiteDatabase.execSQL("insert into HoaDon(sdt,ngayTao,soDien,donGiaDien,soNguoi,donGiaNuoc,phiDichVu,ghiChu,tienPhong,anhThanhToan,trangThai,maPhong,maNguoiThue) values" +
                "('0236475775','2025-09-22',5,3500,3,100000,100000,'Thu tiền tháng 9',3500000,'imageData',0,1,'quynh01')");


        String createTableCTHoaDon="create table CTHoaDon(" +
                "maCTHoaDon INTEGER PRIMARY KEY AUTOINCREMENT," +
                "maPhong INTEGER REFERENCES PhongTro(maPhong)," +
                "maNguoiThue TEXT REFERENCES NguoiThue(maNguoiThue)," +
                "soDien INTEGER NOT NULL," +
                "soNuoc INTEGER NOT NULL," +
                "phiDichVu INTEGER NOT NULL," +
                "tongTien INTEGER NOT NULL)";
        sqLiteDatabase.execSQL(createTableCTHoaDon);

        //Tạo bảng NguoiThue
        String createTableNguoiThue ="create table NguoiThue(" +
                "maNguoiThue TEXT PRIMARY KEY," +
                "matKhauNT TEXT NOT NULL," +
                "tenNguoiThue TEXT NOT NULL," +
                "thuongTru TEXT NOT NULL," +
                "sdt TEXT NOT NULL," +
                "CCCD TEXT NOT NULL," +
                "namSinh date NOT NULL," +
                "gioiTinh INTEGER NOT NULL," +
                "maPhong INTEGER REFERENCES PhongTro(maPhong)," +
                "chuTroId TEXT)"; // Chủ trọ nào tạo người thuê này
        sqLiteDatabase.execSQL(createTableNguoiThue);

//        sqLiteDatabase.execSQL("insert into NguoiThue values('quynh01','quynh','Lưu Tuấn Quỳnh','Bắc Giang','3456789987','847837487','03/08/2004',0,1)," +
//                "('huy','huy','Phạm Quang Huy','Hải Dương','123456789','123456789','03/09/2004',1,1),"+
//                "('nam','nam','Nguyễn Phương Nam','Hà Nội','3456789987','847837487','03/05/2004',1,2)");

        //Tạo bảng HopDong
        String createTableHopDong = "create table HopDong(" +
                "maHopDong INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sdt TEXT NOT NULL," +
                "CCCD INTEGER NOT NULL," +
                "thuongTru TEXT NOT NULL," +
                "ngayKy DATE NOT NULL," +
                "thoiHan INTEGER NOT NULL," +
                "tienCoc INTEGER NOT NULL," +
                "giaTien INTEGER NOT NULL," +
                "soNguoi INTEGER NOT NULL," +
                "soXe INTEGER NOT NULL," +
                "ghiChu TEXT," +
                "hinhAnh BLOB," +
                "maNguoiThue TEXT REFERENCES NguoiThue(maNguoiThue)," +
                "maPhong INTEGER REFERENCES PhongTro(maPhong))";
        sqLiteDatabase.execSQL(createTableHopDong);


        //Tạo bảng SuCo
        String createTableSuCo = "create table SuCo(" +
                "maSuCo INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tenSuCo TEXT NOT NULL," +
                "noiDung TEXT NOT NULL," +
                "trangThai INTEGER NOT NULL," +
                "maPhong INTEGER REFERENCES PhongTro(maPhong)," +
                "maNguoiThue INTEGER REFERENCES NguoiThue(maNguoiThue))";
        sqLiteDatabase.execSQL(createTableSuCo);

        sqLiteDatabase.execSQL("insert into SuCo(tenSuCo, noiDung, trangThai, maPhong, maNguoiThue) values" +
                "('Điện','Hỏng bóng đèn',0,1,1)," +
                "('Nước','Vỡ ống nước',0,1,1)");

        //Tạo bảng ngân hàng
        String createTableNganHang = "create table NganHang(" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tenTKNganHang TEXT ," +
                "tenNganHang TEXT ," +
                "STK TEXT," +
                "HinhAnh BLOB)";
        sqLiteDatabase.execSQL(createTableNganHang);

        //Tạo bảng BaiDang (bài đăng cho thuê)
        String createTableBaiDang = "create table BaiDang(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tieuDe TEXT NOT NULL," +
                "diaChi TEXT," +
                "giaThang INTEGER," +
                "dienTich REAL," +
                "tienNghi TEXT," +
                "trangThai TEXT," +
                "hinhAnh TEXT," +
                "maPhong INTEGER," +
                "chuTroId TEXT REFERENCES ChuTro(maChuTro))";
        sqLiteDatabase.execSQL(createTableBaiDang);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                // Tạo bảng PhongTroImages mới để lưu nhiều ảnh
                String createTablePhongTroImages = "create table PhongTroImages(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "maPhong INTEGER REFERENCES PhongTro(maPhong)," +
                        "imagePath TEXT NOT NULL," +
                        "thuTu INTEGER DEFAULT 0," +
                        "isMain INTEGER DEFAULT 0)";
                sqLiteDatabase.execSQL(createTablePhongTroImages);

                // Cập nhật ảnh chính cho bảng PhongTro
                sqLiteDatabase.execSQL("UPDATE PhongTro SET imagePath = 'phong_tro_1_1' WHERE maPhong = 1");
                sqLiteDatabase.execSQL("UPDATE PhongTro SET imagePath = 'phong_tro_1_2' WHERE maPhong = 2");

                // Chuyển dữ liệu từ cột imagePath sang bảng mới nếu có
                sqLiteDatabase.execSQL("insert into PhongTroImages(maPhong,imagePath,thuTu,isMain) " +
                        "SELECT maPhong, imagePath, 1, 1 FROM PhongTro WHERE imagePath IS NOT NULL AND imagePath != ''");

                // Thêm ảnh thật cho các phòng
                sqLiteDatabase.execSQL("insert into PhongTroImages(maPhong,imagePath,thuTu,isMain) values" +
                        "(1,'phong_tro_1_1',1,1)," +
                        "(1,'phong_tro_1_2',2,0)," +
                        "(1,'phong_tro_1_3',3,0)," +
                        "(2,'phong_tro_1_2',1,1)," +
                        "(2,'phong_tro_1_1',2,0)," +
                        "(2,'phong_tro_1_3',3,0)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (oldVersion < 3) {
            try {
                // Cập nhật ảnh cho phòng trọ hiện có
                sqLiteDatabase.execSQL("UPDATE PhongTro SET imagePath = 'phong_tro_1_1' WHERE maPhong = 1");
                sqLiteDatabase.execSQL("UPDATE PhongTro SET imagePath = 'phong_tro_1_2' WHERE maPhong = 2");
                
                // Xóa dữ liệu cũ trong PhongTroImages và thêm mới
                sqLiteDatabase.execSQL("DELETE FROM PhongTroImages");
                sqLiteDatabase.execSQL("insert into PhongTroImages(maPhong,imagePath,thuTu,isMain) values" +
                        "(1,'phong_tro_1_1',1,1)," +
                        "(1,'phong_tro_1_2',2,0)," +
                        "(1,'phong_tro_1_3',3,0)," +
                        "(2,'phong_tro_1_2',1,1)," +
                        "(2,'phong_tro_1_1',2,0)," +
                        "(2,'phong_tro_1_3',3,0)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (oldVersion < 4) {
            try {
                // Thay đổi ảnh phòng thứ 2 từ phong_tro_1_2 sang phong_tro_1_3
                sqLiteDatabase.execSQL("UPDATE PhongTro SET imagePath = 'phong_tro_1_3' WHERE maPhong = 2");
                sqLiteDatabase.execSQL("UPDATE PhongTroImages SET imagePath = 'phong_tro_1_3' WHERE maPhong = 2 AND isMain = 1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (oldVersion < 5) {
            try {
                // Thêm phòng thứ 3 và cập nhật dữ liệu để có sự đa dạng hơn
                sqLiteDatabase.execSQL("INSERT INTO PhongTro(maLoai,tenPhong,giaTien,tienNghi,trangThai,imagePath) VALUES " +
                        "(1,'P202',3800000,'Điều hoà, Nóng lạnh, Tủ lạnh, Tủ quần áo, Máy giặt',0,'phong_tro_1_2')");
                
                // Cập nhật tên phòng 2 để phân biệt
                sqLiteDatabase.execSQL("UPDATE PhongTro SET tenPhong = 'P103' WHERE maPhong = 2");
                
                // Thêm ảnh cho phòng mới
                sqLiteDatabase.execSQL("INSERT INTO PhongTroImages(maPhong,imagePath,thuTu,isMain) VALUES " +
                        "(3,'phong_tro_1_2',1,1)," +
                        "(3,'phong_tro_1_3',2,0)," + 
                        "(3,'phong_tro_1_1',3,0)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (oldVersion < 6) {
            try {
                // Tạo bảng ChuTro nếu chưa có và thêm dữ liệu mẫu
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS ChuTro(" +
                        "maChuTro TEXT PRIMARY KEY," +
                        "matKhau TEXT NOT NULL," +
                        "tenChuTro TEXT," +
                        "sdt TEXT)");

                sqLiteDatabase.execSQL("INSERT OR IGNORE INTO ChuTro(maChuTro, matKhau, tenChuTro, sdt) VALUES " +
                        "('chutro1','12345','Chủ trọ 1','0900000001')," +
                        "('chutro2','12345','Chủ trọ 2','0900000002')," +
                        "('chutro3','12345','Chủ trọ 3','0900000003')");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (oldVersion < 7) {
            try {
                // Thêm cột approved cho ChuTro nếu chưa có
                sqLiteDatabase.execSQL("ALTER TABLE ChuTro ADD COLUMN approved INTEGER DEFAULT 0");
                // Duyệt sẵn các tài khoản seed
                sqLiteDatabase.execSQL("UPDATE ChuTro SET approved = 1 WHERE maChuTro IN ('chutro1','chutro2','chutro3')");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (oldVersion < 8) {
            try {
                // Thêm cột email và cccd nếu chưa có
                sqLiteDatabase.execSQL("ALTER TABLE ChuTro ADD COLUMN email TEXT");
                sqLiteDatabase.execSQL("ALTER TABLE ChuTro ADD COLUMN cccd TEXT");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (oldVersion < 9) {
            try {
                sqLiteDatabase.execSQL("ALTER TABLE ChuTro ADD COLUMN banned INTEGER DEFAULT 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Create BaiDang table for upgraded users
        if (oldVersion < 10) {
            try {
                String createTableBaiDang = "create table IF NOT EXISTS BaiDang(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "tieuDe TEXT NOT NULL," +
                        "diaChi TEXT," +
                        "giaThang INTEGER," +
                        "dienTich REAL," +
                        "tienNghi TEXT," +
                        "trangThai TEXT," +
                        "hinhAnh TEXT," +
                        "chuTroId TEXT REFERENCES ChuTro(maChuTro))";
                sqLiteDatabase.execSQL(createTableBaiDang);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (oldVersion < 11) {
            try {
                // Ensure table exists even if missed in v10
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS BaiDang (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, tieuDe TEXT NOT NULL, diaChi TEXT, giaThang INTEGER, dienTich REAL, tienNghi TEXT, trangThai TEXT, hinhAnh TEXT, maPhong INTEGER, chuTroId TEXT REFERENCES ChuTro(maChuTro))");
            } catch (Exception e) { e.printStackTrace(); }
        }
        if (oldVersion < 12) {
            try {
                sqLiteDatabase.execSQL("ALTER TABLE BaiDang ADD COLUMN maPhong INTEGER");
            } catch (Exception e) { /* might already exist */ }
        }
        
        // Version 13: Thêm cột chuTroId vào bảng NguoiThue
        if (oldVersion < 13) {
            try {
                sqLiteDatabase.execSQL("ALTER TABLE NguoiThue ADD COLUMN chuTroId TEXT");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
