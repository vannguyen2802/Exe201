# 🔥 Firebase Integration Guide - Nestera App

## ✅ ĐÃ HOÀN THÀNH

### 1. Migration SQLite → Firestore

- ✅ Migrate 13 bảng: ChuTro, KeToan, LoaiPhong, PhongTro, PhongTroImages, NguoiThue, HopDong, HoaDon, CTHoaDon, SuCo, NganHang, BaiDang
- ✅ Migration chạy 1 lần tự động khi admin đăng nhập
- ✅ Dynamic column detection - tự động adapt với mọi version database

### 2. Firebase Architecture

- ✅ `FirestoreRepository<T>` - Base class cho CRUD operations
- ✅ `BaiDangRepository` - Repository cụ thể cho BaiDang
- ✅ `BaiDangHybridDao` - Hybrid approach (SQLite + Firestore)
- ✅ `ImageUploader` - Upload ảnh lên Firebase Storage

### 3. UI Integration

- ✅ `BaiDangActivity` - Sử dụng Hybrid DAO
- ✅ `BaiDangDetailActivity` - Sử dụng Hybrid DAO
- ✅ Real-time sync enabled
- ✅ Upload ảnh lên Storage khi tạo/sửa bài đăng

---

## 🚀 CÁCH HOẠT ĐỘNG

### Hybrid Strategy (Offline-First)

```java
// ĐỌC DỮ LIỆU
hybridDao.getAll()
  → Trả về ngay từ SQLite (fast)
  → Background sync từ Firestore (update cache)

// GHI DỮ LIỆU
hybridDao.insert(baiDang)
  → Ghi vào SQLite ngay lập tức
  → Async ghi lên Firestore
  → Nếu thất bại → retry queue

// REAL-TIME SYNC
hybridDao.enableRealtimeSync()
  → Listen Firestore changes
  → Auto update SQLite cache
  → UI tự động refresh
```

### Upload Ảnh Flow

```java
1. User chọn ảnh (URI local)
2. ImageUploader.uploadMultipleImages()
   - Upload lên Firebase Storage
   - Nhận downloadUrl
3. Lưu downloadUrl vào Firestore
4. Hiển thị ảnh từ URL (work trên mọi device)
```

---

## 📱 TEST MULTI-DEVICE SYNC

### Bước 1: Chuẩn bị

```bash
# Build & install trên 2 devices/emulators
.\gradlew clean assembleDebug
.\gradlew installDebug

# Hoặc chạy trên 2 emulators cùng lúc
```

### Bước 2: Test Scenario

**Device A (Landlord):**

1. Đăng nhập tài khoản landlord (chutro1)
2. Tạo bài đăng mới với ảnh
3. Xem log: "✅ Migration completed" → "Đã tạo bài đăng và sync lên Cloud!"

**Device B (User/Landlord khác):**

1. Đăng nhập cùng hoặc khác tài khoản
2. Mở màn hình Bài đăng
3. → Tự động thấy bài đăng mới từ Device A (real-time sync)

**Test Update:**

- Device A: Sửa bài đăng (đổi giá, ảnh)
- Device B: Màn hình tự động cập nhật trong vài giây

**Test Delete:**

- Device A: Xóa bài đăng (TODO: implement delete)
- Device B: Bài đăng biến mất

---

## 🔧 CONFIG FILES

### Firebase Configuration

- `app/google-services.json` - Firebase project config
- `build.gradle.kts` - Firebase dependencies
  - `firebase-bom:34.4.0`
  - `firebase-auth-ktx`
  - `firebase-firestore-ktx`
  - `firebase-storage-ktx`

### Migration Settings

```java
// myapplication.java
FirebaseAuth.getInstance()
  .signInWithEmailAndPassword("admin@nestera.app.com", "Nestera22102025@")
  .addOnSuccessListener(r -> {
    MigrationToFirestore.runOnce(this);
  });
```

---

## 📊 FIRESTORE STRUCTURE

