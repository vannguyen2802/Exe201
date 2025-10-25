package com.example.nestera.Firebase;

import com.example.nestera.model.NguoiThue;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho NguoiThue - đọc/ghi Firestore
 */
public class NguoiThueRepository extends FirestoreRepository<NguoiThue> {

    public NguoiThueRepository() {
        super("nguoiThue");
    }

    @Override
    protected NguoiThue fromDocument(QueryDocumentSnapshot doc) {
        NguoiThue nt = new NguoiThue();
        nt.setMaNguoiThue(doc.getString("maNguoiThue"));
        nt.setMatKhauNT(doc.getString("matKhauNT"));
        nt.setTenNguoiThue(doc.getString("tenNguoiThue"));
        nt.setThuongTru(doc.getString("thuongTru"));
        nt.setSdt(doc.getString("sdt"));
        nt.setCCCD(doc.getString("CCCD"));
        nt.setNamSinh(doc.getString("namSinh"));
        nt.setGioiTinh(doc.getLong("gioiTinh") != null ? doc.getLong("gioiTinh").intValue() : 0);
        nt.setMaPhong(doc.getLong("maPhong") != null ? doc.getLong("maPhong").intValue() : 0);
        nt.setChuTroId(doc.getString("chuTroId"));
        return nt;
    }

    @Override
    protected Map<String, Object> toDocument(NguoiThue item) {
        Map<String, Object> map = new HashMap<>();
        map.put("maNguoiThue", item.getMaNguoiThue());
        map.put("matKhauNT", item.getMatKhauNT());
        map.put("tenNguoiThue", item.getTenNguoiThue());
        map.put("thuongTru", item.getThuongTru());
        map.put("sdt", item.getSdt());
        map.put("CCCD", item.getCCCD());
        map.put("namSinh", item.getNamSinh());
        map.put("gioiTinh", item.getGioiTinh());
        map.put("maPhong", item.getMaPhong());
        map.put("chuTroId", item.getChuTroId());
        return map;
    }

    /**
     * Lấy người thuê theo chủ trọ
     */
    public void getByChuTro(String chuTroId, FirestoreCallback<List<NguoiThue>> callback) {
        db.collection(collectionName)
                .whereEqualTo("chuTroId", chuTroId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<NguoiThue> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy người thuê theo mã phòng
     */
    public void getByMaPhong(int maPhong, FirestoreCallback<List<NguoiThue>> callback) {
        db.collection(collectionName)
                .whereEqualTo("maPhong", maPhong)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<NguoiThue> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy người thuê chưa có phòng (maPhong <= 0)
     */
    public void getUnassignedTenants(FirestoreCallback<List<NguoiThue>> callback) {
        getAll(new FirestoreCallback<List<NguoiThue>>() {
            @Override
            public void onSuccess(List<NguoiThue> result) {
                List<NguoiThue> unassigned = new ArrayList<>();
                for (NguoiThue nt : result) {
                    if (nt.getMaPhong() <= 0) {
                        unassigned.add(nt);
                    }
                }
                callback.onSuccess(unassigned);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Tìm kiếm người thuê theo tên hoặc SĐT
     */
    public void search(String keyword, FirestoreCallback<List<NguoiThue>> callback) {
        getAll(new FirestoreCallback<List<NguoiThue>>() {
            @Override
            public void onSuccess(List<NguoiThue> result) {
                List<NguoiThue> filtered = new ArrayList<>();
                String query = keyword.toLowerCase();
                for (NguoiThue nt : result) {
                    String ten = nt.getTenNguoiThue() != null ? nt.getTenNguoiThue().toLowerCase() : "";
                    String sdt = nt.getSdt() != null ? nt.getSdt() : "";
                    if (ten.contains(query) || sdt.contains(query)) {
                        filtered.add(nt);
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
    public void listenForChanges(FirestoreCallback<List<NguoiThue>> callback) {
        db.collection(collectionName)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<NguoiThue> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            list.add(fromDocument(doc));
                        }
                        callback.onSuccess(list);
                    }
                });
    }
}
