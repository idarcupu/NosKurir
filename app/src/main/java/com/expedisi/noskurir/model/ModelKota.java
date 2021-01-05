package com.expedisi.noskurir.model;

import java.io.Serializable;

public class ModelKota implements Serializable {
    String barat, timur, utara, selatan;
    String pulau, provinsi, nama, sameday;
    String idkota;

    public ModelKota() {
    }

    public String getIdkota() {
        return idkota;
    }

    public void setIdkota(String idkota) {
        this.idkota = idkota;
    }

    public ModelKota(String barat, String timur, String utara, String selatan, String pulau, String provinsi, String nama, String sameday) {
        this.barat = barat;
        this.timur = timur;
        this.utara = utara;
        this.selatan = selatan;
        this.pulau = pulau;
        this.provinsi = provinsi;
        this.nama = nama;
        this.sameday = sameday;
    }

    public String getBarat() {
        return barat;
    }

    public String getTimur() {
        return timur;
    }

    public String getUtara() {
        return utara;
    }

    public String getSelatan() {
        return selatan;
    }

    public String getPulau() {
        return pulau;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public String getNama() {
        return nama;
    }

    public String getSameday() {
        return sameday;
    }
}
