package  com.example.nestera.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nestera.Activity.VNPAYConfig;
import com.example.nestera.Adapter.NganHangSpinner_Adapter;
import com.example.nestera.Dao.NganHangDao;
import com.example.nestera.Dao.hoaDonDao;
import com.example.nestera.R;
import com.example.nestera.model.NganHang;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Calendar;

public class ThanhToan_Activity extends AppCompatActivity {
    private Button btnThanhToanVNPAY;

    Spinner spNganHang;
    ImageView imgAnhQR_tt, imgAnhThanhToan;
    Button btnChonAnhtt, btnXacNhantt, btnHuytt;
    final int REQUEST_CODE_FOLDER = 456;
    hoaDonDao hoadonDao;
    NganHangDao nhDao;
    int mahoadon;
    String amount;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        // --- Bỏ dòng khởi tạo apiService ---
        // apiService = RetrofitClient.getClient(SERVER_URL).create(ApiService.class);

        anhXaView();
        hoadonDao = new hoaDonDao(this);
        nhDao = new NganHangDao(this);

        mahoadon = getIntent().getIntExtra("mahoadon", -1);
        if (mahoadon == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int tongTien = hoadonDao.getTongTien(mahoadon);
        amount = String.valueOf(tongTien);

        // Cập nhật logic kiểm tra tổng tiền cho nút VNPAY
        if (tongTien <= 0) {
            btnThanhToanVNPAY.setEnabled(false);
            btnThanhToanVNPAY.setText("Không thể thanh toán (0đ)");
        }

        setupNganHangSpinner();
        setupButtonClickListeners();
    }

    private void anhXaView() {
        spNganHang = findViewById(R.id.spnNganHang);
        imgAnhQR_tt = findViewById(R.id.imgAnhQR_tt);
        imgAnhThanhToan = findViewById(R.id.imgAnhThanhToan);
        btnChonAnhtt = findViewById(R.id.btnChonAnhtt);
        btnXacNhantt = findViewById(R.id.btnXacNhantt);
        btnHuytt = findViewById(R.id.btnHuytt);
        // Ánh xạ lại nút VNPAY
        btnThanhToanVNPAY = findViewById(R.id.btnThanhToanVNPAY);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void setupButtonClickListeners() {
        // Thay đổi listener cho nút VNPAY
        btnThanhToanVNPAY.setOnClickListener(v -> createVnpayPaymentUrl());
        // ... (giữ nguyên code các nút khác)
        btnHuytt.setOnClickListener(v-> finish());
    }


    // --- THÊM HÀM TẠO URL THANH TOÁN VNPAY ---
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void createVnpayPaymentUrl() {
        String vnp_TxnRef = VNPAYConfig.getRandomNumber(8); // Mã giao dịch, mỗi lần tạo phải khác nhau
        String vnp_IpAddr = "127.0.0.1"; // IP Address, trên mobile có thể để mặc định
        String vnp_TmnCode = VNPAYConfig.vnp_TmnCode;

        // VNPAY yêu cầu số tiền nhân 100
        long amountValue = Long.parseLong(amount) * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPAYConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPAYConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountValue));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");

        // URL trả về sau khi thanh toán thành công/thất bại.
        // Cần phải định nghĩa một scheme cho ứng dụng của bạn trong AndroidManifest.xml
        // Ví dụ: app://nestera
        vnp_Params.put("vnp_ReturnUrl", "app://nestera");

        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Định dạng ngày tạo giao dịch
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Sắp xếp các tham số theo thứ tự alphabet
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPAYConfig.hmacSHA512(VNPAYConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = VNPAYConfig.vnp_PayUrl + "?" + queryUrl;

        // Mở URL thanh toán bằng trình duyệt
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
        startActivity(browserIntent);
    }

    private void setupNganHangSpinner() {

        ArrayList<NganHang> listnh = (ArrayList<NganHang>) nhDao.getAll();

        NganHangSpinner_Adapter nganHangSpinnerAdapter = new NganHangSpinner_Adapter(this, listnh);

        spNganHang.setAdapter(nganHangSpinnerAdapter);

        spNganHang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                byte[] anhqr = listnh.get(position).getHinhAnh();

                Bitmap bitmap = BitmapFactory.decodeByteArray(anhqr, 0, anhqr.length);

                imgAnhQR_tt.setImageBitmap(bitmap);

            }

            @Override

            public void onNothingSelected(AdapterView<?> parent) {}

        });

    }

// Bỏ phương thức onActivityResult của MoMo SDK vì không còn dùng

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {

            try {

                Uri uri = data.getData();

                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                imgAnhThanhToan.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            }

        }

    }
}