package com.example.nestera.Firebase;

import android.content.Context;
import android.util.Log;

import com.example.nestera.Dao.hoaDonDao;
import com.example.nestera.model.HoaDon;

import java.util.List;

/**
 * Hybrid Data Manager cho HoaDon
 * ĐỌC: SQLite (cache) → Firestore (background sync)
 * GHI: Song song SQLite + Firestore
 */
public class HoaDonHybridDao {
    private static final String TAG = "HoaDonHybrid";
    private final Context context;
    private final hoaDonDao localDao;
    private final HoaDonRepository remoteRepo;

    public HoaDonHybridDao(Context context) {
        this.context = context;
        this.localDao = new hoaDonDao(context);
        this.remoteRepo = new HoaDonRepository();
    }

    /**
     * Lấy tất cả hóa đơn
     */
    public List<HoaDon> getAll() {
        List<HoaDon> localData = localDao.getAll();
        syncFromFirestore();
        return localData;
    }

    /**
     * Lấy tất cả hóa đơn với callback - đợi sync Firestore xong
     */
    public void getAllWithSync(FirestoreRepository.FirestoreCallback<List<HoaDon>> callback) {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<HoaDon>>() {
            @Override
            public void onSuccess(List<HoaDon> remoteData) {
                updateLocalCache(remoteData);
                callback.onSuccess(localDao.getAll());
                Log.d(TAG, "Synced " + remoteData.size() + " HoaDon from Firestore");
            }

            @Override
            public void onError(Exception e) {
                callback.onSuccess(localDao.getAll());
                Log.e(TAG, "Sync failed, using local data", e);
            }
        });
    }

    /**
     * Lấy hóa đơn theo ID
     */
    public HoaDon getById(int id) {
        HoaDon local = localDao.getID(String.valueOf(id));
        
        // Background sync
        remoteRepo.getById(String.valueOf(id), new FirestoreRepository.FirestoreCallback<HoaDon>() {
            @Override
            public void onSuccess(HoaDon result) {
                if (result != null) {
                    localDao.update(result);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync invoice from Firestore", e);
            }
        });
        
        return local;
    }

    /**
     * Lấy hóa đơn theo phòng
     */
    public List<HoaDon> getByMaPhong(int maPhong) {
        List<HoaDon> localData = localDao.getHoaDonByMaPhong(maPhong);
        
        // Background sync
        remoteRepo.getByMaPhong(maPhong, new FirestoreRepository.FirestoreCallback<List<HoaDon>>() {
            @Override
            public void onSuccess(List<HoaDon> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync invoices by room", e);
            }
        });
        
        return localData;
    }

    /**
     * Lấy hóa đơn chưa thanh toán
     */
    public List<HoaDon> getAllUnpaid() {
        // Sử dụng getAll và filter trong Java thay vì SQL query riêng
        List<HoaDon> all = localDao.getAll();
        List<HoaDon> unpaid = new java.util.ArrayList<>();
        for (HoaDon h : all) {
            if (h.getTrangThai() != 2) { // 2 = đã thanh toán
                unpaid.add(h);
            }
        }
        
        // Background sync
        remoteRepo.getUnpaidBills(new FirestoreRepository.FirestoreCallback<List<HoaDon>>() {
            @Override
            public void onSuccess(List<HoaDon> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync unpaid bills", e);
            }
        });
        
        return unpaid;
    }

    /**
     * Lấy hóa đơn theo tháng
     */
    public List<HoaDon> getByMonth(int month, int year) {
        // Filter from getAll() instead
        List<HoaDon> all = localDao.getAll();
        List<HoaDon> filtered = new java.util.ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        for (HoaDon h : all) {
            if (h.getNgayTao() != null) {
                cal.setTime(h.getNgayTao());
                if (cal.get(java.util.Calendar.MONTH) == month && 
                    cal.get(java.util.Calendar.YEAR) == year) {
                    filtered.add(h);
                }
            }
        }
        
        // Background sync
        remoteRepo.getByMonth(month, year, new FirestoreRepository.FirestoreCallback<List<HoaDon>>() {
            @Override
            public void onSuccess(List<HoaDon> result) {
                updateLocalCache(result);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync monthly invoices", e);
            }
        });
        
        return filtered;
    }

    /**
     * Thêm hóa đơn mới - ghi song song
     */
    public long insert(HoaDon hoaDon) {
        // 1. Ghi local ngay
        long localId = localDao.insert(hoaDon);
        hoaDon.setMaHoaDon((int) localId);
        
        // 2. Ghi remote async
        remoteRepo.insert(hoaDon, new FirestoreRepository.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String documentId) {
                Log.d(TAG, "HoaDon synced to Firestore: " + documentId);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to sync HoaDon to Firestore", e);
            }
        });
        
        return localId;
    }

    /**
     * Cập nhật hóa đơn - ghi song song
     */
    public int update(HoaDon hoaDon) {
        // 1. Update local
        int rows = localDao.update(hoaDon);
        
        // 2. Update remote async
        remoteRepo.update(String.valueOf(hoaDon.getMaHoaDon()), hoaDon, 
            new FirestoreRepository.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "HoaDon updated in Firestore");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Failed to update HoaDon in Firestore", e);
                }
            });
        
        return rows;
    }

    /**
     * Xóa hóa đơn - ghi song song
     */
    public int delete(int id) {
        // 1. Delete local
        int rows = localDao.delete(String.valueOf(id));
        
        // 2. Delete remote async
        remoteRepo.delete(String.valueOf(id), new FirestoreRepository.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "HoaDon deleted from Firestore");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to delete HoaDon from Firestore", e);
            }
        });
        
        return rows;
    }

    /**
     * Đồng bộ từ Firestore xuống SQLite
     */
    private void syncFromFirestore() {
        remoteRepo.getAll(new FirestoreRepository.FirestoreCallback<List<HoaDon>>() {
            @Override
            public void onSuccess(List<HoaDon> remoteData) {
                updateLocalCache(remoteData);
                Log.d(TAG, "Synced " + remoteData.size() + " HoaDon from Firestore");
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
    private void updateLocalCache(List<HoaDon> remoteData) {
        for (HoaDon h : remoteData) {
            HoaDon existing = localDao.getID(String.valueOf(h.getMaHoaDon()));
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
        remoteRepo.listenForChanges(new FirestoreRepository.FirestoreCallback<List<HoaDon>>() {
            @Override
            public void onSuccess(List<HoaDon> result) {
                // Update local cache inline
                for (HoaDon h : result) {
                    HoaDon existing = localDao.getID(String.valueOf(h.getMaHoaDon()));
                    if (existing != null) {
                        localDao.update(h);
                    } else {
                        localDao.insert(h);
                    }
                }
                Log.d(TAG, "Real-time update: " + result.size() + " invoices");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Real-time sync error", e);
            }
        });
    }
}
