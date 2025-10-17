package com.example.nestera.model;

public class NguoiThue {
    private String maNguoithue,matKhauNT,tenNguoiThue,thuongTru,sdt,cCCD;
    private String namSinh;
    private int gioiTinh;
    private int maPhong;
    private String chuTroId; // Chủ trọ nào tạo người thuê này

    public NguoiThue() {
    }

    public String getMaNguoithue() {
        return maNguoithue;
    }

    public void setMaNguoithue(String maNguoithue) {
        this.maNguoithue = maNguoithue;
    }

    public String getMatKhauNT() {
        return matKhauNT;
    }

    public void setMatKhauNT(String matKhauNT) {
        this.matKhauNT = matKhauNT;
    }

    public String getTenNguoiThue() {
        return tenNguoiThue;
    }

    public void setTenNguoiThue(String tenNguoiThue) {
        this.tenNguoiThue = tenNguoiThue;
    }

    public String getThuongTru() {
        return thuongTru;
    }

    public void setThuongTru(String thuongTru) {
        this.thuongTru = thuongTru;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getcCCD() {
        return cCCD;
    }

    public void setcCCD(String cCCD) {
        this.cCCD = cCCD;
    }

    public String getNamSinh() {
        return namSinh;
    }

    public void setNamSinh(String namSinh) {
        this.namSinh = namSinh;
    }

    public int getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(int gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public int getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(int maPhong) {
        this.maPhong = maPhong;
    }

    public String getChuTroId() {
        return chuTroId;
    }

    public void setChuTroId(String chuTroId) {
        this.chuTroId = chuTroId;
    }

    public NguoiThue(String maNguoithue, String matKhauNT, String tenNguoiThue, String thuongTru, String sdt, String cCCD, String namSinh, int gioiTinh, int maPhong) {
        this.maNguoithue = maNguoithue;
        this.matKhauNT = matKhauNT;
        this.tenNguoiThue = tenNguoiThue;
        this.thuongTru = thuongTru;
        this.sdt = sdt;
        this.cCCD = cCCD;
        this.namSinh = namSinh;
        this.gioiTinh = gioiTinh;
        this.maPhong = maPhong;
    }
}
