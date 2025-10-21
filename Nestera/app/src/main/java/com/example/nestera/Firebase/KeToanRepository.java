package com.example.nestera.Firebase;

import com.example.nestera.model.KeToan;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository cho KeToan - đọc/ghi Firestore
 */
public class KeToanRepository extends FirestoreRepository<KeToan> {

    public KeToanRepository() {
        super("keToan");
    }

    @Override
    protected KeToan fromDocument(QueryDocumentSnapshot doc) {
        KeToan kt = new KeToan();
        kt.setMaKeToan(doc.getString("maKeToan"));
        kt.setTenKeToan(doc.getString("tenKeToan"));
        kt.setEmail(doc.getString("email"));
        kt.setSdt(doc.getString("sdt"));
        kt.setMatKhau(doc.getString("matKhau"));
        kt.setMaChuTro(doc.getString("maChuTro"));
        return kt;
    }

    @Override
    protected Map<String, Object> toDocument(KeToan item) {
        Map<String, Object> map = new HashMap<>();
        map.put("maKeToan", item.getMaKeToan());
        map.put("tenKeToan", item.getTenKeToan());
        map.put("email", item.getEmail());
        map.put("sdt", item.getSdt());
        map.put("matKhau", item.getMatKhau()); // Chú ý: Nên mã hóa mật khẩu!
        map.put("maChuTro", item.getMaChuTro());
        return map;
    }

    /**
     * Xác thực kế toán
     */
    public void authenticate(String email, String password, FirestoreCallback<KeToan> callback) {
        db.collection(collectionName)
                .whereEqualTo("email", email)
                .whereEqualTo("matKhau", password)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        KeToan kt = fromDocument((QueryDocumentSnapshot) querySnapshot.getDocuments().get(0));
                        callback.onSuccess(kt);
                    } else {
                        callback.onError(new Exception("Email hoặc mật khẩu không đúng"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Lấy kế toán theo chủ trọ
     */
    public void getByChuTro(String maChuTro, FirestoreCallback<List<KeToan>> callback) {
        db.collection(collectionName)
                .whereEqualTo("maChuTro", maChuTro)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<KeToan> list = new ArrayList<>();
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
    public void listenForChanges(FirestoreCallback<List<KeToan>> callback) {
        db.collection(collectionName)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }
                    
                    if (snapshots != null) {
                        List<KeToan> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            list.add(fromDocument(doc));
                        }
                        callback.onSuccess(list);
                    }
                });
    }
}