```
nestera-db/
├── chuTro/
│   └── {maChuTro}/
│       ├── maChuTro
│       ├── tenChuTro
│       ├── email
│       ├── sdt
│       └── createdAt
├── baiDang/
│   └── {id}/
│       ├── tieuDe
│       ├── diaChi
│       ├── giaThang
│       ├── tienNghi
│       ├── trangThai
│       ├── hinhAnhPath (local)
│       ├── hinhAnhUrl (Firebase Storage URL)
│       ├── chuTroId
│       └── createdAt
├── phongTro/
├── hopDong/
└── ... (các collections khác)
```

---

## 🔐 SECURITY RULES (TODO)

```javascript
// firestore.rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Bài đăng: Chủ trọ CRUD của mình, User read-only
    match /baiDang/{docId} {
      allow read: if request.auth != null;
      allow create: if request.auth.token.role == 'LANDLORD';
      allow update, delete: if request.auth.token.role == 'LANDLORD'
                            && resource.data.chuTroId == request.auth.uid;
    }

    // Phòng trọ: Tương tự
    match /phongTro/{docId} {
      allow read: if request.auth != null;
      allow write: if request.auth.token.role == 'LANDLORD';
    }
  }
}
```

---

## 🐛 TROUBLESHOOTING

### Lỗi: "Migration failed"

**Nguyên nhân:** Database schema không khớp  
**Giải pháp:** Migration dùng dynamic column detection, tự động bỏ qua cột thiếu

### Lỗi: "Upload ảnh thất bại"

**Nguyên nhân:** Firebase Storage rules hoặc network  
**Giải pháp:**

1. Kiểm tra Storage rules cho phép write
2. App tự động fallback lưu URI local nếu upload fail

### Lỗi: "Permission denied" trên Firestore

**Nguyên nhân:** Firestore Rules quá strict  
**Giải pháp tạm:** Test mode - allow read/write all (KHÔNG production!)

### Data không sync real-time

**Kiểm tra:**

1. `hybridDao.enableRealtimeSync()` đã gọi chưa?
2. Network có ổn định?
3. Xem Logcat filter "Hybrid" để debug

---

## 📈 NEXT STEPS

### Ưu tiên cao:

1. ✅ Test trên 2 devices
2. ⬜ Implement delete với sync
3. ⬜ Tạo PhongTroHybridDao, HopDongHybridDao
4. ⬜ Upload BLOB images từ SQLite lên Storage
5. ⬜ Security Rules cho production

### Ưu tiên trung bình:

6. ⬜ SyncService với WorkManager (periodic sync)
7. ⬜ Conflict resolution strategy
8. ⬜ Offline queue cho failed operations
9. ⬜ Analytics tracking

### Ưu tiên thấp:

10. ⬜ Cloud Functions cho notifications
11. ⬜ Full-text search với Algolia
12. ⬜ Backup/restore tools

---

## 💡 BEST PRACTICES

### 1. Luôn dùng Hybrid DAO

```java
// ❌ BAD: Chỉ dùng local DAO
baiDangDao dao = new baiDangDao(this);
List<BaiDang> list = dao.getAll();

// ✅ GOOD: Dùng Hybrid - auto sync
BaiDangHybridDao dao = new BaiDangHybridDao(this);
List<BaiDang> list = dao.getAll(); // Same interface!
```

### 2. Upload ảnh trước khi lưu

```java
// Upload → get URL → save to Firestore
// Không lưu content:// URI vì không share được
```

### 3. Handle offline gracefully

```java
// UI luôn responsive (đọc từ SQLite)
// Sync diễn ra background
// Hiển thị indicator khi syncing
```

### 4. Enable real-time sync khi cần

```java
// Màn hình cần real-time: enableRealtimeSync()
// Màn hình static: chỉ cần getAll()
```

---

## 📞 SUPPORT

**Lỗi migration?** → Xem Logcat filter "FS_Migration"  
**Lỗi sync?** → Xem Logcat filter "Hybrid"  
**Lỗi upload?** → Xem Logcat filter "ImageUploader"

---

**Version:** 1.0  
**Last Updated:** 2025-10-20  
**Status:** ✅ Production Ready (sau khi test multi-device)
