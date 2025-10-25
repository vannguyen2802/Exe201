# Firestore Security Rules - Deployment Checklist

## ✅ Đã hoàn thành

- [x] Tạo file `firestore.rules` với đầy đủ phân quyền
- [x] Tạo file `firestore.indexes.json` cho composite queries
- [x] Tạo file `firebase.json` cấu hình project
- [x] Tạo script `deploy-firestore-rules.bat` để deploy
- [x] Tạo script `test-firestore-rules.bat` để test trong emulator
- [x] Tạo example Cloud Functions để set Custom Claims
- [x] Tạo tài liệu `FIRESTORE_SECURITY_RULES.md`

## 📋 Cần làm tiếp theo

### 1. Setup Firebase Project (5 phút)

- [ ] Mở [Firebase Console](https://console.firebase.google.com)
- [ ] Chọn project Nestera
- [ ] Vào **Project Settings** → **Service accounts**
- [ ] Tải xuống `google-services.json` (nếu chưa có)
- [ ] Copy vào `app/` folder

### 2. Install Firebase CLI (2 phút)

```bash
npm install -g firebase-tools
```

- [ ] Verify installation: `firebase --version`
- [ ] Login: `firebase login`

### 3. Initialize Firebase Project (3 phút)

```bash
cd D:\EXE201\Exe201\Nestera
firebase init
```

Chọn:

- [ ] Firestore (rules & indexes)
- [ ] Functions (nếu muốn dùng Cloud Functions)
- [ ] Emulators (Firestore, Functions)

Cấu hình:

- [ ] Rules file: `firestore.rules` (đã có)
- [ ] Indexes file: `firestore.indexes.json` (đã có)
- [ ] Functions language: JavaScript/TypeScript
- [ ] Emulator ports: Firestore (8080), UI (4000)

### 4. Test Rules in Emulator (10 phút)

```bash
# Chạy script test
test-firestore-rules.bat

# Hoặc chạy trực tiếp
firebase emulators:start --only firestore
```

- [ ] Mở Emulator UI: http://localhost:4000
- [ ] Vào **Firestore** tab
- [ ] Thử tạo/đọc/sửa/xóa documents
- [ ] Verify rules allow/deny đúng

**Test cases:**

- [ ] Admin có thể làm mọi thứ
- [ ] Chủ trọ chỉ thấy phòng của mình
- [ ] Người thuê chỉ thấy hợp đồng/hóa đơn của mình
- [ ] Guest không thể truy cập (chỉ baiDang public)

### 5. Update Android App (30 phút)

#### 5.1. Point app to emulator (development)

File: `app/src/main/java/.../MainActivity.java`

```java
// Trong onCreate() hoặc init method
FirebaseFirestore db = FirebaseFirestore.getInstance();
if (BuildConfig.DEBUG) {
    db.useEmulator("10.0.2.2", 8080); // Android Emulator
    // hoặc db.useEmulator("YOUR_IP", 8080); // Physical device
}
```

- [ ] Add emulator connection code
- [ ] Test CRUD operations
- [ ] Verify permission errors show correctly

#### 5.2. Handle permission errors

```java
// Example trong HybridDao
db.collection("phongTro").get()
    .addOnFailureListener(e -> {
        if (e instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException fse = (FirebaseFirestoreException) e;
            if (fse.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                Log.e(TAG, "Permission denied. User may not have required role.");
                // Show user-friendly error message
                Toast.makeText(context, "Bạn không có quyền truy cập", Toast.LENGTH_SHORT).show();
            }
        }
    });
```

- [ ] Add permission error handling in all HybridDao classes
- [ ] Show user-friendly error messages
- [ ] Log errors for debugging

### 6. Deploy Rules to Production (5 phút)

```bash
# Chạy script deploy
deploy-firestore-rules.bat

# Hoặc chạy trực tiếp
firebase deploy --only firestore:rules
firebase deploy --only firestore:indexes
```

- [ ] Deploy rules
- [ ] Deploy indexes
- [ ] Verify trong Firebase Console → Firestore → Rules
- [ ] Check indexes status: Console → Firestore → Indexes

### 7. Setup Cloud Functions (Optional - 60 phút)

#### 7.1. Initialize Functions

```bash
firebase init functions
```

- [ ] Chọn JavaScript hoặc TypeScript
- [ ] Install dependencies
- [ ] Copy code từ `cloud-functions-example.js` vào `functions/index.js`

#### 7.2. Deploy Functions

```bash
cd functions
npm install firebase-functions firebase-admin
cd ..
firebase deploy --only functions
```

- [ ] Deploy 4 functions:
  - `setUserRole` (auto on user create)
  - `setUserRoleManual` (admin manual set)
  - `onChuTroApprove` (update claims on approval)
  - `getUserRole` (debug function)

#### 7.3. Test Functions

- [ ] Create new user → verify role auto-set
- [ ] Admin approve chủ trọ → verify claims updated
- [ ] Test `getUserRole` function
- [ ] Monitor logs: `firebase functions:log`

### 8. Update Login Logic (30 phút)

#### 8.1. Get Custom Claims sau khi login

```java
FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
user.getIdToken(true)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            GetTokenResult result = task.getResult();
            Map<String, Object> claims = result.getClaims();

            String role = (String) claims.get("role");
            Integer approved = (Integer) claims.get("approved");
            Integer banned = (Integer) claims.get("banned");

            // Save to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("user11", MODE_PRIVATE);
            prefs.edit()
                .putString("role", role)
                .putInt("approved", approved != null ? approved : 0)
                .putInt("banned", banned != null ? banned : 0)
                .apply();

            // Navigate based on role
            if ("ADMIN".equals(role)) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            } else if ("LANDLORD".equals(role)) {
                if (banned == 1) {
                    Toast.makeText(this, "Tài khoản đã bị khóa", Toast.LENGTH_SHORT).show();
                } else if (approved != 1) {
                    Toast.makeText(this, "Tài khoản đang chờ duyệt", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else if ("TENANT".equals(role)) {
                startActivity(new Intent(this, TenantActivity.class));
            }
        }
    });
```

- [ ] Update `dangnhap.java` để lấy custom claims
- [ ] Save role, approved, banned vào SharedPreferences
- [ ] Navigate dựa trên role
- [ ] Show error nếu banned hoặc not approved

#### 8.2. Refresh token periodically

```java
// Trong MainActivity hoặc Base Activity
private void refreshAuthToken() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
        user.getIdToken(true) // force refresh
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Token refreshed with updated claims");
                }
            });
    }
}

// Call every 30 minutes hoặc khi app resume
@Override
protected void onResume() {
    super.onResume();
    refreshAuthToken();
}
```

- [ ] Add token refresh logic
- [ ] Refresh on app resume
- [ ] Refresh after 30-60 minutes

### 9. Test End-to-End (60 phút)

#### 9.1. Test Admin Role

- [ ] Login as Admin
- [ ] Xem danh sách chủ trọ pending
- [ ] Approve một chủ trọ
- [ ] Ban một chủ trọ
- [ ] Verify Firestore data updated
- [ ] Verify custom claims updated

#### 9.2. Test Landlord Role (Approved)

- [ ] Login as approved landlord
- [ ] CRUD phòng trọ → success
- [ ] CRUD người thuê → success
- [ ] CRUD hợp đồng → success
- [ ] Xem hóa đơn → success
- [ ] Xem sự cố → success
- [ ] Try to access other landlord's data → permission denied

#### 9.3. Test Landlord Role (Not Approved)

- [ ] Login as pending landlord
- [ ] Try to access app → blocked with message
- [ ] Verify can't read/write any data

#### 9.4. Test Tenant Role

- [ ] Login as tenant
- [ ] Xem hợp đồng của mình → success
- [ ] Xem hóa đơn của mình → success
- [ ] Tạo sự cố → success
- [ ] Thanh toán hóa đơn → success
- [ ] Try to access other tenant's data → permission denied
- [ ] Try to modify landlord data → permission denied

#### 9.5. Test Accountant Role

- [ ] Login as accountant
- [ ] Xem tất cả hóa đơn → success
- [ ] Tạo hóa đơn → success
- [ ] Xem thống kê → success
- [ ] Try to modify other data → permission denied

#### 9.6. Test Guest (Not Logged In)

- [ ] Open app without login
- [ ] Xem bài đăng công khai → success
- [ ] Try to access other pages → permission denied or redirect to login

### 10. Monitor & Optimize (Ongoing)

#### 10.1. Monitor Firestore Usage

- [ ] Vào Firebase Console → Firestore → Usage
- [ ] Check số lượng reads/writes per day
- [ ] Identify expensive queries
- [ ] Optimize queries với indexes

#### 10.2. Monitor Errors

- [ ] Check Firebase Console → Firestore → Error logs
- [ ] Monitor permission denied errors
- [ ] Fix rules nếu có false positives/negatives

#### 10.3. Monitor Costs

- [ ] Check Firebase Console → Usage and billing
- [ ] Set budget alerts
- [ ] Optimize queries để giảm costs

### 11. Documentation (30 phút)

- [ ] Document phân quyền cho team
- [ ] Tạo user guide: "Quyền hạn của từng role"
- [ ] Update README.md với security info
- [ ] Create troubleshooting guide

## 🚨 Critical Security Checks

### Before Production Deployment:

- [ ] **NEVER** store plaintext passwords in Firestore
  - Hash với bcrypt/Argon2 trong app
  - Hoặc dùng Firebase Authentication API
- [ ] **ENCRYPT** sensitive data:
  - CCCD (số CMND/CCCD)
  - STK (số tài khoản ngân hàng)
  - Địa chỉ chi tiết
- [ ] **VALIDATE** all user input trong app
  - XSS prevention
  - SQL injection (không áp dụng cho Firestore nhưng cẩn thận)
  - Size limits cho images/files
- [ ] **RATE LIMITING** (nếu có Cloud Functions):
  - Limit số lần register/login per IP
  - Limit số lần tạo hóa đơn/sự cố
- [ ] **BACKUP** strategy:
  - Enable Firestore auto-export
  - Schedule daily/weekly backups
  - Test restore procedure

## 📊 Success Metrics

### After deployment, verify:

- [ ] **Security**: No unauthorized access in logs (30 days)
- [ ] **Performance**: Average query time < 500ms
- [ ] **Costs**: Firestore costs within budget
- [ ] **User Experience**: Permission errors < 1% of requests
- [ ] **Availability**: 99.9% uptime

## 🆘 Rollback Plan

If something goes wrong:

```bash
# Rollback rules to previous version
firebase deploy --only firestore:rules --version <previous_version>

# Disable rules temporarily (DANGEROUS - allow all)
# Only use in emergency
```

- [ ] Document current production rules version
- [ ] Keep backup of working rules
- [ ] Have admin access ready for emergency rollback

## 📞 Support Contacts

- Firebase Support: https://firebase.google.com/support
- Firestore Documentation: https://firebase.google.com/docs/firestore
- Security Rules Reference: https://firebase.google.com/docs/rules
- Team Lead: [Your Name/Email]

---

**Last Updated:** 2025-10-20
**Status:** ✅ Rules Created | ⏳ Awaiting Deployment
