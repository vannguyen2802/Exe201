package com.example.nestera.model;

public class PhongTro {
    private int maPhong,maLoai,gia;
    private String tenPhong,tienNghi;
    private int trangThai;
    private String imageUrl;
    private String imagePath; // Đường dẫn ảnh từ database
    private String diaChi; // Địa chỉ phòng trọ
    private int timNguoiOGhep; // 0: không tìm, 1-4: số người cần tìm
    private int soNguoiHienTai; // Số người hiện tại đang ở

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

    public PhongTro(int maPhong, int maLoai, int gia, String tenPhong, String tienNghi, int trangThai, String imageUrl) {
        this.maPhong = maPhong;
        this.maLoai = maLoai;
        this.gia = gia;
        this.tenPhong = tenPhong;
        this.tienNghi = tienNghi;
        this.trangThai = trangThai;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Convenience method for price formatting
    public double getGiaPhong() {
        return this.gia;
    }

    public void setGiaPhong(double gia) {
        this.gia = (int) gia;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getTimNguoiOGhep() {
        return timNguoiOGhep;
    }

    public void setTimNguoiOGhep(int timNguoiOGhep) {
        this.timNguoiOGhep = timNguoiOGhep;
    }

    public int getSoNguoiHienTai() {
        return soNguoiHienTai;
    }

    public void setSoNguoiHienTai(int soNguoiHienTai) {
        this.soNguoiHienTai = soNguoiHienTai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
}
