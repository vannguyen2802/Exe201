package com.example.nestera.Firebase;

import com.example.nestera.model.HoaDon;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho HoaDon - đọc/ghi Firestore
 */
public class HoaDonRepository extends FirestoreRepository<HoaDon> {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public HoaDonRepository() {
        super("hoaDon");
    }

    @Override
    protected HoaDon fromDocument(QueryDocumentSnapshot doc) {
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(doc.getLong("maHoaDon") != null ? doc.getLong("maHoaDon").intValue() : 0);
        hd.setSdt(doc.getString("sdt"));
        
        // Parse date
        String ngayTaoStr = doc.getString("ngayTao");
        if (ngayTaoStr != null) {
            try {
                hd.setNgayTao(sdf.parse(ngayTaoStr));
            } catch (Exception e) {
                hd.setNgayTao(new Date());
            }
        }
        
        hd.setSoDien(doc.getLong("soDien") != null ? doc.getLong("soDien").intValue() : 0);
        hd.setDonGiaDien(doc.getLong("donGiaDien") != null ? doc.getLong("donGiaDien").intValue() : 0);
        hd.setSoNguoi(doc.getLong("soNguoi") != null ? doc.getLong("soNguoi").intValue() : 0);
        hd.setDonGiaNuoc(doc.getLong("donGiaNuoc") != null ? doc.getLong("donGiaNuoc").intValue() : 0);
        hd.setPhiDichVu(doc.getLong("phiDichVu") != null ? doc.getLong("phiDichVu").intValue() : 0);
        hd.setGhiChu(doc.getString("ghiChu"));
        hd.setTienPhong(doc.getLong("tienPhong") != null ? doc.getLong("tienPhong").intValue() : 0);
        hd.setTrangThai(doc.getLong("trangThai") != null ? doc.getLong("trangThai").intValue() : 0);
        hd.setMaPhong(doc.getLong("maPhong") != null ? doc.getLong("maPhong").intValue() : 0);
        hd.setMaNguoiThue(doc.getString("maNguoiThue"));
        
        // Note: BLOB anhThanhToan không migrate, dùng URL từ Storage
        String imageUrl = doc.getString("anhThanhToanUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // TODO: Load image từ URL khi cần
        }
        
        return hd;
    }

    @Override
    protected Map<String, Object> toDocument(HoaDon item) {
        Map<String, Object> map = new HashMap<>();
        map.put("maHoaDon", item.getMaHoaDon());
        map.put("sdt", item.getSdt());
        map.put("ngayTao", item.getNgayTao() != null ? sdf.format(item.getNgayTao()) : null);
        map.put("soDien", item.getSoDien());
        map.put("donGiaDien", item.getDonGiaDien());
        map.put("soNguoi", item.getSoNguoi());
        map.put("donGiaNuoc", item.getDonGiaNuoc());
        map.put("phiDichVu", item.getPhiDichVu());
        map.put("ghiChu", item.getGhiChu());
        map.put("tienPhong", item.getTienPhong());
        map.put("trangThai", item.getTrangThai());
        map.put("maPhong", item.getMaPhong());
        map.put("maNguoiThue", item.getMaNguoiThue());
        return map;
    }

    /**
     * Lấy hóa đơn theo mã phòng
     */
    public void getByMaPhong(int maPhong, FirestoreCallback<List<HoaDon>> callback) {
        db.collection(collectionName)
                .whereEqualTo("maPhong", maPhong)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<HoaDon> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy hóa đơn theo người thuê
     */
    public void getByNguoiThue(String maNguoiThue, FirestoreCallback<List<HoaDon>> callback) {
        db.collection(collectionName)
                .whereEqualTo("maNguoiThue", maNguoiThue)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<HoaDon> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy hóa đơn theo trạng thái (0: chưa thanh toán, 1: đã thanh toán)
     */
    public void getByTrangThai(int trangThai, FirestoreCallback<List<HoaDon>> callback) {
        db.collection(collectionName)
                .whereEqualTo("trangThai", trangThai)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<HoaDon> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy hóa đơn chưa thanh toán
     */
    public void getUnpaidBills(FirestoreCallback<List<HoaDon>> callback) {
        getByTrangThai(0, callback);
    }

    /**
     * Lấy hóa đơn theo tháng
     */
    public void getByMonth(int month, int year, FirestoreCallback<List<HoaDon>> callback) {
        getAll(new FirestoreCallback<List<HoaDon>>() {
            @Override
            public void onSuccess(List<HoaDon> result) {
                List<HoaDon> filtered = new ArrayList<>();
                for (HoaDon hd : result) {
                    if (hd.getNgayTao() != null) {
                        @SuppressWarnings("deprecation")
                        int hdMonth = hd.getNgayTao().getMonth() + 1; // 0-based
                        @SuppressWarnings("deprecation")
                        int hdYear = hd.getNgayTao().getYear() + 1900;
                        if (hdMonth == month && hdYear == year) {
                            filtered.add(hd);
                        }
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
    public void listenForChanges(FirestoreCallback<List<HoaDon>> callback) {
        db.collection(collectionName)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<HoaDon> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            list.add(fromDocument(doc));
                        }
                        callback.onSuccess(list);
                    }
                });
    }
}
