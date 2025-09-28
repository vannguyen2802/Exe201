package com.example.nestera.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nestera.Adapter.SPPhong_Adapter;
import com.example.nestera.Adapter.SuCo_Adapter;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Dao.phongTroDao;
import com.example.nestera.Dao.suCoDao;
import com.example.nestera.R;
import com.example.nestera.model.NguoiThue;
import com.example.nestera.model.PhongTro;
import com.example.nestera.model.suCo;

import java.util.ArrayList;

public class suCo_Activity extends AppCompatActivity {

    ListView lstSuCo;
    ArrayList<suCo> list;
    ArrayList<suCo> listtemp;
    EditText edtSearch;
    ArrayList<PhongTro> list_phong;
    ArrayList<PhongTro> list_phongtemp;
    SuCo_Adapter adapter;
    suCo item;
    suCoDao dao;
    ImageView btnAdd;
    EditText edtMaSuCo, edtLoaiSuCo, edtMoTa, edtPhong;
    Button btnHuy, btnXacNhan;
    int position, maPhong,mp;
    CheckBox chk;
    phongTroDao dao_phong;
    PhongTro item_phong;
    SPPhong_Adapter spinnerAdapter;
    nguoiThueDao ntDao;
    String tenPhong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_su_co);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sự cố");

        Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        ntDao = new nguoiThueDao(suCo_Activity.this);
        dao_phong=new phongTroDao(suCo_Activity.this);
        if(username.equalsIgnoreCase("admin")){

        }else {
            NguoiThue nt = ntDao.getID(username);
            maPhong = nt.getMaPhong();
            tenPhong = dao_phong.getTenPhongTheoMaPhong(maPhong);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lstSuCo = findViewById(R.id.lstSuCo);
        dao = new suCoDao(suCo_Activity.this);
        btnAdd = findViewById(R.id.btnadd_toolbar);


        listtemp = (ArrayList<suCo>) dao.getAll();
        list_phongtemp = (ArrayList<PhongTro>) dao_phong.getAll();
        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                list.clear();
                for (suCo sc: listtemp){
                    for (PhongTro pt1: list_phongtemp) {
                        pt1 = dao_phong.getID(String.valueOf(sc.getMaPhong()));
                        if (pt1.getTenPhong().contains(charSequence.toString())){
                            list.add(sc);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        capnhatLv();
        if (username.equalsIgnoreCase("admin")) {
            btnAdd.setVisibility(View.GONE);
        } else {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opendialog(suCo_Activity.this, 0);
                }
            });
        }
        if (username.equalsIgnoreCase("admin")) {
            capnhatLv();
        } else {
             mp = ntDao.getMaPhongByUser(username);
            list = new ArrayList<suCo>();
            list = (ArrayList<suCo>) dao.getSuCoByMaPhong(mp);
            adapter = new SuCo_Adapter(suCo_Activity.this, this, list);
            lstSuCo.setAdapter(adapter);
        }
        if (username.equalsIgnoreCase("admin")) {

        }else {
            lstSuCo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    item = list.get(i);
                    opendialog(suCo_Activity.this, 1);
                    return false;
                }
            });
        }

    }

    public void opendialog(Context context, int type) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_su_co);
        edtMaSuCo = dialog.findViewById(R.id.edtMaSuCo);
        edtMaSuCo.setVisibility(View.GONE);
        edtLoaiSuCo = dialog.findViewById(R.id.edtLoaiSuCo);
        edtMoTa = dialog.findViewById(R.id.edtMota);
        edtPhong = dialog.findViewById(R.id.edtPhong);
        chk = dialog.findViewById(R.id.chkDaSua);
        btnHuy = dialog.findViewById(R.id.btnHuy);
        btnXacNhan = dialog.findViewById(R.id.btnXacNhan);
        list_phong = new ArrayList<PhongTro>();
        dao_phong = new phongTroDao(context);
        list_phong = (ArrayList<PhongTro>) dao_phong.getAll();
        spinnerAdapter = new SPPhong_Adapter(context, list_phong);
        chk.setVisibility(View.GONE);
        edtPhong.setEnabled(false);

        edtPhong.setText(tenPhong);
        edtMaSuCo.setEnabled(false);
        edtLoaiSuCo.setInputType(InputType.TYPE_NULL);
        edtLoaiSuCo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] loaiSuCo = {"Điện", "Nước", "Khác"};
                AlertDialog.Builder builder = new AlertDialog.Builder(suCo_Activity.this);
                builder.setTitle("Chọn Loại sự cố");
                builder.setItems(loaiSuCo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        edtLoaiSuCo.setText(loaiSuCo[i]);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        if (type != 0) {
            edtMaSuCo.setText(item.getMaSuCo() + "");
            edtLoaiSuCo.setText(item.getTenSuCo() + "");
            edtMoTa.setText(item.getNoiDung() + "");
            edtPhong.setText(tenPhong);
            if (item.getTrangThai() == 1) {
                chk.setChecked(true);
                edtLoaiSuCo.setEnabled(false);
                edtMoTa.setEnabled(false);
            } else {
                chk.setChecked(false);
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
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtLoaiSuCo.getText().toString()) || TextUtils.isEmpty(edtMoTa.getText().toString())) {
                    Toast.makeText(context, "Bạn phải nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                item = new suCo();
                item.setTenSuCo(edtLoaiSuCo.getText().toString());
                item.setNoiDung(edtMoTa.getText().toString());
                item.setMaPhong(maPhong);
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
                    item.setMaSuCo(Integer.parseInt(edtMaSuCo.getText().toString()));
                    if (dao.update(item) > 0) {
                        Toast.makeText(context, "Sửa thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Sửa thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                capnhatlv_nt();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void capnhatLv() {

        list = (ArrayList<suCo>) dao.getAll();
        adapter = new SuCo_Adapter(suCo_Activity.this, this, list);
        lstSuCo.setAdapter(adapter);
    }public void capnhatlv_nt(){
        SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
        String username = preferences.getString("username11", "...");
        mp = ntDao.getMaPhongByUser(username);
        list = new ArrayList<suCo>();
        list = (ArrayList<suCo>) dao.getSuCoByMaPhong(mp);
        adapter = new SuCo_Adapter(suCo_Activity.this, this, list);
        lstSuCo.setAdapter(adapter);
    }
}