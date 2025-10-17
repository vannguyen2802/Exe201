package com.example.nestera.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nestera.Dao.chuTroDao;
import com.example.nestera.Dao.nguoiThueDao;
import com.example.nestera.R;
import com.example.nestera.model.ChuTro;
import com.example.nestera.model.NguoiThue;

import java.util.ArrayList;
import java.util.List;

public class ManageAccountsActivity extends AppCompatActivity {
    android.widget.TableLayout tlLandlords, tlPending, tlTenants;
    Button btnApproveAll;
    ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_accounts);
        setTitle("Quản lý tài khoản");

        tlLandlords = findViewById(R.id.tlLandlords);
        tlPending = findViewById(R.id.tlPending);
        tlTenants = findViewById(R.id.tlTenants);
        btnApproveAll = findViewById(R.id.btnApproveAll);
        ivBack = findViewById(R.id.ivBack);
        
        // Xử lý nút back
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chuTroDao ctDao = new chuTroDao(this);
        nguoiThueDao ntDao = new nguoiThueDao(this);

        // Headers for landlords
        addHeaderRow(tlLandlords, new String[]{"Username","Họ tên","Email","SĐT","CCCD","Trạng thái","Action"});
        List<ChuTro> allLandlords = ctDao.getAll();
        for (ChuTro ct : allLandlords) {
            android.widget.TableRow row = new android.widget.TableRow(this);
            String status = ct.getApproved()==1?(ct.getBanned()==1?"Bị ban":"Active"): (ct.getApproved()==-1?"Từ chối":"Chờ duyệt");
            String[] vals = new String[]{ nullSafe(ct.getMaChuTro()), nullSafe(ct.getTenChuTro()), nullSafe(ct.getEmail()), nullSafe(ct.getSdt()), nullSafe(ct.getCccd()), status };
            for (String v : vals){
                android.widget.TextView tv = new android.widget.TextView(this);
                tv.setText(v); tv.setPadding(12,12,12,12); tv.setSingleLine(true); tv.setEllipsize(android.text.TextUtils.TruncateAt.END);
                row.addView(tv);
            }
            Button action = new Button(this);
            if (ct.getBanned()==1){
                action.setText("Active");
                action.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        int r = ctDao.unbanUser(ct.getMaChuTro());
                        if (r>0){ Toast.makeText(ManageAccountsActivity.this, "Đã mở khóa "+ct.getMaChuTro(), Toast.LENGTH_SHORT).show(); recreate(); }
                    }
                });
            } else {
                action.setText("Ban");
                action.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        int r = ctDao.banUser(ct.getMaChuTro());
                        if (r>0){ Toast.makeText(ManageAccountsActivity.this, "Đã khóa "+ct.getMaChuTro(), Toast.LENGTH_SHORT).show(); recreate(); }
                    }
                });
            }
            row.addView(action);
            tlLandlords.addView(row);
        }

        addHeaderRow(tlPending, new String[]{"Username","Họ tên","Email","SĐT","CCCD","Duyệt","Từ chối"});
        List<ChuTro> pending = ctDao.getPending();
        for (ChuTro ct : pending) {
            addDataRowWithApprove(tlPending, ct, ctDao);
        }

        addHeaderRow(tlTenants, new String[]{"Mã","Họ tên","Thường trú","SĐT","CCCD","Giới tính","Số Phòng","Chủ trọ"});
        List<NguoiThue> tenants = ntDao.getAll();
        for (NguoiThue nt : tenants) {
            // Chuyển đổi giới tính: 0 = Nữ, 1 = Nam
            String gioiTinh = (nt.getGioiTinh() == 1) ? "Nam" : "Nữ";
            // Lấy tên phòng (ví dụ: P201) nếu có, nếu chưa có phòng thì ghi chú
            String soPhong;
            if (nt.getMaPhong() > 0) {
                try {
                    com.example.nestera.Dao.phongTroDao pDao = new com.example.nestera.Dao.phongTroDao(this);
                    String tenP = pDao.getTenPhongTheoMaPhong(nt.getMaPhong());
                    soPhong = (tenP != null && !tenP.isEmpty()) ? tenP : String.valueOf(nt.getMaPhong());
                } catch (Exception e) {
                    soPhong = String.valueOf(nt.getMaPhong());
                }
            } else {
                soPhong = "Chưa có phòng";
            }
            // Tên chủ trọ
            String landlordName = "";
            try {
                ChuTro owner = ctDao.getID(nt.getChuTroId());
                if (owner != null && owner.getTenChuTro() != null && !owner.getTenChuTro().isEmpty()) {
                    landlordName = owner.getTenChuTro();
                } else {
                    landlordName = nt.getChuTroId();
                }
            } catch (Exception e) {
                landlordName = nt.getChuTroId();
            }
            addDataRow(tlTenants, new String[]{
                    nullSafe(nt.getMaNguoithue()),
                    nullSafe(nt.getTenNguoiThue()),
                    nullSafe(nt.getThuongTru()),
                    nullSafe(nt.getSdt()),
                    nullSafe(nt.getcCCD()),
                    gioiTinh,
                    soPhong,
                    nullSafe(landlordName)
            }, false);
        }

        btnApproveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r = ctDao.approveAllPending();
                Toast.makeText(ManageAccountsActivity.this, "Đã duyệt " + r + " tài khoản", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });
    }

    private String nullSafe(String s){ return s==null?"":s; }

    private void addHeaderRow(android.widget.TableLayout table, String[] titles){
        android.widget.TableRow row = new android.widget.TableRow(this);
        for (String t : titles){
            android.widget.TextView tv = new android.widget.TextView(this);
            tv.setText(t);
            tv.setPadding(12,12,12,12);
            tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            row.addView(tv);
        }
        table.addView(row);
    }

    private void addDataRow(android.widget.TableLayout table, String[] values, boolean highlight){
        android.widget.TableRow row = new android.widget.TableRow(this);
        for (String v : values){
            android.widget.TextView tv = new android.widget.TextView(this);
            tv.setText(v);
            tv.setPadding(12,12,12,12);
            tv.setSingleLine(true);
            tv.setEllipsize(android.text.TextUtils.TruncateAt.END);
            if (highlight){ tv.setTextColor(getResources().getColor(android.R.color.holo_orange_dark)); }
            row.addView(tv);
        }
        table.addView(row);
    }

    private void addDataRowWithApprove(android.widget.TableLayout table, ChuTro ct, chuTroDao dao){
        android.widget.TableRow row = new android.widget.TableRow(this);
        String[] vals = new String[]{ nullSafe(ct.getMaChuTro()), nullSafe(ct.getTenChuTro()), nullSafe(ct.getEmail()), nullSafe(ct.getSdt()), nullSafe(ct.getCccd()) };
        for (String v: vals){
            android.widget.TextView tv = new android.widget.TextView(this);
            tv.setText(v);
            tv.setPadding(12,12,12,12);
            tv.setSingleLine(true);
            tv.setEllipsize(android.text.TextUtils.TruncateAt.END);
            row.addView(tv);
        }
        Button approve = new Button(this);
        approve.setText("Duyệt");
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r = dao.approveUser(ct.getMaChuTro());
                if (r>0){
                    Toast.makeText(ManageAccountsActivity.this, "Đã duyệt "+ct.getMaChuTro(), Toast.LENGTH_SHORT).show();
                    recreate();
                }
            }
        });
        row.addView(approve);
        Button reject = new Button(this);
        reject.setText("Từ chối");
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r = dao.rejectUser(ct.getMaChuTro());
                if (r>0){
                    Toast.makeText(ManageAccountsActivity.this, "Đã từ chối "+ct.getMaChuTro(), Toast.LENGTH_SHORT).show();
                    recreate();
                }
            }
        });
        row.addView(reject);
        table.addView(row);
    }
}


