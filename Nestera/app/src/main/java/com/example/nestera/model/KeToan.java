package com.example.nestera.model;

public class KeToan {
    private String maKeToan;
    private String tenKeToan;
    private String matkhauKT;
    private String email;
    private String sdt;
    private String maChuTro;

    public KeToan() {
    }

    public KeToan(String maKeToan, String tenKeToan, String matkhauKT) {
        this.maKeToan = maKeToan;
        this.tenKeToan = tenKeToan;
        this.matkhauKT = matkhauKT;
    }

    public String getMaKeToan() {
        return maKeToan;
    }

    public void setMaKeToan(String maKeToan) {
        this.maKeToan = maKeToan;
    }

    public String getTenKeToan() {
        return tenKeToan;
    }

    public void setTenKeToan(String tenKeToan) {
        this.tenKeToan = tenKeToan;
    }

    public String getMatkhauKT() {
        return matkhauKT;
    }

    public void setMatkhauKT(String matkhauKT) {
        this.matkhauKT = matkhauKT;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getMatKhau() {
        return matkhauKT;
    }

    public void setMatKhau(String matKhau) {
        this.matkhauKT = matKhau;
    }

    public String getMaChuTro() {
        return maChuTro;
    }

    public void setMaChuTro(String maChuTro) {
        this.maChuTro = maChuTro;
    }
}
