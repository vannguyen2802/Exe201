package com.example.nestera.Firebase;

import com.example.nestera.model.suCo;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho suCo - đọc/ghi Firestore
 */
public class SuCoRepository extends FirestoreRepository<suCo> {

    public SuCoRepository() {
        super("suCo");
    }

    @Override
    protected suCo fromDocument(QueryDocumentSnapshot doc) {
        suCo sc = new suCo();
        sc.setMasuCo(doc.getLong("masuCo") != null ? doc.getLong("masuCo").intValue() : 0);
        sc.setTensuCo(doc.getString("tensuCo"));
        sc.setNoiDung(doc.getString("noiDung"));
        sc.setTrangThai(doc.getLong("trangThai") != null ? doc.getLong("trangThai").intValue() : 0);
        sc.setMaPhong(doc.getLong("maPhong") != null ? doc.getLong("maPhong").intValue() : 0);
        sc.setMaNguoiThue(doc.getString("maNguoiThue"));
        return sc;
    }

    @Override
    protected Map<String, Object> toDocument(suCo item) {
        Map<String, Object> map = new HashMap<>();
        map.put("masuCo", item.getMasuCo());
        map.put("tensuCo", item.getTensuCo());
        map.put("noiDung", item.getNoiDung());
        map.put("trangThai", item.getTrangThai());
        map.put("maPhong", item.getMaPhong());
        map.put("maNguoiThue", item.getMaNguoiThue());
        return map;
    }

    /**
     * Lấy sự cố theo mã phòng
     */
    public void getByMaPhong(int maPhong, FirestoreCallback<List<suCo>> callback) {
        db.collection(collectionName)
                .whereEqualTo("maPhong", maPhong)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<suCo> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy sự cố theo trạng thái (0: chưa xử lý, 1: đang xử lý, 2: đã xử lý)
     */
    public void getByTrangThai(int trangThai, FirestoreCallback<List<suCo>> callback) {
        db.collection(collectionName)
                .whereEqualTo("trangThai", trangThai)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<suCo> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy sự cố chưa xử lý
     */
    public void getPendingIssues(FirestoreCallback<List<suCo>> callback) {
        getByTrangThai(0, callback);
    }

    /**
     * Lắng nghe real-time changes
     */
    public void listenForChanges(FirestoreCallback<List<suCo>> callback) {
        db.collection(collectionName)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<suCo> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            list.add(fromDocument(doc));
                        }
                        callback.onSuccess(list);
                    }
                });
    }
}
