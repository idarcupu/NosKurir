package com.expedisi.noskurir.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.expedisi.noskurir.Fungsi;
import com.expedisi.noskurir.R;
import com.expedisi.noskurir.model.ModelGudang;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterGudang extends RecyclerView.Adapter<AdapterGudang.Vhnya> {
    Context c;
    Activity a;
    ArrayList<ModelGudang> list;
    BottomSheetDialog bsd;
    Fungsi f;

    public AdapterGudang(Context c, Activity a, ArrayList<ModelGudang> list, BottomSheetDialog bsd) {
        this.c = c;
        this.a = a;
        this.list = list;
        this.bsd = bsd;
        f = new Fungsi(c, a);
    }

    @NonNull
    @Override
    public Vhnya onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.adaptergudang, null);
        return new Vhnya(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Vhnya h, int position) {
        ModelGudang m = list.get(position);
        h.namagudang.setText(m.getNama());
        h.alamatgudang.setText(m.getAlamat());
        h.km.setText(String.valueOf(Math.ceil(f.jarakGratis(m.getLatkurir(), m.getLonkurir(), m.getLat(), m.getLon()) )));
        h.dipilih.setTag(h);
        h.dipilih.setOnClickListener(v -> {
            AlertDialog.Builder ab = new AlertDialog.Builder(c);
            ab.setMessage("Anda yakin akan mengirim ke "+m.getNama()+"?");
            ab.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    f.dr.child("transaksi").child(m.getNoresi()).child("agen").setValue(m.getIdnya()+"_proses", (databaseError, databaseReference) -> bsd.cancel());
                }
            });
            ab.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    bsd.cancel();
                }
            });
            ab.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Vhnya extends RecyclerView.ViewHolder {
        LinearLayout dipilih;
        TextView namagudang, alamatgudang, km;

        public Vhnya(View v){
            super(v);
            dipilih = v.findViewById(R.id.dipilih);
            namagudang = v.findViewById(R.id.namagudang);
            alamatgudang = v.findViewById(R.id.alamatgudang);
            km = v.findViewById(R.id.km);
        }
    }
}
