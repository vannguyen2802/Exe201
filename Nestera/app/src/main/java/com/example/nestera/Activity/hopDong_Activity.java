package com.example.nestera.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.List;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nestera.Adapter.HopDong_Adapter;
import com.example.nestera.Adapter.NguoiThueSpinerAdapter;
import com.example.nestera.Firebase.ImageUploader;
import com.example.nestera.Dao.hopDongDao;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.Firebase.BaiDangHybridDao;
import com.example.nestera.Firebase.HopDongHybridDao;
import com.example.nestera.Firebase.NguoiThueHybridDao;
import com.example.nestera.Firebase.PhongTroHybridDao;
import com.example.nestera.MainActivity;
import com.example.nestera.R;
import com.example.nestera.model.HopDong;
import com.example.nestera.model.NguoiThue;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class hopDong_Activity extends AppCompatActivity {
    HopDongHybridDao hybridDao;
    ArrayList<HopDong> list;
    ArrayList<NguoiThue> list_nt;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    ListView lsthopDong;
    HopDong_Adapter hopDongAdapter;
    HopDong item;
    ArrayList<HopDong> list_hdm = new ArrayList<>();
    ArrayList<HopDong> list_hdnt = new ArrayList<>();
    ImageView btnAdd,imgAnhhd;
    Button btnChonAnh;
    EditText edtma_hd, edtTenkh_hd, edtSdt_hd, edtCCCD_hd, edtDiaChi_hd, edtNgayki_hd, edtSothang_hd, edtSoPhong_hd, edtTienCoc_hd, edtTienPhong_hd, edtSonguoi_hd, edtSoxe_hd, edtGhiChu_hd;
    Spinner spinner;
    Dialog currentDialog; // Lưu reference của dialog hiện tại
    int position,maphong,gia,mp;
    String mant,dc,sdt,cccd;
    NguoiThueHybridDao hybridDao_nt;
    NguoiThue nt;
    NguoiThueSpinerAdapter spinerAdapter;
    PhongTroHybridDao hybridDao_pt;
    BaiDangHybridDao baiDangHybridDao;
    // Keep old DAOs for specialty methods
    nguoiThueDao dao_nt;
    phongTroDao dao_pt;
    Button btnTaoHD,btnHuy;
    byte[] hinhAnh;
    java.util.ArrayList<Uri> selectedImageUris = new java.util.ArrayList<>(); // Nhiều ảnh hợp đồng
    TextView tvSelectedImages; // Hiển thị số ảnh đã chọn
    final int REQUEST_CODE_FOLDER = 456;
    public static final int REQUEST_CODE_RENEW = 1004;
    public Integer pendingRenewHopDongId = null;
    public Integer pendingRenewThoiHan = null;
    public Integer pendingRenewNewMaPhong = null;
    public Integer pendingRenewOldMaPhong = null;
    public byte[] pendingRenewImageBytes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        android.util.Log.d("HopDongActivity", "=== onCreate() START ===");
        android.util.Log.d("HopDongActivity", "Activity: " + this.getClass().getSimpleName());
        
        setContentView(R.layout.activity_hop_dong);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quản lý hợp đồng");

        Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        btnAdd = findViewById(R.id.btnadd_toolbar);
        // Enable add button for creating contracts
        btnAdd.setVisibility(View.VISIBLE);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        lsthopDong = findViewById(R.id.lsthopDong);
        hybridDao = new HopDongHybridDao(hopDong_Activity.this);
        hybridDao.enableRealtimeSync(); // Enable real-time sync
        baiDangHybridDao = new BaiDangHybridDao(hopDong_Activity.this);
        list = new ArrayList<>(); // Initialize empty, will load via callback
        
        // Load data from Firebase first
        android.util.Log.d("HopDongDebug", "Loading contracts from Firebase...");
        hybridDao.getAllWithSync(new com.example.nestera.Firebase.FirestoreRepository.FirestoreCallback<java.util.List<HopDong>>() {
            @Override
            public void onSuccess(java.util.List<HopDong> syncedList) {
                runOnUiThread(() -> {
                    list.clear();
                    list.addAll(syncedList);
                    
                    android.util.Log.d("HopDongDebug", "✅ Loaded " + list.size() + " contracts from Firebase");
                    
                    // Apply landlord filtering if needed
                    applyLandlordFiltering();
                    
                    // Setup adapter after data is loaded
                    setupContractListAdapter();
                    
                    // Handle role-specific logic AFTER data loads
                    handleRoleSpecificLogic();
                });
            }

            @Override
            public void onError(Exception e) {
                android.util.Log.e("HopDongDebug", "❌ Failed to load contracts", e);
                runOnUiThread(() -> {
                    list.clear();
                    setupContractListAdapter();
                    // Still handle role logic even on error
                    handleRoleSpecificLogic();
                });
            }
        });
    }
    
    private void handleRoleSpecificLogic() {
        SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        String role = preferences.getString("role", "");
        
        android.util.Log.d("HopDongDebug", "=== handleRoleSpecificLogic START ===");
        android.util.Log.d("HopDongDebug", "Role: " + role + ", Username: " + username);
        
        maphong = getIntent().getIntExtra("maphong", -1);
        android.util.Log.d("HopDongDebug", "Intent maphong: " + maphong);
        
        // Simple logic without additional Firebase calls to avoid conflicts
        if("LANDLORD".equalsIgnoreCase(role)) {
            if (maphong != -1) {
                android.util.Log.d("HopDongDebug", "LANDLORD with specific room: " + maphong);
                // Just show the data that's already loaded
            } else {
                android.util.Log.d("HopDongDebug", "LANDLORD viewing all contracts");
            }
        } else {
            android.util.Log.d("HopDongDebug", "Non-LANDLORD role: " + role);
            // Handle tenant logic if needed
        }
        
        android.util.Log.d("HopDongDebug", "=== handleRoleSpecificLogic END ===");
    }
    
    private void applyLandlordFiltering() {
        // Nếu là Chủ trọ, chỉ hiển thị hợp đồng của phòng thuộc chủ trọ này
        try {
            String roleLocal = getSharedPreferences("user11", MODE_PRIVATE).getString("role", "");
            String landlordId = getSharedPreferences("user11", MODE_PRIVATE).getString("username11", "");
            if ("LANDLORD".equalsIgnoreCase(roleLocal)) {
                java.util.HashSet<Integer> ownedRooms = new java.util.HashSet<>();
                java.util.List<com.example.nestera.model.BaiDang> posts = new com.example.nestera.Dao.baiDangDao(this).getByChuTro(landlordId);
                for (com.example.nestera.model.BaiDang bd : posts) {
                    try { if (bd.getMaPhong() != null) ownedRooms.add(bd.getMaPhong()); } catch (Exception ignored) {}
                }
                java.util.ArrayList<HopDong> filtered = new java.util.ArrayList<>();
                for (HopDong hd : list) {
                    if (ownedRooms.contains(hd.getMaPhong())) filtered.add(hd);
                }
                list.clear();
                list.addAll(filtered);
                android.util.Log.d("HopDongDebug", "Filtered to " + list.size() + " contracts for landlord");
            }
        } catch (Exception ignored) {}
    }
    
    private void setupContractListAdapter() {
        // Mặc định hiển thị danh sách ngắn gọn với nút Chi tiết cho LANDLORD
        lsthopDong.setAdapter(new android.widget.BaseAdapter() {
            @Override public int getCount() { return list.size(); }
            @Override public Object getItem(int position) { return list.get(position); }
            @Override public long getItemId(int position) { return list.get(position).getMaHopDong(); }
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    v = getLayoutInflater().inflate(R.layout.item_hopdong_brief, parent, false);
                }
                HopDong hd = list.get(position);
                ((android.widget.TextView)v.findViewById(R.id.tvMaHD)).setText("Mã: " + hd.getMaHopDong());
                ((android.widget.TextView)v.findViewById(R.id.tvNgayKy)).setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(hd.getNgayKy()));
                ((android.widget.TextView)v.findViewById(R.id.tvTenKhach)).setText("Khách: " + hd.getMaNguoiThue());
                String tenPhongBrief = hd.getTenPhong();
                if (tenPhongBrief == null || tenPhongBrief.isEmpty()) {
                    try {
                        com.example.nestera.Dao.phongTroDao pdao = new com.example.nestera.Dao.phongTroDao(hopDong_Activity.this);
                        com.example.nestera.model.PhongTro p = pdao.getID(String.valueOf(hd.getMaPhong()));
                        if (p != null && p.getTenPhong() != null) tenPhongBrief = p.getTenPhong();
                    } catch (Exception ignored) {}
                }
                ((android.widget.TextView)v.findViewById(R.id.tvSoPhong)).setText("Phòng: " + (tenPhongBrief!=null?tenPhongBrief:""+hd.getMaPhong()));
                // Trạng thái hợp đồng:
                // 1) Nếu phòng trống -> Đã Kết Thúc
                // 2) Nếu chưa hủy nhưng đã quá hạn (ngày ký + số tháng <= hôm nay) -> Hết Hạn
                // 3) Ngược lại -> Đang Thuê
                String statusText = "Đang Thuê";
                int color = 0xFF22C55E; // green
                boolean isCanceled = false;
                try {
                    com.example.nestera.Dao.phongTroDao pdao = new com.example.nestera.Dao.phongTroDao(hopDong_Activity.this);
                    com.example.nestera.model.PhongTro pr = pdao.getID(String.valueOf(hd.getMaPhong()));
                    if (pr != null) isCanceled = (pr.getTrangThai() == 0);
                } catch (Exception ignored) {}
                if (isCanceled) {
                    statusText = "Đã Kết Thúc";
                    color = 0xFFDC3545; // red
                } else {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(hd.getNgayKy());
                    cal.add(java.util.Calendar.MONTH, hd.getThoiHan());
                    java.util.Date endDate = cal.getTime();
                    java.util.Date today = new java.util.Date();
                    if (!today.before(endDate)) { // today >= endDate
                        statusText = "Hết Hạn";
                        color = 0xFFF59E0B; // orange
                    }
                }
                android.widget.TextView tvSt = v.findViewById(R.id.tvTrangThaiHD);
                tvSt.setText(statusText);
                tvSt.setTextColor(color);
                v.findViewById(R.id.btnChiTiet).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v2) {
                        // Mở màn Xem hợp đồng: dùng adapter chi tiết hiện tại
                        java.util.ArrayList<HopDong> single = new java.util.ArrayList<>();
                        single.add(hd);
                        lsthopDong.setAdapter(new HopDong_Adapter(hopDong_Activity.this, single, hopDong_Activity.this));
                    }
                });
                return v;
            }
        });
        SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        String role = preferences.getString("role", "");
        
        maphong = getIntent().getIntExtra("maphong", -1);
        list_hdm = new ArrayList<>(hybridDao.getHopDongByMaPhong(maphong));

        dao_nt=new nguoiThueDao(hopDong_Activity.this);
        mp = dao_nt.getMaPhongByUser(username);
        list_hdnt = new ArrayList<>(hybridDao.getHopDongByMaPhong(mp));
        
        // Kiểm tra theo role thay vì username
        if("LANDLORD".equalsIgnoreCase(role)) {
            // Chủ trọ: hiển thị hợp đồng của phòng hoặc cho phép tạo mới
            if (maphong != -1) {
                // Có mã phòng cụ thể
                if (list_hdm.isEmpty()) {
                    // Chưa có hợp đồng -> hiển thị thông báo và nút tạo thay vì auto-open dialog
                    list.clear();
                    android.util.Log.d("HopDongDebug", "No contracts for room " + maphong + ", auto-opening create dialog");
                    // Auto-open create contract dialog for new contracts
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        openDialog(); // Auto-open create dialog
                    }, 500);
                } else {
                    // Đã có hợp đồng -> nếu yêu cầu mở thẳng chi tiết, hiển thị ngay item đầu tiên
                    boolean openDetail = getIntent().getBooleanExtra("openDetail", false);
                    if (openDetail) {
                        java.util.ArrayList<HopDong> single = new java.util.ArrayList<>();
                        single.add(list_hdm.get(0));
                        lsthopDong.setAdapter(new HopDong_Adapter(hopDong_Activity.this, single, hopDong_Activity.this));
                    } else {
                        list.clear();
                        list.addAll(list_hdm);
                        if (lsthopDong.getAdapter() != null) {
                            ((android.widget.BaseAdapter)lsthopDong.getAdapter()).notifyDataSetChanged();
                        }
                    }
                }
            } else {
                // Không có mã phòng -> hiển thị tất cả hợp đồng dạng rút gọn
                // adapter đã set ở trên
                android.util.Log.d("HopDongDebug", "Showing all contracts for landlord");
            }
        } else {
            // Người thuê: chỉ xem hợp đồng của mình
            if (list_hdnt.isEmpty()) {
                list.clear();
                AlertDialog.Builder builder = new AlertDialog.Builder(hopDong_Activity.this);
                builder.setTitle("Cảnh báo");
                builder.setIcon(R.drawable.baseline_warning_24);
                builder.setMessage("Bạn chưa có hợp đồng");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                builder.show();
            } else {
                hopDongAdapter = new HopDong_Adapter(hopDong_Activity.this, list_hdnt, this);
                lsthopDong.setAdapter(hopDongAdapter);
            }
        }


    }
    public void startPickRenewImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_RENEW);
    }
    public void xoa(String Id){
        AlertDialog.Builder builder = new AlertDialog.Builder(hopDong_Activity.this);
        builder.setTitle("Cảnh báo");
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setMessage("Bạn có chắc chắn muốn kết thúc hợp đồng");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Lấy thông tin hợp đồng hiện tại (khi vào từ Quản lý hợp đồng, intent có thể không có maphong)
                String maNguoiThue;
                maphong = getIntent().getIntExtra("maphong", -1);
                HopDong current = null;
                try { current = hybridDao.getID(Id); } catch (Exception ignored) {}
                if (current != null) {
                    if (maphong == -1) maphong = current.getMaPhong();
                    maNguoiThue = current.getMaNguoiThue();
                } else {
                    maNguoiThue = hybridDao.getMaNguoiThueByMaPhong(maphong);
                }
                
                // Không xóa hợp đồng; đánh dấu là ĐÃ HỦY bằng cách đặt phòng về trạng thái trống
                // và giữ bản ghi hợp đồng để vẫn hiển thị trong danh sách
                hybridDao.updateTrangThaiPhong(maphong, 0);
                // Đồng bộ trạng thái bài đăng về Còn trống
                try {
                    com.example.nestera.Dao.baiDangDao baiDangDao = new com.example.nestera.Dao.baiDangDao(hopDong_Activity.this);
                    java.util.List<com.example.nestera.model.BaiDang> baiDangList = baiDangDao.getAll();
                    for (com.example.nestera.model.BaiDang bd : baiDangList) {
                        if (bd.getMaPhong() != null && bd.getMaPhong() == maphong) {
                            bd.setTrangThai("Còn trống");
                            baiDangDao.update(bd);
                            break;
                        }
                    }
                } catch (Exception ignored) {}
                
                // Cập nhật người thuê về trạng thái chưa có phòng
                if (maNguoiThue != null && !maNguoiThue.isEmpty()) {
                    NguoiThue nguoiThue = dao_nt.getID(maNguoiThue);
                    if (nguoiThue != null) {
                        nguoiThue.setMaPhong(0); // Set về 0 để đánh dấu chưa có phòng
                        dao_nt.update(nguoiThue);
                    }
                }
                
                dialogInterface.cancel();
                Toast.makeText(hopDong_Activity.this, "Kết thúc hợp đồng thành công", Toast.LENGTH_SHORT).show();
                // Nếu mở từ chi tiết phòng (openDetail) thì quay về, nếu không thì refresh danh sách
                boolean openedForDetail = getIntent().getBooleanExtra("openDetail", false);
                if (openedForDetail) {
                    finish();
                } else {
                    recreate();
                }
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        builder.show();
    }
    public void openDialog() {
        currentDialog = new Dialog(hopDong_Activity.this);
        currentDialog.setContentView(R.layout.item_taohopdong);
        
        // Đặt kích thước dialog rộng hơn
        if (currentDialog.getWindow() != null) {
            currentDialog.getWindow().setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.95), // 95% chiều rộng màn hình
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        
        edtSdt_hd = currentDialog.findViewById(R.id.edtSdt_hd);
        edtCCCD_hd = currentDialog.findViewById(R.id.edtCCCD_hd);
        edtDiaChi_hd = currentDialog.findViewById(R.id.edtDiaChi_hd);
        edtNgayki_hd = currentDialog.findViewById(R.id.edtNgayki_hd);
        edtSothang_hd = currentDialog.findViewById(R.id.edtSothang_hd);
        edtSoPhong_hd = currentDialog.findViewById(R.id.edtSoPhong_hd);
        edtTienCoc_hd = currentDialog.findViewById(R.id.edtTienCoc_hd);
        edtTienPhong_hd = currentDialog.findViewById(R.id.edtTienPhong_hd);
        edtSonguoi_hd = currentDialog.findViewById(R.id.edtSonguoi_hd);
        edtSoxe_hd = currentDialog.findViewById(R.id.edtSoxe_hd);
        edtGhiChu_hd = currentDialog.findViewById(R.id.edtGhiChu_hd);
        btnTaoHD = currentDialog.findViewById(R.id.btnTao);
        btnHuy = currentDialog.findViewById(R.id.btnHuy);
        imgAnhhd = currentDialog.findViewById(R.id.imgAnhhd);
        btnChonAnh = currentDialog.findViewById(R.id.btnChonAnh);
        tvSelectedImages = currentDialog.findViewById(R.id.tvSelectedImages);
        spinner = currentDialog.findViewById(R.id.spnNguoiThue);
        
        // Reset danh sách ảnh khi mở dialog
        selectedImageUris.clear();
        tvSelectedImages.setText("Chưa chọn ảnh");
        
        dao_pt = new phongTroDao(hopDong_Activity.this);
        dao_nt = new nguoiThueDao(hopDong_Activity.this);
        
        maphong = getIntent().getIntExtra("maphong", -1);
        android.util.Log.d("HopDongDebug", "MaPhong: " + maphong);
        
        // Lấy danh sách người thuê do CHỦ TRỌ hiện tại tạo
        list_nt = new ArrayList<NguoiThue>();
        String currentLandlord = getSharedPreferences("user11", MODE_PRIVATE).getString("username11", "");
        try {
            list_nt = (ArrayList<NguoiThue>) dao_nt.getByChuTro(currentLandlord);
        } catch (Exception e) {
            list_nt = new ArrayList<>();
        }
        
        // Lọc CHỈ những người chưa có phòng (maPhong <= 0)
        ArrayList<NguoiThue> nguoiThueChuaCoPhong = new ArrayList<>();
        for (NguoiThue nt : list_nt) {
            int maPhongCuaNguoiThue = nt.getMaPhong();
            if (maPhongCuaNguoiThue <= 0) {
                nguoiThueChuaCoPhong.add(nt);
            }
        }
        
        // Kiểm tra nếu không có người chưa có phòng
        if (nguoiThueChuaCoPhong.isEmpty()) {
            Toast.makeText(hopDong_Activity.this, 
                "Tất cả người thuê đã có hợp đồng. Vui lòng thêm người thuê mới hoặc kết thúc hợp đồng cũ trước.", 
                Toast.LENGTH_LONG).show();
            currentDialog.dismiss();
            finish();
            return;
        }
        
        // Chỉ sử dụng danh sách người thuê đạt điều kiện
        list_nt = nguoiThueChuaCoPhong;
        spinerAdapter = new NguoiThueSpinerAdapter(hopDong_Activity.this, list_nt);
        spinner.setAdapter(spinerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mant = list_nt.get(i).getMaNguoithue();
                cccd = list_nt.get(i).getcCCD();
                sdt = list_nt.get(i).getSdt();
                dc = list_nt.get(i).getThuongTru();
                edtCCCD_hd.setText(cccd+"");
                edtSdt_hd.setText(sdt);
                edtDiaChi_hd.setText(dc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Cho phép chọn nhiều ảnh
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh hợp đồng"), REQUEST_CODE_FOLDER);
            }
        });


        maphong = getIntent().getIntExtra("maphong", -1);
        android.util.Log.d("HopDongDebug", "Auto-filling for room ID: " + maphong);
        
        // Try to get data from PhongTro first
        gia = dao_pt.getGiaPhongTheoMaPhong(maphong);
        String tenphongTemp = dao_pt.getTenPhongTheoMaPhong(maphong);
        
        // If PhongTro doesn't have data, get from BaiDang by ID
        if (gia == 0 || tenphongTemp == null || tenphongTemp.isEmpty()) {
            try {
                // Use maphong as BaiDang ID since maPhong field is null in Firestore
                com.example.nestera.model.BaiDang baiDang = baiDangHybridDao.getById(maphong);
                if (baiDang != null) {
                    gia = baiDang.getGiaThang();
                    tenphongTemp = baiDang.getTieuDe(); // Use title as room name
                    
                    // If we extracted maPhong from title, use it for room name formatting
                    if (baiDang.getMaPhong() != null) {
                        tenphongTemp = "Phòng " + baiDang.getMaPhong();
                    }
                    
                    android.util.Log.d("HopDongDebug", "Using BaiDang data - Price: " + gia + ", Title: " + tenphongTemp + ", MaPhong: " + baiDang.getMaPhong());
                } else {
                    android.util.Log.w("HopDongDebug", "No BaiDang found for ID: " + maphong);
                }
            } catch (Exception e) {
                android.util.Log.e("HopDongDebug", "Error getting BaiDang data", e);
            }
        }
        
        final String tenphong = tenphongTemp; // Make final for inner class usage
        
        android.util.Log.d("HopDongDebug", "Final - Room price: " + gia + ", Room name: " + tenphong);
        
        edtTienPhong_hd.setText(gia + "");
        edtSoPhong_hd.setText(tenphong != null ? tenphong : "Phòng " + maphong);
        edtNgayki_hd.setText(sdf.format(new Date()));
        // edtSoPhong_hd.setEnabled(false); // Allow editing room number
        // edtTienPhong_hd.setEnabled(false); // Allow editing room price
        edtNgayki_hd.setEnabled(false);
        edtCCCD_hd.setEnabled(false);
        edtSdt_hd.setEnabled(false);
        edtDiaChi_hd.setEnabled(false);
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDialog.dismiss();
                finish();
            }
        });

        btnTaoHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edtSothang_hd.getText().toString())||TextUtils.isEmpty(edtTienCoc_hd.getText().toString())||TextUtils.isEmpty(edtSonguoi_hd.getText().toString())||TextUtils.isEmpty(edtSoxe_hd.getText().toString())){
                    Toast.makeText(hopDong_Activity.this, "Bạn phải nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int st = Integer.parseInt(edtSothang_hd.getText().toString());
                    if (st <= 0) {
                        Toast.makeText(hopDong_Activity.this, "Số tháng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(hopDong_Activity.this, "Số tháng phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int tc = Integer.parseInt(edtTienCoc_hd.getText().toString());
                    if (tc <= 0) {
                        Toast.makeText(hopDong_Activity.this, "Tiền cọc phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    if(tc>gia){
//                        Toast.makeText(hopDong_Activity.this, "Tiền cọc phải nhỏ hơnn tiền phòng", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                } catch (Exception e) {
                    Toast.makeText(hopDong_Activity.this, "Tiền cọc phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int sn = Integer.parseInt(edtSonguoi_hd.getText().toString());
                    if (sn <= 0) {
                        Toast.makeText(hopDong_Activity.this, "Số người phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(hopDong_Activity.this, "Số người phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int sx = Integer.parseInt(edtSoxe_hd.getText().toString());
                    if (sx <= 0) {
                        Toast.makeText(hopDong_Activity.this, "Số xe phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(hopDong_Activity.this, "Số xe phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra phải có ảnh hợp đồng
                if (selectedImageUris.isEmpty()) {
                    Toast.makeText(hopDong_Activity.this, "Vui lòng chọn ảnh hợp đồng", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Upload nhiều ảnh hợp đồng lên Firebase Storage
                android.app.ProgressDialog progress = new android.app.ProgressDialog(hopDong_Activity.this);
                progress.setMessage("Đang upload " + selectedImageUris.size() + " ảnh hợp đồng...");
                progress.setCancelable(false);
                progress.show();

                int soThang = Integer.parseInt(edtSothang_hd.getText().toString());
                int tienCoc = Integer.parseInt(edtTienCoc_hd.getText().toString());
                int soNguoi = Integer.parseInt(edtSonguoi_hd.getText().toString());
                int soXe = Integer.parseInt(edtSoxe_hd.getText().toString());

                ImageUploader uploader = new ImageUploader(hopDong_Activity.this);
                uploader.uploadMultipleImages(selectedImageUris, "hopDong", new ImageUploader.MultiUploadCallback() {
                    @Override
                    public void onAllSuccess(java.util.List<String> downloadUrls) {
                        progress.dismiss();
                        
                        // Lưu URLs cách nhau bằng dấu ";"
                        String imageUrls = android.text.TextUtils.join(";", downloadUrls);
                        Log.d("hopDong_Activity", "Upload success: " + downloadUrls.size() + " images");
                        
                        // Tạo hợp đồng với Firebase URLs
                        item = new HopDong();
                        item.setMaNguoiThue(mant);
                        item.setMaPhong(maphong);
                        item.setTenPhong(tenphong);
                        item.setGhiChu(edtGhiChu_hd.getText().toString());
                        item.setNgayKy(new Date());
                        item.setSdt(sdt);
                        item.setCCCD(cccd);
                        item.setGiaTien(gia);
                        item.setThuongTru(dc);
                        item.setThoiHan(soThang);
                        item.setTienCoc(tienCoc);
                        item.setSoNguoi(soNguoi);
                        item.setSoXe(soXe);
                        item.setImageUrl(imageUrls); // Lưu nhiều Firebase URLs
                        item.setHinhAnhhd(null); // BLOB để null
                        item.setGhiChu(edtGhiChu_hd.getText().toString());
                        
                        // Debug log imageUrl
                        android.util.Log.d("HopDongDebug", "Saving contract with imageUrl: " + imageUrls);
                        
                        if (hybridDao.insert(item) > 0) {
                            // Cập nhật trạng thái phòng đã thuê
                            hybridDao.updateTrangThaiPhong(maphong, 1);
                            
                            // Cập nhật mã phòng cho người thuê
                            NguoiThue nguoiThueUpdate = dao_nt.getID(mant);
                            if (nguoiThueUpdate != null) {
                                nguoiThueUpdate.setMaPhong(maphong);
                                dao_nt.update(nguoiThueUpdate);
                            }
                            
                            // Cập nhật trạng thái bài đăng thành "Đã thuê"
                            com.example.nestera.Dao.baiDangDao baiDangDao = new com.example.nestera.Dao.baiDangDao(hopDong_Activity.this);
                            java.util.List<com.example.nestera.model.BaiDang> baiDangList = baiDangDao.getAll();
                            for (com.example.nestera.model.BaiDang bd : baiDangList) {
                                if (bd.getMaPhong() != null && bd.getMaPhong() == maphong) {
                                    bd.setTrangThai("Đã thuê");
                                    baiDangDao.update(bd);
                                    break;
                                }
                            }
                            
                            Toast.makeText(hopDong_Activity.this, "Tạo hợp đồng thành công!", Toast.LENGTH_SHORT).show();
                            currentDialog.dismiss();
                            // Reload danh sách hợp đồng
                            list_hdm = new ArrayList<>(hybridDao.getHopDongByMaPhong(maphong));
                            hopDongAdapter = new HopDong_Adapter(hopDong_Activity.this, list_hdm, hopDong_Activity.this);
                            lsthopDong.setAdapter(hopDongAdapter);
                            
                            // Quay lại và refresh
                            finish();
                        } else {
                            Toast.makeText(hopDong_Activity.this, "Tạo hợp đồng thất bại", Toast.LENGTH_SHORT).show();
                            currentDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        progress.dismiss();
                        Toast.makeText(hopDong_Activity.this, 
                            "Lỗi upload ảnh: " + e.getMessage() + ". Hợp đồng không được lưu!", 
                            Toast.LENGTH_LONG).show();
                        Log.e("hopDong_Activity", "Upload failed, data NOT saved", e);
                    }
                });
            }
        });
        currentDialog.show();
    }
    void capNhapLv(){
        // ĐỢI sync từ Firestore trước khi hiển thị
        hybridDao.getAllWithSync(new com.example.nestera.Firebase.FirestoreRepository.FirestoreCallback<List<HopDong>>() {
            @Override
            public void onSuccess(List<HopDong> syncedList) {
                list = (ArrayList<HopDong>) syncedList;
                hopDongAdapter = new HopDong_Adapter(hopDong_Activity.this, list, hopDong_Activity.this);
                lsthopDong.setAdapter(hopDongAdapter);
                android.util.Log.d("hopDong_Activity", "capNhapLv: Loaded " + list.size() + " contracts after sync");
            }

            @Override
            public void onError(Exception e) {
                // Fallback to local data
                list = (ArrayList<HopDong>) hybridDao.getAll();
                hopDongAdapter = new HopDong_Adapter(hopDong_Activity.this, list, hopDong_Activity.this);
                lsthopDong.setAdapter(hopDongAdapter);
                android.util.Log.e("hopDong_Activity", "Sync failed, using local data", e);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null){
            selectedImageUris.clear();
            
            // Xử lý nhiều ảnh được chọn
            if (data.getClipData() != null) {
                // Người dùng chọn nhiều ảnh
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    try {
                        // Lấy quyền truy cập persistent
                        final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    } catch (Exception ignored) {}
                    selectedImageUris.add(uri);
                }
            } else if (data.getData() != null) {
                // Người dùng chỉ chọn 1 ảnh
                Uri uri = data.getData();
                try {
                    final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);
                } catch (Exception ignored) {}
                selectedImageUris.add(uri);
            }
            
            // Hiển thị ảnh đầu tiên làm preview
            if (!selectedImageUris.isEmpty()) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUris.get(0));
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgAnhhd.setImageBitmap(bitmap);
                    tvSelectedImages.setText("Đã chọn " + selectedImageUris.size() + " ảnh");
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Lỗi: Không thể đọc ảnh", Toast.LENGTH_SHORT).show();
                    selectedImageUris.clear();
                    tvSelectedImages.setText("Chưa chọn ảnh");
                }
            }

        }
        if (requestCode == REQUEST_CODE_RENEW && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                pendingRenewImageBytes = baos.toByteArray();
                if (pendingRenewHopDongId != null && pendingRenewThoiHan != null) {
                    HopDong hd = hybridDao.getID(String.valueOf(pendingRenewHopDongId));
                    if (hd != null) {
                        int oldRoom = hd.getMaPhong();
                        if (pendingRenewNewMaPhong != null) {
                            hd.setMaPhong(pendingRenewNewMaPhong);
                        }
                        hd.setThoiHan(pendingRenewThoiHan);
                        hd.setNgayKy(new java.util.Date());
                        hd.setHinhAnhhd(pendingRenewImageBytes);
                        hybridDao.update(hd);
                        // Cập nhật trạng thái phòng
                        updateTrangThaiPhongSauGiaHan(hd.getMaPhong());
                        Toast.makeText(this, "Gia hạn thành công và đã cập nhật ảnh mới", Toast.LENGTH_SHORT).show();
                        // làm mới màn hình
                        recreate();
                    }
                }
            } catch (Exception ignored) {}
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateTrangThaiPhongSauGiaHan(int maPhong) {
        // Cập nhật phòng về trạng thái đã thuê
        hybridDao.updateTrangThaiPhong(maPhong, 1);
        // Đồng bộ trạng thái bài đăng
        com.example.nestera.Dao.baiDangDao baiDangDao = new com.example.nestera.Dao.baiDangDao(this);
        java.util.List<com.example.nestera.model.BaiDang> baiDangList = baiDangDao.getAll();
        for (com.example.nestera.model.BaiDang bd : baiDangList) {
            if (bd.getMaPhong() != null && bd.getMaPhong() == maPhong) {
                bd.setTrangThai("Đã thuê");
                baiDangDao.update(bd);
                break;
            }
        }
    }

    private void releaseOldRoom(int maPhong) {
        // Trả phòng cũ về trạng thái trống và cập nhật bài đăng liên quan nếu có
        hybridDao.updateTrangThaiPhong(maPhong, 0);
        com.example.nestera.Dao.baiDangDao baiDangDao = new com.example.nestera.Dao.baiDangDao(this);
        java.util.List<com.example.nestera.model.BaiDang> baiDangList = baiDangDao.getAll();
        for (com.example.nestera.model.BaiDang bd : baiDangList) {
            if (bd.getMaPhong() != null && bd.getMaPhong() == maPhong) {
                bd.setTrangThai("Còn trống");
                baiDangDao.update(bd);
                break;
            }
        }
    }
}