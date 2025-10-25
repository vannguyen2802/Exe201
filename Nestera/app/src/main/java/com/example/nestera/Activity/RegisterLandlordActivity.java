package com.example.nestera.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nestera.Dao.chuTroDao;
import com.example.nestera.Firebase.ChuTroHybridDao;
import com.example.nestera.Firebase.FirestoreRepository;
import com.example.nestera.R;
import com.example.nestera.dangnhap;
import com.example.nestera.model.ChuTro;

public class RegisterLandlordActivity extends AppCompatActivity {

    private ChuTroHybridDao hybridDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_landlord);
        setTitle("Đăng ký chủ trọ");

        EditText edtUser = findViewById(R.id.edtUserId);
        EditText edtPass = findViewById(R.id.edtPassword);
        EditText edtConfirm = findViewById(R.id.edtConfirm);
        EditText edtName = findViewById(R.id.edtFullName);
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPhone = findViewById(R.id.edtPhone);
        EditText edtCCCD = findViewById(R.id.edtCCCD);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        // Khởi tạo hybridDao để tránh NullPointerException
        hybridDao = new ChuTroHybridDao(this);

        chuTroDao dao = new chuTroDao(this);

        btnSubmit.setOnClickListener(v -> {
            String u = edtUser.getText().toString().trim();
            String p = edtPass.getText().toString().trim();
            String cp = edtConfirm.getText().toString().trim();
            String n = edtName.getText().toString().trim();
            String e = edtEmail.getText().toString().trim();
            String s = edtPhone.getText().toString().trim();
            String c = edtCCCD.getText().toString().trim();

            if (TextUtils.isEmpty(u) || TextUtils.isEmpty(p)) {
                Toast.makeText(RegisterLandlordActivity.this, "Vui lòng nhập tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!p.equals(cp)) {
                Toast.makeText(RegisterLandlordActivity.this, "Xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(e) || !android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                Toast.makeText(RegisterLandlordActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(c) || c.length() != 12 || !c.matches("\\d{12}")) {
                Toast.makeText(RegisterLandlordActivity.this, "CCCD phải gồm 12 số", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dao.exists(u)) {
                Toast.makeText(RegisterLandlordActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            ChuTro ct = new ChuTro(u, p, n, e, s, c);
            hybridDao.insert(ct.getMaChuTro(),ct, new FirestoreRepository.FirestoreCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterLandlordActivity.this,
                                "Đăng ký thành công! ID Firestore: " + result,
                                Toast.LENGTH_SHORT).show();
                        // Có thể chuyển sang màn hình đăng nhập
                        startActivity(new Intent(RegisterLandlordActivity.this, dangnhap.class));
                        finish();
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(RegisterLandlordActivity.this,
                                    "Lỗi khi đồng bộ với Firestore: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });
    }
}


