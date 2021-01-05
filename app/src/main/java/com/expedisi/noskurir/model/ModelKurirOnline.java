package com.expedisi.noskurir.model;

public class ModelKurirOnline {
    String idkurir, status;

    public ModelKurirOnline() {
    }

    public ModelKurirOnline(String idkurir, String status) {
        this.idkurir = idkurir;
        this.status = status;
    }

    public String getIdkurir() {
        return idkurir;
    }

    public String getStatus() {
        return status;
    }
}
