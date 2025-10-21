package com.example.nestera.Firebase;

import android.content.Context;
import android.util.Log;

import com.example.nestera.Dao.suCoDao;
import com.example.nestera.model.suCo;

import java.util.List;
import java.util.ArrayList;

/**
 * Hybrid Data Manager cho suCo
 * ĐỌC: SQLite (cache) → Firestore (background sync)
 * GHI: Song song SQLite + Firestore
 */
public class SuCoHybridDao {
    private static final String TAG = "SuCoHybrid";
    private final Context context;
    private final suCoDao localDao;
    private final SuCoRepository remoteRepo;

    public SuCoHybridDao(Context context) {
        this.context = context;
        this.localDao = new suCoDao(context);
        this.remoteRepo = new SuCoRepository();
    }

    /**
     * Lấy tất cả sự cố
     */
    public List<suCo> getAll() {
        List<suCo> localData = localDao.getAll();
        syncFromFirestore();
        return localData;
    }

    /**
     * Lấy sự cố theo ID
     */
    public suCo getById(int id) {
        suCo local = localDao.getID(String.valueOf(id));
        
        // Background sync
        remoteRepo.getById(String.valueOf(id), new FirestoreRepository.FirestoreCallback<suCo>() {
            @Override
            public void onSuccess(suCo result) {
                if (result != null) {
                    localDao.update(result);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync issue from Firestore", e);
            }
        });
        
        return local;
    }

    /**
     * Lấy sự cố theo phòng
     */
    public List<suCo> getByMaPhong(int maPhong) {
        List<suCo> localData = localDao.getSuCoByMaPhong(maPhong);
        
        // Background sync
        remoteRepo.getByMaPhong(maPhong, new FirestoreRepository.FirestoreCallback<List<suCo>>() {
            @Override
            public void onSuccess(List<suCo> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync issues by room", e);
            }
        });
        
        return localData;
    }

    /**
     * Lấy sự cố chưa xử lý
     */
    public List<suCo> getPendingIssues() {
        // Filter from all since no specific DAO method
        List<suCo> all = localDao.getAll();
        List<suCo> pending = new ArrayList<>();
        for (suCo sc : all) {
            if (sc.getTrangThai() == 0) pending.add(sc);
        }
        
        // Background sync
        remoteRepo.getPendingIssues(new FirestoreRepository.FirestoreCallback<List<suCo>>() {
            @Override
            public void onSuccess(List<suCo> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync pending issues", e);
            }
        });
        
        return pending;
    }

    /**
     * Lấy sự cố theo trạng thái
     */
    public List<suCo> getByTrangThai(int trangThai) {
        // Filter from all since no specific DAO method
        List<suCo> all = localDao.getAll();
        List<suCo> result = new ArrayList<>();
        for (suCo sc : all) {
            if (sc.getTrangThai() == trangThai) result.add(sc);
        }
        
        // Background sync
        remoteRepo.getByTrangThai(trangThai, new FirestoreRepository.FirestoreCallback<List<suCo>>() {
            @Override
            public void onSuccess(List<suCo> r) {
                updateLocalCache(r);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync issues by status", e);
            }
        });
        
        return result;
    }

    /**
     * Thêm sự cố mới - ghi song song
     */
    public long insert(suCo suCo) {
        // 1. Ghi local ngay
        long localId = localDao.insert(suCo);
        suCo.setMasuCo((int) localId);
        
        // 2. Ghi remote async
        remoteRepo.insert(suCo, new FirestoreRepository.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String documentId) {
                Log.d(TAG, "suCo synced to Firestore: " + documentId);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync suCo to Firestore", e);
            }
        });
        
        return localId;
    }

    /**
     * Cập nhật sự cố - ghi song song
     */
    public int update(suCo suCo) {
        // 1. Update local
        int rows = localDao.update(suCo);
        
        // 2. Update remote async
        remoteRepo.update(String.valueOf(suCo.getMasuCo()), suCo, 
            new FirestoreRepository.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "suCo updated in Firestore");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Failed to update suCo in Firestore", e);
                }
            });
        
        return rows;
    }

    /**
     * Xóa sự cố - ghi song song
     */
    public int delete(int id) {
        // 1. Delete local
        int rows = localDao.delete(String.valueOf(id));
        
        // 2. Delete remote async
        remoteRepo.delete(String.valueOf(id), new FirestoreRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "suCo deleted from Firestore");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to delete suCo from Firestore", e);
            }
        });
        
        return rows;
    }

    /**
     * Đồng bộ từ Firestore xuống SQLite
     */
    private void syncFromFirestore() {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<suCo>>() {
            @Override
            public void onSuccess(List<suCo> remoteData) {
                updateLocalCache(remoteData);
                Log.d(TAG, "Synced " + remoteData.size() + " suCo from Firestore");
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
    private void updateLocalCache(List<suCo> remoteData) {
        for (suCo s : remoteData) {
            suCo existing = localDao.getID(String.valueOf(s.getMasuCo()));
            if (existing != null) {
                localDao.update(s);
            } else {
                localDao.insert(s);
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
        remoteRepo.listenForChanges(new FirestoreRepository.FirestoreCallback<List<suCo>>() {
            @Override
            public void onSuccess(List<suCo> result) {
                updateLocalCache(result);
                Log.d(TAG, "Real-time update: " + result.size() + " issues");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Real-time sync error", e);
            }
        });
    }
}
