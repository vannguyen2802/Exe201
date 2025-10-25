package com.example.nestera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Dao.chuTroDao;
import com.example.nestera.Activity.AdminDashboardActivity;
import com.example.nestera.Firebase.NguoiThueHybridDao;
import com.example.nestera.Firebase.ChuTroHybridDao;
import com.example.nestera.Fragment.frg_thongtintaikhoan;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class dangnhap extends AppCompatActivity {
    TextInputEditText edtUser, edtPass;
    TextInputLayout tilPass;
    NguoiThueHybridDao hybridDao;
    ChuTroHybridDao chuTroHybridDao;
    nguoiThueDao dao;
    chuTroDao chuTroDao;
    CheckBox chkluu;
    Button btnDN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangnhap);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        chkluu = findViewById(R.id.chkLuu);
        btnDN = findViewById(R.id.btnDangNhap);
        hybridDao = new NguoiThueHybridDao(dangnhap.this);
        chuTroHybridDao = new ChuTroHybridDao(dangnhap.this);
        dao = new nguoiThueDao(dangnhap.this);
        chuTroDao = new chuTroDao(dangnhap.this);
        TextView txtDangKy = findViewById(R.id.txtDangKyChuTro);

        SharedPreferences sharedPreferences = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        edtUser.setText(sharedPreferences.getString("USERNAME", ""));
        edtPass.setText(sharedPreferences.getString("PASSWORD", ""));
        chkluu.setChecked(sharedPreferences.getBoolean("REMEMBER", false));
        btnDN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
        txtDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dangnhap.this, com.example.nestera.Activity.RegisterLandlordActivity.class));
            }
        });
    }

    private void checkLogin() {
        String strUser = edtUser.getText().toString();
        String strPass = edtPass.getText().toString();
        if (strPass.isEmpty() || strUser.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        // Chỉ tài khoản admin mới check local
        if (strUser.equalsIgnoreCase("admin")) {
            if (strPass.equals("NesteraAdmin22102005@")) {
                Toast.makeText(this, "Đăng nhập thành công (Admin)", Toast.LENGTH_SHORT).show();
                remember(strUser, strPass, chkluu.isChecked());
                Intent i = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                i.putExtra("user", strUser);
                SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username11", strUser);
                editor.putString("role", "ADMIN");
                editor.apply();
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Sai mật khẩu Admin!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        // Tất cả tài khoản khác: check Firebase (chủ trọ), nếu fail mới check local người thuê
        chuTroHybridDao.authenticate(strUser, strPass, new com.example.nestera.Firebase.FirestoreRepository.FirestoreCallback<com.example.nestera.model.ChuTro>() {
            @Override
            public void onSuccess(com.example.nestera.model.ChuTro chuTro) {
                runOnUiThread(() -> {
                    Toast.makeText(dangnhap.this, "Đăng nhập thành công (Chủ trọ)", Toast.LENGTH_SHORT).show();
                    remember(strUser, strPass, chkluu.isChecked());
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("user", strUser);
                    SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username11", strUser);
                    editor.putString("role", "LANDLORD");
                    editor.apply();
                    startActivity(i);
                    finish();
                });
            }
            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    // Nếu không phải chủ trọ, thử local người thuê
                    if (dao.CheckLoginNT(strUser, strPass) > 0) {
                        Toast.makeText(dangnhap.this, "Đăng nhập thành công (Người thuê)", Toast.LENGTH_SHORT).show();
                        remember(strUser, strPass, chkluu.isChecked());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("user", strUser);
                        SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username11", strUser);
                        editor.putString("role", "USER");
                        editor.apply();
                        startActivity(i);
                        finish();
                    } else {
                        // Thông báo lỗi phù hợp
                        String errorMsg = e.getMessage();
                        if (errorMsg != null && (errorMsg.contains("ban") || errorMsg.contains("khóa"))) {
                            Toast.makeText(dangnhap.this, "Tài khoản đã bị khóa", Toast.LENGTH_SHORT).show();
                        } else if (errorMsg != null && errorMsg.contains("duyệt")) {
                            Toast.makeText(dangnhap.this, "Tài khoản đang chờ Admin duyệt", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(dangnhap.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void remember(String u, String p, boolean status) {
        SharedPreferences sharedPreferences = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if (!status) {
            edit.clear();
        } else {
            edit.putString("USERNAME", u);
            edit.putString("PASSWORD", p);
            edit.putBoolean("REMEMBER", status);
        }
        edit.commit();
    }
}