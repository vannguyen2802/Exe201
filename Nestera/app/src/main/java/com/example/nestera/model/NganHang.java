package com.example.nestera.model;

public class NganHang {
    private int Id;
    private String TenTKNganHang, TenNganHang, STK;
    private byte[] HinhAnh;
    private String imageUrl; // Firebase Storage URL
    private String soTaiKhoan;
    private String chuTaiKhoan;
    private String chiNhanh;
    private String maChuTro;

    public NganHang() {
    }

    public NganHang(int id, String tenTKNganHang, String tenNganHang, String STK, byte[] hinhAnh) {
        Id = id;
        TenTKNganHang = tenTKNganHang;
        TenNganHang = tenNganHang;
        this.STK = STK;
        HinhAnh = hinhAnh;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTenTKNganHang() {
        return TenTKNganHang;
    }

    public void setTenTKNganHang(String tenTKNganHang) {
        TenTKNganHang = tenTKNganHang;
    }

    public String getTenNganHang() {
        return TenNganHang;
    }

    public void setTenNganHang(String tenNganHang) {
        TenNganHang = tenNganHang;
    }

    public String getSTK() {
        return STK;
    }

    public void setSTK(String STK) {
        this.STK = STK;
    }

    public byte[] getHinhAnh() {
        return HinhAnh;
    }

    public void setHinhAnh(byte[] hinhAnh) {
        HinhAnh = hinhAnh;
    }

    // Alias methods for Repository compatibility
    public int getMaNganHang() { return getId(); }
    public void setMaNganHang(int id) { setId(id); }
    
    public String getSoTaiKhoan() { return soTaiKhoan != null ? soTaiKhoan : STK; }
    public void setSoTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; this.STK = soTaiKhoan; }
    
    public String getChuTaiKhoan() { return chuTaiKhoan != null ? chuTaiKhoan : TenTKNganHang; }
    public void setChuTaiKhoan(String chuTaiKhoan) { this.chuTaiKhoan = chuTaiKhoan; this.TenTKNganHang = chuTaiKhoan; }
    
    public String getChiNhanh() { return chiNhanh; }
    public void setChiNhanh(String chiNhanh) { this.chiNhanh = chiNhanh; }
    
    public String getMaChuTro() { return maChuTro; }
    public void setMaChuTro(String maChuTro) { this.maChuTro = maChuTro; }
    
    public String getTenNganHangAlias() { return TenNganHang; }
    public void setTenNganHangAlias(String tenNganHang) { this.TenNganHang = tenNganHang; }

    // Firebase Storage URL getter/setter
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
