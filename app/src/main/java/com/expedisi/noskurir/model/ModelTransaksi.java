package com.expedisi.noskurir.model;

public class ModelTransaksi {
    String addr1, addr2, agen, agen2, cabang, cabang2, idkurir, idkurir2, kotaasal, kotatujuan, lat1, lon1, lat2, lon2, noresi, idpemesan, status, jenis;
    String namapemesan;
    int bobot, jarak, kirim, ongkir, total;

    public ModelTransaksi() {
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public ModelTransaksi(String addr1, String addr2, String agen, String agen2, String cabang, String cabang2, String idkurir, String idkurir2, String kotaasal, String kotatujuan, String lat1, String lon1, String lat2, String lon2, String noresi, String idpemesan, String status, String namapemesan, int bobot, int jarak, int kirim, int ongkir, int total) {
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.agen = agen;
        this.agen2 = agen2;
        this.cabang = cabang;
        this.cabang2 = cabang2;
        this.idkurir = idkurir;
        this.idkurir2 = idkurir2;
        this.kotaasal = kotaasal;
        this.kotatujuan = kotatujuan;
        this.lat1 = lat1;
        this.lon1 = lon1;
        this.lat2 = lat2;
        this.lon2 = lon2;
        this.noresi = noresi;
        this.idpemesan = idpemesan;
        this.status = status;
        this.namapemesan = namapemesan;
        this.bobot = bobot;
        this.jarak = jarak;
        this.kirim = kirim;
        this.ongkir = ongkir;
        this.total = total;
    }

    public String getAddr1() {
        return addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public String getAgen() {
        return agen;
    }

    public String getAgen2() {
        return agen2;
    }

    public String getCabang() {
        return cabang;
    }

    public String getCabang2() {
        return cabang2;
    }

    public String getIdkurir() {
        return idkurir;
    }

    public String getIdkurir2() {
        return idkurir2;
    }

    public String getKotaasal() {
        return kotaasal;
    }

    public String getKotatujuan() {
        return kotatujuan;
    }

    public String getLat1() {
        return lat1;
    }

    public String getLon1() {
        return lon1;
    }

    public String getLat2() {
        return lat2;
    }

    public String getLon2() {
        return lon2;
    }

    public String getNoresi() {
        return noresi;
    }

    public String getIdpemesan() {
        return idpemesan;
    }

    public String getStatus() {
        return status;
    }

    public String getNamapemesan() {
        return namapemesan;
    }

    public int getBobot() {
        return bobot;
    }

    public int getJarak() {
        return jarak;
    }

    public int getKirim() {
        return kirim;
    }

    public int getOngkir() {
        return ongkir;
    }

    public int getTotal() {
        return total;
    }
}
