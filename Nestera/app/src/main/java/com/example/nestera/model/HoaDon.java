package com.example.nestera.model;

import java.util.Date;

public class HoaDon {
    private int maHoaDon,soDien,donGiaDien,soNguoi,donGiaNuoc,phiDichVu,tienPhong,trangThai,maPhong;
    private String sdt,ghiChu,maNguoiThue;
    private Date ngayTao;
    private byte[] anhThanhToan;

    public HoaDon() {
    }

    public HoaDon(int maHoaDon, int soDien, int donGiaDien, int soNguoi, int donGiaNuoc, int phiDichVu, int tienPhong, int trangThai, int maPhong, String sdt, String ghiChu, String maNguoiThue, Date ngayTao, byte[] anhThanhToan) {
        this.maHoaDon = maHoaDon;
        this.soDien = soDien;
        this.donGiaDien = donGiaDien;
        this.soNguoi = soNguoi;
        this.donGiaNuoc = donGiaNuoc;
        this.phiDichVu = phiDichVu;
        this.tienPhong = tienPhong;
        this.trangThai = trangThai;
        this.maPhong = maPhong;
        this.sdt = sdt;
        this.ghiChu = ghiChu;
        this.maNguoiThue = maNguoiThue;
        this.ngayTao = ngayTao;
        this.anhThanhToan = anhThanhToan;
    }

    public int getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public int getSoDien() {
        return soDien;
    }

    public void setSoDien(int soDien) {
        this.soDien = soDien;
    }

    public int getDonGiaDien() {
        return donGiaDien;
    }

    public void setDonGiaDien(int donGiaDien) {
        this.donGiaDien = donGiaDien;
    }

    public int getSoNguoi() {
        return soNguoi;
    }

    public void setSoNguoi(int soNguoi) {
        this.soNguoi = soNguoi;
    }

    public int getDonGiaNuoc() {
        return donGiaNuoc;
    }

    public void setDonGiaNuoc(int donGiaNuoc) {
        this.donGiaNuoc = donGiaNuoc;
    }

    public int getPhiDichVu() {
        return phiDichVu;
    }

    public void setPhiDichVu(int phiDichVu) {
        this.phiDichVu = phiDichVu;
    }

    public int getTienPhong() {
        return tienPhong;
    }

    public void setTienPhong(int tienPhong) {
        this.tienPhong = tienPhong;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public int getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(int maPhong) {
        this.maPhong = maPhong;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
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

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public byte[] getAnhThanhToan() {
        return anhThanhToan;
    }

    public void setAnhThanhToan(byte[] anhThanhToan) {
        this.anhThanhToan = anhThanhToan;
    }
}
