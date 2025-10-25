/**
 * MIGRATION GUIDE: DAO → HybridDao
 * 
 * Cách chuyển từ SQLite-only DAO sang Hybrid (SQLite + Firestore)
 */

// ============================================================================
// EXAMPLE 1: PhongTroActivity
// ============================================================================

// ❌ BEFORE (SQLite only)
public class PhongTroActivity extends AppCompatActivity {
    private phongTroDao dao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new phongTroDao(this);
        loadRooms();
    }
    
    private void loadRooms() {
        List<PhongTro> rooms = dao.getAll();
        adapter.setData(rooms);
    }
    
    private void addRoom(PhongTro room) {
        long id = dao.insert(room);
        loadRooms(); // Refresh
    }
}

// ✅ AFTER (Hybrid: SQLite + Firestore)
public class PhongTroActivity extends AppCompatActivity {
    private PhongTroHybridDao hybridDao; // Changed
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hybridDao = new PhongTroHybridDao(this); // Changed
        hybridDao.enableRealtimeSync(); // NEW: Real-time sync
        loadRooms();
    }
    
    private void loadRooms() {
        List<PhongTro> rooms = hybridDao.getAll(); // Same API!
        adapter.setData(rooms);
        // Data từ SQLite → Instant display ⚡
        // Background sync từ Firestore → Auto update ☁️
    }
    
    private void addRoom(PhongTro room) {
        long id = hybridDao.insert(room); // Same API!
        loadRooms(); // Refresh
        // SQLite insert → UI update ngay ⚡
        // Firestore sync → Device khác cũng thấy ☁️
    }
}

// ============================================================================
// EXAMPLE 2: HopDongActivity
// ============================================================================

// ❌ BEFORE
public class HopDongActivity extends AppCompatActivity {
    private hopDongDao dao;
    
    private void loadActiveContracts() {
        List<HopDong> contracts = dao.getAllActiveContracts();
        // ...
    }
}

// ✅ AFTER
public class HopDongActivity extends AppCompatActivity {
    private HopDongHybridDao hybridDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hybridDao = new HopDongHybridDao(this);
        hybridDao.enableRealtimeSync(); // Bật real-time
    }
    
    private void loadActiveContracts() {
        List<HopDong> contracts = hybridDao.getActiveContracts();
        // Instant từ SQLite + background sync từ Firestore
    }
}

// ============================================================================
// EXAMPLE 3: HoaDonActivity
// ============================================================================

// ❌ BEFORE
public class HoaDonActivity extends AppCompatActivity {
    private hoaDonDao dao;
    
    private void loadUnpaidBills() {
        List<HoaDon> bills = dao.getAllUnpaid();
        adapter.setData(bills);
    }
    
    private void markAsPaid(HoaDon bill) {
        bill.setTrangThai(1);
        dao.update(bill);
        loadUnpaidBills();
    }
}

// ✅ AFTER
public class HoaDonActivity extends AppCompatActivity {
    private HoaDonHybridDao hybridDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hybridDao = new HoaDonHybridDao(this);
        hybridDao.enableRealtimeSync(); // Real-time notifications
    }
    
    private void loadUnpaidBills() {
        List<HoaDon> bills = hybridDao.getUnpaidBills();
        adapter.setData(bills);
        // Firestore listener → Khi chủ trọ thanh toán, tenant thấy ngay!
    }
    
    private void markAsPaid(HoaDon bill) {
        bill.setTrangThai(1);
        hybridDao.update(bill);
        loadUnpaidBills();
        // SQLite update → UI refresh ngay
        // Firestore sync → Chủ trọ thấy ngay tenant đã trả tiền
    }
}

// ============================================================================
// EXAMPLE 4: LoginActivity (ChuTro Authentication)
// ============================================================================

// ❌ BEFORE
public class LoginActivity extends AppCompatActivity {
    private chuTroDao dao;
    
