package com.expedisi.noskurir.helper;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.SparseIntArray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public abstract class HakAkses extends AppCompatActivity {
    private SparseIntArray cekKesalahan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cekKesalahan = new SparseIntArray();
    }

    public abstract void cekIzin(int kode);
    public void permintaanIzinApp(final int id, final int kode){
        final String[] daftarAkses = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.VIBRATE,
                android.Manifest.permission.INTERNET};

        cekKesalahan.put(kode, id);
        int izinApp = PackageManager.PERMISSION_GRANTED;
        boolean statusIzin = false;
        for(String izin: daftarAkses){
            izinApp = izinApp + ContextCompat.checkSelfPermission(this, izin);
            statusIzin = statusIzin || ActivityCompat.shouldShowRequestPermissionRationale(this, izin);
        }

        if(izinApp != PackageManager.PERMISSION_GRANTED){
            if(statusIzin){
                ActivityCompat.requestPermissions(HakAkses.this, daftarAkses, kode);
            }else{
                ActivityCompat.requestPermissions(this, daftarAkses, kode);
            }
        }else{
            cekIzin(kode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int kode, String[] permissions, int[] hasil) {
        super.onRequestPermissionsResult(kode, permissions, hasil);
        int izinApp = PackageManager.PERMISSION_GRANTED;
        for(int listIzin: hasil){
            izinApp = izinApp + listIzin;
        }

        if(hasil.length > 0 && PackageManager.PERMISSION_GRANTED == izinApp){
            cekIzin(kode);
        }else{
            turnGPSOn();
        }
    }

    private void turnGPSOn() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }
}

