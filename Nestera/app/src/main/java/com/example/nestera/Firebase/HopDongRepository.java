package com.example.nestera.Firebase;

import com.example.nestera.model.HopDong;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho HopDong - đọc/ghi Firestore
 */
public class HopDongRepository extends FirestoreRepository<HopDong> {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public HopDongRepository() {
        super("hopDong");
    }

    @Override
    protected HopDong fromDocument(QueryDocumentSnapshot doc) {
        HopDong h = new HopDong();
        h.setMaHopDong(doc.getLong("maHopDong") != null ? doc.getLong("maHopDong").intValue() : 0);
        h.setSdt(doc.getString("sdt"));
        h.setCCCD(doc.getLong("CCCD") != null ? doc.getLong("CCCD").intValue() : 0);
        h.setThuongTru(doc.getString("thuongTru"));
        
        // Parse date
        String ngayKyStr = doc.getString("ngayKy");
        if (ngayKyStr != null) {
            try {
                h.setNgayKy(sdf.parse(ngayKyStr));
            } catch (Exception e) {
                h.setNgayKy(new Date());
            }
        }
        
        h.setThoiHan(doc.getLong("thoiHan") != null ? doc.getLong("thoiHan").intValue() : 0);
        h.setTienCoc(doc.getLong("tienCoc") != null ? doc.getLong("tienCoc").intValue() : 0);
        h.setGiaTien(doc.getLong("giaTien") != null ? doc.getLong("giaTien").intValue() : 0);
        h.setSoNguoi(doc.getLong("soNguoi") != null ? doc.getLong("soNguoi").intValue() : 0);
        h.setSoXe(doc.getLong("soXe") != null ? doc.getLong("soXe").intValue() : 0);
        h.setGhiChu(doc.getString("ghiChu"));
        h.setMaNguoiThue(doc.getString("maNguoiThue"));
        h.setMaPhong(doc.getLong("maPhong") != null ? doc.getLong("maPhong").intValue() : 0);
        
        // Note: BLOB hinhAnh không migrate, dùng URL từ Storage
        String imageUrl = doc.getString("hinhAnhUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // TODO: Load image từ URL khi cần
        }
        
        return h;
    }

    @Override
    protected Map<String, Object> toDocument(HopDong item) {
        Map<String, Object> map = new HashMap<>();
        map.put("maHopDong", item.getMaHopDong());
        map.put("sdt", item.getSdt());
        map.put("CCCD", item.getCCCD());
        map.put("thuongTru", item.getThuongTru());
        map.put("ngayKy", item.getNgayKy() != null ? sdf.format(item.getNgayKy()) : null);
        map.put("thoiHan", item.getThoiHan());
        map.put("tienCoc", item.getTienCoc());
        map.put("giaTien", item.getGiaTien());
        map.put("soNguoi", item.getSoNguoi());
        map.put("soXe", item.getSoXe());
        map.put("ghiChu", item.getGhiChu());
        map.put("maNguoiThue", item.getMaNguoiThue());
        map.put("maPhong", item.getMaPhong());
        return map;
    }

    /**
     * Lấy hợp đồng theo mã phòng
     */
    public void getByMaPhong(int maPhong, FirestoreCallback<List<HopDong>> callback) {
        db.collection(collectionName)
                .whereEqualTo("maPhong", maPhong)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<HopDong> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy hợp đồng theo người thuê
     */
    public void getByNguoiThue(String maNguoiThue, FirestoreCallback<List<HopDong>> callback) {
        db.collection(collectionName)
                .whereEqualTo("maNguoiThue", maNguoiThue)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<HopDong> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy hợp đồng còn hiệu lực (chưa hết hạn)
     */
    public void getActiveContracts(FirestoreCallback<List<HopDong>> callback) {
        getAll(new FirestoreCallback<List<HopDong>>() {
            @Override
            public void onSuccess(List<HopDong> result) {
                List<HopDong> active = new ArrayList<>();
                Date now = new Date();
                for (HopDong h : result) {
                    if (h.getNgayKy() != null && h.getThoiHan() > 0) {
                        // Calculate expiration date
                        long expirationTime = h.getNgayKy().getTime() + 
                                            (h.getThoiHan() * 30L * 24 * 60 * 60 * 1000); // months to ms
                        if (expirationTime > now.getTime()) {
                            active.add(h);
                        }
                    }
                }
                callback.onSuccess(active);
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
    public void listenForChanges(FirestoreCallback<List<HopDong>> callback) {
        db.collection(collectionName)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<HopDong> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            list.add(fromDocument(doc));
                        }
                        callback.onSuccess(list);
                    }
                });
    }
}
