package com.example.nestera.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.nestera.Adapter.LoaiPhongSpinnerAdapter;
import com.example.nestera.Adapter.Phong_Adapter;
import com.example.nestera.Adapter.ImagePreviewAdapter;
import com.example.nestera.Dao.LoaiPhongDao;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.MainActivity;
import com.example.nestera.R;
import com.example.nestera.model.LoaiPhong;
import com.example.nestera.model.PhongTro;

import java.util.ArrayList;
import java.util.List;

public class phong_Activity extends AppCompatActivity {
    ListView lstPhong;
    ArrayList<PhongTro> list;
    ArrayList<PhongTro> listtemp;
    ArrayList<LoaiPhong> list_lp;
    Phong_Adapter adapter;
    PhongTro item;
    phongTroDao dao;
    ImageView btnAdd;
    EditText edtmaPhong, edttenPhong, edtGia, edtTienNghi, edtSearch, edtDiaChi;
    Button btnHuy, btnXacNhan, btnChonAnh;
    Spinner spinner;
    int position, maLoaiPhong;
    CheckBox chk;
    
    // Xử lý ảnh
    private static final int REQUEST_IMAGE_PICK = 1001;
    private ArrayList<Uri> selectedImages;
    private RecyclerView recyclerViewImages;
    private ImagePreviewAdapter imageAdapter;
    private TextView txtSoAnhDaChon;
    LoaiPhongDao dao_lp;
    LoaiPhong item_lp;
    LoaiPhongSpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        dao = new phongTroDao(phong_Activity.this);
        btnAdd = findViewById(R.id.btnadd_toolbar);

