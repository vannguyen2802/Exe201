package com.example.nestera.model;

public class NganHang {
    private int Id;
    private String TenTKNganHang, TenNganHang, STK;
    private byte[] HinhAnh;

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
}
