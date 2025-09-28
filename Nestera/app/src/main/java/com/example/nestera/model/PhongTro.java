package com.example.nestera.model;

public class PhongTro {
    private int maPhong,maLoai,gia;
    private String tenPhong,tienNghi;
    private int trangThai;

    public PhongTro() {
    }

    public PhongTro(int maPhong, int maLoai, int gia, String tenPhong, String tienNghi, int trangThai) {
        this.maPhong = maPhong;
        this.maLoai = maLoai;
        this.gia = gia;
        this.tenPhong = tenPhong;
        this.tienNghi = tienNghi;
        this.trangThai = trangThai;
    }

    public int getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(int maPhong) {
        this.maPhong = maPhong;
    }

    public int getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(int maLoai) {
        this.maLoai = maLoai;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getTienNghi() {
        return tienNghi;
    }

    public void setTienNghi(String tienNghi) {
        this.tienNghi = tienNghi;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
}