        listtemp= (ArrayList<PhongTro>) dao.getAll();
        edtSearch=findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                list.clear();
                for (PhongTro pt : listtemp){
                    if (pt.getTenPhong().contains(charSequence.toString())){
                        list.add(pt);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        capNhapLv();
        setupEventListeners();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleImagePickerResult(requestCode, resultCode, data);
    }
    
    private void setupEventListeners() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          opendialog(phong_Activity.this, 0);
                                      }
                                  }
        );
        lstPhong.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = list.get(i);
                opendialog(phong_Activity.this, 1);
                return false;
            }
        });
    }

    public void opendialog(Context context, int type) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_phong);
        edtmaPhong = dialog.findViewById(R.id.edtMaPhong);
        edtTienNghi = dialog.findViewById(R.id.edtTienNghi);
        edttenPhong = dialog.findViewById(R.id.edtTenPhong);
        edtGia = dialog.findViewById(R.id.edtGia);
        edtDiaChi = dialog.findViewById(R.id.edtDiaChi);
        chk = dialog.findViewById(R.id.chkDaChoThue);
        spinner = dialog.findViewById(R.id.spnLoaiPhong);
        btnHuy = dialog.findViewById(R.id.btnHuy);
        btnXacNhan = dialog.findViewById(R.id.btnXacNhan);
        btnChonAnh = dialog.findViewById(R.id.btnChonAnh);
        txtSoAnhDaChon = dialog.findViewById(R.id.txtSoAnhDaChon);
        recyclerViewImages = dialog.findViewById(R.id.recyclerViewImages);
        chk.setVisibility(View.GONE);
        
        // Setup RecyclerView cho ảnh
        selectedImages = new ArrayList<>();
        imageAdapter = new ImagePreviewAdapter(context, selectedImages, position -> {
            selectedImages.remove(position);
            imageAdapter.notifyItemRemoved(position);
            updateImageCount();
        });
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewImages.setAdapter(imageAdapter);
        
        // Khởi tạo số lượng ảnh
        updateImageCount();
        
        // Xử lý chọn ảnh
        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // Flags để có thể lưu persistent permission
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ((Activity) context).startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });
        
        list_lp = new ArrayList<LoaiPhong>();
        dao_lp = new LoaiPhongDao(context);
        list_lp = (ArrayList<LoaiPhong>) dao_lp.getAll();
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
        if (type != 0) {
            edtmaPhong.setText(item.getMaPhong() + "");
            edttenPhong.setText(item.getTenPhong() + "");
            edtTienNghi.setText(item.getTienNghi() + "");
            edtGia.setText(item.getGia() + "");
            edtDiaChi.setText(item.getDiaChi() + "");

            if (item.getTrangThai() == 1) {
                chk.setChecked(true);
                edtmaPhong.setEnabled(false);
                edttenPhong.setEnabled(false);
                edtTienNghi.setEnabled(false);
                edtGia.setEnabled(false);
                edtDiaChi.setEnabled(false);
                spinner.setEnabled(false);
            } else {
                chk.setChecked(false);
            }
            for (int i = 0; i < list_lp.size(); i++) {
                if (item.getMaLoai() == (list_lp.get(i).getMaLoaiPhong())) {
                    position = i;
                }
                Log.i("zzzzzzzzzzzz", "posPhong: " + position);
                spinner.setSelection(position);
            }
        }
            btnHuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnXacNhan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(edttenPhong.getText().toString()) || TextUtils.isEmpty(edtTienNghi.getText().toString()) || 
                    TextUtils.isEmpty(edtGia.getText().toString()) || TextUtils.isEmpty(edtDiaChi.getText().toString())) {
                        Toast.makeText(context, "Bạn phải nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int checkTen = 0;
                    for (PhongTro pt :list){
                        if(pt.getTenPhong().equalsIgnoreCase(edttenPhong.getText().toString())){
                            checkTen=1;
                            break;
                        }
                    }
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
                    item = new PhongTro();
                    item.setTenPhong(edttenPhong.getText().toString());
                    item.setTienNghi(edtTienNghi.getText().toString());
                    item.setGia(Integer.parseInt(edtGia.getText().toString()));
                    item.setDiaChi(edtDiaChi.getText().toString());
                    item.setMaLoai(maLoaiPhong);
                    // Mặc định khi tạo phòng: không tìm người ở ghép, 0 người hiện tại
                    item.setTimNguoiOGhep(0);
                    item.setSoNguoiHienTai(0);
                    item.setImagePath(""); // Set empty image path để tránh null
                    if (chk.isChecked()) {
                        item.setTrangThai(1);
                    } else {
                        item.setTrangThai(0);
                    }
                    if (type == 0) {
                        long result = dao.insert(item);
                        Log.d("PhongTro", "Insert result: " + result);
                        if (result > 0) {
                            // Lưu ảnh vào bảng PhongTroImages nếu có
                            if (selectedImages != null && !selectedImages.isEmpty()) {
                                dao.saveImagesForPhong((int) result, selectedImages);
                                Log.d("PhongTro", "Saved " + selectedImages.size() + " images for room " + result);
                            }
                            Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        item.setMaPhong(Integer.parseInt(edtmaPhong.getText().toString()));
                        if (dao.update(item) > 0) {
                            Toast.makeText(context, "Sửa thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Sửa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

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
                dao.delete(Id);
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
        list = (ArrayList<PhongTro>) dao.getAll();
        Log.d("PhongTro", "capNhapLv - Total rooms: " + list.size());
        adapter = new Phong_Adapter(phong_Activity.this, this, list);
        lstPhong.setAdapter(adapter);
    }
    public void xemHD(int i){
        PhongTro pp = list.get(i);
        int maPhong = pp.getMaPhong();

        Intent intent = new Intent(phong_Activity.this, hopDong_Activity.class);
        intent.putExtra("maphong", maPhong);
        startActivity(intent);
    }
    
    private void updateImageCount() {
        if (txtSoAnhDaChon != null) {
            txtSoAnhDaChon.setText("Đã chọn: " + selectedImages.size() + " ảnh");
            recyclerViewImages.setVisibility(selectedImages.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }
    
    public void handleImagePickerResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // Nhiều ảnh được chọn
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    if (!selectedImages.contains(imageUri)) {
                        // Lưu quyền truy cập lâu dài cho URI
                        try {
                            getContentResolver().takePersistableUriPermission(imageUri, 
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            selectedImages.add(imageUri);
                            Log.d("PhongTro", "Granted persistent permission for: " + imageUri);
                        } catch (SecurityException e) {
                            Log.e("PhongTro", "Cannot grant persistent permission for: " + imageUri, e);
                            // Vẫn thêm vào list, sẽ xử lý fallback sau
                            selectedImages.add(imageUri);
                        }
                    }
                }
            } else if (data.getData() != null) {
                // Một ảnh được chọn
                Uri imageUri = data.getData();
                if (!selectedImages.contains(imageUri)) {
                    try {
                        getContentResolver().takePersistableUriPermission(imageUri, 
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        selectedImages.add(imageUri);
                        Log.d("PhongTro", "Granted persistent permission for: " + imageUri);
                    } catch (SecurityException e) {
                        Log.e("PhongTro", "Cannot grant persistent permission for: " + imageUri, e);
                        selectedImages.add(imageUri);
                    }
                }
            }
            
            imageAdapter.notifyDataSetChanged();
            updateImageCount();
        }
    }

}