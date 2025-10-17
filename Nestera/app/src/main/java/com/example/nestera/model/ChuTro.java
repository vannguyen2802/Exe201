package com.example.nestera.model;

public class ChuTro {
    private String maChuTro;
    private String matKhau;
    private String tenChuTro;
    private String sdt;
    private String email;
    private String cccd;
    private int approved; // 0: pending, 1: approved
    private int banned;   // 0: active, 1: banned

    public ChuTro() {}

    public ChuTro(String maChuTro, String matKhau, String tenChuTro, String sdt) {
        this.maChuTro = maChuTro;
        this.matKhau = matKhau;
        this.tenChuTro = tenChuTro;
        this.sdt = sdt;
    }

    public ChuTro(String maChuTro, String matKhau, String tenChuTro, String email, String sdt, String cccd) {
        this.maChuTro = maChuTro;
        this.matKhau = matKhau;
        this.tenChuTro = tenChuTro;
        this.email = email;
        this.sdt = sdt;
        this.cccd = cccd;
    }

    public String getMaChuTro() { return maChuTro; }
    public void setMaChuTro(String maChuTro) { this.maChuTro = maChuTro; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getTenChuTro() { return tenChuTro; }
    public void setTenChuTro(String tenChuTro) { this.tenChuTro = tenChuTro; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public int getApproved() { return approved; }
    public void setApproved(int approved) { this.approved = approved; }

    public int getBanned() { return banned; }
    public void setBanned(int banned) { this.banned = banned; }
}


