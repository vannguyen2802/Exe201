# HYBRID DAO - Hướng Dẫn Sử Dụng

## 📚 Tổng Quan

Hybrid DAO pattern kết hợp **SQLite** (local cache) và **Firestore** (cloud database) để đạt được:

- ⚡ **Tốc độ cao**: Đọc ngay từ SQLite (offline-first)
- ☁️ **Cloud sync**: Tự động đồng bộ với Firestore
- 📱 **Multi-device**: Real-time sync giữa các thiết bị
- 🔄 **Offline support**: Hoạt động khi mất mạng

---

## 🏗️ Kiến Trúc

```
┌─────────────────┐
│   Activity      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  HybridDao      │ ← Abstraction layer
└────┬───────┬────┘
     │       │
     ▼       ▼
┌─────┐   ┌──────────┐
│SQLite│  │Firestore │
└─────┘   └──────────┘
  Fast      Cloud
```

---

## 📋 Danh Sách HybridDao Classes

Tất cả đã được tạo tại `com.example.nestera.Firebase/`:

| HybridDao Class    | Entity    | Repository          | Local DAO    |
| ------------------ | --------- | ------------------- | ------------ |
| BaiDangHybridDao   | BaiDang   | BaiDangRepository   | baiDangDao   |
| PhongTroHybridDao  | PhongTro  | PhongTroRepository  | phongTroDao  |
| HopDongHybridDao   | HopDong   | HopDongRepository   | hopDongDao   |
| NguoiThueHybridDao | NguoiThue | NguoiThueRepository | nguoiThueDao |
| HoaDonHybridDao    | HoaDon    | HoaDonRepository    | hoaDonDao    |
| SuCoHybridDao      | SuCo      | SuCoRepository      | suCoDao      |
| ChuTroHybridDao    | ChuTro    | ChuTroRepository    | chuTroDao    |
| LoaiPhongHybridDao | LoaiPhong | LoaiPhongRepository | LoaiPhongDao |

---

## 🚀 Cách Sử Dụng

### 1️⃣ Khởi tạo trong Activity

```java
public class PhongTroActivity extends AppCompatActivity {
    private PhongTroHybridDao hybridDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo HybridDao
        hybridDao = new PhongTroHybridDao(this);

        // Bật real-time sync (optional)
        hybridDao.enableRealtimeSync();

        // Load dữ liệu
        loadData();
    }
}
```

### 2️⃣ Đọc Dữ Liệu (Read)

```java
// Lấy tất cả - trả về ngay từ SQLite, background sync từ Firestore
List<PhongTro> rooms = hybridDao.getAll();

// Lấy theo ID
PhongTro room = hybridDao.getById(1);

// Lấy phòng trống
List<PhongTro> available = hybridDao.getAvailableRooms();

// Lấy theo loại phòng
List<PhongTro> vip = hybridDao.getByLoaiPhong(2);
```

**Flow:**

1. Đọc từ SQLite → Trả về ngay (fast) ⚡
2. Background sync từ Firestore → Update SQLite ☁️
3. UI tự động refresh nếu có thay đổi 🔄

### 3️⃣ Ghi Dữ Liệu (Create/Update/Delete)

```java
// Thêm mới - ghi song song SQLite + Firestore
PhongTro newRoom = new PhongTro();
newRoom.setTenPhong("Phòng VIP 101");
newRoom.setGiaPhong(3000000);
long id = hybridDao.insert(newRoom);

// Cập nhật - ghi song song
newRoom.setGiaPhong(3500000);
hybridDao.update(newRoom);

// Xóa - ghi song song
hybridDao.delete(newRoom.getMaPhong());
```

**Flow:**

1. Ghi vào SQLite ngay lập tức → UI update ngay ⚡
2. Background sync lên Firestore → Sync tới các device khác ☁️
3. Nếu lỗi → Log error, retry sau 🔄

### 4️⃣ Real-time Sync

