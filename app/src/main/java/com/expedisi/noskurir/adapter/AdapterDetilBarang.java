package com.expedisi.noskurir.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expedisi.noskurir.R;

import java.util.ArrayList;

public class AdapterDetilBarang extends RecyclerView.Adapter<AdapterDetilBarang.Vhnya> {

    Context c;
    Activity a;
    ArrayList<String> list;

    public AdapterDetilBarang(Context c, Activity a, ArrayList<String> list) {
        this.c = c;
        this.a = a;
        this.list = list;
    }

    @NonNull
    @Override
    public Vhnya onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.adapterdetilbarang, null);
        return new Vhnya(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Vhnya h, int position) {
        String s = list.get(position);
        h.apa.setText("- "+s);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Vhnya extends RecyclerView.ViewHolder{

        TextView apa;

        public Vhnya(View v){
            super(v);

            apa = v.findViewById(R.id.apa);
        }

    }
}
