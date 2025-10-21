package com.example.nestera.Firebase;

import com.example.nestera.model.BaiDang;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho BaiDang - đọc/ghi Firestore
 * Thay thế baiDangDao dần dần
 */
public class BaiDangRepository extends FirestoreRepository<BaiDang> {

    public BaiDangRepository() {
        super("baiDang");
    }

    @Override
    protected BaiDang fromDocument(QueryDocumentSnapshot doc) {
        BaiDang b = new BaiDang();
        b.setId(doc.getLong("id") != null ? doc.getLong("id").intValue() : 0);
        b.setTieuDe(doc.getString("tieuDe"));
        b.setDiaChi(doc.getString("diaChi"));
        b.setGiaThang(doc.getLong("giaThang") != null ? doc.getLong("giaThang").intValue() : 0);
        b.setDienTich(doc.getDouble("dienTich") != null ? doc.getDouble("dienTich") : 0.0);
        b.setTienNghi(doc.getString("tienNghi"));
        b.setTrangThai(doc.getString("trangThai"));
        b.setHinhAnh(doc.getString("hinhAnhPath")); // Local path
        b.setMaPhong(doc.getLong("maPhong") != null ? doc.getLong("maPhong").intValue() : null);
        b.setChuTroId(doc.getString("chuTroId"));
        // Thêm URL từ Storage nếu có
        String imageUrl = doc.getString("hinhAnhUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            b.setHinhAnh(imageUrl); // Override bằng URL
        }
        return b;
    }

    @Override
    protected Map<String, Object> toDocument(BaiDang item) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", item.getId());
        map.put("tieuDe", item.getTieuDe());
        map.put("diaChi", item.getDiaChi());
        map.put("giaThang", item.getGiaThang());
        map.put("dienTich", item.getDienTich());
        map.put("tienNghi", item.getTienNghi());
        map.put("trangThai", item.getTrangThai());
        map.put("hinhAnhPath", item.getHinhAnh());
        map.put("maPhong", item.getMaPhong());
        map.put("chuTroId", item.getChuTroId());
        return map;
    }

    /**
     * Lấy bài đăng theo chủ trọ
     */
    public void getByChuTro(String chuTroId, FirestoreCallback<List<BaiDang>> callback) {
        db.collection(collectionName)
                .whereEqualTo("chuTroId", chuTroId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<BaiDang> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy bài đăng theo trạng thái (Còn trống, Đã thuê)
     */
    public void getByTrangThai(String trangThai, FirestoreCallback<List<BaiDang>> callback) {
        db.collection(collectionName)
                .whereEqualTo("trangThai", trangThai)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<BaiDang> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Tìm kiếm bài đăng theo từ khóa (title, address)
     */
    public void search(String keyword, FirestoreCallback<List<BaiDang>> callback) {
        // Firestore không support full-text search native
        // Phải lấy tất cả rồi filter trên client
        getAll(new FirestoreCallback<List<BaiDang>>() {
            @Override
            public void onSuccess(List<BaiDang> result) {
                List<BaiDang> filtered = new ArrayList<>();
                String query = keyword.toLowerCase();
                for (BaiDang b : result) {
                    String title = b.getTieuDe() != null ? b.getTieuDe().toLowerCase() : "";
                    String addr = b.getDiaChi() != null ? b.getDiaChi().toLowerCase() : "";
                    if (title.contains(query) || addr.contains(query)) {
                        filtered.add(b);
                    }
                }
                callback.onSuccess(filtered);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Lắng nghe real-time changes
     */
    public void listenForChanges(FirestoreCallback<List<BaiDang>> callback) {
        db.collection(collectionName)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<BaiDang> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            list.add(fromDocument(doc));
                        }
                        callback.onSuccess(list);
                    }
                });
    }
}
