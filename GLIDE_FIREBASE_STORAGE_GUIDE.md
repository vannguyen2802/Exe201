# 📸 Hướng dẫn Load Ảnh từ Firebase Storage với Glide

## ✅ Đã hoàn thành

- ✅ Thêm Glide dependency vào `build.gradle.kts`
- ✅ Tạo Firebase Storage Security Rules (`storage.rules`)
- ✅ Cập nhật `firebase.json` để deploy storage rules
- ✅ Cập nhật `deploy-firestore-rules.bat` để deploy cả storage rules

## 📖 Cách sử dụng Glide để load ảnh từ Firebase Storage

### 1. Load ảnh từ URL vào ImageView

```java
import com.bumptech.glide.Glide;

// Load ảnh từ Firebase Storage URL
String imageUrl = "https://firebasestorage.googleapis.com/v0/b/...";
ImageView imageView = findViewById(R.id.imageView);

Glide.with(context)
    .load(imageUrl)
    .placeholder(R.drawable.placeholder) // Ảnh hiển thị khi đang load
    .error(R.drawable.error_image)       // Ảnh hiển thị khi lỗi
    .into(imageView);
```

### 2. Load ảnh trong RecyclerView Adapter

```java
public class BaiDangAdapter extends RecyclerView.Adapter<BaiDangAdapter.ViewHolder> {

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaiDang item = list.get(position);

        // Load ảnh từ Firebase Storage
        if (item.getHinhAnh() != null && !item.getHinhAnh().isEmpty()) {
            String firstImageUrl = item.getHinhAnh().split(";")[0]; // Lấy ảnh đầu tiên

            Glide.with(holder.itemView.getContext())
                .load(firstImageUrl)
                .placeholder(R.drawable.placeholder_room)
                .error(R.drawable.ic_image_error)
                .centerCrop()
                .into(holder.ivImage);
        }
    }
}
```

### 3. Load nhiều ảnh (Gallery/ViewPager)

```java
// Split URLs từ string "url1;url2;url3"
String hinhAnh = baiDang.getHinhAnh(); // "https://...;https://...;https://..."
String[] imageUrls = hinhAnh.split(";");

// Load vào ViewPager/RecyclerView
for (String url : imageUrls) {
    Glide.with(context)
        .load(url)
        .into(imageView);
}
```

### 4. Load ảnh với cache

```java
import com.bumptech.glide.load.engine.DiskCacheStrategy;

Glide.with(context)
    .load(imageUrl)
    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache cả ảnh gốc và ảnh đã resize
    .into(imageView);
```

### 5. Load ảnh tròn (Circle)

```java
import com.bumptech.glide.request.RequestOptions;

RequestOptions options = new RequestOptions()
    .circleCrop()
    .placeholder(R.drawable.default_avatar)
    .error(R.drawable.default_avatar);

Glide.with(context)
    .load(imageUrl)
    .apply(options)
    .into(imageView);
```

## 🔧 Sửa code hiện tại để hiển thị ảnh từ Firebase

### Ví dụ 1: BaiDangActivity - Hiển thị danh sách bài đăng

**File cần sửa:** `BaiDangAdapter.java` hoặc custom adapter

```java
// TRƯỚC (load từ local URI - không work với Firebase URL):
if (item.getHinhAnh() != null) {
    imageView.setImageURI(Uri.parse(item.getHinhAnh()));
}

// SAU (load từ Firebase Storage URL):
if (item.getHinhAnh() != null && !item.getHinhAnh().isEmpty()) {
    String firstImage = item.getHinhAnh().split(";")[0];
    Glide.with(context)
        .load(firstImage)
        .placeholder(R.drawable.placeholder_room)
        .centerCrop()
        .into(imageView);
}
```

### Ví dụ 2: PhongTroActivity - Hiển thị phòng trọ

```java
// Load ảnh phòng trọ từ Firebase Storage
if (phongTro.getImageUrl() != null && !phongTro.getImageUrl().isEmpty()) {
    Glide.with(this)
        .load(phongTro.getImageUrl())
        .placeholder(R.drawable.placeholder_room)
        .error(R.drawable.ic_room_error)
        .into(ivRoomImage);
} else {
    // Fallback: Load từ SQLite BLOB (nếu chưa migrate)
    byte[] imageBytes = phongTro.getImageBytes();
    if (imageBytes != null) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        ivRoomImage.setImageBitmap(bitmap);
    }
}
```

