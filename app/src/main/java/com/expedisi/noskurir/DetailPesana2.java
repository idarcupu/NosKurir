package com.expedisi.noskurir;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.expedisi.noskurir.adapter.AdapterGudang;
import com.expedisi.noskurir.model.ModelGudang;
import com.expedisi.noskurir.model.ModelTransaksi;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ncorti.slidetoact.SlideToActView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPesana2 extends AppCompatActivity {
    LinearLayout lsamakota, llainkota, ldetail, lpilihgudang, lisilainkota;
    TextView alamatjemput, namapengguna, alamatantar, mapjemput, mapantar, alamatantarlain, mapantarlain;
    CircleImageView fotopengguna;
    CardView cvpilihgudang;
    String noresi;
    SlideToActView geser;
    RecyclerView rvgudang;
    Fungsi f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_pesana2);

        f = new Fungsi(this, this);

        noresi = getIntent().getExtras().getString("noresi");

        ldetail = findViewById(R.id.ldetail);
        lsamakota = findViewById(R.id.lsamakota);
        llainkota = findViewById(R.id.llainkota);
        alamatantar = findViewById(R.id.alamatantar);
        alamatantarlain = findViewById(R.id.alamatantarlain);
        mapantarlain = findViewById(R.id.mapantarlain);
        lpilihgudang = findViewById(R.id.lpilihgudang);
        lisilainkota = findViewById(R.id.lisilainkota);
        fotopengguna = findViewById(R.id.fotopengguna);
        namapengguna = findViewById(R.id.namapengguna);
        cvpilihgudang = findViewById(R.id.cvpilihgudang);
        alamatjemput = findViewById(R.id.alamatjemput);

        mapjemput = findViewById(R.id.mapjemput);
        mapantar = findViewById(R.id.mapantar);

        geser = findViewById(R.id.geser);
        geser.setSliderIcon(R.mipmap.ic_launcher);

        f.dr.child("transaksi").child(noresi).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot transaksi) {
                ModelTransaksi m = transaksi.getValue(ModelTransaksi.class);

//                if(m.getStatus().equals("Menunggu")){
//                    geser.setText("Jemput Barang");
//                    geser.setOnSlideCompleteListener(slideToActView -> {
//                        if(m.getKotaasal().equals(m.getKotatujuan())){
//                            f.dr.child("transaksi").child(noresi).child("status").setValue("Menuju");
//                            geser.resetSlider();
//                            geser.setText("Antar Barang");
//                        }else{
//                            if(!m.getGudang().equals("kosong")){
//                                f.dr.child("transaksi").child(noresi).child("status").setValue("Menuju");
//                                geser.resetSlider();
//                                geser.setText("Antar Barang");
//                            }else{
//                                geser.resetSlider();
//                                Toast.makeText(DetailPesana2.this, "Anda belum memilih gudang Nos.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }else if(m.getStatus().equals("Menuju")){
//                    geser.setText("Antar Barang");
//                    geser.setOnSlideCompleteListener(slideToActView -> {
//                        f.dr.child("transaksi").child(noresi).child("status").setValue("Antar");
//                        geser.resetSlider();
//                        geser.setText("Selesaikan");
//                        geser.setReversed(true);
//                    });
//                }else if(m.getStatus().equals("Antar")){
//                    geser.setText("Selesaikan");
//                    geser.setReversed(true);
//                    geser.setOnSlideCompleteListener(slideToActView -> {
//                        String st = m.getKotaasal().equals(m.getKotatujuan()) ? "Sukses" : "SampaiGudang";
//                        HashMap<String, Object> mp = new HashMap();
//                        mp.put("transaksi/"+noresi+"/status", st);
//                        String[] gudangsplit = m.getGudang().split(Pattern.quote("_"));
//                        mp.put("transaksi/"+noresi+"/gudang", gudangsplit[0]+"_darikurir");
//                        mp.put("transaksi/"+noresi+"/idkurir", f.kurir("idku")+"_selesai");
//                        f.dr.updateChildren(mp);
//                        geser.resetSlider();
//                        geser.setText("Antar Barang");
//                        geser.setReversed(true);
//                        finish();
//                    });
//                }else if(m.getStatus().equals("SampaiGudang") || m.getStatus().equals("Sukses")){
//                    geser.setVisibility(View.GONE);
//                }

                ldetail.setOnClickListener(v -> {
                    for(DataSnapshot dskat: transaksi.child("z_kat").getChildren()){
                        Log.e("noscus", dskat.getValue(String.class));
                    }
                });

//                f.dr.child("user").child(m.getIdpemesan()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot user) {
//                        String nama = user.child("nama").getValue(String.class);
//                        namapengguna.setText(nama);
//                        alamatjemput.setText(m.getAddr1());
//                        mapjemput.setOnClickListener(v -> f.panggilMap(m.getLat1(), m.getLon1()));
//
//                        if(m.getKotaasal().equals(m.getKotatujuan())){
//                            lsamakota.setVisibility(View.VISIBLE);
//                            llainkota.setVisibility(View.GONE);
//                            lisilainkota.setVisibility(View.GONE);
//                            alamatantar.setText(m.getAddr2());
//                            mapantar.setOnClickListener(v -> f.panggilMap(m.getLat2(), m.getLon2()));
//                        }else{
//                            lsamakota.setVisibility(View.GONE);
//                            llainkota.setVisibility(View.VISIBLE);
//                            if(!m.getGudang().equals("kosong")){
//                                cvpilihgudang.setVisibility(View.GONE);
//                                lisilainkota.setVisibility(View.VISIBLE);
//                                String[] gudangsplit = m.getGudang().split(Pattern.quote("_"));
//                                f.dr.child("gudang").child(m.getKotaasal()).child(gudangsplit[0]).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot gudang) {
//                                        ModelGudang mg = gudang.getValue(ModelGudang.class);
//                                        alamatantarlain.setText(mg.getAlamat());
//                                        mapantarlain.setOnClickListener(v -> f.panggilMap(mg.getLat(), mg.getLon()));
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }else{
//                                cvpilihgudang.setVisibility(View.VISIBLE);
//                                lisilainkota.setVisibility(View.GONE);
//                            }
//                            lpilihgudang.setOnClickListener(v -> bsbgudang(m.getNoresi(), m.getKotaasal()));
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void bsbgudang(String noresi, String kota){
        final BottomSheetDialog bsd = new BottomSheetDialog(DetailPesana2.this);
        View v = getLayoutInflater().inflate(R.layout.bsbgudang, null);
        ArrayList<ModelGudang> listgudang = new ArrayList<>();
        rvgudang = v.findViewById(R.id.rvgudang);
        rvgudang.setLayoutManager(new GridLayoutManager(this, 1));
        rvgudang.setHasFixedSize(true);
        final AdapterGudang ag = new AdapterGudang(this, this, listgudang, bsd);
        rvgudang.setAdapter(ag);

        f.dr.child("gudang").child(kota).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gudang) {
                f.dr.child("kurirLatLon").child(f.kurir("idku")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot kurirlatlon) {
                        String lat = kurirlatlon.child("lat").getValue(String.class);
                        String lon = kurirlatlon.child("lon").getValue(String.class);
                        listgudang.clear();
                        for(DataSnapshot dsgudang: gudang.getChildren()){
                            ModelGudang mg = dsgudang.getValue(ModelGudang.class);
                            mg.setNoresi(noresi);
                            mg.setLatkurir(lat);
                            mg.setLonkurir(lon);
                            listgudang.add(mg);
                        }
                        ag.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bsd.setContentView(v);
        bsd.show();
    }
}