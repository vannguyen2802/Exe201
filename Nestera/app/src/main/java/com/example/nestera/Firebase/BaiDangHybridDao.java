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
     * Thêm mới - ghi song song
     */
    public long insert(BaiDang baiDang) {
        // 1. Ghi local ngay
        long localId = localDao.insert(baiDang);
        baiDang.setId((int) localId);
        
        // 2. Ghi remote async
        remoteRepo.insert(baiDang, new FirestoreRepository.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String documentId) {
                // Optional: lưu documentId vào local
                android.util.Log.d("Hybrid", "Synced to Firestore: " + documentId);
            }

            @Override
            public void onError(Exception e) {
                android.util.Log.e("Hybrid", "Failed to sync to Firestore", e);
                // TODO: Thêm vào queue để retry sau
            }
        });
        
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
     * Xóa - ghi song song
     */
    public int delete(String id) {
        // 1. Delete local
        int rows = localDao.delete(id);
        
        // 2. Delete remote async
        remoteRepo.delete(id, new FirestoreRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                android.util.Log.d("Hybrid", "Deleted from Firestore");
            }

            @Override
            public void onError(Exception e) {
                android.util.Log.e("Hybrid", "Failed to delete from Firestore", e);
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
