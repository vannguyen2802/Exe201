# 🔄 MIGRATION SCRIPT - ACTIVITIES TO HYBRID DAO

## 📋 Activities Cần Migrate

### ✅ Đã Migrate

- [x] BaiDangActivity.java
- [x] BaiDangDetailActivity.java

### ⏳ Cần Migrate

#### 1️⃣ hopDong_Activity.java

```java
// Import
import com.example.nestera.Firebase.HopDongHybridDao;
import com.example.nestera.Firebase.NguoiThueHybridDao;
import com.example.nestera.Firebase.PhongTroHybridDao;

// Declaration
HopDongHybridDao hybridDao;
NguoiThueHybridDao hybridDao_nt;
PhongTroHybridDao hybridDao_pt;

// onCreate()
hybridDao = new HopDongHybridDao(this);
hybridDao.enableRealtimeSync();
list = (ArrayList<HopDong>) hybridDao.getAll();

// Replace all dao. → hybridDao.
// dao.getHopDongByMaPhong() → hybridDao.getByMaPhong()
// dao.insert() → hybridDao.insert()
// dao.getAll() → hybridDao.getAll()
```

#### 2️⃣ nguoiThue_Activity.java

```java
// Import
import com.example.nestera.Firebase.NguoiThueHybridDao;

// Declaration
NguoiThueHybridDao hybridDao;

// onCreate()
hybridDao = new NguoiThueHybridDao(this);
hybridDao.enableRealtimeSync();
list = (ArrayList<NguoiThue>) hybridDao.getAll();

// Replace
// dao.getAll() → hybridDao.getAll()
// dao.insert() → hybridDao.insert()
// dao.update() → hybridDao.update()
// dao.delete() → hybridDao.delete()
```

#### 3️⃣ hoaDon_Activity.java

```java
// Import
import com.example.nestera.Firebase.HoaDonHybridDao;

// Declaration
HoaDonHybridDao hybridDao;

// onCreate()
hybridDao = new HoaDonHybridDao(this);
hybridDao.enableRealtimeSync();
list = (ArrayList<HoaDon>) hybridDao.getAll();

// Replace
// dao.getAllUnpaid() → hybridDao.getUnpaidBills()
// dao.getByMonth() → hybridDao.getByMonth()
// dao.insert() → hybridDao.insert()
```

#### 4️⃣ suCo_Activity.java

```java
// Import
import com.example.nestera.Firebase.SuCoHybridDao;

// Declaration
SuCoHybridDao hybridDao;

// onCreate()
hybridDao = new SuCoHybridDao(this);
hybridDao.enableRealtimeSync();
list = (ArrayList<SuCo>) hybridDao.getAll();

// Replace
// dao.getAllPending() → hybridDao.getPendingIssues()
// dao.getByMaPhong() → hybridDao.getByMaPhong()
// dao.update() → hybridDao.update()
```

#### 5️⃣ loaiPhong_Activity.java

```java
// Import
import com.example.nestera.Firebase.LoaiPhongHybridDao;

// Declaration
LoaiPhongHybridDao hybridDao;

// onCreate()
hybridDao = new LoaiPhongHybridDao(this);
hybridDao.enableRealtimeSync();
list = (ArrayList<LoaiPhong>) hybridDao.getAll();
```

#### 6️⃣ MainActivity.java

```java
// Check for nguoiThueDao usage
// Replace with NguoiThueHybridDao where applicable
```

#### 7️⃣ Fragments

- frg_doimatkhau.java → NguoiThueHybridDao
- frg_thongkebieudo.java → HoaDonHybridDao
- frg_thongtintaikhoan.java → NguoiThueHybridDao

---

## 🔧 AUTOMATED MIGRATION

Chạy script này để replace tự động:

### Step 1: Backup

```bash
git add .
git commit -m "Before HybridDao migration"
```

### Step 2: Find & Replace

#### For hopDong_Activity.java

```
Find: hopDongDao dao;
Replace: HopDongHybridDao hybridDao;

Find: dao = new hopDongDao
Replace: hybridDao = new HopDongHybridDao

Find: dao\.
Replace: hybridDao.
```

#### For nguoiThue_Activity.java

```
Find: nguoiThueDao dao;
Replace: NguoiThueHybridDao hybridDao;

Find: dao = new nguoiThueDao
Replace: hybridDao = new NguoiThueHybridDao

Find: dao\.
Replace: hybridDao.
```

#### For hoaDon_Activity.java

```
Find: hoaDonDao dao;
Replace: HoaDonHybridDao hybridDao;

Find: dao = new hoaDonDao
Replace: hybridDao = new HoaDonHybridDao

Find: dao\.getAllUnpaid
Replace: hybridDao.getUnpaidBills
```

#### For suCo_Activity.java

```
Find: suCoDao dao;
Replace: SuCoHybridDao hybridDao;

Find: dao = new suCoDao
Replace: hybridDao = new SuCoHybridDao

Find: dao\.getAllPending
Replace: hybridDao.getPendingIssues
```

