# Firestore Security Rules - Nestera App

## Tổng quan

File `firestore.rules` định nghĩa các quyền truy cập (Security Rules) cho Firestore Database của ứng dụng Nestera.

## Cấu trúc phân quyền

### 1. Vai trò (Roles)

- **ADMIN**: Quản trị viên - toàn quyền trên tất cả collections
- **LANDLORD**: Chủ trọ - quản lý phòng trọ, người thuê, hợp đồng của mình
- **TENANT**: Người thuê - xem hợp đồng, hóa đơn, tạo sự cố
- **ACCOUNTANT**: Kế toán - xem và quản lý hóa đơn

### 2. Collections và Quyền truy cập

#### ChuTro (Landlord)

- **Read**: Admin (all), Chủ trọ (own data), Kế toán (approved only)
- **Create**: Public (đăng ký mới - pending approval)
- **Update**: Admin (all), Chủ trọ (own data, không thay đổi approved/banned)
- **Delete**: Admin only

#### NguoiThue (Tenant)

- **Read**: Admin, Chủ trọ (own tenants), Người thuê (own data), Kế toán
- **Create**: Admin, Chủ trọ (own tenants)
- **Update**: Admin, Chủ trọ (own tenants), Người thuê (password only)
- **Delete**: Admin, Chủ trọ (own tenants)

#### KeToan (Accountant)

- **Read**: Admin, Kế toán (own data), Chủ trọ (own accountants)
- **Create**: Admin, Chủ trọ (own accountants)
- **Update**: Admin, Chủ trọ (own accountants), Kế toán (password only)
- **Delete**: Admin, Chủ trọ (own accountants)

#### LoaiPhong (Room Type)

- **Read**: Authenticated users
- **Create/Update**: Admin, Chủ trọ (own types)
- **Delete**: Admin, Chủ trọ (own types)

#### PhongTro (Room)

- **Read**: Authenticated users
- **Create/Update**: Admin, Chủ trọ (own rooms)
- **Delete**: Admin, Chủ trọ (own rooms)

#### HopDong (Contract)

- **Read**: Admin, Chủ trọ (own contracts), Người thuê (own contracts), Kế toán
- **Create/Update**: Admin, Chủ trọ (own contracts)
- **Delete**: Admin, Chủ trọ (own contracts)

#### HoaDon (Invoice)

- **Read**: Admin, Chủ trọ (own invoices), Người thuê (own invoices), Kế toán
- **Create**: Admin, Chủ trọ (own invoices), Kế toán
- **Update**: Admin, Chủ trọ (own invoices), Kế toán, Người thuê (payment status only)
- **Delete**: Admin, Chủ trọ (own invoices)

#### SuCo (Issue)

- **Read**: Admin, Chủ trọ (own issues), Người thuê (own issues)
- **Create**: Admin, Người thuê (own issues)
- **Update**: Admin, Chủ trọ (change status), Người thuê (content only)
- **Delete**: Admin, Chủ trọ (own issues)

#### BaiDang (Post/Listing)

- **Read**: Public (anyone)
- **Create/Update**: Admin, Chủ trọ (own posts)
- **Delete**: Admin, Chủ trọ (own posts)

#### NganHang (Bank Account)

- **Read**: Admin, Chủ trọ (own accounts)
- **Create/Update/Delete**: Admin, Chủ trọ (own accounts)

#### ThongBao (Notification)

- **Read**: Admin, User (own notifications)
- **Create**: Admin, Chủ trọ
- **Update**: Admin, User (mark as read)
- **Delete**: Admin, User (own notifications)

#### ThongKe (Statistics)

- **Read**: Admin, Chủ trọ (own stats), Kế toán
- **Write**: Backend only (Cloud Functions)

## Helper Functions

### isSignedIn()

Kiểm tra user đã đăng nhập chưa

### isAdmin()

Kiểm tra user có role ADMIN

### isLandlord()

Kiểm tra user có role LANDLORD

### isTenant()

Kiểm tra user có role TENANT

### isAccountant()

Kiểm tra user có role ACCOUNTANT

### isOwner(maChuTro)

Kiểm tra user có phải chủ sở hữu document không (theo maChuTro)

### isTenantOf(maNguoiThue)

Kiểm tra user có phải người thuê của document không (theo maNguoiThue)

### isApproved(data)

Kiểm tra document đã được approve chưa

### isBanned(data)

Kiểm tra document có bị ban không

## Deployment

### Bước 1: Cài đặt Firebase CLI

```bash
npm install -g firebase-tools
```

### Bước 2: Login Firebase

```bash
firebase login
```

### Bước 3: Khởi tạo Firebase Project (nếu chưa có)

```bash
cd D:\EXE201\Exe201\Nestera
firebase init firestore
```

- Chọn project: `nestera-xxxx`
- Firestore Rules file: `firestore.rules`
- Firestore Indexes file: `firestore.indexes.json`

