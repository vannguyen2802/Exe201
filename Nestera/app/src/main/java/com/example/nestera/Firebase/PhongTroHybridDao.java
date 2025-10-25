package com.example.nestera.Firebase;

import android.content.Context;
import android.util.Log;

import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.model.PhongTro;

import java.util.List;

/**
 * Hybrid Data Manager cho PhongTro
 * ĐỌC: SQLite (cache) → Firestore (background sync)
 * GHI: Song song SQLite + Firestore
 */
public class PhongTroHybridDao {
    private static final String TAG = "PhongTroHybrid";
    private final Context context;
    private final phongTroDao localDao;
    private final PhongTroRepository remoteRepo;

    public PhongTroHybridDao(Context context) {
        this.context = context;
        this.localDao = new phongTroDao(context);
        this.remoteRepo = new PhongTroRepository();
    }

    /**
     * Lấy tất cả phòng trọ
     */
    public List<PhongTro> getAll() {
        List<PhongTro> localData = localDao.getAll();
        syncFromFirestore();
        return localData;
    }

    /**
     * Lấy phòng trọ theo ID
     */
    public PhongTro getById(int id) {
        PhongTro local = localDao.getID(String.valueOf(id));
        
        // Background sync specific room
        remoteRepo.getById(String.valueOf(id), new FirestoreRepository.FirestoreCallback<PhongTro>() {
            @Override
            public void onSuccess(PhongTro result) {
                if (result != null) {
                    localDao.update(result);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync room from Firestore", e);
            }
        });
        
        return local;
    }

    /**
     * Lấy phòng theo loại phòng
     */
    public List<PhongTro> getAllByLoaiPhong(int loaiPhongId) {
        // Filter from getAll() since phongTroDao doesn't have this method
        List<PhongTro> all = localDao.getAll();
        List<PhongTro> filtered = new java.util.ArrayList<>();
        for (PhongTro p : all) {
            if (p.getMaLoai() == loaiPhongId) {
                filtered.add(p);
            }
        }
        
        // Background sync
        remoteRepo.getByLoaiPhong(loaiPhongId, new FirestoreRepository.FirestoreCallback<List<PhongTro>>() {
            @Override
            public void onSuccess(List<PhongTro> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync rooms by loaiPhong", e);
            }
        });
        
        return filtered;
    }

    /**
     * Lấy phòng trống
     */
    public List<PhongTro> getAvailableRooms() {
        // Use getPhongByTrangThai(0) - 0 = available
        List<PhongTro> localData = localDao.getPhongByTrangThai(0);
        
        // Background sync
        remoteRepo.getAvailableRooms(new FirestoreRepository.FirestoreCallback<List<PhongTro>>() {
            @Override
            public void onSuccess(List<PhongTro> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync available rooms", e);
            }
        });
        
        return localData;
    }

    /**
     * Thêm phòng mới - ghi song song
     */
    public long insert(PhongTro phongTro) {
        // 1. Ghi local ngay
        long localId = localDao.insert(phongTro);
        phongTro.setMaPhong((int) localId);
        
        // 2. Ghi remote async
        remoteRepo.insert(phongTro, new FirestoreRepository.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String documentId) {
                Log.d(TAG, "PhongTro synced to Firestore: " + documentId);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync PhongTro to Firestore", e);
            }
        });
        
        return localId;
    }

    /**
     * Cập nhật phòng - ghi song song
     */
    public int update(PhongTro phongTro) {
        // 1. Update local
        int rows = localDao.update(phongTro);
        
        // 2. Update remote async
        remoteRepo.update(String.valueOf(phongTro.getMaPhong()), phongTro, 
            new FirestoreRepository.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "PhongTro updated in Firestore");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Failed to update PhongTro in Firestore", e);
                }
            });
        
        return rows;
    }

    /**
     * Xóa phòng trọ - ghi song song
     */
    public int delete(int id) {
        // 1. Delete local
        int rows = localDao.delete(String.valueOf(id));
        
        // 2. Delete remote async
        remoteRepo.delete(String.valueOf(id), new FirestoreRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "PhongTro deleted from Firestore");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to delete PhongTro from Firestore", e);
            }
        });
        
        return rows;
    }

    /**
     * Đồng bộ từ Firestore xuống SQLite
     */
    private void syncFromFirestore() {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<PhongTro>>() {
            @Override
            public void onSuccess(List<PhongTro> remoteData) {
                updateLocalCache(remoteData);
                Log.d(TAG, "Synced " + remoteData.size() + " PhongTro from Firestore");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Sync failed", e);
            }
        });
    }

    /**
     * Update local cache với data từ Firestore
     */
    private void updateLocalCache(List<PhongTro> remoteData) {
        for (PhongTro p : remoteData) {
            PhongTro existing = localDao.getID(String.valueOf(p.getMaPhong()));
            if (existing != null) {
                localDao.update(p);
            } else {
                localDao.insert(p);
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
        remoteRepo.listenForChanges(new FirestoreRepository.FirestoreCallback<List<PhongTro>>() {
            @Override
            public void onSuccess(List<PhongTro> result) {
                // Update local cache inline
                for (PhongTro p : result) {
                    PhongTro existing = localDao.getID(String.valueOf(p.getMaPhong()));
                    if (existing != null) {
                        localDao.update(p);
                    } else {
                        localDao.insert(p);
                    }
                }
                Log.d(TAG, "Real-time update: " + result.size() + " rooms");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Real-time sync error", e);
            }
        });
    }
}
