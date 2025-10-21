package com.example.nestera.Firebase;

import android.content.Context;
import android.util.Log;

import com.example.nestera.Dao.chuTroDao;
import com.example.nestera.model.ChuTro;

import java.util.List;

/**
 * Hybrid Data Manager cho ChuTro
 * ĐỌC: SQLite (cache) → Firestore (background sync)
 * GHI: Song song SQLite + Firestore
 */
public class ChuTroHybridDao {
    private static final String TAG = "ChuTroHybrid";
    private final Context context;
    private final chuTroDao localDao;
    private final ChuTroRepository remoteRepo;

    public ChuTroHybridDao(Context context) {
        this.context = context;
        this.localDao = new chuTroDao(context);
        this.remoteRepo = new ChuTroRepository();
    }

    /**
     * Lấy tất cả chủ trọ
     */
    public List<ChuTro> getAll() {
        List<ChuTro> localData = localDao.getAll();
        syncFromFirestore();
        return localData;
    }

    /**
     * Lấy chủ trọ theo ID
     */
    public ChuTro getById(String id) {
        ChuTro local = localDao.getID(id);
        
        // Background sync
        remoteRepo.getById(id, new FirestoreRepository.FirestoreCallback<ChuTro>() {
            @Override
            public void onSuccess(ChuTro result) {
                if (result != null) {
                    localDao.update(result);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync landlord from Firestore", e);
            }
        });
        
        return local;
    }

    /**
     * Xác thực chủ trọ (login)
     */
    public void authenticate(String email, String password, FirestoreRepository.FirestoreCallback<ChuTro> callback) {
        // Local DAO doesn't have authenticate method, use remote only
        
        // Sync với remote để đảm bảo dữ liệu mới nhất
        remoteRepo.authenticate(email, password, new FirestoreRepository.FirestoreCallback<ChuTro>() {
            @Override
            public void onSuccess(ChuTro result) {
                // Update local cache
                ChuTro existing = localDao.getID(result.getMaChuTro());
                if (existing != null) {
                    localDao.update(result);
                } else {
                    localDao.insert(result);
                }
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Lấy chủ trọ đang chờ duyệt
     */
    public List<ChuTro> getPendingLandlords() {
        List<ChuTro> localData = localDao.getPending();
        
        // Background sync
        remoteRepo.getPendingLandlords(new FirestoreRepository.FirestoreCallback<List<ChuTro>>() {
            @Override
            public void onSuccess(List<ChuTro> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync pending landlords", e);
            }
        });
        
        return localData;
    }

    /**
     * Thêm chủ trọ mới - ghi song song
     */
    public long insert(ChuTro chuTro) {
        // 1. Ghi local ngay
        localDao.insert(chuTro);
        
        // 2. Ghi remote async
        remoteRepo.insert(chuTro, new FirestoreRepository.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String documentId) {
                Log.d(TAG, "ChuTro synced to Firestore: " + documentId);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync ChuTro to Firestore", e);
            }
        });
        
        return 1;
    }

    /**
     * Cập nhật chủ trọ - ghi song song
     */
    public int update(ChuTro chuTro) {
        // 1. Update local
        int rows = localDao.update(chuTro);
        
        // 2. Update remote async
        remoteRepo.update(chuTro.getMaChuTro(), chuTro, 
            new FirestoreRepository.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "ChuTro updated in Firestore");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Failed to update ChuTro in Firestore", e);
                }
            });
        
        return rows;
    }

    /**
     * Xóa chủ trọ - ghi song song
     */
    public int delete(String id) {
        // 1. Delete local
        int rows = localDao.delete(id);
        
        // 2. Delete remote async
        remoteRepo.delete(id, new FirestoreRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "ChuTro deleted from Firestore");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to delete ChuTro from Firestore", e);
            }
        });
        
        return rows;
    }

    /**
     * Đồng bộ từ Firestore xuống SQLite
     */
    private void syncFromFirestore() {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<ChuTro>>() {
            @Override
            public void onSuccess(List<ChuTro> remoteData) {
                updateLocalCache(remoteData);
                Log.d(TAG, "Synced " + remoteData.size() + " ChuTro from Firestore");
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
    private void updateLocalCache(List<ChuTro> remoteData) {
        for (ChuTro c : remoteData) {
            ChuTro existing = localDao.getID(c.getMaChuTro());
            if (existing != null) {
                localDao.update(c);
            } else {
                localDao.insert(c);
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
        remoteRepo.listenForChanges(new FirestoreRepository.FirestoreCallback<List<ChuTro>>() {
            @Override
            public void onSuccess(List<ChuTro> result) {
                updateLocalCache(result);
                Log.d(TAG, "Real-time update: " + result.size() + " landlords");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Real-time sync error", e);
            }
        });
    }
}
