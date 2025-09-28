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
import com.example.nestera.Dao.LoaiPhongDao;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.MainActivity;
import com.example.nestera.R;
import com.example.nestera.model.LoaiPhong;
import com.example.nestera.model.PhongTro;

import java.util.ArrayList;

public class phong_Activity extends AppCompatActivity {
    ListView lstPhong;
    ArrayList<PhongTro> list;
    ArrayList<PhongTro> listtemp;
    ArrayList<LoaiPhong> list_lp;
    Phong_Adapter adapter;
    PhongTro item;
    phongTroDao dao;
    ImageView btnAdd;
    EditText edtmaPhong, edttenPhong, edtGia, edtTienNghi,edtSearch;
    Button btnHuy, btnXacNhan;
    Spinner spinner;
    int position, maLoaiPhong;
    CheckBox chk;
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
        chk = dialog.findViewById(R.id.chkDaChoThue);
        spinner = dialog.findViewById(R.id.spnLoaiPhong);
        btnHuy = dialog.findViewById(R.id.btnHuy);
        btnXacNhan = dialog.findViewById(R.id.btnXacNhan);
        chk.setVisibility(View.GONE);
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

            if (item.getTrangThai() == 1) {
                chk.setChecked(true);
                edtmaPhong.setEnabled(false);
                edttenPhong.setEnabled(false);
                edtTienNghi.setEnabled(false);
                edtGia.setEnabled(false);
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
                    if (TextUtils.isEmpty(edttenPhong.getText().toString()) || TextUtils.isEmpty(edtTienNghi.getText().toString()) || TextUtils.isEmpty(edtGia.getText().toString())) {
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
                    item.setMaLoai(maLoaiPhong);
                    if (chk.isChecked()) {
                        item.setTrangThai(1);
                    } else {
                        item.setTrangThai(0);
                    }
                    if (type == 0) {
                        if (dao.insert(item) > 0) {
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

}