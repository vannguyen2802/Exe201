package com.example.nestera.Firebase;

import android.content.Context;
import android.util.Log;

import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.model.NguoiThue;

import java.util.List;
import java.util.ArrayList;

/**
 * Hybrid Data Manager cho NguoiThue
 * ĐỌC: SQLite (cache) → Firestore (background sync)
 * GHI: Song song SQLite + Firestore
 */
public class NguoiThueHybridDao {
    private static final String TAG = "NguoiThueHybrid";
    private final Context context;
    private final nguoiThueDao localDao;
    private final NguoiThueRepository remoteRepo;

    public NguoiThueHybridDao(Context context) {
        this.context = context;
        this.localDao = new nguoiThueDao(context);
        this.remoteRepo = new NguoiThueRepository();
    }

    /**
     * Lấy tất cả người thuê
     */
    public List<NguoiThue> getAll() {
        List<NguoiThue> localData = localDao.getAll();
        syncFromFirestore();
        return localData;
    }

    /**
     * Lấy tất cả người thuê với callback - đợi sync Firestore xong
     */
    public void getAllWithSync(FirestoreRepository.FirestoreCallback<List<NguoiThue>> callback) {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<NguoiThue>>() {
            @Override
            public void onSuccess(List<NguoiThue> remoteData) {
                updateLocalCache(remoteData);
                callback.onSuccess(localDao.getAll());
                Log.d(TAG, "Synced " + remoteData.size() + " NguoiThue from Firestore");
            }

            @Override
            public void onError(Exception e) {
                callback.onSuccess(localDao.getAll());
                Log.e(TAG, "Sync failed, using local data", e);
            }
        });
    }

    /**
     * Lấy người thuê theo ID
     */
    public NguoiThue getById(String id) {
        NguoiThue local = localDao.getID(id);
        
        // Background sync
        remoteRepo.getById(id, new FirestoreRepository.FirestoreCallback<NguoiThue>() {
            @Override
            public void onSuccess(NguoiThue result) {
                if (result != null) {
                    localDao.update(result);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync tenant from Firestore", e);
            }
        });
        
        return local;
    }

    /**
     * Alias for getById - for Activity compatibility
     */
    public NguoiThue getID(String id) {
        return getById(id);
    }

    /**
     * Lấy người thuê theo chủ trọ
     */
    public List<NguoiThue> getByChuTro(String chuTroId) {
        List<NguoiThue> localData = localDao.getByChuTro(chuTroId);
        
        // Background sync
        remoteRepo.getByChuTro(chuTroId, new FirestoreRepository.FirestoreCallback<List<NguoiThue>>() {
            @Override
            public void onSuccess(List<NguoiThue> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync tenants by landlord", e);
            }
        });
        
        return localData;
    }

    /**
     * Lấy người thuê theo chủ trọ với callback - đợi sync Firestore xong
     */
    public void getByChuTroWithSync(String chuTroId, FirestoreRepository.FirestoreCallback<List<NguoiThue>> callback) {
        remoteRepo.getByChuTro(chuTroId, new FirestoreRepository.FirestoreCallback<List<NguoiThue>>() {
            @Override
            public void onSuccess(List<NguoiThue> result) {
                updateLocalCache(result);
                callback.onSuccess(localDao.getByChuTro(chuTroId));
            }

            @Override
            public void onError(Exception e) {
                callback.onSuccess(localDao.getByChuTro(chuTroId));
                Log.e(TAG, "Sync failed for getByChuTro", e);
            }
        });
    }

    /**
     * Lấy người thuê theo phòng
     */
    public List<NguoiThue> getByMaPhong(int maPhong) {
        List<NguoiThue> localData = localDao.getNguoiThueByMaPhong(maPhong);
        
        // Background sync
        remoteRepo.getByMaPhong(maPhong, new FirestoreRepository.FirestoreCallback<List<NguoiThue>>() {
            @Override
            public void onSuccess(List<NguoiThue> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync tenants by room", e);
            }
        });
        
        return localData;
    }

    /**
     * Lấy người thuê chưa có phòng
     */
    public List<NguoiThue> getUnassignedTenants() {
        List<NguoiThue> localData = localDao.getNguoiThueChuaCoPhong();
        
        // Background sync
        remoteRepo.getUnassignedTenants(new FirestoreRepository.FirestoreCallback<List<NguoiThue>>() {
            @Override
            public void onSuccess(List<NguoiThue> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync unassigned tenants", e);
            }
        });
        
        return localData;
    }

    /**
     * Lấy người thuê theo username
     */
    public ArrayList<NguoiThue> getNguoiThueByUser(String user) {
        ArrayList<NguoiThue> result = localDao.getNguoiThueByUser(user);
        return result != null ? result : new ArrayList<>();
    }

    /**
     * Thêm người thuê mới - ghi song song
     */
    public long insert(NguoiThue nguoiThue) {
        // 1. Ghi local ngay
        localDao.insert(nguoiThue);

        // 2. Ghi remote async, dùng mã người thuê làm documentId
        remoteRepo.insert(nguoiThue.getMaNguoithue(), nguoiThue, new FirestoreRepository.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String documentId) {
                Log.d(TAG, "NguoiThue synced to Firestore: " + documentId);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync NguoiThue to Firestore", e);
            }
        });

        return 1;
    }

    /**
     * Cập nhật người thuê - ghi song song
     */
    public int update(NguoiThue nguoiThue) {
        // 1. Update local
        int rows = localDao.update(nguoiThue);
        
        // 2. Update remote async
        remoteRepo.update(nguoiThue.getMaNguoithue(), nguoiThue, 
            new FirestoreRepository.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "NguoiThue updated in Firestore");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Failed to update NguoiThue in Firestore", e);
                }
            });
        
        return rows;
    }

    /**
     * Xóa người thuê - ghi song song
     */
    public int delete(String id) {
        Log.d(TAG, "Attempting to delete tenant with ID: " + id);
        
        // 1. Delete local
        int rows = localDao.delete(id);
        Log.d(TAG, "Local delete result: " + rows + " rows affected");
        
        // 2. Delete remote async
        remoteRepo.delete(id, new FirestoreRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "NguoiThue deleted from Firestore");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to delete NguoiThue from Firestore", e);
            }
        });
        
        return rows;
    }

    /**
     * Đồng bộ từ Firestore xuống SQLite
     */
    private void syncFromFirestore() {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<NguoiThue>>() {
            @Override
            public void onSuccess(List<NguoiThue> remoteData) {
                updateLocalCache(remoteData);
                Log.d(TAG, "Synced " + remoteData.size() + " NguoiThue from Firestore");
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
    private void updateLocalCache(List<NguoiThue> remoteData) {
        for (NguoiThue n : remoteData) {
            NguoiThue existing = localDao.getID(n.getMaNguoithue());
            if (existing != null) {
                localDao.update(n);
            } else {
                localDao.insert(n);
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
        remoteRepo.listenForChanges(new FirestoreRepository.FirestoreCallback<List<NguoiThue>>() {
            @Override
            public void onSuccess(List<NguoiThue> result) {
                updateLocalCache(result);
                Log.d(TAG, "Real-time update: " + result.size() + " tenants");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Real-time sync error", e);
            }
        });
    }
}
