package com.example.nestera.Firebase;

import android.content.Context;
import android.util.Log;

import com.example.nestera.Dao.LoaiPhongDao;
import com.example.nestera.model.LoaiPhong;

import java.util.List;

/**
 * Hybrid Data Manager cho LoaiPhong
 * ĐỌC: SQLite (cache) → Firestore (background sync)
 * GHI: Song song SQLite + Firestore
 */
public class LoaiPhongHybridDao {
    private static final String TAG = "LoaiPhongHybrid";
    private final Context context;
    private final LoaiPhongDao localDao;
    private final LoaiPhongRepository remoteRepo;

    public LoaiPhongHybridDao(Context context) {
        this.context = context;
        this.localDao = new LoaiPhongDao(context);
        this.remoteRepo = new LoaiPhongRepository();
    }

    /**
     * Lấy tất cả loại phòng
     */
    public List<LoaiPhong> getAll() {
        List<LoaiPhong> localData = localDao.getAll();
        syncFromFirestore();
        return localData;
    }

    /**
     * Lấy loại phòng theo ID
     */
    public LoaiPhong getById(int id) {
        LoaiPhong local = localDao.getID(String.valueOf(id));
        
        // Background sync
        remoteRepo.getById(String.valueOf(id), new FirestoreRepository.FirestoreCallback<LoaiPhong>() {
            @Override
            public void onSuccess(LoaiPhong result) {
                if (result != null) {
                    localDao.update(result);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync room type from Firestore", e);
            }
        });
        
        return local;
    }

    /**
     * Thêm loại phòng mới - ghi song song
     */
    public long insert(LoaiPhong loaiPhong) {
        // 1. Ghi local ngay
        long localId = localDao.insert(loaiPhong);
        loaiPhong.setMaLoaiPhong((int) localId);
        
        // 2. Ghi remote async
        remoteRepo.insert(loaiPhong, new FirestoreRepository.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String documentId) {
                Log.d(TAG, "LoaiPhong synced to Firestore: " + documentId);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync LoaiPhong to Firestore", e);
            }
        });
        
        return localId;
    }

    /**
     * Cập nhật loại phòng - ghi song song
     */
    public int update(LoaiPhong loaiPhong) {
        // 1. Update local
        int rows = localDao.update(loaiPhong);
        
        // 2. Update remote async
        remoteRepo.update(String.valueOf(loaiPhong.getMaLoaiPhong()), loaiPhong, 
            new FirestoreRepository.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "LoaiPhong updated in Firestore");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Failed to update LoaiPhong in Firestore", e);
                }
            });
        
        return rows;
    }

    /**
     * Xóa loại phòng - ghi song song
     */
    public int delete(int id) {
        // 1. Delete local
        int rows = localDao.delete(String.valueOf(id));
        
        // 2. Delete remote async
        remoteRepo.delete(String.valueOf(id), new FirestoreRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "LoaiPhong deleted from Firestore");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to delete LoaiPhong from Firestore", e);
            }
        });
        
        return rows;
    }

    /**
     * Đồng bộ từ Firestore xuống SQLite
     */
    private void syncFromFirestore() {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<LoaiPhong>>() {
            @Override
            public void onSuccess(List<LoaiPhong> remoteData) {
                updateLocalCache(remoteData);
                Log.d(TAG, "Synced " + remoteData.size() + " LoaiPhong from Firestore");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Sync failed", e);
            }
        });
    }

    /**
     * Update local cache
     */
    private void updateLocalCache(List<LoaiPhong> remoteData) {
        for (LoaiPhong lp : remoteData) {
            LoaiPhong existing = localDao.getID(String.valueOf(lp.getMaLoaiPhong()));
            if (existing != null) {
                localDao.update(lp);
            } else {
                localDao.insert(lp);
            }
        }
    }

    /**
     * Force sync ngay lập tức
     */
    public void forceSync() {
        syncFromFirestore();
    }

    /**
     * Listen real-time changes
     */
    public void enableRealtimeSync() {
        remoteRepo.listenForChanges(new FirestoreRepository.FirestoreCallback<List<LoaiPhong>>() {
            @Override
            public void onSuccess(List<LoaiPhong> result) {
                updateLocalCache(result);
                Log.d(TAG, "Real-time update: " + result.size() + " room types");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Real-time sync error", e);
            }
        });
    }
}