### Bước 4: Deploy Rules

```bash
firebase deploy --only firestore:rules
```

### Bước 5: Verify trên Console

- Mở [Firebase Console](https://console.firebase.google.com)
- Chọn project Nestera
- Vào **Firestore Database** → **Rules**
- Kiểm tra rules đã được deploy

## Testing Security Rules

### Test trong Firebase Console

1. Vào **Firestore Database** → **Rules** → **Rules Playground**
2. Chọn collection và operation (read/write)
3. Nhập auth token hoặc simulate user
4. Kiểm tra kết quả allow/deny

### Test trong Emulator (Recommended)

```bash
# Start Firestore Emulator
firebase emulators:start --only firestore

# Trong Android app, thay đổi Firestore endpoint
FirebaseFirestore db = FirebaseFirestore.getInstance();
db.useEmulator("10.0.2.2", 8080); // Android Emulator
// hoặc
db.useEmulator("localhost", 8080); // Physical device
```

## Lưu ý bảo mật

### 1. Custom Claims cho Roles

Để sử dụng `request.auth.token.role`, cần set Custom Claims khi user đăng nhập:

**Backend (Cloud Functions hoặc Admin SDK):**

```javascript
admin.auth().setCustomUserClaims(uid, { role: "LANDLORD" });
```

**Android App (sau khi login):**

```java
FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
user.getIdToken(true)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            String idToken = task.getResult().getToken();
            // Token này chứa custom claims (role)
        }
    });
```

### 2. Validate dữ liệu

Rules đã có validate cơ bản:

- Người thuê chỉ thay đổi password, không thay đổi thông tin khác
- Chủ trọ không tự approve hoặc ban tài khoản mình
- User chỉ tạo document với maChuTro/maNguoiThue của mình

### 3. Rate Limiting

Firebase có built-in rate limiting, nhưng nên thêm:

- App-level throttling cho write operations
- Captcha cho registration
- Cloud Functions để validate complex logic

### 4. Sensitive Data

Một số trường cần encrypt trước khi ghi Firestore:

- `matKhau` / `matKhauNT` / `matKhauKT` (mật khẩu)
- `CCCD` (số CMND/CCCD)
- `STK` / `soTaiKhoan` (số tài khoản ngân hàng)

**Khuyến nghị:**

- Hash password bằng bcrypt/Argon2 TRƯỚC KHI ghi Firestore
- Encrypt CCCD, STK bằng AES-256
- Không bao giờ gửi plaintext password qua network

## Migration Plan

### Phase 1: Deploy Rules (✅ Completed)

- [x] Tạo firestore.rules
- [x] Test rules trong emulator
- [ ] Deploy to production

### Phase 2: Update App Authentication

- [ ] Implement Custom Claims trong Cloud Functions
- [ ] Update login logic để lấy role từ token
- [ ] Test phân quyền trong app

### Phase 3: Test End-to-End

- [ ] Test Admin: duyệt chủ trọ, ban user
- [ ] Test Landlord: CRUD phòng, hợp đồng, hóa đơn
- [ ] Test Tenant: xem hóa đơn, tạo sự cố, thanh toán
- [ ] Test Accountant: xem/quản lý hóa đơn

### Phase 4: Monitor & Optimize

- [ ] Monitor Firestore usage/costs
- [ ] Optimize queries với indexes
- [ ] Add Cloud Functions cho complex validation

## Troubleshooting

### Lỗi "Missing or insufficient permissions"

**Nguyên nhân:** User không có quyền truy cập document
**Giải pháp:**

1. Kiểm tra `request.auth.uid` có khớp với `maChuTro`/`maNguoiThue` không
2. Kiểm tra Custom Claims đã được set chưa (`request.auth.token.role`)
3. Check Rules Playground để debug

### Lỗi "PERMISSION_DENIED" khi write

**Nguyên nhân:** Rules deny operation
**Giải pháp:**

1. Kiểm tra `request.resource.data` có đúng format không
2. Kiểm tra user có quyền write không (isOwner, isAdmin, etc.)
3. Check field validation (ví dụ: `approved == 0` khi create)

### Rules không apply sau deploy

**Nguyên nhân:** Cache hoặc deploy chưa hoàn tất
**Giải pháp:**

1. Đợi 1-2 phút để rules propagate
2. Clear app cache và refresh token
3. Verify trong Console: Firestore → Rules → Published

## Resources

- [Firestore Security Rules Documentation](https://firebase.google.com/docs/firestore/security/get-started)
- [Security Rules Testing](https://firebase.google.com/docs/rules/unit-tests)
- [Custom Claims Guide](https://firebase.google.com/docs/auth/admin/custom-claims)
- [Best Practices](https://firebase.google.com/docs/firestore/security/rules-conditions)
