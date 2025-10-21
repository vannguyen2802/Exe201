package com.example.nestera.Firebase;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base Repository cho Firestore operations
 * Các repository khác sẽ extend class này
 */
public abstract class FirestoreRepository<T> {
    protected static final String TAG = "FirestoreRepo";
    protected final FirebaseFirestore db;
    protected final String collectionName;

    protected FirestoreRepository(String collectionName) {
        this.db = FirebaseFirestore.getInstance();
        this.collectionName = collectionName;
    }

    /**
     * Convert Firestore document thành model object
     */
    protected abstract T fromDocument(QueryDocumentSnapshot doc);

    /**
     * Convert model object thành Map để lưu Firestore
     */
    protected abstract Map<String, Object> toDocument(T item);

    /**
     * Lấy tất cả documents từ collection
     */
    public void getAll(FirestoreCallback<List<T>> callback) {
        db.collection(collectionName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<T> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        list.add(fromDocument(doc));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting all " + collectionName, e);
                    callback.onError(e);
                });
    }

    /**
     * Lấy document theo ID
     */
    public void getById(String id, FirestoreCallback<T> callback) {
        db.collection(collectionName)
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Create a temporary QueryDocumentSnapshot-like object
                        // Since we can't directly cast, we'll need to handle this differently
                        try {
                            @SuppressWarnings("unchecked")
                            T item = (T) documentSnapshot.toObject(Object.class);
                            callback.onSuccess(item);
                        } catch (Exception e) {
                            callback.onError(e);
                        }
                    } else {
                        callback.onError(new Exception("Document not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting " + collectionName + " by ID", e);
                    callback.onError(e);
                });
    }

    /**
     * Thêm document mới
     */
    public void insert(T item, FirestoreCallback<String> callback) {
        Map<String, Object> data = toDocument(item);
        data.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
        
        db.collection(collectionName)
                .add(data)
                .addOnSuccessListener(docRef -> {
                    Log.d(TAG, "Insert success: " + docRef.getId());
                    callback.onSuccess(docRef.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error inserting " + collectionName, e);
                    callback.onError(e);
                });
    }

    /**
     * Cập nhật document
     */
    public void update(String id, T item, FirestoreCallback<Void> callback) {
        Map<String, Object> data = toDocument(item);
        data.put("updatedAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
        
        db.collection(collectionName)
                .document(id)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Update success: " + id);
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating " + collectionName, e);
                    callback.onError(e);
                });
    }

    /**
     * Xóa document
     */
    public void delete(String id, FirestoreCallback<Void> callback) {
        db.collection(collectionName)
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Delete success: " + id);
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting " + collectionName, e);
                    callback.onError(e);
                });
    }

    /**
     * Callback interface cho Firestore operations
     */
    public interface FirestoreCallback<R> {
        void onSuccess(R result);
        void onError(Exception e);
    }
}
