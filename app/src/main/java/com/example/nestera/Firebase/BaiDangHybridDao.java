package com.example.nestera.Firebase;

import android.content.Context;

import com.example.nestera.Dao.baiDangDao;
import com.example.nestera.model.BaiDang;

import java.util.List;

/**
 * Hybrid Data Manager - Sử dụng cả SQLite (cache) và Firestore (cloud)
 * 
 * Chiến lược:
 * - ĐỌC: Ưu tiên SQLite (nhanh), fallback Firestore
 * - GHI: Ghi song song cả SQLite + Firestore
 * - SYNC: Background sync khi có network
 */
public class BaiDangHybridDao {
    private static final String TAG = "BaiDangHybridDao";
    private final Context context;
    private final baiDangDao localDao;
    private final BaiDangRepository remoteRepo;

    public BaiDangHybridDao(Context context) {
        this.context = context;
        this.localDao = new baiDangDao(context);
        this.remoteRepo = new BaiDangRepository();
    }

    /**
     * Lấy tất cả - ưu tiên local, đồng bộ background
     */
    public List<BaiDang> getAll() {
        // 1. Lấy từ SQLite ngay lập tức (fast)
        List<BaiDang> localData = localDao.getAll();
        
        // 2. Background sync từ Firestore
        syncFromFirestore();
        
        return localData;
    }

