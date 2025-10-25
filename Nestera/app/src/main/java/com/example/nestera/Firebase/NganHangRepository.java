package com.example.nestera.Firebase;

import com.example.nestera.model.NganHang;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho NganHang - đọc/ghi Firestore
 */
public class NganHangRepository extends FirestoreRepository<NganHang> {

    public NganHangRepository() {
        super("nganHang");
    }

    @Override
    protected NganHang fromDocument(QueryDocumentSnapshot doc) {
        NganHang nh = new NganHang();
        nh.setMaNganHang(doc.getLong("maNganHang") != null ? doc.getLong("maNganHang").intValue() : 0);
        nh.setTenNganHang(doc.getString("tenNganHang"));
        nh.setSoTaiKhoan(doc.getString("soTaiKhoan"));
        nh.setChuTaiKhoan(doc.getString("chuTaiKhoan"));
        nh.setChiNhanh(doc.getString("chiNhanh"));
        nh.setMaChuTro(doc.getString("maChuTro"));
        return nh;
    }

    @Override
    protected Map<String, Object> toDocument(NganHang item) {
        Map<String, Object> map = new HashMap<>();
        map.put("maNganHang", item.getMaNganHang());
        map.put("tenNganHang", item.getTenNganHang());
        map.put("soTaiKhoan", item.getSoTaiKhoan());
        map.put("chuTaiKhoan", item.getChuTaiKhoan());
        map.put("chiNhanh", item.getChiNhanh());
        map.put("maChuTro", item.getMaChuTro());
        return map;
    }

    /**
     * Lấy thông tin ngân hàng theo chủ trọ
     */
    public void getByChuTro(String maChuTro, FirestoreCallback<List<NganHang>> callback) {
        db.collection(collectionName)
                .whereEqualTo("maChuTro", maChuTro)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<NganHang> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lắng nghe real-time changes
     */
    public void listenForChanges(FirestoreCallback<List<NganHang>> callback) {
        db.collection(collectionName)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<NganHang> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            list.add(fromDocument(doc));
                        }
                        callback.onSuccess(list);
                    }
                });
    }
}
