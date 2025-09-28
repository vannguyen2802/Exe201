package com.example.nestera.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nestera.Adapter.HoaDon_Adapter;
import com.example.nestera.Adapter.NganHangSpinner_Adapter;
import com.example.nestera.Dao.NganHangDao;
import com.example.nestera.Dao.hoaDonDao;
import com.example.nestera.R;
import com.example.nestera.model.HoaDon;
import com.example.nestera.model.NganHang;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ThanhToan_Activity extends AppCompatActivity {
    Spinner spNganHang;
    ImageView imgAnhQR_tt, imgAnhThanhToan;
    ListView lstHoaDon;
    Button btnChonAnhtt,btnXacNhantt,btnHuytt;
    EditText edtmaHoaDon;
    ArrayList<HoaDon> list;
    HoaDon_Adapter hoaDonAdapter;
    hoaDon_Activity hoaDonActivity;
    ArrayList<NganHang> listnh;
    hoaDonDao hoadonDao;
    NganHangDao nhDao;
    HoaDon hoaDon;
    NganHangSpinner_Adapter nganHangSpinnerAdapter;
    int maNganHang;
    byte[] anhthanhtoan;
    byte[] anhqr;
    int mahoadon;
    final int REQUEST_CODE_FOLDER = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);


        spNganHang=findViewById(R.id.spnNganHang);
        imgAnhQR_tt=findViewById(R.id.imgAnhQR_tt);
        imgAnhThanhToan=findViewById(R.id.imgAnhThanhToan);
        btnChonAnhtt=findViewById(R.id.btnChonAnhtt);
        btnXacNhantt=findViewById(R.id.btnXacNhantt);
        btnHuytt=findViewById(R.id.btnHuytt);
        edtmaHoaDon=findViewById(R.id.edtMaHoaDon);
        edtmaHoaDon.setVisibility(View.GONE);
        hoaDonActivity=new hoaDon_Activity();
        hoadonDao=new hoaDonDao(ThanhToan_Activity.this);
        list= (ArrayList<HoaDon>) hoadonDao.getAll();
        hoaDon=new HoaDon();

        mahoadon=getIntent().getIntExtra("mahoadon",-1);

        btnChonAnhtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_FOLDER);
//                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_FOLDER);
            }
        });

        nhDao = new NganHangDao(ThanhToan_Activity.this);
        listnh=new ArrayList<NganHang>();
        listnh= (ArrayList<NganHang>) nhDao.getAll();
        nganHangSpinnerAdapter = new NganHangSpinner_Adapter(ThanhToan_Activity.this,listnh);
        spNganHang.setAdapter(nganHangSpinnerAdapter);
        spNganHang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                maNganHang= listnh.get(i).getId();
                anhqr=listnh.get(i).getHinhAnh();
                Bitmap bitmap = BitmapFactory.decodeByteArray(anhqr,0,anhqr.length);
                imgAnhQR_tt.setImageBitmap(bitmap);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        edtmaHoaDon.setText(String.valueOf(mahoadon));

        btnHuytt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnXacNhantt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgAnhThanhToan.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                anhthanhtoan = byteArrayOutputStream.toByteArray();
                hoaDon.setAnhThanhToan(anhthanhtoan);
                hoaDon.setTrangThai(1);
                hoaDon.setMaHoaDon(Integer.parseInt(edtmaHoaDon.getText().toString()));
                if (hoadonDao.updateanh(hoaDon)>0){
                    Toast.makeText(ThanhToan_Activity.this, "Đã gửi", Toast.LENGTH_SHORT).show();
                    hoadonDao.updateTrangThaiHoaDon(mahoadon,1);
                }else {
                    Toast.makeText(ThanhToan_Activity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                }
                Intent intent=new Intent(ThanhToan_Activity.this,hoaDon_Activity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAnhThanhToan.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}