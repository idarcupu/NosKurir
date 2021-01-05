package com.expedisi.noskurir.model;

public class ModelGudang {
    String alamat, idagen, idnya, lat, lon, nama, latkurir, lonkurir, noresi;

    public ModelGudang() {
    }

    public String getNoresi() {
        return noresi;
    }

    public void setNoresi(String noresi) {
        this.noresi = noresi;
    }

    public ModelGudang(String alamat, String idagen, String idnya, String lat, String lon, String nama) {
        this.alamat = alamat;
        this.idagen = idagen;
        this.idnya = idnya;
        this.lat = lat;
        this.lon = lon;
        this.nama = nama;
    }

    public String getLatkurir() {
        return latkurir;
    }

    public void setLatkurir(String latkurir) {
        this.latkurir = latkurir;
    }

    public String getLonkurir() {
        return lonkurir;
    }

    public void setLonkurir(String lonkurir) {
        this.lonkurir = lonkurir;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getIdagen() {
        return idagen;
    }

    public String getIdnya() {
        return idnya;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getNama() {
        return nama;
    }
}
