package com.example.nestera.model;

public class suCo {
    private int maSuCo;
    private String tenSuCo,noiDung;
    private int trangThai,maPhong,maNguoiThue;


    public int getMaSuCo() {
        return maSuCo;
    }

    public void setMaSuCo(int maSuCo) {
        this.maSuCo = maSuCo;
    }

    public String getTenSuCo() {
        return tenSuCo;
    }

    public void setTenSuCo(String tenSuCo) {
        this.tenSuCo = tenSuCo;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
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

    public int getMaNguoiThue() {
        return maNguoiThue;
    }

    public void setMaNguoiThue(int maNguoiThue) {
        this.maNguoiThue = maNguoiThue;
    }

    // Alias methods for Repository compatibility
    public int getMasuCo() { return getMaSuCo(); }
    public void setMasuCo(int maSuCo) { setMaSuCo(maSuCo); }
    public String getTensuCo() { return getTenSuCo(); }
    public void setTensuCo(String tenSuCo) { setTenSuCo(tenSuCo); }
    // Overload to accept String for maNguoiThue (Repository compatibility)
    public void setMaNguoiThue(String maNguoiThue) {
        if (maNguoiThue != null && !maNguoiThue.isEmpty()) {
            this.maNguoiThue = Integer.parseInt(maNguoiThue);
        }
    }

    public suCo(int maSuCo, String tenSuCo, String noiDung, int trangThai, int maPhong, int maNguoiThue) {
        this.maSuCo = maSuCo;
        this.tenSuCo = tenSuCo;
        this.noiDung = noiDung;
        this.trangThai = trangThai;
        this.maPhong = maPhong;
        this.maNguoiThue = maNguoiThue;
    }

    public suCo() {
    }
}
