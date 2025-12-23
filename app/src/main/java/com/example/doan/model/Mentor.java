package com.example.doan.model;

import java.io.Serializable;

public class Mentor implements Serializable {

    private String MaMentor;
    private String HoTen;
    private String ChucVu;
    private String NoiLamViec;
    private String ChuyenNganh;
    private String LinkLienHe;
    private String AnhDaiDien;

    // ===== Getter =====
    public String getMaMentor() {
        return MaMentor;
    }

    public String getHoTen() {
        return HoTen;
    }

    public String getChucVu() {
        return ChucVu;
    }

    public String getNoiLamViec() {
        return NoiLamViec;
    }

    public String getChuyenNganh() {
        return ChuyenNganh;
    }

    public String getLinkLienHe() {
        return LinkLienHe;
    }

    public String getAnhDaiDien() {
        return AnhDaiDien;
    }
}
