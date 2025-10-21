package com.example.nestera.Firebase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.UUID;

/**
 * Helper class để upload ảnh lên Firebase Storage
 * Sử dụng cho: PhongTro, HopDong, HoaDon, BaiDang
 */
public class ImageUploader {
    private static final String TAG = "ImageUploader";
    private final FirebaseStorage storage;
    private final Context context;

    public ImageUploader(Context context) {
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     * Upload ảnh từ URI (content://) lên Storage
     * @param imageUri URI của ảnh đã chọn
     * @param folder Thư mục lưu (phongTro, hopDong, hoaDon, baiDang)
     * @param callback Callback trả về URL hoặc lỗi
     */
    public void uploadImage(Uri imageUri, String folder, UploadCallback callback) {
        if (imageUri == null) {
            callback.onError(new Exception("URI is null"));
            return;
        }

        String fileName = folder + "/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference ref = storage.getReference().child(fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Log.d(TAG, "Upload success: " + uri.toString());
                                callback.onSuccess(uri.toString());
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Upload failed", e);
                    callback.onError(e);
                });
    }

    /**
     * Upload ảnh từ byte array (BLOB từ SQLite)
     */
    public void uploadImageBytes(byte[] imageBytes, String folder, UploadCallback callback) {
        if (imageBytes == null || imageBytes.length == 0) {
            callback.onError(new Exception("Image bytes is empty"));
            return;
        }

        String fileName = folder + "/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference ref = storage.getReference().child(fileName);

        ref.putBytes(imageBytes)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Log.d(TAG, "Upload bytes success: " + uri.toString());
                                callback.onSuccess(uri.toString());
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Upload bytes failed", e);
                    callback.onError(e);
                });
    }

    /**
     * Upload nhiều ảnh từ danh sách URI
     */
    public void uploadMultipleImages(java.util.List<Uri> uris, String folder, MultiUploadCallback callback) {
        java.util.List<String> urls = new java.util.ArrayList<>();
        java.util.concurrent.atomic.AtomicInteger completed = new java.util.concurrent.atomic.AtomicInteger(0);
        
        for (Uri uri : uris) {
            uploadImage(uri, folder, new UploadCallback() {
                @Override
                public void onSuccess(String downloadUrl) {
                    synchronized (urls) {
                        urls.add(downloadUrl);
                    }
                    if (completed.incrementAndGet() == uris.size()) {
                        callback.onAllSuccess(urls);
                    }
                }

                @Override
                public void onError(Exception e) {
                    callback.onError(e);
                }
            });
        }
    }

    public interface UploadCallback {
        void onSuccess(String downloadUrl);
        void onError(Exception e);
    }

    public interface MultiUploadCallback {
        void onAllSuccess(java.util.List<String> downloadUrls);
        void onError(Exception e);
    }
}
