package com.expedisi.noskurir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.expedisi.noskurir.adapter.AdapterTransaksi;
import com.expedisi.noskurir.auth.Login;
import com.expedisi.noskurir.helper.AmbilLokasi;
import com.expedisi.noskurir.model.ModelKurirOnline;
import com.expedisi.noskurir.model.ModelTransaksi;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    RecyclerView rvpesanan;
    ArrayList<ModelTransaksi> listtransaksi = new ArrayList<>();
    AdapterTransaksi at;
    private GoogleApiClient gac;
    CircleImageView fotokurir;
    TextView namakurir, emailkurir, menujemput, menuantar;
    ImageView titik3;
    SwitchButton tombolbekerja;
    String tokennya;
    AmbilLokasi ambilLokasi;
    ConnectivityManager conMgr;
    NetworkInfo netInfo;
    Fungsi f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        f = new Fungsi(this, this);
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
        ambilLokasi = new AmbilLokasi(this, this);

        tokennya = FirebaseInstanceId.getInstance().getToken();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken(getString(R.string.default_web_client_id)).build();
        gac = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        f.dr.child("token_kurir").child(f.sp.getString("idku", "")).setValue(tokennya);



        rvpesanan = findViewById(R.id.rvpesanan);
        rvpesanan.setLayoutManager(new GridLayoutManager(this, 1));
        rvpesanan.setHasFixedSize(true);
        at = new AdapterTransaksi(this, this, listtransaksi);
        rvpesanan.setAdapter(at);

        menujemput = findViewById(R.id.menujemput);
        menuantar = findViewById(R.id.menuantar);
        daftarPesanan("jemput");

        menuantar.setOnClickListener(v -> daftarPesanan("antar"));

        menujemput.setOnClickListener(v -> daftarPesanan("jemput"));

        titik3 = findViewById(R.id.titik3);
        fotokurir = findViewById(R.id.fotokurir);
        namakurir = findViewById(R.id.namakurir);
        emailkurir = findViewById(R.id.emailkurir);
        tombolbekerja = findViewById(R.id.tombolbekerja);
        f.dr.child("kurirOnline").child(f.kurir("idku")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot kurirOnline) {
                if(kurirOnline.exists()){
                    tombolbekerja.setChecked(true);
                }else{
                    tombolbekerja.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        tombolbekerja.setOnCheckedChangeListener((view, isChecked) -> {
            f.dr.child("kurirOnline").child(f.kurir("idku")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot kurirOnline) {
                    if(isChecked){
                        if(!kurirOnline.exists()){
                            ModelKurirOnline mko = new ModelKurirOnline(f.kurir("idku"), "Online");
                            f.dr.child("kurirOnline").child(f.kurir("idku")).setValue(mko);
                        }

                        if(ambilLokasi.mGoogleApiClient.isConnected() && ambilLokasi.mGoogleApiClient != null){

                        }else{
                            ambilLokasi.getLokasi(netInfo);
                        }
                    }else{
                        if(kurirOnline.exists()){
                            f.dr.child("kurirOnline").child(f.kurir("idku")).removeValue();
                        }
                        if(!ambilLokasi.mGoogleApiClient.isConnected() && !ambilLokasi.mGoogleApiClient.isConnecting()){

                        }else{
                            ambilLokasi.pauseConnect();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });

        Picasso.with(this).load(f.sp.getString("fotoku", "")).into(fotokurir);
        namakurir.setText(f.kurir("namaku"));
        emailkurir.setText(f.kurir("emailku"));

        titik3.setOnClickListener(v -> bsbMenuAtas());
    }

    private void daftarPesanan(String kode) {
        String kodenya = kode.equals("jemput") ? "idkurir" : "idkurir2";
        f.dr.child("transaksi").orderByChild(kodenya).equalTo(f.kurir("idku")+"_proses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot transaksi) {
                listtransaksi.clear();
                for(DataSnapshot ds: transaksi.getChildren()){
                    ModelTransaksi m = ds.getValue(ModelTransaksi.class);
                    m.setJenis(kode);
                    listtransaksi.add(m);
                }
                at.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        ambilLokasi.pauseConnect();
        super.onPause();
    }

    private void bsbMenuAtas(){
        BottomSheetDialog bsd = new BottomSheetDialog(MainActivity.this);
        View v = getLayoutInflater().inflate(R.layout.bsbmenuatas, null);
        RelativeLayout logout = v.findViewById(R.id.logout);
        logout.setOnClickListener(v1 -> hapusSharedPreferences(bsd));

        bsd.setContentView(v);
        bsd.show();
    }

    public void hapusSharedPreferences(BottomSheetDialog bsd) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    f.dr.child("kurir").child(f.kurir("idku")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot kurir) {
                            f.dr.child("kurirOnline").child(f.kurir("idku")).removeValue();
                            f.ed.clear();
                            f.ed.commit();
                            Keluar();
                            startActivity(new Intent(MainActivity.this, Login.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Anda yakin akan keluar dari aplikasi?").setPositiveButton("Iya", dialogClickListener)
                .setNegativeButton("Batal", dialogClickListener).show();
        bsd.cancel();
    }

    public void Keluar(){
        f.fa.signOut();
        Auth.GoogleSignInApi.signOut(gac).setResultCallback(status -> {

        });
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    System.exit(0);
                }
            },250);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Klik sekali lagi untuk keluar.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}