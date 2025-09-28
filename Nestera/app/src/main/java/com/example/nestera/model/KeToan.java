package com.example.nestera.model;

public class KeToan {
    private String maKeToan;
    private String tenKeToan;
    private String matkhauKT;

    public KeToan() {
    }

    public KeToan(String maKeToan, String tenKeToan, String matkhauKT) {
        this.maKeToan = maKeToan;
        this.tenKeToan = tenKeToan;
        this.matkhauKT = matkhauKT;
    }

    public String getMaKeToan() {
        return maKeToan;
    }

    public void setMaKeToan(String maKeToan) {
        this.maKeToan = maKeToan;
    }

    public String getTenKeToan() {
        return tenKeToan;
    }

    public void setTenKeToan(String tenKeToan) {
        this.tenKeToan = tenKeToan;
    }

    public String getMatkhauKT() {
        return matkhauKT;
    }

    public void setMatkhauKT(String matkhauKT) {
        this.matkhauKT = matkhauKT;
    }
}
