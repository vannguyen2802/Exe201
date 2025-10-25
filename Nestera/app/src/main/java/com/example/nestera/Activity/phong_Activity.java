package com.example.nestera.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nestera.Adapter.LoaiPhongSpinnerAdapter;
import com.example.nestera.Adapter.Phong_Adapter;
import com.example.nestera.Firebase.BaiDangHybridDao;
import com.example.nestera.Firebase.LoaiPhongHybridDao;
import com.example.nestera.Firebase.PhongTroHybridDao;
import com.example.nestera.MainActivity;
import com.example.nestera.R;
import com.example.nestera.model.LoaiPhong;
import com.example.nestera.model.PhongTro;

import java.util.ArrayList;

public class phong_Activity extends AppCompatActivity {
    ListView lstPhong;
    ArrayList<com.example.nestera.model.BaiDang> list;
    ArrayList<PhongTro> listtemp;
    ArrayList<LoaiPhong> list_lp;
    com.example.nestera.Adapter.RoomFromPostAdapter adapter;
    //PhongTro item;
    PhongTroHybridDao hybridDao;
    BaiDangHybridDao baiDangHybridDao;
    ImageView btnAdd;
    EditText edtmaPhong, edttenPhong, edtGia, edtTienNghi,edtSearch;
    Button btnHuy, btnXacNhan;
    Spinner spinner;
    int position, maLoaiPhong;
    CheckBox chk;
    LoaiPhongHybridDao hybridDao_lp;
    LoaiPhong item_lp;
    LoaiPhongSpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        android.util.Log.d("PhongActivity", "=== onCreate() START ===");  
        android.util.Log.d("PhongActivity", "Activity: " + this.getClass().getSimpleName());
        