```java
// Bật real-time listener
hybridDao.enableRealtimeSync();

// Firestore sẽ tự động:
// 1. Lắng nghe changes từ cloud
// 2. Update SQLite khi có thay đổi
// 3. Trigger UI refresh (nếu implement callback)
```

**Use case:**

- User A tạo phòng trên device 1
- User B tự động thấy phòng mới trên device 2 (real-time!)

### 5️⃣ Force Sync

```java
// Khi cần sync ngay lập tức (vd: sau khi login)
hybridDao.forceSync();

// Hoặc trong onResume để đảm bảo data mới nhất
@Override
protected void onResume() {
    super.onResume();
    hybridDao.forceSync();
}
```

---

## 📝 Ví Dụ Cụ Thể

### Example 1: HopDongActivity

```java
public class HopDongActivity extends AppCompatActivity {
    private HopDongHybridDao hybridDao;
    private RecyclerView recyclerView;
    private HopDongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hop_dong);

        // Setup
        hybridDao = new HopDongHybridDao(this);
        setupRecyclerView();

        // Bật real-time sync
        hybridDao.enableRealtimeSync();

        // Load data
        loadContracts();
    }

    private void loadContracts() {
        // Lấy hợp đồng đang hoạt động
        List<HopDong> contracts = hybridDao.getActiveContracts();
        adapter.setData(contracts);

        // Data từ SQLite sẽ hiển thị ngay
        // Background sync từ Firestore sẽ update nếu có thay đổi
    }

    private void createContract(HopDong hopDong) {
        // Ghi song song SQLite + Firestore
        long id = hybridDao.insert(hopDong);

        if (id > 0) {
            Toast.makeText(this, "Tạo hợp đồng thành công", Toast.LENGTH_SHORT).show();
            loadContracts(); // Refresh UI
        }
    }
}
```

### Example 2: HoaDonActivity với Filter

```java
public class HoaDonActivity extends AppCompatActivity {
    private HoaDonHybridDao hybridDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hybridDao = new HoaDonHybridDao(this);
        hybridDao.enableRealtimeSync();
    }

    private void loadUnpaidBills() {
        // Lấy hóa đơn chưa thanh toán
        List<HoaDon> unpaid = hybridDao.getUnpaidBills();
        // Display...
    }

    private void loadBillsByMonth(int month, int year) {
        // Lấy hóa đơn theo tháng
        List<HoaDon> bills = hybridDao.getByMonth(month, year);
        // Display...
    }

    private void markAsPaid(HoaDon hoaDon) {
        hoaDon.setTrangThai(1); // 1 = đã thanh toán
        hybridDao.update(hoaDon);

        // SQLite update ngay → UI refresh
        // Firestore sync → Device khác cũng thấy
    }
}
```

### Example 3: SuCoActivity với Real-time

```java
public class SuCoActivity extends AppCompatActivity {
    private SuCoHybridDao hybridDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hybridDao = new SuCoHybridDao(this);

        // Bật real-time để nhận thông báo sự cố mới ngay lập tức
        hybridDao.enableRealtimeSync();

        loadPendingIssues();
    }

    private void loadPendingIssues() {
        // Lấy sự cố chưa xử lý
        List<SuCo> pending = hybridDao.getPendingIssues();

        // Khi có sự cố mới từ tenant khác
        // → Real-time listener tự động update SQLite
        // → Thông báo cho landlord biết ngay
    }

    private void resolveIssue(SuCo suCo) {
        suCo.setTrangThai(2); // 2 = đã xử lý
        hybridDao.update(suCo);

        // Tenant sẽ thấy trạng thái mới ngay lập tức
    }
}
```

### Example 4: ChuTroActivity với Authentication

