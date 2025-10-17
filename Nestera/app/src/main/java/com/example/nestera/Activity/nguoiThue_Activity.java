package com.example.nestera.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nestera.Adapter.NguoiThue_Adapter;
import com.example.nestera.Adapter.SPPhong_Adapter;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.R;
import com.example.nestera.model.NguoiThue;
import com.example.nestera.model.PhongTro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class nguoiThue_Activity extends AppCompatActivity {

    ListView lstNguoiThue;
    ArrayList<NguoiThue> list;
    ArrayList<NguoiThue> listtemp;
    EditText edtSearch;
    NguoiThue_Adapter nguoiThueAdapter;
    NguoiThue nguoiThue;
    static nguoiThueDao dao;
    ImageView btnadd;
    Dialog dialog;
    EditText edtUser,edtPass,edtRePass,edtHoTen,edtNamSinh,edtSDT,edtThuongTru,edtCCCD;
    RadioButton rdoNam, rdoNu, rdoKhac;
    Spinner spnPhong;
    Button btnXacNhan, btnHuy;
    phongTroDao troDao;
    SPPhong_Adapter spPhongAdapter;
    ArrayList<PhongTro> listpt;
    PhongTro phongTro;
    int maPhongTro,age;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nguoi_thue);
        
        try {
            // Lấy thông tin user hiện tại
            android.content.SharedPreferences prefs = getSharedPreferences("user11", MODE_PRIVATE);
            String username = prefs.getString("username11", "");
            String role = prefs.getString("role", "");
            android.util.Log.d("NguoiThueDebug", "Username: " + username + ", Role: " + role);
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        btnadd=findViewById(R.id.btnadd_toolbar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Người thuê");

        Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


            lstNguoiThue=findViewById(R.id.lstNguoiThue);
            dao =new nguoiThueDao(nguoiThue_Activity.this);

            // Khởi tạo list trước
            list = new ArrayList<>();
            listtemp = new ArrayList<>();
            
            android.util.Log.d("NguoiThueDebug", "Starting to load data...");
            
            // Load danh sách theo role
            if ("ADMIN".equalsIgnoreCase(role)) {
                // Admin xem tất cả
                android.util.Log.d("NguoiThueDebug", "Loading all for ADMIN");
                listtemp = (ArrayList<NguoiThue>) dao.getAll();
            } else if ("LANDLORD".equalsIgnoreCase(role)) {
                // Chủ trọ chỉ xem người thuê do mình tạo (lọc theo chuTroId)
                android.util.Log.d("NguoiThueDebug", "Loading for LANDLORD: " + username);
                if (username != null && !username.isEmpty()) {
                    listtemp = (ArrayList<NguoiThue>) dao.getByChuTro(username);
                    android.util.Log.d("NguoiThueDebug", "Found " + listtemp.size() + " tenants");
                } else {
                    listtemp = new ArrayList<>();
                    android.util.Log.d("NguoiThueDebug", "Username is empty");
                }
            } else {
                // Role khác không xem được
                android.util.Log.d("NguoiThueDebug", "Unknown role: " + role);
                listtemp = new ArrayList<>();
            }
            
            // Copy từ listtemp sang list
            if (listtemp != null) {
                list.addAll(listtemp);
            }
            android.util.Log.d("NguoiThueDebug", "Total in list: " + list.size());
            
            // Khởi tạo adapter
            try {
                nguoiThueAdapter = new NguoiThue_Adapter(nguoiThue_Activity.this, this, list);
                lstNguoiThue.setAdapter(nguoiThueAdapter);
                android.util.Log.d("NguoiThueDebug", "Adapter set successfully");
            } catch (Exception e) {
                android.util.Log.e("NguoiThueDebug", "Error setting adapter", e);
            }
        
            edtSearch=findViewById(R.id.edtSearch);
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (list != null && listtemp != null) {
                        list.clear();
                        for (NguoiThue nt : listtemp){
                            if (nt.getTenNguoiThue() != null && nt.getTenNguoiThue().contains(charSequence.toString())){
                                list.add(nt);
                            }
                        }
                        if (nguoiThueAdapter != null) {
                            nguoiThueAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            btnadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogAdd(nguoiThue_Activity.this);
                }
            });
            
            lstNguoiThue.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    nguoiThue=list.get(i);
                    openDialogUpdate(nguoiThue_Activity.this);
                    return false;
                }
            });
            
        } catch (Exception e) {
            android.util.Log.e("NguoiThueDebug", "Error in onCreate", e);
            Toast.makeText(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    protected void openDialogAdd(final Context context){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_nguoithue);
        edtUser=dialog.findViewById(R.id.edtUser);
        edtPass=dialog.findViewById(R.id.edtPass);
        edtRePass=dialog.findViewById(R.id.edtNhapLaiPass);
        edtHoTen=dialog.findViewById(R.id.edtHoTen);
        edtNamSinh=dialog.findViewById(R.id.edtNamSinh);
        edtSDT=dialog.findViewById(R.id.edtSDT);
        edtThuongTru=dialog.findViewById(R.id.edtThuongTru);
        edtCCCD=dialog.findViewById(R.id.edtCCCD);
        rdoNam=dialog.findViewById(R.id.rdoNam);
        rdoNu=dialog.findViewById(R.id.rdoNu);
        rdoKhac=dialog.findViewById(R.id.rdoKhac);
        spnPhong=dialog.findViewById(R.id.spnPhong);
        btnXacNhan=dialog.findViewById(R.id.btnXacNhan);
        btnHuy=dialog.findViewById(R.id.btnHuy);
        rdoNam.setChecked(true);

        troDao=new phongTroDao(context);
        listpt = new ArrayList<PhongTro>();
        listpt= (ArrayList<PhongTro>) troDao.getAll();
        
        // Chỉ hiển thị phòng do chủ trọ hiện tại sở hữu (dựa theo BaiDang.chuTroId)
        try {
            android.content.SharedPreferences sp = getSharedPreferences("user11", MODE_PRIVATE);
            String currentOwner = sp.getString("username11", "");
            java.util.HashSet<Integer> ownedRooms = new java.util.HashSet<>();
            java.util.List<com.example.nestera.model.BaiDang> posts = new com.example.nestera.Dao.baiDangDao(context).getByChuTro(currentOwner);
            for (com.example.nestera.model.BaiDang bd : posts) {
                try { if (bd.getMaPhong() != null) ownedRooms.add(bd.getMaPhong()); } catch (Exception ignored) {}
            }
            java.util.ArrayList<PhongTro> filtered = new java.util.ArrayList<>();
            for (PhongTro p : listpt) {
                if (ownedRooms.contains(p.getMaPhong())) filtered.add(p);
            }
            listpt = filtered;
        } catch (Exception e) { /* fallback: keep list as-is */ }
        
        // Thêm option "Chưa có phòng" ở đầu danh sách
        PhongTro phongTrong = new PhongTro();
        phongTrong.setMaPhong(0); // maPhong = 0 nghĩa là chưa có phòng
        phongTrong.setTenPhong("Chưa có phòng");
        phongTrong.setGia(0);
        listpt.add(0, phongTrong); // Thêm vào đầu danh sách
        
        spPhongAdapter=new SPPhong_Adapter(context,listpt);
        spnPhong.setAdapter(spPhongAdapter);
        
        // Mặc định chọn "Chưa có phòng"
        spnPhong.setSelection(0);
        maPhongTro = 0;
        
        edtNamSinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar lich = Calendar.getInstance();//tạo đối tượng để lấy ngày giờ hiện tại
                int year = lich.get(Calendar.YEAR);
                int month = lich.get(Calendar.MONTH);
                int day = lich.get(Calendar.DAY_OF_MONTH);
                //Tạo đối tượng DatePickerDialog và show nó
                DatePickerDialog datedg = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        edtNamSinh.setText(String.format("%02d/%02d/%d", dayOfMonth, month, year));

                        // Tính tuổi từ ngày sinh
                        age = calculateAge(year, month, dayOfMonth);

                        // Kiểm tra nếu tuổi không lớn hơn 18
                        if (age < 18) {
                            // Hiển thị thông báo hoặc thực hiện hành động phù hợp
                            Toast.makeText(context, "Người thuê phải trên 18 tuổi.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, year, month, day);
                datedg.show();
            }
        });
        spnPhong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                maPhongTro = listpt.get(i).getMaPhong();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtUser.getText().toString())||TextUtils.isEmpty(edtPass.getText().toString())||TextUtils.isEmpty(edtThuongTru.getText().toString())||TextUtils.isEmpty(edtHoTen.getText().toString())||TextUtils.isEmpty(edtCCCD.getText().toString())||TextUtils.isEmpty(edtSDT.getText().toString())||TextUtils.isEmpty(edtNamSinh.getText().toString())){
                    Toast.makeText(context, "Bạn phải nhập đầy đủ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (age < 18) {
                    // Hiển thị thông báo hoặc thực hiện hành động phù hợp
                    Toast.makeText(context, "Người thuê phải trên 18 tuổi.", Toast.LENGTH_SHORT).show();
                    return;
                }
                nguoiThue = new NguoiThue();
                nguoiThue.setMaNguoithue(edtUser.getText().toString());
                nguoiThue.setMatKhauNT(edtPass.getText().toString());
                nguoiThue.setTenNguoiThue(edtHoTen.getText().toString());
                nguoiThue.setThuongTru(edtThuongTru.getText().toString());
                nguoiThue.setSdt(edtSDT.getText().toString());
                nguoiThue.setcCCD(edtCCCD.getText().toString());
                nguoiThue.setNamSinh(edtNamSinh.getText().toString());

                // Lưu chuTroId (username của chủ trọ hiện tại)
                android.content.SharedPreferences prefs = getSharedPreferences("user11", MODE_PRIVATE);
                String currentUsername = prefs.getString("username11", "");
                nguoiThue.setChuTroId(currentUsername);

                nguoiThue.setMaPhong(maPhongTro);
                // Giới tính: 1 = Nam, 0 = Nữ, 2 = Khác
                if (rdoNam.isChecked()){
                    nguoiThue.setGioiTinh(1); // Nam
                } else if (rdoNu.isChecked()) {
                    nguoiThue.setGioiTinh(0); // Nữ
                } else if (rdoKhac.isChecked()){
                    nguoiThue.setGioiTinh(2); // Khác
                }

                if (!nguoiThue.getSdt().matches("^0\\d{9}$")){
                    Toast.makeText(context, "Sđt sai định dạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!nguoiThue.getcCCD().matches("^0\\d{11}$")){
                    Toast.makeText(context, "CCCD sai định dạng", Toast.LENGTH_SHORT).show();
                    return;
                }



                String pass=edtPass.getText().toString();
                String repass=edtRePass.getText().toString();
                if (pass.equals(repass)){
                    Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                    return;
                }



                if (dao.insert(nguoiThue)>0){

                    Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
                capNhatList();
                dialog.dismiss();


            }
        });
        dialog.show();

    }

    protected void openDialogUpdate(final Context context){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_nguoithue);
        edtUser=dialog.findViewById(R.id.edtUser);
        edtPass=dialog.findViewById(R.id.edtPass);
        edtRePass=dialog.findViewById(R.id.edtNhapLaiPass);
        edtHoTen=dialog.findViewById(R.id.edtHoTen);
        edtNamSinh=dialog.findViewById(R.id.edtNamSinh);
        edtSDT=dialog.findViewById(R.id.edtSDT);
        edtThuongTru=dialog.findViewById(R.id.edtThuongTru);
        edtCCCD=dialog.findViewById(R.id.edtCCCD);
        rdoNam=dialog.findViewById(R.id.rdoNam);
        rdoNu=dialog.findViewById(R.id.rdoNu);
        rdoKhac=dialog.findViewById(R.id.rdoKhac);
        spnPhong=dialog.findViewById(R.id.spnPhong);
        btnXacNhan=dialog.findViewById(R.id.btnXacNhan);
        btnHuy=dialog.findViewById(R.id.btnHuy);
        edtUser.setVisibility(View.GONE);
        edtPass.setVisibility(View.GONE);
        edtRePass.setVisibility(View.GONE);
        TextView txt1 = dialog.findViewById(R.id.txt1);
        TextView txt2 = dialog.findViewById(R.id.txt2);
        TextView txt3 = dialog.findViewById(R.id.txt3);
        txt1.setVisibility(View.GONE);
        txt2.setVisibility(View.GONE);
        txt3.setVisibility(View.GONE);

        troDao=new phongTroDao(context);
        listpt = new ArrayList<PhongTro>();
        listpt= (ArrayList<PhongTro>) troDao.getAll();
        
        // Chỉ hiển thị phòng do chủ trọ hiện tại sở hữu (dựa theo BaiDang.chuTroId)
        try {
            android.content.SharedPreferences sp = getSharedPreferences("user11", MODE_PRIVATE);
            String currentOwner = sp.getString("username11", "");
            java.util.HashSet<Integer> ownedRooms = new java.util.HashSet<>();
            java.util.List<com.example.nestera.model.BaiDang> posts = new com.example.nestera.Dao.baiDangDao(context).getByChuTro(currentOwner);
            for (com.example.nestera.model.BaiDang bd : posts) {
                try { if (bd.getMaPhong() != null) ownedRooms.add(bd.getMaPhong()); } catch (Exception ignored) {}
            }
            java.util.ArrayList<PhongTro> filtered = new java.util.ArrayList<>();
            for (PhongTro p : listpt) {
                if (ownedRooms.contains(p.getMaPhong())) filtered.add(p);
            }
            listpt = filtered;
        } catch (Exception e) { /* fallback: keep list as-is */ }
        
        // Thêm option "Chưa có phòng" ở đầu danh sách
        PhongTro phongTrong = new PhongTro();
        phongTrong.setMaPhong(0);
        phongTrong.setTenPhong("Chưa có phòng");
        phongTrong.setGia(0);
        listpt.add(0, phongTrong);
        
        spPhongAdapter=new SPPhong_Adapter(context,listpt);
        spnPhong.setAdapter(spPhongAdapter);

        edtUser.setText(nguoiThue.getMaNguoithue());
        edtPass.setText(nguoiThue.getMatKhauNT());
        edtRePass.setText(nguoiThue.getMatKhauNT());
        edtHoTen.setText(nguoiThue.getTenNguoiThue());
        edtThuongTru.setText(nguoiThue.getThuongTru());
        edtSDT.setText(nguoiThue.getSdt());
        edtCCCD.setText(String.valueOf(nguoiThue.getcCCD()));
        edtNamSinh.setText(nguoiThue.getNamSinh());
        edtNamSinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar lich = Calendar.getInstance();//tạo đối tượng để lấy ngày giờ hiện tại
                int year = lich.get(Calendar.YEAR);
                int month = lich.get(Calendar.MONTH);
                int day = lich.get(Calendar.DAY_OF_MONTH);
                //Tạo đối tượng DatePickerDialog và show nó
                DatePickerDialog datedg = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        edtNamSinh.setText(String.format("%02d/%02d/%d", dayOfMonth, month, year));

                        // Tính tuổi từ ngày sinh
                         age = calculateAge(year, month, dayOfMonth);

                        // Kiểm tra nếu tuổi không lớn hơn 18
                        if (age < 18) {
                            // Hiển thị thông báo hoặc thực hiện hành động phù hợp
                            Toast.makeText(context, "Người thuê phải trên 18 tuổi.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, year, month, day);
                datedg.show();
            }
        });

        for (int i=0; i<listpt.size();i++){
            if (nguoiThue.getMaPhong()==(listpt.get(i).getMaPhong())){
                maPhongTro=i;
            }
        }
        spnPhong.setSelection(maPhongTro);

        // Giới tính: 1 = Nam, 0 = Nữ, 2 = Khác
        if (nguoiThue.getGioiTinh()==1){
            rdoNam.setChecked(true); // Nam
        } else if (nguoiThue.getGioiTinh()==0) {
            rdoNu.setChecked(true); // Nữ
        } else if (nguoiThue.getGioiTinh()==2) {
            rdoKhac.setChecked(true); // Khác
        }
        spnPhong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                maPhongTro = listpt.get(i).getMaPhong();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lưu lại các thông tin cần giữ trước khi tạo object mới
                String oldMaNguoiThue = nguoiThue.getMaNguoithue();
                String oldChuTroId = nguoiThue.getChuTroId();
                
                nguoiThue = new NguoiThue();
                nguoiThue.setMaNguoithue(oldMaNguoiThue); // Giữ mã người thuê cũ
                nguoiThue.setMatKhauNT(edtPass.getText().toString());
                nguoiThue.setTenNguoiThue(edtHoTen.getText().toString());
                nguoiThue.setThuongTru(edtThuongTru.getText().toString());
                nguoiThue.setSdt(edtSDT.getText().toString());
                nguoiThue.setcCCD(edtCCCD.getText().toString());
                nguoiThue.setNamSinh(edtNamSinh.getText().toString());
                nguoiThue.setMaPhong(maPhongTro);
                nguoiThue.setChuTroId(oldChuTroId); // Giữ lại chuTroId cũ
                
                // Giới tính: 1 = Nam, 0 = Nữ, 2 = Khác
                if (rdoNam.isChecked()){
                    nguoiThue.setGioiTinh(1); // Nam
                } else if (rdoNu.isChecked()) {
                    nguoiThue.setGioiTinh(0); // Nữ
                }else if (rdoKhac.isChecked()){
                    nguoiThue.setGioiTinh(2); // Khác
                }
                if (TextUtils.isEmpty(edtUser.getText().toString())||TextUtils.isEmpty(edtPass.getText().toString())||TextUtils.isEmpty(edtThuongTru.getText().toString())||TextUtils.isEmpty(edtHoTen.getText().toString())||TextUtils.isEmpty(edtCCCD.getText().toString())||TextUtils.isEmpty(edtSDT.getText().toString())||TextUtils.isEmpty(edtNamSinh.getText().toString())){
                    Toast.makeText(context, "Bạn phải nhập đầy đủ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (age < 18) {
                    // Hiển thị thông báo hoặc thực hiện hành động phù hợp
                    Toast.makeText(context, "Người thuê phải trên 18 tuổi.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!nguoiThue.getSdt().matches("^0\\d{9}$")){
                    Toast.makeText(context, "Sđt sai định dạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!nguoiThue.getcCCD().matches("^0\\d{11}$")){
                    Toast.makeText(context, "CCCD sai định dạng", Toast.LENGTH_SHORT).show();
                    return;
                }




                if (dao.update(nguoiThue)>0){
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
                capNhatList();
                dialog.dismiss();


            }
        });
        dialog.show();

    }
    void capNhatList(){
        // Lấy thông tin user hiện tại
        android.content.SharedPreferences prefs = getSharedPreferences("user11", MODE_PRIVATE);
        String username = prefs.getString("username11", "");
        String role = prefs.getString("role", "");
        
        // Load danh sách theo role
        if ("ADMIN".equalsIgnoreCase(role)) {
            // Admin xem tất cả
            list = (ArrayList<NguoiThue>) dao.getAll();
            listtemp = (ArrayList<NguoiThue>) dao.getAll();
        } else if ("LANDLORD".equalsIgnoreCase(role)) {
            // Chủ trọ chỉ xem người thuê của mình
            if (username != null && !username.isEmpty()) {
                list = (ArrayList<NguoiThue>) dao.getByChuTro(username);
                listtemp = (ArrayList<NguoiThue>) dao.getByChuTro(username);
            } else {
                list = new ArrayList<>();
                listtemp = new ArrayList<>();
            }
        } else {
            // Role khác không xem được
            list = new ArrayList<>();
            listtemp = new ArrayList<>();
        }
        
        nguoiThueAdapter=new NguoiThue_Adapter(nguoiThue_Activity.this,this,list);
        lstNguoiThue.setAdapter(nguoiThueAdapter);
    }

    public void xoa(final String  Id){
        AlertDialog.Builder builder = new AlertDialog.Builder(nguoiThue_Activity.this);
        builder.setTitle("Delete");
        builder.setIcon(R.drawable.warning);//set icon
        builder.setMessage("Bạn có chắc chắn muốn xóa không?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                dao.delete(Id);
                capNhatList();
                dialogInterface.cancel();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        builder.show();
    }
    private int calculateAge(int birthYear, int birthMonth, int birthDay) {
        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1;
        int currentDay = today.get(Calendar.DAY_OF_MONTH);

        int age = currentYear - birthYear;

        // Kiểm tra nếu chưa đến sinh nhật trong năm nay
        if (birthMonth > currentMonth || (birthMonth == currentMonth && birthDay > currentDay)) {
            age--;
        }

        return age;
    }
}
