// Đặt file này trong cùng package với các file Activity của bạn
package com.example.nestera.Activity;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class VNPAYConfig {
    // --- THAY THẾ BẰNG THÔNG TIN CỦA BẠN ---
    public static final String vnp_TmnCode = "OOPSJWRW"; // Mã website của bạn do VNPAY cung cấp
    public static final String vnp_HashSecret = "QPGX59MTK8ALSXS719LF45N39IVU30HK"; // Chuỗi bí mật của bạn do VNPAY cung cấp
    // -----------------------------------------

    public static final String vnp_Version = "2.1.0";
    public static final String vnp_Command = "pay";
    public static final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    // Hàm tạo chữ ký HMAC-SHA512
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    // Hàm tạo mã tham chiếu giao dịch ngẫu nhiên
    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}