```java
public class LoginActivity extends AppCompatActivity {
    private ChuTroHybridDao hybridDao;

    private void login(String email, String password) {
        hybridDao = new ChuTroHybridDao(this);

        // Authenticate với hybrid approach
        hybridDao.authenticate(email, password, new FirestoreRepository.FirestoreCallback<ChuTro>() {
            @Override
            public void onSuccess(ChuTro chuTro) {
                // Login thành công
                if (chuTro.getTrangThai() == 1) { // Approved
                    // Save to SharedPreferences
                    // Navigate to MainActivity
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                } else if (chuTro.getTrangThai() == 0) { // Pending
                    Toast.makeText(LoginActivity.this, "Tài khoản đang chờ duyệt", Toast.LENGTH_SHORT).show();
                } else { // Banned
                    Toast.makeText(LoginActivity.this, "Tài khoản đã bị khóa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

---

## ⚠️ Lưu Ý Quan Trọng

### 1. Offline Behavior

```java
// Khi KHÔNG có internet:
List<PhongTro> rooms = hybridDao.getAll();
// → Vẫn trả về data từ SQLite
// → Background sync sẽ fail nhẹ nhàng (log error)
// → UI vẫn hoạt động bình thường

// Khi insert offline:
hybridDao.insert(newRoom);
// → Ghi vào SQLite thành công
// → Firestore sync fail → Cần retry mechanism (SyncService)
```

### 2. Real-time Sync Performance

```java
// CHỈ bật real-time cho Activity quan trọng
@Override
protected void onCreate(Bundle savedInstanceState) {
    hybridDao.enableRealtimeSync(); // ✅ OK cho main screens
}

// KHÔNG bật cho tất cả Activity
// → Tốn battery, tốn bandwidth
```

### 3. Memory Leaks

```java
// Nên detach listener khi destroy
private ListenerRegistration listener;

@Override
protected void onDestroy() {
    super.onDestroy();
    if (listener != null) {
        listener.remove(); // Cleanup Firestore listener
    }
}
```

---

## 🔧 Troubleshooting

### Issue 1: Data không sync

```bash
# Check Logcat
adb logcat | grep -i "Hybrid"

# Tìm dòng:
# "Synced X records from Firestore" ✅
# "Sync failed" ❌ → Check network/Firestore Rules
```

### Issue 2: Duplicate inserts

```java
// Problem: Insert 2 lần khi có network
hybridDao.insert(room); // Ghi SQLite + Firestore
hybridDao.insert(room); // ❌ Duplicate

// Solution: Check ID trước khi insert
if (room.getMaPhong() == 0) { // Chưa có ID
    hybridDao.insert(room);
}
```

### Issue 3: Real-time không hoạt động

- Check Firestore Rules (phải allow read)
- Check network connection
- Check Logcat cho "Real-time sync error"

---

## 📊 So Sánh Performance

| Operation          | SQLite Only | Firestore Only | HybridDao                |
| ------------------ | ----------- | -------------- | ------------------------ |
| Read (có network)  | ⚡ 5ms      | ☁️ 500ms       | ⚡ 5ms + background sync |
| Read (offline)     | ⚡ 5ms      | ❌ Fail        | ⚡ 5ms                   |
| Write (có network) | ⚡ 5ms      | ☁️ 300ms       | ⚡ 5ms + async sync      |
| Write (offline)    | ⚡ 5ms      | ❌ Fail        | ⚡ 5ms (sync sau)        |
| Multi-device sync  | ❌ Không    | ✅ Có          | ✅ Có                    |

**Kết luận:** HybridDao có **best of both worlds**! 🎉

---

## 🎯 Next Steps

1. **Tích hợp vào Activities** ✅

   - Thay `phongTroDao` → `PhongTroHybridDao`
   - Thay `hopDongDao` → `HopDongHybridDao`
   - Etc.

2. **Test Multi-device Sync** 🧪

   - Build app
   - Install trên 2 devices/emulators
   - Tạo/sửa/xóa data → Xem real-time sync

3. **Tạo SyncService** 🔄

   - WorkManager periodic job
   - Retry failed Firestore operations
   - Handle conflict resolution

4. **Security Rules** 🔒
   - Update Firestore Rules
   - Phân quyền theo user role

---

## 📞 Support

Gặp vấn đề? Check:

1. Logcat với filter "Hybrid"
2. Firestore Console → Xem data có sync không
3. Network connection
4. Firestore Security Rules

Happy Coding! 🚀