    private void login(String email, String password) {
        ChuTro chuTro = dao.authenticate(email, password);
        if (chuTro != null) {
            // Login success
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}

// ✅ AFTER
public class LoginActivity extends AppCompatActivity {
    private ChuTroHybridDao hybridDao;
    
    private void login(String email, String password) {
        hybridDao = new ChuTroHybridDao(this);
        
        hybridDao.authenticate(email, password, new FirestoreRepository.FirestoreCallback<ChuTro>() {
            @Override
            public void onSuccess(ChuTro chuTro) {
                // Check trạng thái từ Firestore (latest data)
                if (chuTro.getTrangThai() == 1) { // Approved
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else if (chuTro.getTrangThai() == 0) { // Pending
                    Toast.makeText(LoginActivity.this, "Tài khoản chờ duyệt", Toast.LENGTH_SHORT).show();
                } else { // Banned
                    Toast.makeText(LoginActivity.this, "Tài khoản bị khóa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

// ============================================================================
// EXAMPLE 5: SuCoActivity (Real-time Issue Tracking)
// ============================================================================

// ❌ BEFORE
public class SuCoActivity extends AppCompatActivity {
    private suCoDao dao;
    
    private void loadPendingIssues() {
        List<SuCo> issues = dao.getAllPending();
        adapter.setData(issues);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadPendingIssues(); // Manual refresh
    }
}

// ✅ AFTER
public class SuCoActivity extends AppCompatActivity {
    private SuCoHybridDao hybridDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hybridDao = new SuCoHybridDao(this);
        
        // REAL-TIME: Tenant báo sự cố → Landlord thấy ngay!
        hybridDao.enableRealtimeSync();
        
        loadPendingIssues();
    }
    
    private void loadPendingIssues() {
        List<SuCo> issues = hybridDao.getPendingIssues();
        adapter.setData(issues);
        // Firestore listener tự động update khi có sự cố mới
        // Không cần manual refresh!
    }
    
    private void resolveIssue(SuCo issue) {
        issue.setTrangThai(2); // Resolved
        hybridDao.update(issue);
        // Tenant thấy ngay trạng thái đã xử lý!
    }
}

// ============================================================================
// EXAMPLE 6: NguoiThueActivity
// ============================================================================

// ❌ BEFORE
public class NguoiThueActivity extends AppCompatActivity {
    private nguoiThueDao dao;
    
    private void loadTenants() {
        List<NguoiThue> tenants = dao.getAll();
        adapter.setData(tenants);
    }
}

// ✅ AFTER
public class NguoiThueActivity extends AppCompatActivity {
    private NguoiThueHybridDao hybridDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hybridDao = new NguoiThueHybridDao(this);
        hybridDao.enableRealtimeSync();
        loadTenants();
    }
    
    private void loadTenants() {
        String chuTroId = getCurrentUserId();
        List<NguoiThue> tenants = hybridDao.getByChuTro(chuTroId);
        adapter.setData(tenants);
        // Multi-device: Update tenant info trên desktop → Mobile thấy ngay
    }
    
    private void loadUnassignedTenants() {
        List<NguoiThue> unassigned = hybridDao.getUnassignedTenants();
        // Tenant chưa có phòng → Assign phòng → Sync across devices
    }
}

// ============================================================================
// MIGRATION CHECKLIST
// ============================================================================

/*
 * Files cần sửa:
 * 
 * ✅ BaiDangActivity.java (DONE - already using BaiDangHybridDao)
 * ⬜ PhongTroActivity.java
 * ⬜ HopDongActivity.java
 * ⬜ NguoiThueActivity.java
 * ⬜ HoaDonActivity.java
 * ⬜ SuCoActivity.java
 * ⬜ LoginActivity.java (ChuTroActivity)
 * ⬜ LoaiPhongActivity.java
 * 
 * STEPS:
 * 1. Import HybridDao class
 * 2. Replace dao → hybridDao
 * 3. Add enableRealtimeSync() in onCreate()
 * 4. Test offline behavior
 * 5. Test multi-device sync
 */

// ============================================================================
// KEY BENEFITS
// ============================================================================

/*
 * ⚡ PERFORMANCE:
 * - Read: 5ms (SQLite) vs 500ms (Firestore only)
 * - Write: Instant UI update, background cloud sync
 * 
 * 📱 MULTI-DEVICE:
 * - Real-time sync across devices
 * - Collaborative features (landlord + tenant + accountant)
 * 
 * 🔄 OFFLINE-FIRST:
 * - Hoạt động khi mất mạng
 * - Auto-sync khi có network trở lại
 * 
 * 💪 RELIABILITY:
 * - SQLite cache đảm bảo data availability
 * - Firestore đảm bảo data persistence
 */
