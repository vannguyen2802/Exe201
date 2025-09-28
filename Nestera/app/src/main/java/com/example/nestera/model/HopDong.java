package com.example.nestera.model;

import java.util.Date;

public class HopDong {
    private int maHopDong;
    private String sdt;
    private String CCCD;
    private String thuongTru;
    private Date ngayKy;
    private int thoiHan;
    private String tenPhong;
    private int tienCoc;
    private int giaTien;
    private int soNguoi;
    private int soXe;
    private String ghiChu;
    private String maNguoiThue;
    private int maPhong;
    private byte[] hinhAnhhd;



    public HopDong() {
    }

    public HopDong(int maHopDong, String sdt, String CCCD, String thuongTru, Date ngayKy, int thoiHan, String tenPhong, int tienCoc, int giaTien, int soNguoi, int soXe, String ghiChu, String maNguoiThue, int maPhong, byte[] hinhAnhhd) {
        this.maHopDong = maHopDong;
        this.sdt = sdt;
        this.CCCD = CCCD;
        this.thuongTru = thuongTru;
        this.ngayKy = ngayKy;
        this.thoiHan = thoiHan;
        this.tenPhong = tenPhong;
        this.tienCoc = tienCoc;
        this.giaTien = giaTien;
        this.soNguoi = soNguoi;
        this.soXe = soXe;
        this.ghiChu = ghiChu;
        this.maNguoiThue = maNguoiThue;
        this.maPhong = maPhong;
        this.hinhAnhhd = hinhAnhhd;
    }

    public int getMaHopDong() {
        return maHopDong;
    }

    public void setMaHopDong(int maHopDong) {
        this.maHopDong = maHopDong;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getCCCD() {
        return CCCD;
    }

    public void setCCCD(String CCCD) {
        this.CCCD = CCCD;
    }

    public String getThuongTru() {
        return thuongTru;
    }

    public void setThuongTru(String thuongTru) {
        this.thuongTru = thuongTru;
    }

    public Date getNgayKy() {
        return ngayKy;
    }

    public void setNgayKy(Date ngayKy) {
        this.ngayKy = ngayKy;
    }

    public int getThoiHan() {
        return thoiHan;
    }

    public void setThoiHan(int thoiHan) {
        this.thoiHan = thoiHan;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public int getTienCoc() {
        return tienCoc;
    }

    public void setTienCoc(int tienCoc) {
        this.tienCoc = tienCoc;
    }

    public int getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(int giaTien) {
        this.giaTien = giaTien;
    }

    public int getSoNguoi() {
        return soNguoi;
    }

    public void setSoNguoi(int soNguoi) {
        this.soNguoi = soNguoi;
    }

    public int getSoXe() {
        return soXe;
    }

    public void setSoXe(int soXe) {
        this.soXe = soXe;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getMaNguoiThue() {
        return maNguoiThue;
    }

    public void setMaNguoiThue(String maNguoiThue) {
        this.maNguoiThue = maNguoiThue;
    }

    public int getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(int maPhong) {
        this.maPhong = maPhong;
    }

    public byte[] getHinhAnhhd() {
        return hinhAnhhd;
    }

    public void setHinhAnhhd(byte[] hinhAnhhd) {
        this.hinhAnhhd = hinhAnhhd;
    }
}
