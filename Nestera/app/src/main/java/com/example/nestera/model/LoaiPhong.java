package com.example.nestera.model;

public class LoaiPhong {
    private int maLoaiPhong;
    private String tenLoaiPhong;
    private int phiDichVu;
    private int giaDien;
    private int giaNuoc;

    public LoaiPhong() {
    }

    public LoaiPhong(int maLoaiPhong, String tenLoaiPhong, int phiDichVu, int giaDien, int giaNuoc) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.phiDichVu = phiDichVu;
        this.giaDien = giaDien;
        this.giaNuoc = giaNuoc;
    }

    public int getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public void setMaLoaiPhong(int maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public String getTenLoaiPhong() {
        return tenLoaiPhong;
    }

    public void setTenLoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }

    public int getPhiDichVu() {
        return phiDichVu;
    }

    public void setPhiDichVu(int phiDichVu) {
        this.phiDichVu = phiDichVu;
    }

    public int getGiaDien() {
        return giaDien;
    }

    public void setGiaDien(int giaDien) {
        this.giaDien = giaDien;
    }

    public int getGiaNuoc() {
        return giaNuoc;
    }

    public void setGiaNuoc(int giaNuoc) {
        this.giaNuoc = giaNuoc;
    }
}
