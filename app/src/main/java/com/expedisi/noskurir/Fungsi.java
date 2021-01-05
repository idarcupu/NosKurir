package com.expedisi.noskurir;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Fungsi {
    Context c;
    Activity a;
    public SharedPreferences sp;
    public SharedPreferences.Editor ed;
    public FirebaseAuth fa;
    public DatabaseReference dr;
    public ProgressDialog pd;
    public final String tag = "com.expedisi.noskurir";
    public static final String xyz = "https://fcm.googleapis.com/fcm/send";
    String goolgeMap = "com.google.android.apps.maps";
    Uri gmmIntentUri;
    Intent mapIntent;
    public StorageReference sr;
    public static final int kodenya = 8910;

    public double jarakGratis(String k1, String k2, String l1, String l2){
        Location loc1 = new Location("");
        loc1.setLatitude(Float.valueOf(l1));
        loc1.setLongitude(Float.valueOf(l2));

        Location loc2 = new Location("");
        loc2.setLatitude(Float.valueOf(k1));
        loc2.setLongitude(Float.valueOf(k2));

        double kilometer = loc1.distanceTo(loc2) / 1000;
        return kilometer;
    }

    public Fungsi(Context c) {
        this.c = c;
    }

    public Fungsi(Context c, Activity a) {
        this.c = c;
        this.a = a;
        sp = c.getSharedPreferences(tag, Context.MODE_PRIVATE);
        ed = sp.edit();
        dr = FirebaseDatabase.getInstance().getReference();
        sr = FirebaseStorage.getInstance().getReference();
        fa = FirebaseAuth.getInstance();
        pd = new ProgressDialog(c);
        pd.setMessage("Memuat data...");
        pd.setCancelable(false);
    }

    public String kurir(String kode){
        return sp.getString(kode, "");
    }

    public void panggilMap(String lat, String lon) {
        gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon);
        mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(goolgeMap);
        if (mapIntent.resolveActivity(a.getPackageManager()) != null) {
            c.startActivity(mapIntent);
        } else {
            Toast.makeText(c, "Google Maps Belum Terinstal. Silahkan Install Terlebih dahulu.",
                    Toast.LENGTH_LONG).show();
        }
    }

    String jamsekarang;
    public String jamSekarang(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        jamsekarang = format.format(cal.getTime());
        return jamsekarang;
    }

    public String tglSekarang(String kode){
        DateFormat tglformat = null;
        if(kode.equals("indo")){
            tglformat = new SimpleDateFormat("dd-MM-yyyy");
        }else if(kode.equals("eropa")){
            tglformat = new SimpleDateFormat("yyyy-MM-dd");
        }
        Date date = new Date();
        return tglformat.format(date);
    }
}
