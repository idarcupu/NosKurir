package com.expedisi.noskurir.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.expedisi.noskurir.DetailPesana2;
import com.expedisi.noskurir.DetailPesanan;
import com.expedisi.noskurir.R;
import com.expedisi.noskurir.model.ModelTransaksi;

import java.util.ArrayList;

public class AdapterTransaksi extends RecyclerView.Adapter<AdapterTransaksi.Vhnya> {
    Context c;
    Activity a;
    ArrayList<ModelTransaksi> list;

    public AdapterTransaksi(Context c, Activity a, ArrayList<ModelTransaksi> list) {
        this.c = c;
        this.a = a;
        this.list = list;
    }

    @NonNull
    @Override
    public Vhnya onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.adaptertransaksi, null);
        return new Vhnya(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Vhnya h, int position) {
        ModelTransaksi m = list.get(position);
        h.cvtransaksi.setTag(h);
        h.cvtransaksi.setOnClickListener(v -> {
            if(m.getJenis().equals("jemput")){
                Intent ke = new Intent(c, DetailPesanan.class);
                ke.putExtra("noresi", m.getNoresi());
                a.startActivity(ke);
            }else{
                Intent ke = new Intent(c, DetailPesana2.class);
                ke.putExtra("noresi", m.getNoresi());
                a.startActivity(ke);
            }

        });
        h.resi.setText(m.getNoresi());
        h.namapengguna.setText(m.getNamapemesan());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class Vhnya extends RecyclerView.ViewHolder {

        LinearLayout cvtransaksi;
        TextView resi, namapengguna;

        public Vhnya(View v){
            super(v);
            cvtransaksi = v.findViewById(R.id.cvtransaksi);
            resi = v.findViewById(R.id.resi);
            namapengguna = v.findViewById(R.id.namapengguna);
        }
    }
}
