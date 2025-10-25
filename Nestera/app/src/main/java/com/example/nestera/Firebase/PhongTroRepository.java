package com.example.nestera.Firebase;

import com.example.nestera.model.PhongTro;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho PhongTro - đọc/ghi Firestore
 */
public class PhongTroRepository extends FirestoreRepository<PhongTro> {

    public PhongTroRepository() {
        super("phongTro");
    }

    @Override
    protected PhongTro fromDocument(QueryDocumentSnapshot doc) {
        PhongTro p = new PhongTro();
        p.setMaPhong(doc.getLong("maPhong") != null ? doc.getLong("maPhong").intValue() : 0);
        p.setMaLoai(doc.getLong("maLoai") != null ? doc.getLong("maLoai").intValue() : 0);
        p.setTenPhong(doc.getString("tenPhong"));
        p.setGia(doc.getLong("giaTien") != null ? doc.getLong("giaTien").intValue() : 0);
        p.setTienNghi(doc.getString("tienNghi"));
        p.setTrangThai(doc.getLong("trangThai") != null ? doc.getLong("trangThai").intValue() : 0);
        p.setDiaChi(doc.getString("diaChi"));
        
        // ImagePath có thể là local hoặc URL từ Storage
        String imagePath = doc.getString("imagePath");
        String mainImageUrl = doc.getString("mainImageUrl");
        if (mainImageUrl != null && !mainImageUrl.isEmpty()) {
            p.setImagePath(mainImageUrl); // Ưu tiên URL từ Storage
        } else if (imagePath != null) {
            p.setImagePath(imagePath); // Fallback local path
        }
        
        return p;
    }

    @Override
    protected Map<String, Object> toDocument(PhongTro item) {
        Map<String, Object> map = new HashMap<>();
        map.put("maPhong", item.getMaPhong());
        map.put("maLoai", item.getMaLoai());
        map.put("tenPhong", item.getTenPhong());
        map.put("giaTien", item.getGia());
        map.put("tienNghi", item.getTienNghi());
        map.put("trangThai", item.getTrangThai());
        map.put("diaChi", item.getDiaChi());
        map.put("imagePath", item.getImagePath());
        return map;
    }

    /**
     * Lấy phòng theo loại phòng
     */
    public void getByLoaiPhong(int maLoai, FirestoreCallback<List<PhongTro>> callback) {
        db.collection(collectionName)
                .whereEqualTo("maLoai", maLoai)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<PhongTro> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy phòng theo trạng thái (0: trống, 1: đã thuê)
     */
    public void getByTrangThai(int trangThai, FirestoreCallback<List<PhongTro>> callback) {
        db.collection(collectionName)
                .whereEqualTo("trangThai", trangThai)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<PhongTro> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy phòng trống (chưa thuê)
     */
    public void getAvailableRooms(FirestoreCallback<List<PhongTro>> callback) {
        getByTrangThai(0, callback);
    }

    /**
     * Tìm kiếm phòng theo tên hoặc địa chỉ
     */
    public void search(String keyword, FirestoreCallback<List<PhongTro>> callback) {
        getAll(new FirestoreCallback<List<PhongTro>>() {
            @Override
            public void onSuccess(List<PhongTro> result) {
                List<PhongTro> filtered = new ArrayList<>();
                String query = keyword.toLowerCase();
                for (PhongTro p : result) {
                    String tenPhong = p.getTenPhong() != null ? p.getTenPhong().toLowerCase() : "";
                    String diaChi = p.getDiaChi() != null ? p.getDiaChi().toLowerCase() : "";
                    if (tenPhong.contains(query) || diaChi.contains(query)) {
                        filtered.add(p);
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
    public void listenForChanges(FirestoreCallback<List<PhongTro>> callback) {
        db.collection(collectionName)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<PhongTro> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            list.add(fromDocument(doc));
                        }
                        callback.onSuccess(list);
                    }
                });
    }
}
