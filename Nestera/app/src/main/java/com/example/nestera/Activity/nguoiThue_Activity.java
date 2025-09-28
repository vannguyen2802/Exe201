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

        listtemp= (ArrayList<NguoiThue>) dao.getAll();
        edtSearch=findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                list.clear();
                for (NguoiThue nt : listtemp){
                    if (nt.getTenNguoiThue().contains(charSequence.toString())){
                        list.add(nt);
                    }
                }
                nguoiThueAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        capNhatList();

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
        spPhongAdapter=new SPPhong_Adapter(context,listpt);
        spnPhong.setAdapter(spPhongAdapter);
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


                nguoiThue.setMaPhong(maPhongTro);
                if (rdoNam.isChecked()){
                    nguoiThue.setGioiTinh(0);
                } else if (rdoNu.isChecked()) {
                    nguoiThue.setGioiTinh(1);
                } else if (rdoKhac.isChecked()){
                    nguoiThue.setGioiTinh(2);
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

        if (nguoiThue.getGioiTinh()==0){
            rdoNam.setChecked(true);
        } else if (nguoiThue.getGioiTinh()==1) {
            rdoNu.setChecked(true);
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
                nguoiThue = new NguoiThue();
                nguoiThue.setMaNguoithue(edtUser.getText().toString());
                nguoiThue.setMatKhauNT(edtPass.getText().toString());
                nguoiThue.setTenNguoiThue(edtHoTen.getText().toString());
                nguoiThue.setThuongTru(edtThuongTru.getText().toString());
                nguoiThue.setSdt(edtSDT.getText().toString());
                nguoiThue.setcCCD(edtCCCD.getText().toString());
                nguoiThue.setNamSinh(edtNamSinh.getText().toString());

                nguoiThue.setcCCD(edtCCCD.getText().toString());
                nguoiThue.setMaPhong(maPhongTro);
                if (rdoNam.isChecked()){
                    nguoiThue.setGioiTinh(0);
                } else if (rdoNu.isChecked()) {
                    nguoiThue.setGioiTinh(1);
                }else if (rdoKhac.isChecked()){
                    nguoiThue.setGioiTinh(2);
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
        list = (ArrayList<NguoiThue>) dao.getAll();
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
