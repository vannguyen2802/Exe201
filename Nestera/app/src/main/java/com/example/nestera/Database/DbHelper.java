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
    static final int dbVersion=7; // Thêm trường địa chỉ
    Context context;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public DbHelper(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
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
                "imagePath TEXT," + // Lưu đường dẫn ảnh thay vì BLOB
                "diaChi TEXT," + // Địa chỉ phòng trọ
                "timNguoiOGhep INTEGER DEFAULT 0," + // 0: không tìm, 1-4: số người cần tìm
                "soNguoiHienTai INTEGER DEFAULT 0)"; // Số người hiện tại đang ở
        sqLiteDatabase.execSQL(createTablePhongTro);

        //Thêm dữ liệu bảng PhongTro (với ảnh thật)
        sqLiteDatabase.execSQL("insert into PhongTro(maLoai,tenPhong,giaTien,tienNghi,trangThai,imagePath,diaChi,timNguoiOGhep,soNguoiHienTai) values" +
                "(1,'P102',3500000,'Điều hoà, Nóng lạnh, Tủ lạnh, Tủ quần áo',0,'phong_tro_1_1','123 Đường ABC, Quận 1, TP.HCM',0,0)," +
                "(2,'P103',3200000,'Điều hoà, Nóng lạnh, Tủ lạnh, Tủ quần áo',0,'phong_tro_1_3','456 Đường XYZ, Quận 2, TP.HCM',2,1)," + // Phòng đang tìm 2 người, hiện có 1 người
                "(1,'P202',3800000,'Điều hoà, Nóng lạnh, Tủ lạnh, Tủ quần áo, Máy giặt',0,'phong_tro_1_2','789 Đường DEF, Quận 3, TP.HCM',1,2)"); // Phòng đang tìm 1 người, hiện có 2 người

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
                "maPhong INTEGER REFERENCES PhongTro(maPhong))";
        sqLiteDatabase.execSQL(createTableNguoiThue);

        sqLiteDatabase.execSQL("insert into NguoiThue values('quynh01','quynh','Lưu Tuấn Quỳnh','Bắc Giang','3456789987','847837487','03/08/2004',0,1)," +
                "('huy','huy','Phạm Quang Huy','Hải Dương','123456789','123456789','03/09/2004',1,1),"+
                "('nam','nam','Nguyễn Phương Nam','Hà Nội','3456789987','847837487','03/05/2004',1,2)");

        //Tạo bảng HopDong (đơn giản hóa)
        String createTableHopDong = "create table HopDong(" +
                "maHopDong INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ngayKy DATE NOT NULL," +
                "maNguoiThue TEXT REFERENCES NguoiThue(maNguoiThue)," +
                "maPhong INTEGER REFERENCES PhongTro(maPhong))";
        sqLiteDatabase.execSQL(createTableHopDong);

        //Tạo bảng HinhAnhHopDong để lưu ảnh hợp đồng và CCCD
        String createTableHinhAnhHopDong = "create table HinhAnhHopDong(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "maHopDong INTEGER REFERENCES HopDong(maHopDong)," +
                "loaiHinh INTEGER NOT NULL," + // 1: Ảnh hợp đồng, 2: Ảnh CCCD
                "duongDanAnh TEXT NOT NULL," +
                "ngayTao DATE DEFAULT CURRENT_TIMESTAMP)";
        sqLiteDatabase.execSQL(createTableHinhAnhHopDong);


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
                // Thêm cột timNguoiOGhep và soNguoiHienTai vào bảng PhongTro
                sqLiteDatabase.execSQL("ALTER TABLE PhongTro ADD COLUMN timNguoiOGhep INTEGER DEFAULT 0");
                sqLiteDatabase.execSQL("ALTER TABLE PhongTro ADD COLUMN soNguoiHienTai INTEGER DEFAULT 0");
                
                // Tạo bảng HinhAnhHopDong mới
                sqLiteDatabase.execSQL("CREATE TABLE HinhAnhHopDong(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "maHopDong INTEGER REFERENCES HopDong(maHopDong)," +
                        "loaiHinh INTEGER NOT NULL," +
                        "duongDanAnh TEXT NOT NULL," +
                        "ngayTao DATE DEFAULT CURRENT_TIMESTAMP)");
                
                // Cập nhật dữ liệu mẫu cho phòng có tìm người ở ghép
                sqLiteDatabase.execSQL("UPDATE PhongTro SET timNguoiOGhep = 2, soNguoiHienTai = 1 WHERE maPhong = 2");
                sqLiteDatabase.execSQL("UPDATE PhongTro SET timNguoiOGhep = 1, soNguoiHienTai = 2 WHERE maPhong = 3");
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (oldVersion < 7) {
            try {
                // Thêm cột địa chỉ
                sqLiteDatabase.execSQL("ALTER TABLE PhongTro ADD COLUMN diaChi TEXT");
                
                // Cập nhật địa chỉ mẫu cho các phòng hiện có
                sqLiteDatabase.execSQL("UPDATE PhongTro SET diaChi = '123 Đường ABC, Quận 1, TP.HCM' WHERE maPhong = 1");
                sqLiteDatabase.execSQL("UPDATE PhongTro SET diaChi = '456 Đường XYZ, Quận 2, TP.HCM' WHERE maPhong = 2");
                sqLiteDatabase.execSQL("UPDATE PhongTro SET diaChi = '789 Đường DEF, Quận 3, TP.HCM' WHERE maPhong = 3");
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
