package com.example.nestera.model;

public class BaiDang {
    private int id;
    private String tieuDe;
    private String diaChi;
    private int giaThang;
    private double dienTich;
    private String tienNghi;
    private String trangThai; // Còn trống, Đã thuê
    private String hinhAnh; // tên resource hoặc path
    private String chuTroId;
    private Integer maPhong; // link sang PhongTro

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public int getGiaThang() { return giaThang; }
    public void setGiaThang(int giaThang) { this.giaThang = giaThang; }
    public double getDienTich() { return dienTich; }
    public void setDienTich(double dienTich) { this.dienTich = dienTich; }
    public String getTienNghi() { return tienNghi; }
    public void setTienNghi(String tienNghi) { this.tienNghi = tienNghi; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    public String getChuTroId() { return chuTroId; }
    public void setChuTroId(String chuTroId) { this.chuTroId = chuTroId; }
    public Integer getMaPhong() { return maPhong; }
    public void setMaPhong(Integer maPhong) { this.maPhong = maPhong; }
}


