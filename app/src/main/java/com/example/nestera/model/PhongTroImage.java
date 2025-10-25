package com.example.nestera.model;

public class PhongTroImage {
    private int id;
    private int maPhong;
    private String imagePath;
    private int thuTu;
    private int isMain; // 0: ảnh phụ, 1: ảnh chính

    public PhongTroImage() {
    }

    public PhongTroImage(int maPhong, String imagePath, int thuTu, int isMain) {
        this.maPhong = maPhong;
        this.imagePath = imagePath;
        this.thuTu = thuTu;
        this.isMain = isMain;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(int maPhong) {
        this.maPhong = maPhong;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getThuTu() {
        return thuTu;
    }

    public void setThuTu(int thuTu) {
        this.thuTu = thuTu;
    }

    public int getIsMain() {
        return isMain;
    }

    public void setIsMain(int isMain) {
        this.isMain = isMain;
    }

    // Convenience methods
    public boolean isMainImage() {
        return isMain == 1;
    }

    public void setMainImage(boolean isMain) {
        this.isMain = isMain ? 1 : 0;
    }
}