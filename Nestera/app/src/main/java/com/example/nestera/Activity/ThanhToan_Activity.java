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

import vn.momo.momo_partner.AppMoMoLib;
import vn.momo.momo_partner.MoMoParameterNameMap;
import com.example.nestera.Adapter.HoaDon_Adapter;
import com.example.nestera.Adapter.NganHangSpinner_Adapter;
import com.example.nestera.Dao.NganHangDao;
import com.example.nestera.Dao.hoaDonDao;
import com.example.nestera.R;
import com.example.nestera.model.HoaDon;
import com.example.nestera.model.NganHang;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private String amount = "10000";
    private String fee = "0";
    int environment = 0;//developer default
    private String merchantName = "NESTERA";
    private String merchantCode = "MOMOYQVY20251008";
    private String merchantNameLabel = "NESTERA";
    private String description = "Thanh toán dịch vụ tro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

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
    //Get token through MoMo app
    private void requestPayment() {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
//        if (mahoadon.getText().toString() != null && edAmount.getText().toString().trim().length() != 0)
//            amount = edAmount.getText().toString().trim();

        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        eventValue.put("orderId", "orderId123456789"); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", "Mã đơn hàng"); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", 0); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId",  merchantCode+"merchant_billId_"+System.currentTimeMillis());
        eventValue.put("partnerCode", merchantCode);
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "CGV Cresent Mall");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Special");
            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
            objExtraData.put("movie_format", "2D");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventValue.put("extraData", objExtraData.toString());

        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);


    }
    //Get token callback from MoMo app an submit to server side
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if(data != null) {
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
//                    tvMessage.setText("message: " + "Get token " + data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                    } else {
//                        tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
//                    tvMessage.setText("message: " + message);
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
//                    tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                } else {
                    //TOKEN FAIL
//                    tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                }
            } else {
//                tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
            }
        } else {
//            tvMessage.setText("message: " + this.getString(R.string.not_receive_info_err));
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null){
//            Uri uri = data.getData();
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(uri);
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                imgAnhThanhToan.setImageBitmap(bitmap);
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}