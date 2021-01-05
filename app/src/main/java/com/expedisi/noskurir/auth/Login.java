package com.expedisi.noskurir.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.expedisi.noskurir.Fungsi;
import com.expedisi.noskurir.MainActivity;
import com.expedisi.noskurir.R;
import com.expedisi.noskurir.helper.HakAkses;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Login extends HakAkses implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private SignInButton masuk;
    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount x;
    FirebaseAuth auth;
    FirebaseUser fu;
    Fungsi f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
    }

    @Override
    public void cekIzin(int kode) {

    }

    private void init() {
        f = new Fungsi(this, this);
        auth = FirebaseAuth.getInstance();
        fu = auth.getCurrentUser();
        permintaanIzinApp( R.string.izinkan, 8910);
        masuk = (SignInButton) findViewById(R.id.masuk);
        ubahTeksTombolGoogle(masuk, "Masuk Google Akun");
        masuk.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken(getString(R.string.default_web_client_id)).build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
    }

    public void ubahTeksTombolGoogle(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    public void Masuk(GoogleApiClient googleApiClient){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,8910);
    }

    public void Keluar(FirebaseAuth auth, GoogleApiClient googleApiClient){
        auth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

            }
        });
    }

    @Override
    protected void onStart() {
        if(!f.sp.getString("idku","").equals("") && !f.sp.getString("idku","").equals(null)){
            Intent ke = new Intent(Login.this, MainActivity.class);
            startActivity(ke);
        }
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 8910){
            GoogleSignInResult hasil = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            ambilData(hasil,x,auth, f.dr,"",f.ed,googleApiClient);
        }
    }

    public void ambilData(final GoogleSignInResult result, GoogleSignInAccount x, final FirebaseAuth auth, final DatabaseReference dr, final String imeinya, final SharedPreferences.Editor ed, final GoogleApiClient googleApiClient){
        if(result.isSuccess()){
            x = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(x.getIdToken(), null);
            auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(Login.this, "Gagal Login.", Toast.LENGTH_SHORT).show();
                    } else {
                        dr.child("kurir").orderByChild("email").equalTo(auth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot admin) {
                                if(admin.getChildrenCount() > 0){
                                    for(DataSnapshot adm: admin.getChildren()){
                                        String idnya = adm.child("id_kurir").getValue(String.class);
                                        String email = adm.child("email").getValue(String.class);
                                        String foto_kurir = adm.child("foto_kurir").getValue(String.class);
                                        String nama_kurir = adm.child("nama_kurir").getValue(String.class);
                                        String plat = adm.child("plat").getValue(String.class);

                                        ed.putString("idku", idnya);
                                        ed.putString("emailku", email);
                                        ed.putString("fotoku", foto_kurir);
                                        ed.putString("namaku", nama_kurir);
                                        ed.putString("platku", plat);
                                        ed.commit();
                                        Intent ke = new Intent(Login.this, MainActivity.class);
                                        startActivity(ke);
                                    }
                                }else{
                                    Toast.makeText(Login.this, "Login Gagal.", Toast.LENGTH_SHORT).show();
                                    Keluar(auth, googleApiClient);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.masuk:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Masuk(googleApiClient);
                    } else {
                        if (ContextCompat.checkSelfPermission(Login.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                new AlertDialog.Builder(Login.this)
                                        .setTitle("TerAter Alert")
                                        .setMessage("Izinkan aplikasi mengakses telpon.")
                                        .setCancelable(false)
                                        .setPositiveButton("Izinkan", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                ActivityCompat.requestPermissions(Login.this,
                                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                        8910);
                                                dialogInterface.cancel();
                                                Masuk(googleApiClient);
                                            }
                                        })
                                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                            public void onClick(final DialogInterface dialog, final int id) {
                                                dialog.cancel();
                                            }
                                        })
                                        .create()
                                        .show();
                            } else {
                                ActivityCompat.requestPermissions(Login.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        8910);
                            }
                        }
                    }
                }else {
                    Masuk(googleApiClient);
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
}