### Step 3: Add Imports

Add to each file:

```java
import com.example.nestera.Firebase.XxxHybridDao;
```

### Step 4: Add Real-time Sync

Add after initialization in onCreate():

```java
hybridDao.enableRealtimeSync();
```

---

## ⚠️ SPECIAL CASES

### Case 1: Multiple DAOs in Same File

Example: hopDong_Activity.java uses hopDongDao, nguoiThueDao, phongTroDao

```java
// Declare all
HopDongHybridDao hybridDao;
NguoiThueHybridDao hybridDao_nt;
PhongTroHybridDao hybridDao_pt;

// Initialize all
hybridDao = new HopDongHybridDao(this);
hybridDao_nt = new NguoiThueHybridDao(this);
hybridDao_pt = new PhongTroHybridDao(this);

// Enable sync
hybridDao.enableRealtimeSync();
hybridDao_nt.enableRealtimeSync();
hybridDao_pt.enableRealtimeSync();
```

### Case 2: Custom Methods

Some DAO methods might not exist in HybridDao:

```java
// OLD
int count = dao.getActiveCount();

// NEW - Use local DAO temporarily
int count = new hopDongDao(this).getActiveCount();

// TODO: Add method to HybridDao later
```

### Case 3: Transaction Operations

```java
// OLD
db.beginTransaction();
dao.insert(item1);
dao.insert(item2);
db.setTransactionSuccessful();
db.endTransaction();

// NEW - HybridDao handles each insert independently
hybridDao.insert(item1);
hybridDao.insert(item2);
// Firestore will sync both in background
```

---

## 🧪 TESTING

After migration, test:

1. **Basic CRUD**

   ```
   - Tạo mới → Check SQLite + Firestore
   - Sửa → Check sync
   - Xóa → Check both databases
   ```

2. **Offline Mode**

   ```
   - Turn off WiFi
   - CRUD operations should still work (SQLite)
   - Turn on WiFi → Should sync to Firestore
   ```

3. **Multi-Device**

   ```
   - Device A: Tạo hợp đồng
   - Device B: Should see new contract (real-time)
   ```

4. **Logcat**

   ```bash
   adb logcat | grep -i "Hybrid"

   # Should see:
   # "Synced X records from Firestore"
   # "HopDong synced to Firestore"
   # "Real-time update: X contracts"
   ```

---

## 📝 MIGRATION CHECKLIST

- [ ] hopDong_Activity.java
  - [ ] Import HybridDao classes
  - [ ] Replace dao variables
  - [ ] Replace dao instantiation
  - [ ] Replace dao method calls
  - [ ] Add enableRealtimeSync()
  - [ ] Test CRUD operations
- [ ] nguoiThue_Activity.java

  - [ ] Import NguoiThueHybridDao
  - [ ] Replace dao → hybridDao
  - [ ] Add enableRealtimeSync()
  - [ ] Test

- [ ] hoaDon_Activity.java

  - [ ] Import HoaDonHybridDao
  - [ ] Replace dao → hybridDao
  - [ ] Replace getAllUnpaid → getUnpaidBills
  - [ ] Add enableRealtimeSync()
  - [ ] Test

- [ ] suCo_Activity.java

  - [ ] Import SuCoHybridDao
  - [ ] Replace dao → hybridDao
  - [ ] Replace getAllPending → getPendingIssues
  - [ ] Add enableRealtimeSync()
  - [ ] Test

- [ ] loaiPhong_Activity.java

  - [ ] Import LoaiPhongHybridDao
  - [ ] Replace dao → hybridDao
  - [ ] Add enableRealtimeSync()
  - [ ] Test

- [ ] Fragments

  - [ ] frg_doimatkhau.java
  - [ ] frg_thongkebieudo.java
  - [ ] frg_thongtintaikhoan.java

- [ ] Adapters (if needed)
  - [ ] NguoiThue_Adapter.java
  - [ ] Check for dao usage in adapters

---

## 🎯 BENEFITS AFTER MIGRATION

✅ **Offline-first**: UI instant, no loading spinners  
✅ **Real-time sync**: Multi-device collaboration  
✅ **Cloud backup**: Data safe in Firestore  
✅ **Scalability**: Ready for web/desktop apps sharing same DB

---

## 🚨 ROLLBACK PLAN

If migration fails:

```bash
git reset --hard HEAD~1
# Quay lại commit trước migration
```

Or keep both:

```java
// Use HybridDao for new features
HopDongHybridDao hybridDao = new HopDongHybridDao(this);

// Keep old DAO for legacy code (temporary)
hopDongDao oldDao = new hopDongDao(this);
```

---

## 📞 Need Help?

Check:

1. HYBRID_DAO_GUIDE.md - Detailed examples
2. MIGRATION_EXAMPLES.java - Before/After code
3. Logcat with filter "Hybrid"
4. Firestore Console - Verify data sync

Good luck! 🍀