    /**
     * Lấy tất cả với callback - đợi sync Firestore xong
     */
    public void getAllWithSync(FirestoreRepository.FirestoreCallback<List<BaiDang>> callback) {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<BaiDang>>() {
            @Override
            public void onSuccess(List<BaiDang> remoteData) {
                // Update local cache
                for (BaiDang b : remoteData) {
                    BaiDang existing = localDao.getById(b.getId());
                    if (existing != null) {
                        localDao.update(b);
                    } else {
                        localDao.insert(b);
                    }
                }
                // Trả về data đã sync
                callback.onSuccess(localDao.getAll());
                android.util.Log.d("BaiDangHybrid", "Synced " + remoteData.size() + " records from Firestore");
            }

            @Override
            public void onError(Exception e) {
                // Fallback to local nếu lỗi
                callback.onSuccess(localDao.getAll());
                android.util.Log.e("BaiDangHybrid", "Sync failed, using local data", e);
            }
        });
    }

    /**
     * Lấy theo ID
     */
    public BaiDang getById(int id) {
        BaiDang local = localDao.getById(id);
        
        // Background sync
        remoteRepo.getById(String.valueOf(id), new FirestoreRepository.FirestoreCallback<BaiDang>() {
            @Override
            public void onSuccess(BaiDang result) {
                if (result != null) {
                    localDao.update(result);
                }
            }

            @Override
            public void onError(Exception e) {
                // Ignore, use local
            }
        });
        
        return local;
    }

    /**
     * Lấy bài đăng theo mã phòng
     */
    public BaiDang getByMaPhong(int maPhong) {
        // Try local first
        List<BaiDang> all = localDao.getAll();
        for (BaiDang b : all) {
            if (b.getMaPhong() != null && b.getMaPhong() == maPhong) {
                return b;
            }
        }
        return null;
    }

    /**
     * Lấy theo chủ trọ
     */
    public List<BaiDang> getByChuTro(String chuTroId) {
        List<BaiDang> localData = localDao.getByChuTro(chuTroId);
        
        // Background sync
        remoteRepo.getByChuTro(chuTroId, new FirestoreRepository.FirestoreCallback<List<BaiDang>>() {
            @Override
            public void onSuccess(List<BaiDang> result) {
                // Update local cache inline
                for (BaiDang b : result) {
                    BaiDang existing = localDao.getById(b.getId());
                    if (existing != null) {
                        localDao.update(b);
                    } else {
                        localDao.insert(b);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                // Ignore error, use local data
            }
        });
        
        return localData;
    }

    /**
     * Lấy theo chủ trọ với callback - đợi sync Firestore xong
     */
    public void getByChuTroWithSync(String chuTroId, FirestoreRepository.FirestoreCallback<List<BaiDang>> callback) {
        remoteRepo.getByChuTro(chuTroId, new FirestoreRepository.FirestoreCallback<List<BaiDang>>() {
            @Override
            public void onSuccess(List<BaiDang> result) {
                // Update local cache
                for (BaiDang b : result) {
                    BaiDang existing = localDao.getById(b.getId());
                    if (existing != null) {
                        localDao.update(b);
                    } else {
                        localDao.insert(b);
                    }
                }
                // Trả về data từ local
                callback.onSuccess(localDao.getByChuTro(chuTroId));
            }

            @Override
            public void onError(Exception e) {
                // Fallback to local
                callback.onSuccess(localDao.getByChuTro(chuTroId));
            }
        });
    }

    /**
     * Thêm mới - ghi song song
     */
    public long insert(BaiDang baiDang) {
        // Debug logging
        android.util.Log.d("BaiDangHybrid", "=== INSERT START ===");
        android.util.Log.d("BaiDangHybrid", "Title: " + baiDang.getTieuDe());
        android.util.Log.d("BaiDangHybrid", "ChuTroId: " + baiDang.getChuTroId());
        android.util.Log.d("BaiDangHybrid", "ImageURL: " + baiDang.getHinhAnh());
        
        // 1. Ghi local ngay
        long localId = localDao.insert(baiDang);
        android.util.Log.d("BaiDangHybrid", "Local SQLite insert result: " + localId);
        baiDang.setId((int) localId);
        
        // 2. Ghi remote async sử dụng local ID làm document ID
        android.util.Log.d("BaiDangHybrid", "Starting Firestore sync with document ID: " + localId);
        remoteRepo.insert(String.valueOf(localId), baiDang, new FirestoreRepository.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String documentId) {
                android.util.Log.d("BaiDangHybrid", "✅ Firestore insert SUCCESS! DocID: " + documentId);
            }

            @Override
            public void onError(Exception e) {
                android.util.Log.e("BaiDangHybrid", "❌ Firestore insert FAILED!", e);
                android.util.Log.e("BaiDangHybrid", "Error message: " + e.getMessage());
                if (e.getCause() != null) {
                    android.util.Log.e("BaiDangHybrid", "Cause: " + e.getCause().getMessage());
                }
                // TODO: Thêm vào queue để retry sau
            }
        });
        
        android.util.Log.d("BaiDangHybrid", "=== INSERT END (returning localId: " + localId + ") ===");
        return localId;
    }

    /**
     * Cập nhật - ghi song song
     */
    public int update(BaiDang baiDang) {
        // 1. Update local
        int rows = localDao.update(baiDang);
        
        // 2. Update remote async
        remoteRepo.update(String.valueOf(baiDang.getId()), baiDang, new FirestoreRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                android.util.Log.d("Hybrid", "Updated Firestore");
            }

            @Override
            public void onError(Exception e) {
                android.util.Log.e("Hybrid", "Failed to update Firestore", e);
            }
        });
        
        return rows;
    }

    /**
     * Xóa bài đăng - ghi song song
     */
    public int delete(String id) {
        android.util.Log.d(TAG, "Attempting to delete post with ID: " + id);
        
        // Since we now use local ID as Firestore document ID, deletion is simpler
        android.util.Log.d(TAG, "Local ID = Firestore Document ID: " + id);
        
        // 1. Delete local first
        int rows = localDao.delete(id);
        android.util.Log.d(TAG, "Local delete result: " + rows + " rows affected");
        
        // 2. Delete remote using same ID
        remoteRepo.delete(id, new FirestoreRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                android.util.Log.d(TAG, "✅ Post deleted from Firestore successfully - Doc ID: " + id);
            }

            @Override
            public void onError(Exception e) {
                android.util.Log.e(TAG, "❌ Failed to delete post from Firestore - Doc ID: " + id + " - Error: " + e.getMessage(), e);
                // Try to rollback local delete if remote fails
                // But for now, we'll just log the error
            }
        });
        
        return rows;
    }

    /**
     * Đồng bộ từ Firestore xuống SQLite
     */
    private void syncFromFirestore() {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<BaiDang>>() {
            @Override
            public void onSuccess(List<BaiDang> remoteData) {
                // Update local database
                for (BaiDang b : remoteData) {
                    BaiDang existing = localDao.getById(b.getId());
                    if (existing != null) {
                        localDao.update(b);
                    } else {
                        localDao.insert(b);
                    }
                }
                android.util.Log.d("Hybrid", "Synced " + remoteData.size() + " records from Firestore");
            }

            @Override
            public void onError(Exception e) {
                android.util.Log.e("Hybrid", "Sync failed", e);
            }
        });
    }

    /**
     * Force sync ngay lập tức
     */
    public void forceSync() {
        syncFromFirestore();
    }

    /**
     * Listen real-time changes từ Firestore
     */
    public void enableRealtimeSync() {
        remoteRepo.listenForChanges(new FirestoreRepository.FirestoreCallback<List<BaiDang>>() {
            @Override
            public void onSuccess(List<BaiDang> result) {
                // Auto update local cache
                for (BaiDang b : result) {
                    if (localDao.getById(b.getId()) != null) {
                        localDao.update(b);
                    } else {
                        localDao.insert(b);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                android.util.Log.e("Hybrid", "Realtime sync error", e);
            }
        });
    }
}
