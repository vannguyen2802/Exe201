package com.example.nestera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.Fragment.frg_thongtintaikhoan;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class dangnhap extends AppCompatActivity {
    TextInputEditText edtUser, edtPass;
    TextInputLayout tilPass;
    nguoiThueDao dao;
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
        dao=new nguoiThueDao(dangnhap.this);

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
    }

    private void checkLogin() {
        String strUser = edtUser.getText().toString();
        String strPass = edtPass.getText().toString();
        if (strPass.isEmpty() || strUser.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            if (strUser.equalsIgnoreCase("Admin") && strPass.equalsIgnoreCase("Admin")) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                remember(strUser,strPass,chkluu.isChecked());
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("user",strUser);
                SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username11", strUser); // Lưu thông tin người dùng vào SharedPreferences
                editor.apply();

                startActivity(i);
                finish();
            }else if (dao.CheckLoginNT(strUser,strPass)>0){
                Toast.makeText(this, "Đăng nhập thành công(Người thuê)", Toast.LENGTH_SHORT).show();
                remember(strUser,strPass,chkluu.isChecked());
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("user",strUser);
                Bundle bundle = new Bundle();
                bundle.putString("key", strUser);

                SharedPreferences preferences = getSharedPreferences("user11", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username11", strUser); // Lưu thông tin người dùng vào SharedPreferences
                editor.apply();

                // Tạo Fragment và gán Bundle vào Fragment
                frg_thongtintaikhoan myFragment = new frg_thongtintaikhoan();
                myFragment.setArguments(bundle);
                startActivity(i);

                finish();

            }else {
                Toast.makeText(getApplicationContext(), "Username hoặc Password không đúng.", Toast.LENGTH_SHORT).show();
            }

        }
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