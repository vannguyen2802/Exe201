package com.example.nestera.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nestera.R;

public class VnPayReturn_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay_return);

        TextView tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        Button btnReturnHome = findViewById(R.id.btnReturnHome);

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String responseCode = data.getQueryParameter("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                tvPaymentStatus.setText("Thanh toán thành công!");
                // TODO: Cập nhật trạng thái hóa đơn trong database tại đây
            } else {
                tvPaymentStatus.setText("Thanh toán thất bại.");
            }
        } else {
            tvPaymentStatus.setText("Không nhận được dữ liệu trả về.");
        }

        btnReturnHome.setOnClickListener(v -> {
            // Chuyển về MainActivity hoặc màn hình danh sách hóa đơn
            Intent homeIntent = new Intent(VnPayReturn_Activity.this, hoaDon_Activity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }
}