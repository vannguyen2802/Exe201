package com.example.nestera.Firebase;

import android.content.Context;
import android.util.Log;

import com.example.nestera.Dao.hopDongDao;
import com.example.nestera.model.HopDong;

import java.util.List;

/**
 * Hybrid Data Manager cho HopDong
 * ĐỌC: SQLite (cache) → Firestore (background sync)
 * GHI: Song song SQLite + Firestore
 */
public class HopDongHybridDao {
    private static final String TAG = "HopDongHybrid";
    private final Context context;
    private final hopDongDao localDao;
    private final HopDongRepository remoteRepo;

    public HopDongHybridDao(Context context) {
        this.context = context;
        this.localDao = new hopDongDao(context);
        this.remoteRepo = new HopDongRepository();
    }

    /**
     * Lấy tất cả hợp đồng
     */
    public List<HopDong> getAll() {
        List<HopDong> localData = localDao.getAll();
        syncFromFirestore();
        return localData;
    }

    /**
     * Lấy hợp đồng theo ID
     */
    public HopDong getById(int id) {
        HopDong local = localDao.getID(String.valueOf(id));
        
        // Background sync
        remoteRepo.getById(String.valueOf(id), new FirestoreRepository.FirestoreCallback<HopDong>() {
            @Override
            public void onSuccess(HopDong result) {
                if (result != null) {
                    localDao.update(result);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync contract from Firestore", e);
            }
        });
        
        return local;
    }

    /**
     * Lấy hợp đồng theo phòng
     */
    public List<HopDong> getByMaPhong(int maPhong) {
        List<HopDong> localData = localDao.getHopDongByMaPhong(maPhong);
        
        // Background sync
        remoteRepo.getByMaPhong(maPhong, new FirestoreRepository.FirestoreCallback<List<HopDong>>() {
            @Override
            public void onSuccess(List<HopDong> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync contracts by room", e);
            }
        });
        
        return localData;
    }

    /**
     * Lấy hợp đồng đang hoạt động
     */
    public List<HopDong> getActiveContracts() {
        // Filter active contracts from getAll() - assuming active means trangThai phòng = 1
        List<HopDong> all = localDao.getAll();
        // Return all for now, can add filtering logic if needed
        List<HopDong> localData = all;
        
        // Background sync
        remoteRepo.getActiveContracts(new FirestoreRepository.FirestoreCallback<List<HopDong>>() {
            @Override
            public void onSuccess(List<HopDong> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync active contracts", e);
            }
        });
        
        return localData;
    }

    /**
     * Thêm hợp đồng mới - ghi song song
     */
    public long insert(HopDong hopDong) {
        // 1. Ghi local ngay
        long localId = localDao.insert(hopDong);
        hopDong.setMaHopDong((int) localId);
        
        // 2. Ghi remote async
        remoteRepo.insert(hopDong, new FirestoreRepository.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String documentId) {
                Log.d(TAG, "HopDong synced to Firestore: " + documentId);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync HopDong to Firestore", e);
            }
        });
        
        return localId;
    }

    /**
     * Cập nhật hợp đồng - ghi song song
     */
    public int update(HopDong hopDong) {
        // 1. Update local
        int rows = localDao.update(hopDong);
        
        // 2. Update remote async
        remoteRepo.update(String.valueOf(hopDong.getMaHopDong()), hopDong, 
            new FirestoreRepository.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "HopDong updated in Firestore");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Failed to update HopDong in Firestore", e);
                }
            });
        
        return rows;
    }

    /**
     * Xóa hợp đồng - ghi song song
     */
    public int delete(int id) {
        // 1. Delete local
        int rows = localDao.delete(String.valueOf(id));
        
        // 2. Delete remote async
        remoteRepo.delete(String.valueOf(id), new FirestoreRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "HopDong deleted from Firestore");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to delete HopDong from Firestore", e);
            }
        });
        
        return rows;
    }

    /**
     * Đồng bộ từ Firestore xuống SQLite
     */
    private void syncFromFirestore() {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<HopDong>>() {
            @Override
            public void onSuccess(List<HopDong> remoteData) {
                updateLocalCache(remoteData);
                Log.d(TAG, "Synced " + remoteData.size() + " HopDong from Firestore");
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
    private void updateLocalCache(List<HopDong> remoteData) {
        for (HopDong h : remoteData) {
            HopDong existing = localDao.getID(String.valueOf(h.getMaHopDong()));
            if (existing != null) {
                localDao.update(h);
            } else {
                localDao.insert(h);
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
        remoteRepo.listenForChanges(new FirestoreRepository.FirestoreCallback<List<HopDong>>() {
            @Override
            public void onSuccess(List<HopDong> result) {
                // Update local cache inline
                for (HopDong h : result) {
                    HopDong existing = localDao.getID(String.valueOf(h.getMaHopDong()));
                    if (existing != null) {
                        localDao.update(h);
                    } else {
                        localDao.insert(h);
                    }
                }
                Log.d(TAG, "Real-time update: " + result.size() + " contracts");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Real-time sync error", e);
            }
        });
    }

    /**
     * Lấy hợp đồng theo ID dạng String (legacy method)
     */
    public HopDong getID(String id) {
        return localDao.getID(id);
    }

    /**
     * Lấy danh sách hợp đồng theo mã phòng (legacy method)
     */
    public List<HopDong> getHopDongByMaPhong(int maPhong) {
        return getByMaPhong(maPhong);
    }

    /**
     * Cập nhật trạng thái phòng (delegates to PhongTro operations)
     * Note: This should ideally be in PhongTroHybridDao
     */
    public void updateTrangThaiPhong(int maphong, int trangthai) {
        localDao.updateTrangThaiPhong(maphong, trangthai);
        // TODO: Should sync PhongTro to Firestore via PhongTroHybridDao
    }

    /**
     * Lấy mã người thuê theo mã phòng
     */
    public String getMaNguoiThueByMaPhong(int maPhong) {
        return localDao.getMaNguoiThueByMaPhong(maPhong);
    }
}
