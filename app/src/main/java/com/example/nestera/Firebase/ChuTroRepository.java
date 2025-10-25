package com.example.nestera.Firebase;

import com.example.nestera.model.ChuTro;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho ChuTro - đọc/ghi Firestore
 */
public class ChuTroRepository extends FirestoreRepository<ChuTro> {

    public ChuTroRepository() {
        super("chuTro");
    }

    @Override
    protected ChuTro fromDocument(QueryDocumentSnapshot doc) {
        ChuTro ct = new ChuTro();
        ct.setMaChuTro(doc.getString("maChuTro"));
        ct.setMatKhau(doc.getString("matKhau"));
        ct.setTenChuTro(doc.getString("tenChuTro"));
        ct.setEmail(doc.getString("email"));
        ct.setSdt(doc.getString("sdt"));
        ct.setCccd(doc.getString("cccd"));
        ct.setApproved(doc.getLong("approved") != null ? doc.getLong("approved").intValue() : 0);
        ct.setBanned(doc.getLong("banned") != null ? doc.getLong("banned").intValue() : 0);
        return ct;
    }

    @Override
    protected Map<String, Object> toDocument(ChuTro item) {
        Map<String, Object> map = new HashMap<>();
        map.put("maChuTro", item.getMaChuTro());
        map.put("matKhau", item.getMatKhau());
        map.put("tenChuTro", item.getTenChuTro());
        map.put("email", item.getEmail());
        map.put("sdt", item.getSdt());
        map.put("cccd", item.getCccd());
        map.put("approved", item.getApproved());
        map.put("banned", item.getBanned());
        return map;
    }

    /**
     * Lấy chủ trọ đã được duyệt
     */
    public void getApprovedLandlords(FirestoreCallback<List<ChuTro>> callback) {
        db.collection(collectionName)
                .whereEqualTo("approved", 1)
                .whereEqualTo("banned", 0)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ChuTro> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy chủ trọ chờ duyệt
     */
    public void getPendingLandlords(FirestoreCallback<List<ChuTro>> callback) {
        db.collection(collectionName)
                .whereEqualTo("approved", 0)
                .whereEqualTo("banned", 0)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ChuTro> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Authenticate chủ trọ
     */
    public void authenticate(String username, String password, FirestoreCallback<ChuTro> callback) {
        db.collection(collectionName)
                .whereEqualTo("maChuTro", username)
                .whereEqualTo("matKhau", password)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        ChuTro ct = fromDocument((QueryDocumentSnapshot) querySnapshot.getDocuments().get(0));
                        if (ct.getBanned() == 1) {
                            callback.onError(new Exception("Tài khoản đã bị khóa"));
                        } else if (ct.getApproved() == 0) {
                            callback.onError(new Exception("Tài khoản chưa được duyệt"));
                        } else {
                            callback.onSuccess(ct);
                        }
                    } else {
                        callback.onError(new Exception("Sai tên đăng nhập hoặc mật khẩu"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lắng nghe real-time changes
     */
    public void listenForChanges(FirestoreCallback<List<ChuTro>> callback) {
        db.collection(collectionName)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<ChuTro> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            list.add(fromDocument(doc));
                        }
                        callback.onSuccess(list);
                    }
                });
    }
}
