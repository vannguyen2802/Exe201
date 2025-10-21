package com.example.nestera.Firebase;

import com.example.nestera.model.LoaiPhong;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho LoaiPhong - đọc/ghi Firestore
 */
public class LoaiPhongRepository extends FirestoreRepository<LoaiPhong> {

    public LoaiPhongRepository() {
        super("loaiPhong");
    }

    @Override
    protected LoaiPhong fromDocument(QueryDocumentSnapshot doc) {
        LoaiPhong lp = new LoaiPhong();
        lp.setMaLoai(doc.getLong("maLoai") != null ? doc.getLong("maLoai").intValue() : 0);
        lp.setTenLoai(doc.getString("tenLoai"));
        lp.setPhiDichVu(doc.getLong("phiDichVu") != null ? doc.getLong("phiDichVu").intValue() : 0);
        lp.setGiaDien(doc.getLong("giaDien") != null ? doc.getLong("giaDien").intValue() : 0);
        lp.setGiaNuoc(doc.getLong("giaNuoc") != null ? doc.getLong("giaNuoc").intValue() : 0);
        return lp;
    }

    @Override
    protected Map<String, Object> toDocument(LoaiPhong item) {
        Map<String, Object> map = new HashMap<>();
        map.put("maLoai", item.getMaLoai());
        map.put("tenLoai", item.getTenLoai());
        map.put("phiDichVu", item.getPhiDichVu());
        map.put("giaDien", item.getGiaDien());
        map.put("giaNuoc", item.getGiaNuoc());
        return map;
    }

    /**
     * Lắng nghe real-time changes
     */
    public void listenForChanges(FirestoreCallback<List<LoaiPhong>> callback) {
        db.collection(collectionName)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        java.util.List<LoaiPhong> list = new java.util.ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            list.add(fromDocument(doc));
                        }
                        callback.onSuccess(list);
                    }
                });
    }
}