## 🚨 Sửa lỗi thường gặp

### Lỗi 1: Ảnh không hiển thị (trắng/placeholder)

**Nguyên nhân:**

- Firebase Storage Rules chặn download
- URL không đúng format
- Thiếu internet permission

**Giải pháp:**

1. **Deploy Storage Rules:**

```bash
firebase deploy --only storage
```

2. **Check AndroidManifest.xml:**

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

3. **Check Firebase Console:**

- Vào Firebase Console → Storage → Files
- Verify ảnh đã upload thành công
- Click ảnh → Copy URL → paste vào browser để test

### Lỗi 2: Glide crash với "You cannot start a load on a not yet attached View"

**Giải pháp:**

```java
// Kiểm tra fragment/activity còn active không
if (!((Activity) context).isDestroyed()) {
    Glide.with(context).load(url).into(imageView);
}
```

### Lỗi 3: Memory leak khi load nhiều ảnh

**Giải pháp:**

```java
// Clear Glide cache khi cần
Glide.get(context).clearMemory(); // Gọi trên UI thread

// Clear disk cache
new Thread(() -> {
    Glide.get(context).clearDiskCache();
}).start();
```

## 📱 Test trên thiết bị vật lý

### Bước 1: Deploy Storage Rules

```bash
cd D:\EXE201\Exe201\Nestera
firebase deploy --only storage
```

### Bước 2: Rebuild & Install

```bash
.\gradlew clean assembleDebug
.\gradlew installDebug
```

### Bước 3: Test upload & hiển thị

1. Mở app → Thêm phòng trọ/bài đăng
2. Chọn ảnh → Upload
3. Xem logcat: "Upload success: https://..."
4. Quay lại danh sách → Ảnh hiển thị từ Firebase Storage

### Bước 4: Verify trong Firebase Console

1. Firebase Console → Storage → Files
2. Thấy folder `phongTro/`, `baiDang/`, etc.
3. Click ảnh → Verify có download URL

## 🔐 Firebase Storage Rules - Đã deploy

```
rules_version = '2';

service firebase.storage {
  match /b/{bucket}/o {
    // Ảnh phòng trọ & bài đăng - Public read
    match /phongTro/{imageId} {
      allow read: if true;
      allow write: if request.auth.token.role == "ADMIN" ||
                     request.auth.token.role == "LANDLORD";
    }

    match /baiDang/{imageId} {
      allow read: if true;
      allow write: if request.auth.token.role == "ADMIN" ||
                     request.auth.token.role == "LANDLORD";
    }

    // Hợp đồng, hóa đơn - Authenticated only
    match /hopDong/{imageId} {
      allow read: if request.auth != null;
      allow write: if request.auth.token.role == "ADMIN" ||
                     request.auth.token.role == "LANDLORD";
    }

    match /hoaDon/{imageId} {
      allow read: if request.auth != null;
      allow write: if request.auth.token.role == "ADMIN" ||
                     request.auth.token.role == "LANDLORD" ||
                     request.auth.token.role == "ACCOUNTANT";
    }

    // Sự cố - Authenticated users
    match /suCo/{imageId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 📝 Checklist

- [ ] Deploy Storage Rules: `firebase deploy --only storage`
- [ ] Rebuild app với Glide dependency
- [ ] Sửa code load ảnh: `setImageURI()` → `Glide.with().load()`
- [ ] Test upload ảnh trên thiết bị thật (có internet)
- [ ] Verify ảnh hiển thị đúng trong danh sách
- [ ] Check Firebase Console → Storage → Files có ảnh
- [ ] Test offline: Ảnh có cache và hiển thị không

## 🎯 Ưu điểm của Glide + Firebase Storage

✅ **Performance:**

- Auto cache ảnh (memory + disk)
- Resize ảnh tự động theo size ImageView
- Load ảnh background thread (không lag UI)

✅ **Multi-device sync:**

- Ảnh lưu trên cloud → mọi device đều thấy
- Không tốn dung lượng local storage

✅ **Scalability:**

- Firebase Storage auto scale theo lượng user
- CDN global → load ảnh nhanh mọi nơi

✅ **Offline support:**

- Glide cache ảnh đã load → offline vẫn hiển thị

---

**Tóm lại:**

1. Deploy Storage Rules
2. Rebuild app
3. Sửa code load ảnh: `Glide.with(context).load(url).into(imageView)`
4. Test trên thiết bị thật có internet