        setContentView(R.layout.activity_phong);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Phòng Trọ");

        Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(phong_Activity.this, MainActivity.class));
            }
        });




        lstPhong = findViewById(R.id.lstPhongTro);
        hybridDao = new PhongTroHybridDao(phong_Activity.this);
        hybridDao.enableRealtimeSync(); // Enable real-time sync
        baiDangHybridDao = new BaiDangHybridDao(phong_Activity.this);
        btnAdd = findViewById(R.id.btnadd_toolbar);
        // Ẩn nút thêm (+) trên màn Phòng trọ
        if (btnAdd != null) {
            btnAdd.setVisibility(View.GONE);
        }

        // Lấy dữ liệu từ bài đăng thay vì PhongTro
        list = new java.util.ArrayList<>();
        edtSearch=findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    // Lấy username của chủ trọ hiện tại
                    String currentUser = getSharedPreferences("user11", MODE_PRIVATE).getString("username11", "");
                    
                    // Use Firebase sync để tìm kiếm
                    baiDangHybridDao.getByChuTroWithSync(currentUser, new com.example.nestera.Firebase.FirestoreRepository.FirestoreCallback<java.util.List<com.example.nestera.model.BaiDang>>() {
                        @Override
                        public void onSuccess(java.util.List<com.example.nestera.model.BaiDang> syncedList) {
                            runOnUiThread(() -> {
                                list.clear();
                                if (syncedList != null) {
                                    for (com.example.nestera.model.BaiDang b : syncedList){
                                        if ((b.getTieuDe()!=null && b.getTieuDe().toLowerCase().contains(charSequence.toString().toLowerCase())) ||
                                            (b.getDiaChi()!=null && b.getDiaChi().toLowerCase().contains(charSequence.toString().toLowerCase()))){
                                            list.add(b);
                                        }
                                    }
                                }
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("phong_Activity", "Error during search", e);
                        }
                    });
                } catch (Exception e) {
                    Log.e("phong_Activity", "Error during search", e);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        capNhapLv();
        btnAdd.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          opendialog(phong_Activity.this, 0);
                                      }
                                  }
        );
        lstPhong.setOnItemLongClickListener(null);
    }

    public void opendialog(Context context, int type) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_phong);
        edtmaPhong = dialog.findViewById(R.id.edtMaPhong);
        edtTienNghi = dialog.findViewById(R.id.edtTienNghi);
        edttenPhong = dialog.findViewById(R.id.edtTenPhong);
        edtGia = dialog.findViewById(R.id.edtGia);
        chk = dialog.findViewById(R.id.chkDaChoThue);
        spinner = dialog.findViewById(R.id.spnLoaiPhong);
        btnHuy = dialog.findViewById(R.id.btnHuy);
        btnXacNhan = dialog.findViewById(R.id.btnXacNhan);
        chk.setVisibility(View.GONE);
        list_lp = new ArrayList<LoaiPhong>();
        list_lp = (ArrayList<LoaiPhong>) hybridDao_lp.getAll();
        spinnerAdapter = new LoaiPhongSpinnerAdapter(context, list_lp);
        //
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 maLoaiPhong = list_lp.get(i).getMaLoaiPhong();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        edtmaPhong.setEnabled(false);
        edtTienNghi.setInputType(InputType.TYPE_NULL);
        edtTienNghi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] tienNghi = {"Điều hoà","Nóng lạnh","Máy Giặt","Tủ lạnh","Tủ quần áo"};
                final boolean[] checkedOptions = {false, false, false, false,false};
                AlertDialog.Builder builder = new AlertDialog.Builder(phong_Activity.this);
                builder.setTitle("Chọn Tiện Nghi trong phòng");
                builder.setMultiChoiceItems(tienNghi, checkedOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // Cập nhật trạng thái của tùy chọn khi người dùng chọn hoặc hủy chọn
                        checkedOptions[which] = isChecked;

                    }
                });
                // Thiết lập nút đồng ý
                builder.setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý khi người dùng bấm nút đồng ý
                        StringBuilder selectedOptions = new StringBuilder();
                        for (int i = 0; i < checkedOptions.length; i++) {
                            if (checkedOptions[i]) {
                                selectedOptions.append(tienNghi[i]).append(", ");
                            }
                        }

                        // Hiển thị các tùy chọn đã chọn
                        if (selectedOptions.length() > 0) {
                            selectedOptions.deleteCharAt(selectedOptions.length() - 2); // Loại bỏ dấu phẩy và khoảng trắng cuối cùng
//                            Toast.makeText(getApplicationContext(), "Đã chọn: " + selectedOptions.toString(), Toast.LENGTH_SHORT).show();
                            edtTienNghi.setText(selectedOptions.toString());
                        } else {
                            Toast.makeText(getApplicationContext(), "Không có tùy chọn được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Thiết lập nút hủy
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng dialog khi người dùng bấm nút hủy
                        dialog.dismiss();
                    }
                });

                // Hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // Không xử lý edit phòng trong chế độ hiển thị từ bài đăng
            btnHuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnXacNhan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(edttenPhong.getText().toString()) || TextUtils.isEmpty(edtTienNghi.getText().toString()) || TextUtils.isEmpty(edtGia.getText().toString())) {
                        Toast.makeText(context, "Bạn phải nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int checkTen = 0; // skip duplicate name check in new mode
                    if(checkTen==1){
                        Toast.makeText(context, "Tên phòng đã tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        int g = Integer.parseInt(edtGia.getText().toString());
                        if (g <= 0) {
                            Toast.makeText(context, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Giá phải là số", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(context, "Chức năng này không khả dụng khi nguồn là bài đăng", Toast.LENGTH_SHORT).show();

                    capNhapLv();
                    dialog.dismiss();
                }
            });
        dialog.show();

    }

    public void xoa(String Id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cảnh báo");
        builder.setIcon(R.drawable.baseline_warning_24);
        builder.setMessage("Bạn có chắc chắn muốn xoá");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hybridDao.delete(Integer.parseInt(Id));
                capNhapLv();
                dialogInterface.cancel();
                Toast.makeText(phong_Activity.this, "Xóa thành công ", Toast.LENGTH_SHORT).show();
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

    public void capNhapLv() {
        // Lấy username của chủ trọ hiện tại
        String currentUser = getSharedPreferences("user11", MODE_PRIVATE).getString("username11", "");
        
        android.util.Log.d("PhongActivity", "Loading posts for user: " + currentUser);
        
        // Use Firebase sync thay vì local DAO
        baiDangHybridDao.getByChuTroWithSync(currentUser, new com.example.nestera.Firebase.FirestoreRepository.FirestoreCallback<java.util.List<com.example.nestera.model.BaiDang>>() {
            @Override
            public void onSuccess(java.util.List<com.example.nestera.model.BaiDang> syncedList) {
                runOnUiThread(() -> {
                    try {
                        if (syncedList != null) {
                            list.clear(); // Clear existing data first
                            list.addAll(syncedList);
                            android.util.Log.d("PhongActivity", "✅ Loaded " + list.size() + " posts from Firebase");
                        } else {
                            list.clear();
                            android.util.Log.w("PhongActivity", "Synced list is null for user: " + currentUser);
                        }
                        
                        // Reuse existing adapter if available
                        if (adapter == null) {
                            adapter = new com.example.nestera.Adapter.RoomFromPostAdapter(phong_Activity.this, list);
                            lstPhong.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        android.util.Log.e("PhongActivity", "Error setting adapter", e);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                android.util.Log.e("PhongActivity", "❌ Error loading posts from Firebase", e);
                runOnUiThread(() -> {
                    // Fallback to empty list
                    list = new java.util.ArrayList<>();
                    adapter = new com.example.nestera.Adapter.RoomFromPostAdapter(phong_Activity.this, list);
                    lstPhong.setAdapter(adapter);
                    Toast.makeText(phong_Activity.this, "Lỗi tải dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    public void xemHD(int i){
        com.example.nestera.model.BaiDang pp = list.get(i);
        int maPhong = (pp.getMaPhong()==null? -1 : pp.getMaPhong());

        Intent intent = new Intent(phong_Activity.this, hopDong_Activity.class);
        intent.putExtra("maphong", maPhong);
        startActivity(intent);
    }
    
    // Method để xóa bài đăng (được gọi từ adapter)
    public void deleteBaiDang(int postId) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa bài đăng này?");
        builder.setPositiveButton("Có", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                try {
                    // Xóa từ Firebase và SQLite
                    long result = baiDangHybridDao.delete(String.valueOf(postId));
                    if (result > 0) {
                        android.widget.Toast.makeText(phong_Activity.this, "Xóa bài đăng thành công", android.widget.Toast.LENGTH_SHORT).show();
                        // Force sync from Firebase after delete
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            // Force reload from Firebase instead of cache
                            String currentUser = getSharedPreferences("user11", MODE_PRIVATE).getString("username11", "");
                            baiDangHybridDao.forceSync(); // Force sync first
                            
                            // Then reload UI
                            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                                capNhapLv(); // Reload data after sync
                            }, 1000);
                        }, 500);
                    } else {
                        android.widget.Toast.makeText(phong_Activity.this, "Xóa thất bại", android.widget.Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    android.util.Log.e("PhongActivity", "Error deleting post", e);
                    android.widget.Toast.makeText(phong_Activity.this, "Lỗi: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Không", null);
        builder.show();
    }

}